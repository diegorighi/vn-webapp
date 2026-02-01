import { AbstractControl, ValidationErrors } from '@angular/forms';

export class CpfValidator {
  static validate(control: AbstractControl): ValidationErrors | null {
    const cpf = control.value;

    if (!cpf) {
      return null; // Deixa o required validar
    }

    // Remove caracteres nao numericos
    const cpfLimpo = cpf.replace(/\D/g, '');

    if (cpfLimpo.length !== 11) {
      return { cpfInvalido: true };
    }

    // Verifica se todos os digitos sao iguais (CPFs invalidos conhecidos)
    if (/^(\d)\1+$/.test(cpfLimpo)) {
      return { cpfInvalido: true };
    }

    // Validacao do primeiro digito verificador
    let soma = 0;
    for (let i = 0; i < 9; i++) {
      soma += parseInt(cpfLimpo.charAt(i), 10) * (10 - i);
    }
    let resto = (soma * 10) % 11;
    if (resto === 10 || resto === 11) {
      resto = 0;
    }
    if (resto !== parseInt(cpfLimpo.charAt(9), 10)) {
      return { cpfInvalido: true };
    }

    // Validacao do segundo digito verificador
    soma = 0;
    for (let i = 0; i < 10; i++) {
      soma += parseInt(cpfLimpo.charAt(i), 10) * (11 - i);
    }
    resto = (soma * 10) % 11;
    if (resto === 10 || resto === 11) {
      resto = 0;
    }
    if (resto !== parseInt(cpfLimpo.charAt(10), 10)) {
      return { cpfInvalido: true };
    }

    return null;
  }
}
