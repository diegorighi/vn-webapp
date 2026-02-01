import { StatusViagemEnum } from './enums';
import { AuditFields } from './pessoa.model';

// ============================================
// VIAGEM
// ============================================
export interface Viagem {
  viagemId: string; // UUID ou ULID
  localizador: string;
  trecho: string[];
  data: string; // ISO datetime string com timezone (OffsetDateTime/ZonedDateTime)
  assento?: string;
  companhiaAereaList: string[];
  moeda: string; // ISO 4217 (BRL, USD, EUR)
  precoTotal: number; // BigDecimal como number (usar scale fixa no backend)
  precoPorPassageiro?: number;
  status: StatusViagemEnum;
  passageirosIds?: string[]; // IDs dos passageiros (titular + dependentes)
}

// ============================================
// VIAGEM COM AUDIT
// ============================================
export interface ViagemComAudit extends Viagem, AuditFields {}
