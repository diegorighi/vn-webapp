package com.vanessaviagem.backoffice.application.ports.in;

import com.vanessaviagem.backoffice.domain.model.Viagem;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Input port for registering a new trip for a client.
 * This use case handles the creation of trip records associated with clients.
 */
public interface RegistrarViagemUseCase {

    /**
     * Registers a new trip for the specified client.
     *
     * @param command the command containing the trip data
     * @return the registered trip
     * @throws com.vanessaviagem.backoffice.domain.exceptions.ClienteNaoEncontradoException
     *         if the client is not found
     */
    Viagem execute(RegistrarViagemCommand command);

    /**
     * Command object containing the data needed to register a trip.
     *
     * @param clienteId the ID of the client (titular or dependent)
     * @param localizador the booking locator code
     * @param trecho the list of route segments (e.g., ["GRU", "CDG", "LHR"])
     * @param data the departure date and time
     * @param assento the seat assignment (optional, may be null)
     * @param companhiaAereaList the list of airlines involved in the trip
     * @param moeda the currency code (e.g., "BRL", "USD")
     * @param precoTotal the total price of the trip
     */
    record RegistrarViagemCommand(
            UUID clienteId,
            String localizador,
            List<String> trecho,
            OffsetDateTime data,
            String assento,
            List<String> companhiaAereaList,
            String moeda,
            BigDecimal precoTotal
    ) {
        public RegistrarViagemCommand {
            java.util.Objects.requireNonNull(clienteId, "clienteId eh obrigatorio");
            java.util.Objects.requireNonNull(localizador, "localizador eh obrigatorio");
            java.util.Objects.requireNonNull(trecho, "trecho eh obrigatorio");
            java.util.Objects.requireNonNull(data, "data eh obrigatorio");
            java.util.Objects.requireNonNull(companhiaAereaList, "companhiaAereaList eh obrigatorio");
            java.util.Objects.requireNonNull(moeda, "moeda eh obrigatorio");
            java.util.Objects.requireNonNull(precoTotal, "precoTotal eh obrigatorio");

            if (trecho.isEmpty()) {
                throw new IllegalArgumentException("trecho nao pode ser vazio");
            }
            if (companhiaAereaList.isEmpty()) {
                throw new IllegalArgumentException("companhiaAereaList nao pode ser vazio");
            }
            if (precoTotal.compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalArgumentException("precoTotal nao pode ser negativo");
            }
        }
    }

    /**
     * Result object containing the outcome of registering a trip.
     *
     * @param viagemId the generated trip ID
     * @param viagem the registered trip
     */
    record RegistrarViagemResult(
            UUID viagemId,
            Viagem viagem
    ) {
        public RegistrarViagemResult {
            java.util.Objects.requireNonNull(viagemId, "viagemId eh obrigatorio");
            java.util.Objects.requireNonNull(viagem, "viagem eh obrigatorio");
        }
    }
}
