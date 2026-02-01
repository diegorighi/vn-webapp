package com.vanessaviagem.backoffice.application.ports.in;

import com.vanessaviagem.backoffice.domain.model.cliente.ClienteDependente;
import com.vanessaviagem.backoffice.domain.model.Pessoa;
import com.vanessaviagem.backoffice.domain.model.enums.Parentesco;
import java.util.UUID;

/**
 * Input port for adding a dependent to an existing titular client.
 * This use case handles the association of dependents with titular clients.
 */
public interface AdicionarDependenteUseCase {

    /**
     * Adds a new dependent to the specified titular client.
     *
     * @param command the command containing the dependent data and titular reference
     * @return the created dependent client
     * @throws com.vanessaviagem.backoffice.domain.exceptions.ClienteNaoEncontradoException
     *         if the titular client is not found
     */
    ClienteDependente execute(AdicionarDependenteCommand command);

    /**
     * Command object containing the data needed to add a dependent.
     *
     * @param titularId the ID of the titular client to add the dependent to
     * @param parentesco the relationship type (CONJUGE, FILHO, FILHA, PAI, MAE, OUTRO)
     * @param dadosPessoais the personal data of the dependent
     */
    record AdicionarDependenteCommand(
            UUID titularId,
            Parentesco parentesco,
            Pessoa dadosPessoais
    ) {
        public AdicionarDependenteCommand {
            java.util.Objects.requireNonNull(titularId, "titularId eh obrigatorio");
            java.util.Objects.requireNonNull(parentesco, "parentesco eh obrigatorio");
            java.util.Objects.requireNonNull(dadosPessoais, "dadosPessoais eh obrigatorio");
        }
    }

    /**
     * Result object containing the outcome of adding a dependent.
     *
     * @param dependenteId the generated dependent ID
     * @param dependente the created dependent client
     */
    record AdicionarDependenteResult(
            UUID dependenteId,
            ClienteDependente dependente
    ) {
        public AdicionarDependenteResult {
            java.util.Objects.requireNonNull(dependenteId, "dependenteId eh obrigatorio");
            java.util.Objects.requireNonNull(dependente, "dependente eh obrigatorio");
        }
    }
}
