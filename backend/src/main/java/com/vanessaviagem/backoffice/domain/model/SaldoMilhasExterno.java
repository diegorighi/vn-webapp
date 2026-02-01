package com.vanessaviagem.backoffice.domain.model;

import com.vanessaviagem.backoffice.domain.model.enums.FonteConsulta;
import com.vanessaviagem.backoffice.domain.model.enums.TipoProgramaMilhas;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * Representa o saldo de milhas obtido de uma fonte externa (programa de fidelidade).
 * Contém o saldo consultado em tempo real e metadados da consulta.
 */
public record SaldoMilhasExterno(
        UUID id,
        UUID tenantId,
        UUID clienteId,
        UUID credencialId,
        TipoProgramaMilhas programa,
        long saldoMilhas,
        long milhasAExpirar,
        LocalDateTime dataExpiracao,
        String nivelFidelidade,
        BigDecimal valorEmReais,
        FonteConsulta fonte,
        LocalDateTime consultadoEm,
        int tempoRespostaMs
) {
    public SaldoMilhasExterno {
        Objects.requireNonNull(id, "id é obrigatório");
        Objects.requireNonNull(tenantId, "tenantId é obrigatório");
        Objects.requireNonNull(clienteId, "clienteId é obrigatório");
        Objects.requireNonNull(credencialId, "credencialId é obrigatório");
        Objects.requireNonNull(programa, "programa é obrigatório");
        Objects.requireNonNull(fonte, "fonte é obrigatório");
        Objects.requireNonNull(consultadoEm, "consultadoEm é obrigatório");

        if (saldoMilhas < 0) {
            throw new IllegalArgumentException("saldoMilhas não pode ser negativo");
        }
        if (tempoRespostaMs < 0) {
            throw new IllegalArgumentException("tempoRespostaMs não pode ser negativo");
        }
    }

    /**
     * Cria um novo registro de saldo consultado.
     */
    public static SaldoMilhasExterno criar(
            UUID tenantId,
            UUID clienteId,
            UUID credencialId,
            TipoProgramaMilhas programa,
            long saldoMilhas,
            FonteConsulta fonte,
            int tempoRespostaMs
    ) {
        return new SaldoMilhasExterno(
                UUID.randomUUID(),
                tenantId,
                clienteId,
                credencialId,
                programa,
                saldoMilhas,
                0,
                null,
                null,
                null,
                fonte,
                LocalDateTime.now(),
                tempoRespostaMs
        );
    }

    /**
     * Cria um novo registro de saldo com informações detalhadas.
     */
    public static SaldoMilhasExterno criarCompleto(
            UUID tenantId,
            UUID clienteId,
            UUID credencialId,
            TipoProgramaMilhas programa,
            long saldoMilhas,
            long milhasAExpirar,
            LocalDateTime dataExpiracao,
            String nivelFidelidade,
            BigDecimal valorEmReais,
            FonteConsulta fonte,
            int tempoRespostaMs
    ) {
        return new SaldoMilhasExterno(
                UUID.randomUUID(),
                tenantId,
                clienteId,
                credencialId,
                programa,
                saldoMilhas,
                milhasAExpirar,
                dataExpiracao,
                nivelFidelidade,
                valorEmReais,
                fonte,
                LocalDateTime.now(),
                tempoRespostaMs
        );
    }

    /**
     * Calcula o valor estimado por milheiro.
     *
     * @return Valor por 1000 milhas em reais, ou null se valor não informado
     */
    public BigDecimal valorPorMilheiro() {
        if (valorEmReais == null || saldoMilhas == 0) {
            return null;
        }
        return valorEmReais.multiply(BigDecimal.valueOf(1000))
                .divide(BigDecimal.valueOf(saldoMilhas), 4, java.math.RoundingMode.HALF_UP);
    }

    /**
     * Verifica se há milhas próximas da expiração.
     */
    public boolean temMilhasAExpirar() {
        return milhasAExpirar > 0 && dataExpiracao != null;
    }

    /**
     * Verifica se a consulta foi realizada via cache.
     */
    public boolean isFromCache() {
        return fonte == FonteConsulta.CACHE;
    }

    /**
     * Verifica se a consulta foi lenta (acima de 5 segundos).
     */
    public boolean isConsultaLenta() {
        return tempoRespostaMs > 5000;
    }
}
