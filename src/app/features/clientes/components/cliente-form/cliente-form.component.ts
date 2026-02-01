import { Component, inject, OnInit, signal, computed, DestroyRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, FormArray, ReactiveFormsModule, Validators } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { timeout, catchError } from 'rxjs/operators';
import { of } from 'rxjs';
import { ClienteService } from '../../services/cliente.service';
import {
  Cliente,
  ClienteFormData,
  ESTADOS_BRASIL,
  TIPOS_DOCUMENTO,
  TIPOS_ENDERECO,
  TIPOS_CONTATO,
  TipoDocumento,
  TipoEndereco,
  TipoContato,
  LIMITE_MIN_ITEMS,
  LIMITE_MAX_ITEMS
} from '../../models/cliente.model';
import { CpfValidator } from '../../../../shared/validators/cpf.validator';
import { ToastService } from '../../../../shared/services/toast.service';

interface ViaCepResponse {
  cep: string;
  logradouro: string;
  complemento: string;
  bairro: string;
  localidade: string;
  uf: string;
  erro?: boolean;
}

@Component({
  selector: 'app-cliente-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './cliente-form.component.html',
  styleUrl: './cliente-form.component.scss'
})
export class ClienteFormComponent implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly router = inject(Router);
  private readonly route = inject(ActivatedRoute);
  private readonly http = inject(HttpClient);
  private readonly clienteService = inject(ClienteService);
  private readonly toastService = inject(ToastService);
  private readonly destroyRef = inject(DestroyRef);

  readonly estados = ESTADOS_BRASIL;
  readonly tiposDocumento = TIPOS_DOCUMENTO;
  readonly tiposEndereco = TIPOS_ENDERECO;
  readonly tiposContato = TIPOS_CONTATO;
  readonly limiteMin = LIMITE_MIN_ITEMS;
  readonly limiteMax = LIMITE_MAX_ITEMS;

  readonly isEditMode = signal(false);
  readonly isViewMode = signal(false);
  readonly isLoading = signal(false);
  readonly cepLoadingIndex = signal<number | null>(null);
  readonly clienteId = signal<string | null>(null);

  // Metodos para verificar limites (nao usar computed pois FormArray.length nao e signal)
  canAddDocumento(): boolean {
    return this.documentosArray.length < this.limiteMax;
  }
  canAddEndereco(): boolean {
    return this.enderecosArray.length < this.limiteMax;
  }
  canAddContato(): boolean {
    return this.contatosArray.length < this.limiteMax;
  }

  canRemoveDocumento(): boolean {
    return this.documentosArray.length > this.limiteMin;
  }
  canRemoveEndereco(): boolean {
    return this.enderecosArray.length > this.limiteMin;
  }
  canRemoveContato(): boolean {
    return this.contatosArray.length > this.limiteMin;
  }

  form!: FormGroup;

  ngOnInit(): void {
    this.initForm();
    this.checkEditMode();
  }

  private initForm(): void {
    this.form = this.fb.group({
      nome: ['', [Validators.required, Validators.minLength(2), Validators.maxLength(50)]],
      sobrenome: ['', [Validators.required, Validators.minLength(2), Validators.maxLength(100)]],
      dataNascimento: ['', [Validators.required]],
      documentos: this.fb.array([], [Validators.minLength(1), Validators.maxLength(3)]),
      enderecos: this.fb.array([], [Validators.minLength(1), Validators.maxLength(3)]),
      contatos: this.fb.array([], [Validators.minLength(1), Validators.maxLength(3)]),
      observacoes: ['', [Validators.maxLength(500)]]
    });

    // Adiciona itens iniciais
    this.addDocumento();
    this.addEndereco();
    this.addContato();
  }

  private checkEditMode(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.clienteId.set(id);

      // Check if route is for editing (/clientes/:id/editar) or just viewing (/clientes/:id)
      const url = this.route.snapshot.url.map(s => s.path).join('/');
      const isEditing = url.endsWith('editar');

      this.isEditMode.set(isEditing);
      this.isViewMode.set(!isEditing);

      this.loadCliente(id);
    }
  }

  private async loadCliente(id: string): Promise<void> {
    const cliente = await this.clienteService.getById(id);
    if (cliente) {
      this.documentosArray.clear();
      this.enderecosArray.clear();
      this.contatosArray.clear();

      this.form.patchValue({
        nome: cliente.nome,
        sobrenome: cliente.sobrenome,
        dataNascimento: cliente.dataNascimento,
        observacoes: cliente.observacoes || ''
      });

      cliente.documentos.forEach(doc => {
        this.addDocumento(doc.tipo, doc.numero, doc.principal, doc.arquivoUrl || '', doc.nomeArquivo || '');
      });

      cliente.enderecos.forEach(end => {
        this.addEndereco(end.tipo, end.cep, end.logradouro, end.numero,
          end.complemento || '', end.bairro, end.cidade, end.estado, end.principal);
      });

      cliente.contatos.forEach(cont => {
        this.addContato(cont.tipo, cont.valor, cont.principal);
      });

      // Disable form if in view mode
      if (this.isViewMode()) {
        this.form.disable();
      }
    } else {
      this.router.navigate(['/app/clientes']);
    }
  }

  // ==========================================
  // DOCUMENTOS
  // ==========================================
  get documentosArray(): FormArray {
    return this.form.get('documentos') as FormArray;
  }

  // ==========================================
  // UPLOAD DE DOCUMENTOS
  // ==========================================
  readonly MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB
  readonly ALLOWED_TYPES = ['image/jpeg', 'image/jpg', 'image/png'];

  addDocumento(
    tipo: TipoDocumento = 'CPF',
    numero: string = '',
    principal: boolean = false,
    arquivoUrl: string = '',
    nomeArquivo: string = ''
  ): void {
    if (this.documentosArray.length >= this.limiteMax) {
      this.toastService.warning(`Limite maximo de ${this.limiteMax} documentos atingido`);
      return;
    }

    const isFirst = this.documentosArray.length === 0;
    const docGroup = this.fb.group({
      tipo: [tipo, Validators.required],
      numero: [numero, [Validators.required]],
      principal: [isFirst || principal],
      arquivo: [null],
      arquivoUrl: [arquivoUrl],
      nomeArquivo: [nomeArquivo]
    });

    this.documentosArray.push(docGroup);
  }

  onFileSelected(event: Event, index: number): void {
    const input = event.target as HTMLInputElement;
    const file = input.files?.[0];

    if (!file) return;

    // Validar tipo
    if (!this.ALLOWED_TYPES.includes(file.type)) {
      this.toastService.error('Tipo de arquivo nao permitido. Use PNG, JPEG ou JPG');
      input.value = '';
      return;
    }

    // Validar tamanho
    if (file.size > this.MAX_FILE_SIZE) {
      this.toastService.error('Arquivo muito grande. Tamanho maximo: 10MB');
      input.value = '';
      return;
    }

    // Criar preview
    const reader = new FileReader();
    reader.onload = (e) => {
      const docGroup = this.documentosArray.at(index);
      docGroup.patchValue({
        arquivo: file,
        arquivoUrl: e.target?.result as string,
        nomeArquivo: file.name
      });
      this.toastService.success(`Arquivo "${file.name}" carregado com sucesso!`);
    };
    reader.readAsDataURL(file);
  }

  removeArquivo(index: number): void {
    const docGroup = this.documentosArray.at(index);
    docGroup.patchValue({
      arquivo: null,
      arquivoUrl: '',
      nomeArquivo: ''
    });
    this.toastService.info('Arquivo removido');
  }

  getFileSizeLabel(bytes: number): string {
    if (bytes === 0) return '0 Bytes';
    const k = 1024;
    const sizes = ['Bytes', 'KB', 'MB'];
    const i = Math.floor(Math.log(bytes) / Math.log(k));
    return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i];
  }

  removeDocumento(index: number): void {
    if (this.documentosArray.length <= this.limiteMin) return;

    const wasPrincipal = this.documentosArray.at(index).get('principal')?.value;
    this.documentosArray.removeAt(index);

    if (wasPrincipal && this.documentosArray.length > 0) {
      this.documentosArray.at(0).get('principal')?.setValue(true);
    }
  }

  setDocumentoPrincipal(index: number): void {
    this.documentosArray.controls.forEach((control, i) => {
      control.get('principal')?.setValue(i === index);
    });
  }

  // ==========================================
  // ENDERECOS
  // ==========================================
  get enderecosArray(): FormArray {
    return this.form.get('enderecos') as FormArray;
  }

  addEndereco(
    tipo: TipoEndereco = 'RESIDENCIAL', cep: string = '', logradouro: string = '',
    numero: string = '', complemento: string = '', bairro: string = '',
    cidade: string = '', estado: string = '', principal: boolean = false
  ): void {
    if (this.enderecosArray.length >= this.limiteMax) {
      this.toastService.warning(`Limite maximo de ${this.limiteMax} enderecos atingido`);
      return;
    }

    const isFirst = this.enderecosArray.length === 0;
    const endGroup = this.fb.group({
      tipo: [tipo, Validators.required],
      cep: [cep, [Validators.required, Validators.pattern(/^\d{5}-\d{3}$/)]],
      logradouro: [logradouro, [Validators.required]],
      numero: [numero, [Validators.required]],
      complemento: [complemento],
      bairro: [bairro, [Validators.required]],
      cidade: [cidade, [Validators.required]],
      estado: [estado, [Validators.required]],
      principal: [isFirst || principal]
    });

    this.enderecosArray.push(endGroup);
  }

  removeEndereco(index: number): void {
    if (this.enderecosArray.length <= this.limiteMin) return;

    const wasPrincipal = this.enderecosArray.at(index).get('principal')?.value;
    this.enderecosArray.removeAt(index);

    if (wasPrincipal && this.enderecosArray.length > 0) {
      this.enderecosArray.at(0).get('principal')?.setValue(true);
    }
  }

  setEnderecoPrincipal(index: number): void {
    this.enderecosArray.controls.forEach((control, i) => {
      control.get('principal')?.setValue(i === index);
    });
  }

  // ==========================================
  // CONTATOS
  // ==========================================
  get contatosArray(): FormArray {
    return this.form.get('contatos') as FormArray;
  }

  addContato(tipo: TipoContato = 'EMAIL', valor: string = '', principal: boolean = false): void {
    if (this.contatosArray.length >= this.limiteMax) {
      this.toastService.warning(`Limite maximo de ${this.limiteMax} contatos atingido`);
      return;
    }

    const isFirst = this.contatosArray.length === 0;
    const contatoGroup = this.fb.group({
      tipo: [tipo, Validators.required],
      valor: [valor, [Validators.required]],
      principal: [isFirst || principal]
    });

    this.contatosArray.push(contatoGroup);
  }

  removeContato(index: number): void {
    if (this.contatosArray.length <= this.limiteMin) return;

    const wasPrincipal = this.contatosArray.at(index).get('principal')?.value;
    this.contatosArray.removeAt(index);

    if (wasPrincipal && this.contatosArray.length > 0) {
      this.contatosArray.at(0).get('principal')?.setValue(true);
    }
  }

  setContatoPrincipal(index: number): void {
    this.contatosArray.controls.forEach((control, i) => {
      control.get('principal')?.setValue(i === index);
    });
  }

  // ==========================================
  // SUBMIT
  // ==========================================
  async onSubmit(): Promise<void> {
    if (this.form.invalid) {
      this.markFormGroupTouched();
      return;
    }

    this.isLoading.set(true);
    const formData: ClienteFormData = this.form.value;

    try {
      if (this.isEditMode()) {
        await this.clienteService.update(this.clienteId()!, formData);
      } else {
        await this.clienteService.create(formData);
      }
      this.router.navigate(['/app/clientes']);
    } catch (error) {
      this.toastService.error('Erro ao salvar cliente');
    } finally {
      this.isLoading.set(false);
    }
  }

  private markFormGroupTouched(): void {
    Object.keys(this.form.controls).forEach(key => {
      const control = this.form.get(key);
      if (control instanceof FormArray) {
        control.controls.forEach(group => {
          if (group instanceof FormGroup) {
            Object.keys(group.controls).forEach(k => {
              group.get(k)?.markAsTouched();
            });
          }
        });
      } else {
        control?.markAsTouched();
      }
    });
  }

  isFieldInvalid(fieldName: string): boolean {
    const field = this.form.get(fieldName);
    return !!(field?.invalid && field?.touched);
  }

  isArrayFieldInvalid(arrayName: string, index: number, fieldName: string): boolean {
    const array = this.form.get(arrayName) as FormArray;
    const field = array.at(index)?.get(fieldName);
    return !!(field?.invalid && field?.touched);
  }

  getFieldError(fieldName: string): string {
    const field = this.form.get(fieldName);
    return this.getErrorMessage(field, fieldName);
  }

  getArrayFieldError(arrayName: string, index: number, fieldName: string): string {
    const array = this.form.get(arrayName) as FormArray;
    const field = array.at(index)?.get(fieldName);
    return this.getErrorMessage(field, fieldName);
  }

  private getErrorMessage(field: any, fieldName: string): string {
    if (!field?.errors) return '';

    if (field.errors['required']) return 'Campo obrigatorio';
    if (field.errors['email']) return 'Email invalido';
    if (field.errors['minlength']) return `Minimo ${field.errors['minlength'].requiredLength} caracteres`;
    if (field.errors['maxlength']) return `Maximo ${field.errors['maxlength'].requiredLength} caracteres`;
    if (field.errors['cpfInvalido']) return 'CPF invalido';
    if (field.errors['pattern']) {
      if (fieldName === 'telefone' || fieldName === 'valor') return 'Formato invalido';
      if (fieldName === 'cep') return 'Formato: 00000-000';
    }

    return 'Valor invalido';
  }

  // ==========================================
  // MASCARAS
  // ==========================================
  onDocumentoInput(event: Event, index: number): void {
    const input = event.target as HTMLInputElement;
    const tipo = this.documentosArray.at(index).get('tipo')?.value;
    let value = input.value.replace(/\D/g, '');

    if (tipo === 'CPF') {
      if (value.length > 11) value = value.slice(0, 11);
      if (value.length > 9) {
        value = `${value.slice(0, 3)}.${value.slice(3, 6)}.${value.slice(6, 9)}-${value.slice(9)}`;
      } else if (value.length > 6) {
        value = `${value.slice(0, 3)}.${value.slice(3, 6)}.${value.slice(6)}`;
      } else if (value.length > 3) {
        value = `${value.slice(0, 3)}.${value.slice(3)}`;
      }
    }

    this.documentosArray.at(index).get('numero')?.setValue(value, { emitEvent: false });
  }

  onContatoInput(event: Event, index: number): void {
    const input = event.target as HTMLInputElement;
    const tipo = this.contatosArray.at(index).get('tipo')?.value;
    let value = input.value;

    if (tipo === 'CELULAR' || tipo === 'TELEFONE' || tipo === 'WHATSAPP') {
      value = value.replace(/\D/g, '');
      if (value.length > 11) value = value.slice(0, 11);

      if (value.length > 6) {
        value = `(${value.slice(0, 2)}) ${value.slice(2, 7)}-${value.slice(7)}`;
      } else if (value.length > 2) {
        value = `(${value.slice(0, 2)}) ${value.slice(2)}`;
      } else if (value.length > 0) {
        value = `(${value}`;
      }
    }

    this.contatosArray.at(index).get('valor')?.setValue(value, { emitEvent: false });
  }

  onCepInput(event: Event, index: number): void {
    const input = event.target as HTMLInputElement;
    let value = input.value.replace(/\D/g, '');
    if (value.length > 8) value = value.slice(0, 8);

    if (value.length > 5) {
      value = `${value.slice(0, 5)}-${value.slice(5)}`;
    }

    this.enderecosArray.at(index).get('cep')?.setValue(value, { emitEvent: false });

    if (value.replace('-', '').length === 8) {
      this.buscarCep(value.replace('-', ''), index);
    }
  }

  private buscarCep(cep: string, index: number): void {
    // Sanitizar entrada - apenas digitos
    const sanitizedCep = cep.replace(/\D/g, '');

    // Validar formato
    if (!/^\d{8}$/.test(sanitizedCep)) {
      this.toastService.warning('CEP invalido');
      return;
    }

    this.cepLoadingIndex.set(index);

    this.http.get<ViaCepResponse>(`https://viacep.com.br/ws/${sanitizedCep}/json/`)
      .pipe(
        timeout(5000), // 5 segundos de timeout
        takeUntilDestroyed(this.destroyRef),
        catchError(() => {
          this.toastService.error('Erro ao buscar CEP. Tente novamente.');
          return of(null);
        })
      )
      .subscribe({
        next: (data) => {
          this.cepLoadingIndex.set(null);

          if (!data) return;

          if (data.erro) {
            this.toastService.warning('CEP nao encontrado');
            return;
          }

          // Validar dados antes de usar
          const enderecoGroup = this.enderecosArray.at(index);
          enderecoGroup.patchValue({
            logradouro: this.sanitizeString(data.logradouro),
            bairro: this.sanitizeString(data.bairro),
            cidade: this.sanitizeString(data.localidade),
            estado: this.sanitizeString(data.uf),
            complemento: this.sanitizeString(data.complemento)
          });

          setTimeout(() => {
            const numeroInput = document.getElementById(`numero-${index}`) as HTMLInputElement;
            if (numeroInput) numeroInput.focus();
          }, 100);
        }
      });
  }

  private sanitizeString(value: string | undefined | null): string {
    if (!value) return '';
    // Remove caracteres potencialmente perigosos
    return value.replace(/[<>\"'&]/g, '').trim();
  }

  onTipoDocumentoChange(index: number): void {
    this.documentosArray.at(index).get('numero')?.setValue('');
  }

  onTipoContatoChange(index: number): void {
    this.contatosArray.at(index).get('valor')?.setValue('');
  }

  getDocumentoPlaceholder(tipo: TipoDocumento): string {
    const placeholders: Record<TipoDocumento, string> = {
      'CPF': '000.000.000-00',
      'RG': '00.000.000-0',
      'CNH': '00000000000',
      'PASSAPORTE': 'AA000000'
    };
    return placeholders[tipo] || '';
  }

  getContatoPlaceholder(tipo: TipoContato): string {
    const placeholders: Record<TipoContato, string> = {
      'EMAIL': 'email@exemplo.com',
      'CELULAR': '(00) 00000-0000',
      'TELEFONE': '(00) 0000-0000',
      'WHATSAPP': '(00) 00000-0000'
    };
    return placeholders[tipo] || '';
  }
}
