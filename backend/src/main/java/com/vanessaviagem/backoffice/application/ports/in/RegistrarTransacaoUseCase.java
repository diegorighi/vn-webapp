package com.vanessaviagem.backoffice.application.ports.in;

import com.vanessaviagem.backoffice.domain.model.ContaPrograma;
import com.vanessaviagem.backoffice.domain.model.Transacao;
import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

/**
 * Input port for registering transactions on program accounts.
 * This use case handles the creation of purchases, bonuses, and sales of miles.
 *
 * <p>Each transaction type has different business rules:
 * <ul>
 *   <li>COMPRA: Increases balance and cost base</li>
 *   <li>BONUS: Increases balance without cost (promotional miles)</li>
 *   <li>VENDA: Decreases balance and cost base proportionally</li>
 * </ul>
 *
 * <p>If a ContaPrograma does not exist for the given program and owner,
 * it will be created automatically for COMPRA and BONUS operations.
 * VENDA operations require an existing account with sufficient balance.</p>
 */
public interface RegistrarTransacaoUseCase {

    /**
     * Registers a purchase (COMPRA) of miles.
     * If no account exists for the program and owner, one is created.
     *
     * @param command the command containing purchase data
     * @return the result with the created transaction and updated account
     * @throws IllegalArgumentException if command data is invalid
     */
    TransacaoResult registrarCompra(RegistrarCompraCommand command);

    /**
     * Registers a bonus (BONUS) of miles.
     * If no account exists for the program and owner, one is created.
     *
     * @param command the command containing bonus data
     * @return the result with the created transaction and updated account
     * @throws IllegalArgumentException if command data is invalid
     */
    TransacaoResult registrarBonus(RegistrarBonusCommand command);

    /**
     * Registers a sale (VENDA) of miles.
     * The account must exist and have sufficient balance.
     *
     * @param command the command containing sale data
     * @return the result with the created transaction, updated account, and profit
     * @throws IllegalArgumentException if command data is invalid
     * @throws com.vanessaviagem.backoffice.domain.exceptions.SaldoMilhasInsuficienteException if balance is insufficient
     * @throws IllegalStateException if the account does not exist
     */
    VendaResult registrarVenda(RegistrarVendaCommand command);

    // --- Command Objects ---

    /**
     * Command object for registering a purchase of miles.
     *
     * @param tenantId the tenant identifier for multi-tenant isolation
     * @param programaId the loyalty program identifier
     * @param programaNome the program name (for denormalization)
     * @param owner the person who owns the miles
     * @param milhas the quantity of miles purchased (must be positive)
     * @param valor the purchase value in BRL (must be positive)
     * @param fonte the source of the purchase (partner name, optional)
     * @param observacao notes about the transaction (optional)
     */
    record RegistrarCompraCommand(
            UUID tenantId,
            UUID programaId,
            String programaNome,
            String owner,
            long milhas,
            BigDecimal valor,
            String fonte,
            String observacao
    ) {
        /**
         * Compact constructor with validation.
         */
        public RegistrarCompraCommand {
            Objects.requireNonNull(tenantId, "tenantId eh obrigatorio");
            Objects.requireNonNull(programaId, "programaId eh obrigatorio");
            Objects.requireNonNull(programaNome, "programaNome eh obrigatorio");
            Objects.requireNonNull(owner, "owner eh obrigatorio");
            Objects.requireNonNull(valor, "valor eh obrigatorio");

            if (programaNome.isBlank()) {
                throw new IllegalArgumentException("programaNome nao pode estar vazio");
            }
            if (owner.isBlank()) {
                throw new IllegalArgumentException("owner nao pode estar vazio");
            }
            if (milhas <= 0) {
                throw new IllegalArgumentException("milhas deve ser positivo");
            }
            if (valor.compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("valor deve ser positivo para compra");
            }
        }
    }

