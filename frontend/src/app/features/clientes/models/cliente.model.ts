export interface Cliente {
  id: string;
  nome: string;
  sobrenome: string;
  dataNascimento: string;
  documentos: Documento[];
  enderecos: Endereco[];
  contatos: Contato[];
  status: ClienteStatus;
  observacoes?: string;
  criadoEm: string;
  atualizadoEm: string;
}

export interface Documento {
  tipo: TipoDocumento;
  numero: string;
  principal: boolean;
  arquivo?: File | null;
  arquivoUrl?: string;
  nomeArquivo?: string;
}

export type TipoDocumento = 'CPF' | 'RG' | 'CNH' | 'PASSAPORTE';

export interface Endereco {
  tipo: TipoEndereco;
  cep: string;
  logradouro: string;
  numero: string;
  complemento?: string;
  bairro: string;
  cidade: string;
  estado: string;
  principal: boolean;
}

export type TipoEndereco = 'RESIDENCIAL' | 'COMERCIAL' | 'COBRANCA' | 'ENTREGA';

export interface Contato {
  tipo: TipoContato;
  valor: string;
  principal: boolean;
}

export type TipoContato = 'EMAIL' | 'CELULAR' | 'TELEFONE' | 'WHATSAPP';

export type ClienteStatus = 'ATIVO' | 'INATIVO' | 'PENDENTE';

export interface ClienteFormData {
  nome: string;
  sobrenome: string;
  dataNascimento: string;
  documentos: Documento[];
  enderecos: Endereco[];
  contatos: Contato[];
  observacoes?: string;
}

export const TIPOS_CONTATO: { value: TipoContato; label: string }[] = [
  { value: 'EMAIL', label: 'Email' },
  { value: 'CELULAR', label: 'Celular' },
  { value: 'TELEFONE', label: 'Telefone Fixo' },
  { value: 'WHATSAPP', label: 'WhatsApp' }
];

// Limites de arrays
export const LIMITE_MIN_ITEMS = 1;
export const LIMITE_MAX_ITEMS = 3;

export const TIPOS_DOCUMENTO: { value: TipoDocumento; label: string }[] = [
  { value: 'CPF', label: 'CPF' },
  { value: 'RG', label: 'RG' },
  { value: 'CNH', label: 'CNH' },
  { value: 'PASSAPORTE', label: 'Passaporte' }
];

export const TIPOS_ENDERECO: { value: TipoEndereco; label: string }[] = [
  { value: 'RESIDENCIAL', label: 'Residencial' },
  { value: 'COMERCIAL', label: 'Comercial' },
  { value: 'COBRANCA', label: 'Cobranca' },
  { value: 'ENTREGA', label: 'Entrega' }
];

export const ESTADOS_BRASIL = [
  { sigla: 'AC', nome: 'Acre' },
  { sigla: 'AL', nome: 'Alagoas' },
  { sigla: 'AP', nome: 'Amapa' },
  { sigla: 'AM', nome: 'Amazonas' },
  { sigla: 'BA', nome: 'Bahia' },
  { sigla: 'CE', nome: 'Ceara' },
  { sigla: 'DF', nome: 'Distrito Federal' },
  { sigla: 'ES', nome: 'Espirito Santo' },
  { sigla: 'GO', nome: 'Goias' },
  { sigla: 'MA', nome: 'Maranhao' },
  { sigla: 'MT', nome: 'Mato Grosso' },
  { sigla: 'MS', nome: 'Mato Grosso do Sul' },
  { sigla: 'MG', nome: 'Minas Gerais' },
  { sigla: 'PA', nome: 'Para' },
  { sigla: 'PB', nome: 'Paraiba' },
  { sigla: 'PR', nome: 'Parana' },
  { sigla: 'PE', nome: 'Pernambuco' },
  { sigla: 'PI', nome: 'Piaui' },
  { sigla: 'RJ', nome: 'Rio de Janeiro' },
  { sigla: 'RN', nome: 'Rio Grande do Norte' },
  { sigla: 'RS', nome: 'Rio Grande do Sul' },
  { sigla: 'RO', nome: 'Rondonia' },
  { sigla: 'RR', nome: 'Roraima' },
  { sigla: 'SC', nome: 'Santa Catarina' },
  { sigla: 'SP', nome: 'Sao Paulo' },
  { sigla: 'SE', nome: 'Sergipe' },
  { sigla: 'TO', nome: 'Tocantins' }
];
