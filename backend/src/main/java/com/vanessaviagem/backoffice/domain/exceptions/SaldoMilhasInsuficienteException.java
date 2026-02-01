package com.vanessaviagem.backoffice.domain.exceptions;

import java.util.UUID;

/**
 * Exception thrown when an operation requires more miles than
 * currently available in a program account.
 *
 * <p>This exception is typically thrown when attempting to:
 * <ul>
 *   <li>Sell more miles than the current balance</li>
 *   <li>Transfer miles exceeding the available amount</li>
 *   <li>Book a trip requiring more miles than available</li>
 * </ul>
 */
public class SaldoMilhasInsuficienteException extends DomainException {

    private final UUID programaId;
    private final long saldoAtual;
    private final long milhasSolicitadas;

    /**
     * Creates a new exception for insufficient miles balance.
     *
     * @param programaId the ID of the miles program
     * @param saldoAtual the current balance in miles
     * @param milhasSolicitadas the amount of miles requested
     */
    public SaldoMilhasInsuficienteException(UUID programaId, long saldoAtual, long milhasSolicitadas) {
        super(String.format(
                "Saldo de milhas insuficiente no programa %s: saldo atual = %d, solicitado = %d",
                programaId, saldoAtual, milhasSolicitadas
        ));
        this.programaId = programaId;
        this.saldoAtual = saldoAtual;
        this.milhasSolicitadas = milhasSolicitadas;
    }

    /**
     * Returns the ID of the miles program.
     *
     * @return the program ID
     */
    public UUID getProgramaId() {
        return programaId;
    }

    /**
     * Returns the current miles balance.
     *
     * @return the current balance
     */
    public long getSaldoAtual() {
        return saldoAtual;
    }

    /**
     * Returns the amount of miles that was requested.
     *
     * @return the requested amount
     */
    public long getMilhasSolicitadas() {
        return milhasSolicitadas;
    }

    /**
     * Returns the deficit (how many miles are missing).
     *
     * @return the deficit amount
     */
    public long getDeficit() {
        return milhasSolicitadas - saldoAtual;
    }
}