    /**
     * Command object for registering a bonus of miles.
     *
     * @param tenantId the tenant identifier for multi-tenant isolation
     * @param programaId the loyalty program identifier
     * @param programaNome the program name (for denormalization)
     * @param owner the person who owns the miles
     * @param milhas the quantity of bonus miles (must be positive)
     * @param fonte the source of the bonus (cashback partner name)
     * @param observacao notes about the transaction (optional)
     */
    record RegistrarBonusCommand(
            UUID tenantId,
            UUID programaId,
            String programaNome,
            String owner,
            long milhas,
            String fonte,
            String observacao
    ) {
        /**
         * Compact constructor with validation.
         */
        public RegistrarBonusCommand {
            Objects.requireNonNull(tenantId, "tenantId eh obrigatorio");
            Objects.requireNonNull(programaId, "programaId eh obrigatorio");
            Objects.requireNonNull(programaNome, "programaNome eh obrigatorio");
            Objects.requireNonNull(owner, "owner eh obrigatorio");

            if (programaNome.isBlank()) {
                throw new IllegalArgumentException("programaNome nao pode estar vazio");
            }
            if (owner.isBlank()) {
                throw new IllegalArgumentException("owner nao pode estar vazio");
            }
            if (milhas <= 0) {
                throw new IllegalArgumentException("milhas deve ser positivo");
            }
        }
    }

    /**
     * Command object for registering a sale of miles.
     *
     * @param tenantId the tenant identifier for multi-tenant isolation
     * @param programaId the loyalty program identifier
     * @param programaNome the program name (for denormalization)
     * @param owner the person who owns the miles
     * @param milhas the quantity of miles to sell (must be positive)
     * @param valorVenda the sale value in BRL (must be positive)
     * @param observacao notes about the transaction (optional)
     */
    record RegistrarVendaCommand(
            UUID tenantId,
            UUID programaId,
            String programaNome,
            String owner,
            long milhas,
            BigDecimal valorVenda,
            String observacao
    ) {
        /**
         * Compact constructor with validation.
         */
        public RegistrarVendaCommand {
            Objects.requireNonNull(tenantId, "tenantId eh obrigatorio");
            Objects.requireNonNull(programaId, "programaId eh obrigatorio");
            Objects.requireNonNull(programaNome, "programaNome eh obrigatorio");
            Objects.requireNonNull(owner, "owner eh obrigatorio");
            Objects.requireNonNull(valorVenda, "valorVenda eh obrigatorio");

            if (programaNome.isBlank()) {
                throw new IllegalArgumentException("programaNome nao pode estar vazio");
            }
            if (owner.isBlank()) {
                throw new IllegalArgumentException("owner nao pode estar vazio");
            }
            if (milhas <= 0) {
                throw new IllegalArgumentException("milhas deve ser positivo");
            }
            if (valorVenda.compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("valorVenda deve ser positivo para venda");
            }
        }
    }

    // --- Result Objects ---

    /**
     * Result object for purchase and bonus transactions.
     *
     * @param transacao the created transaction
     * @param contaAtualizada the updated account state
     */
    record TransacaoResult(
            Transacao transacao,
            ContaPrograma contaAtualizada
    ) {
        /**
         * Compact constructor with validation.
         */
        public TransacaoResult {
            Objects.requireNonNull(transacao, "transacao eh obrigatorio");
            Objects.requireNonNull(contaAtualizada, "contaAtualizada eh obrigatorio");
        }
    }

    /**
     * Result object for sale transactions, including profit calculation.
     *
     * @param transacao the created transaction
     * @param contaAtualizada the updated account state
     * @param lucro the profit (sale value minus proportional cost)
     */
    record VendaResult(
            Transacao transacao,
            ContaPrograma contaAtualizada,
            BigDecimal lucro
    ) {
        /**
         * Compact constructor with validation.
         */
        public VendaResult {
            Objects.requireNonNull(transacao, "transacao eh obrigatorio");
            Objects.requireNonNull(contaAtualizada, "contaAtualizada eh obrigatorio");
            Objects.requireNonNull(lucro, "lucro eh obrigatorio");
        }

        /**
         * Checks if the sale resulted in a profit.
         *
         * @return true if lucro is positive
         */
        public boolean teveLucro() {
            return lucro.compareTo(BigDecimal.ZERO) > 0;
        }

        /**
         * Checks if the sale resulted in a loss.
         *
         * @return true if lucro is negative
         */
        public boolean tevePrejuizo() {
            return lucro.compareTo(BigDecimal.ZERO) < 0;
        }
    }
}
