import { Pessoa, AuditFields } from './pessoa.model';
import { Viagem } from './viagem.model';

// ============================================
// CLIENTE DEPENDENTE
// ============================================
export interface ClienteDependente {
  clienteId: string; // UUID ou ULID
  dadosPessoais: Pessoa;
  viagensList: Viagem[];
}

// ============================================
// CLIENTE TITULAR
// ============================================
export interface ClienteTitular {
  clienteId: string; // UUID ou ULID
  dadosPessoais: Pessoa;
  viagensList: Viagem[];
  isAtivo: boolean;
  dependentesList: Pessoa[];
}

// ============================================
// CLIENTE TITULAR COM AUDIT
// ============================================
export interface ClienteTitularComAudit extends ClienteTitular, AuditFields {}

// ============================================
// CLIENTE DEPENDENTE COM AUDIT
// ============================================
export interface ClienteDependenteComAudit extends ClienteDependente, AuditFields {}
