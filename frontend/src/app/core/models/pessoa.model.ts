import { SexoEnum, TipoContatoEnum, TipoDocumentoEnum } from './enums';

// ============================================
// DOCUMENTO
// ============================================
export interface Documento {
  tipo: TipoDocumentoEnum;
  numero: string;
  validade?: string; // ISO date string (LocalDate)
  emitidoEm?: string; // ISO date string (LocalDate)
}

// ============================================
// ENDERECO
// ============================================
export interface Endereco {
  isPrincipal: boolean;
  logradouro: string;
  numero: number;
  complemento?: string;
  bairro: string;
  cep: string;
  cidade: string;
  estado: string;
  pais: string;
}

// ============================================
// CONTATO
// ============================================
export interface Contato {
  isPrincipal: boolean;
  tipo: TipoContatoEnum;
  valor: string;
}

// ============================================
// PESSOA
// ============================================
export interface Pessoa {
  pessoaId: string; // UUID ou ULID
  primeiroNome: string;
  nomeDoMeio?: string;
  sobrenome: string;
  dataNascimento: string; // ISO date string (LocalDate)
  sexo: SexoEnum;
  documentosList: Documento[];
  enderecoList: Endereco[];
  contatoList: Contato[];
}

// ============================================
// AUDIT FIELDS
// ============================================
export interface AuditFields {
  createdAt: string; // ISO datetime string
  createdBy: string;
  updatedAt?: string; // ISO datetime string
  updatedBy?: string;
  inactivatedAt?: string; // ISO datetime string
  inactivatedBy?: string;
}
