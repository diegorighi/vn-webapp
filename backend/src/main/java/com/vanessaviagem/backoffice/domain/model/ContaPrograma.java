package com.vanessaviagem.backoffice.domain.model;

import com.vanessaviagem.backoffice.domain.exceptions.SaldoMilhasInsuficienteException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * Conta de programa de milhas com rastreamento de custo medio.
 *
 * <p>Regras de negocio:
 * <ul>
 *   <li>COMPRA: +milhas, +custoBase, recalcula custoMedio</li>
 *   <li>BONUS: +milhas, custoBase inalterado, recalcula custoMedio (dilui o custo)</li>
 *   <li>VENDA: -milhas, -custoBase proporcional, custoMedio inalterado</li>
 * </ul>
 */
public record ContaPrograma(
        UUID id,
        UUID tenantId,
        UUID programaId,
        String programaNome,
        String owner,
        long saldoMilhas,
        BigDecimal custoBaseTotalBRL,
        BigDecimal custoMedioMilheiroAtual,
        LocalDateTime criadoEm,
        LocalDateTime atualizadoEm
) {
    private static final int SCALE_CUSTO_MEDIO = 6;
    private static final int SCALE_MONETARIO = 2;

    public ContaPrograma {
        Objects.requireNonNull(id, "id eh obrigatorio");
        Objects.requireNonNull(tenantId, "tenantId eh obrigatorio");
        Objects.requireNonNull(programaId, "programaId eh obrigatorio");
        Objects.requireNonNull(programaNome, "programaNome eh obrigatorio");
        Objects.requireNonNull(owner, "owner eh obrigatorio");
        Objects.requireNonNull(criadoEm, "criadoEm eh obrigatorio");
        Objects.requireNonNull(atualizadoEm, "atualizadoEm eh obrigatorio");

        if (saldoMilhas < 0) {
            throw new IllegalArgumentException("saldoMilhas nao pode ser negativo");
        }
        if (custoBaseTotalBRL == null) {
            custoBaseTotalBRL = BigDecimal.ZERO;
        }
        if (custoMedioMilheiroAtual == null) {
            custoMedioMilheiroAtual = BigDecimal.ZERO;
        }
        if (custoBaseTotalBRL.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("custoBaseTotalBRL nao pode ser negativo");
        }
        if (custoMedioMilheiroAtual.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("custoMedioMilheiroAtual nao pode ser negativo");
        }
        if (saldoMilhas == 0 && custoBaseTotalBRL.compareTo(BigDecimal.ZERO) > 0) {
            throw new IllegalArgumentException(
                    "custoBaseTotalBRL deve ser zero quando saldoMilhas eh zero");
        }
    }

    /**
     * Cria uma nova conta de programa.
     *
     * @param tenantId the tenant identifier
     * @param programaId the program identifier
     * @param programaNome the program name
     * @param owner the owner name
     * @return a new ContaPrograma with zero balance
     */
    public static ContaPrograma criar(UUID tenantId, UUID programaId,
                                       String programaNome, String owner) {
        Objects.requireNonNull(tenantId, "tenantId eh obrigatorio");
        Objects.requireNonNull(programaId, "programaId eh obrigatorio");
        Objects.requireNonNull(programaNome, "programaNome eh obrigatorio");
        Objects.requireNonNull(owner, "owner eh obrigatorio");

        String trimmedProgramaNome = programaNome.trim();
        String trimmedOwner = owner.trim();

        if (trimmedProgramaNome.isBlank()) {
            throw new IllegalArgumentException("programaNome nao pode estar vazio");
        }
        if (trimmedOwner.isBlank()) {
            throw new IllegalArgumentException("owner nao pode estar vazio");
        }

        LocalDateTime agora = LocalDateTime.now();
        return new ContaPrograma(
                UUID.randomUUID(),
                tenantId,
                programaId,
                trimmedProgramaNome,
                trimmedOwner,
                0L,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                agora,
                agora
        );
    }

    /**
     * Aplica uma compra de milhas.
     * Aumenta saldo, aumenta custo base, recalcula custo medio.
     *
     * @param milhas the number of miles to purchase (must be positive)
     * @param valor the purchase value in BRL (must not be null or negative)
     * @return a new ContaPrograma with updated values
     */
    public ContaPrograma aplicarCompra(long milhas, BigDecimal valor) {
        if (milhas <= 0) {
            throw new IllegalArgumentException("milhas deve ser positivo para compra");
        }
        Objects.requireNonNull(valor, "valor eh obrigatorio");
        if (valor.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("valor nao pode ser negativo");
        }

        long novoSaldo = this.saldoMilhas + milhas;
        BigDecimal novoCustoBase = this.custoBaseTotalBRL.add(valor);
        BigDecimal novoCustoMedio = calcularCustoMedio(novoSaldo, novoCustoBase);

        return new ContaPrograma(
                this.id,
                this.tenantId,
                this.programaId,
                this.programaNome,
                this.owner,
                novoSaldo,
                novoCustoBase,
                novoCustoMedio,
                this.criadoEm,
                LocalDateTime.now()
        );
    }

    /**
     * Aplica um bonus de milhas (cashback, promocao, etc).
     * Aumenta saldo, custo base inalterado, recalcula custo medio (dilui).
     *
     * @param milhas the number of bonus miles (must be positive)
     * @return a new ContaPrograma with updated values
     */
    public ContaPrograma aplicarBonus(long milhas) {
        if (milhas <= 0) {
            throw new IllegalArgumentException("milhas deve ser positivo para bonus");
        }

        long novoSaldo = this.saldoMilhas + milhas;
        BigDecimal novoCustoMedio = calcularCustoMedio(novoSaldo, this.custoBaseTotalBRL);

        return new ContaPrograma(
                this.id,
                this.tenantId,
                this.programaId,
                this.programaNome,
                this.owner,
                novoSaldo,
                this.custoBaseTotalBRL,
                novoCustoMedio,
                this.criadoEm,
                LocalDateTime.now()
        );
    }

    /**
     * Aplica uma venda de milhas.
     * Diminui saldo, diminui custo base proporcionalmente.
     *
     * @param milhas the number of miles to sell (must be positive and not exceed balance)
     * @param valorVenda the sale value in BRL (must not be null or negative)
     * @return ResultadoVenda with updated account, removed cost, and profit
     * @throws SaldoMilhasInsuficienteException if miles exceed current balance
     */
    public ResultadoVenda aplicarVenda(long milhas, BigDecimal valorVenda) {
        if (milhas <= 0) {
            throw new IllegalArgumentException("milhas deve ser positivo para venda");
        }
        Objects.requireNonNull(valorVenda, "valorVenda eh obrigatorio");
        if (valorVenda.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("valorVenda nao pode ser negativo");
        }
        if (milhas > this.saldoMilhas) {
            throw new SaldoMilhasInsuficienteException(this.programaId, this.saldoMilhas, milhas);
        }

        // Calcular custo proporcional removido
        BigDecimal custoRemovido;
        if (this.saldoMilhas == milhas) {
            // Vendendo tudo - remove todo o custo base
            custoRemovido = this.custoBaseTotalBRL;
        } else {
            // Custo removido = (milhas vendidas / saldo atual) * custo base
            custoRemovido = BigDecimal.valueOf(milhas)
                    .divide(BigDecimal.valueOf(this.saldoMilhas), SCALE_CUSTO_MEDIO,
                            RoundingMode.HALF_UP)
                    .multiply(this.custoBaseTotalBRL)
                    .setScale(SCALE_MONETARIO, RoundingMode.HALF_UP);
        }

        long novoSaldo = this.saldoMilhas - milhas;
        BigDecimal novoCustoBase = this.custoBaseTotalBRL.subtract(custoRemovido);

        // Se saldo zerou, custo base tambem deve zerar
        if (novoSaldo == 0) {
            novoCustoBase = BigDecimal.ZERO;
        }

        BigDecimal novoCustoMedio = calcularCustoMedio(novoSaldo, novoCustoBase);
        BigDecimal lucro = valorVenda.subtract(custoRemovido);

        ContaPrograma contaAtualizada = new ContaPrograma(
                this.id,
                this.tenantId,
                this.programaId,
                this.programaNome,
                this.owner,
                novoSaldo,
                novoCustoBase,
                novoCustoMedio,
                this.criadoEm,
                LocalDateTime.now()
        );

        return new ResultadoVenda(contaAtualizada, custoRemovido, lucro);
    }

    /**
     * Verifica se a conta tem saldo positivo.
     *
     * @return true if the account has a positive balance
     */
    public boolean temSaldo() {
        return this.saldoMilhas > 0;
    }

    /**
     * Verifica se eh possivel sacar a quantidade de milhas especificada.
     *
     * @param milhas the number of miles to check
     * @return true if the withdrawal is possible
     */
    public boolean podeSacar(long milhas) {
        return milhas > 0 && milhas <= this.saldoMilhas;
    }

    /**
     * Calcula o custo medio por milheiro.
     * custoMedio = custoBase / (saldo / 1000)
     */
    private BigDecimal calcularCustoMedio(long saldo, BigDecimal custoBase) {
        if (saldo == 0 || custoBase.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal milheiros = BigDecimal.valueOf(saldo)
                .divide(BigDecimal.valueOf(1000), SCALE_CUSTO_MEDIO, RoundingMode.HALF_UP);
        return custoBase.divide(milheiros, SCALE_CUSTO_MEDIO, RoundingMode.HALF_UP);
    }
}
