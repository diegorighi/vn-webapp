package com.vanessaviagem.backoffice.application.ports.in;

import com.vanessaviagem.backoffice.domain.model.Transacao;
import com.vanessaviagem.backoffice.domain.model.enums.TipoTransacao;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Input port for querying transactions.
 * This use case handles all read operations for transaction history.
 *
 * <p>Transactions are queried by ContaPrograma (programaId field).
 * Results are ordered by date descending (most recent first).</p>
 */
public interface ConsultarTransacoesUseCase {

    /**
     * Finds all transactions for a specific ContaPrograma.
     * Results are ordered by date descending (most recent first).
     *
     * @param contaProgramaId the account ID (maps to programaId in Transacao)
     * @return a list of transactions (never null, may be empty)
     * @throws NullPointerException if contaProgramaId is null
     */
    List<Transacao> listarPorContaPrograma(UUID contaProgramaId);

    /**
     * Finds all transactions for a specific ContaPrograma within a date range.
     * Results are ordered by date descending (most recent first).
     *
     * @param contaProgramaId the account ID
     * @param inicio the start of the date range (inclusive)
     * @param fim the end of the date range (inclusive)
     * @return a list of transactions within the range (never null, may be empty)
     * @throws NullPointerException if any parameter is null
     * @throws IllegalArgumentException if inicio is after fim
     */
    List<Transacao> listarPorPeriodo(UUID contaProgramaId, LocalDateTime inicio, LocalDateTime fim);

    /**
     * Finds all transactions of a specific type for a ContaPrograma.
     * Results are ordered by date descending (most recent first).
     *
     * @param contaProgramaId the account ID
     * @param tipo the transaction type to filter by (COMPRA, VENDA, or BONUS)
     * @return a list of transactions of the specified type (never null, may be empty)
     * @throws NullPointerException if any parameter is null
     */
    List<Transacao> listarPorTipo(UUID contaProgramaId, TipoTransacao tipo);
}
