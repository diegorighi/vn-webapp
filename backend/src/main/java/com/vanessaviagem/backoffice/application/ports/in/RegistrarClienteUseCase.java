package com.vanessaviagem.backoffice.application.ports.in;

import com.vanessaviagem.backoffice.domain.model.cliente.ClienteTitular;
import com.vanessaviagem.backoffice.domain.model.Pessoa;
import java.util.UUID;

/**
 * Input port for registering a new titular client.
 * This use case handles the creation of new clients in the system.
 */
public interface RegistrarClienteUseCase {

    /**
     * Registers a new titular client with the provided personal data.
     *
     * @param command the command containing the client data to register
     * @return the registered client with generated ID
     * @throws IllegalArgumentException if command data is invalid
     */
    ClienteTitular execute(RegistrarClienteCommand command);

    /**
     * Command object containing the data needed to register a new client.
     *
     * @param dadosPessoais the personal data of the client
     */
    record RegistrarClienteCommand(
            Pessoa dadosPessoais
    ) {
        public RegistrarClienteCommand {
            java.util.Objects.requireNonNull(dadosPessoais, "dadosPessoais eh obrigatorio");
        }
    }

    /**
     * Result object containing the outcome of the registration.
     *
     * @param clienteId the generated client ID
     * @param cliente the registered client
     */
    record RegistrarClienteResult(
            UUID clienteId,
            ClienteTitular cliente
    ) {
        public RegistrarClienteResult {
            java.util.Objects.requireNonNull(clienteId, "clienteId eh obrigatorio");
            java.util.Objects.requireNonNull(cliente, "cliente eh obrigatorio");
        }
    }
}
