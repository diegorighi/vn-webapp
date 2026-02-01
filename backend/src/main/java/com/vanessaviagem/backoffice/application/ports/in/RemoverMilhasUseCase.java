package com.vanessaviagem.backoffice.application.ports.in;

import java.util.Objects;
import java.util.UUID;

/**
 * Input port for removing miles entries.
 * This use case handles the deletion of miles entries from the system.
 */
public interface RemoverMilhasUseCase {

    /**
     * Removes a miles entry from the system.
     *
     * @param command the command containing the miles ID to remove
     * @throws com.vanessaviagem.backoffice.domain.exceptions.MilhasNaoEncontradaException
     *         if the miles entry is not found
     */
    void execute(RemoverMilhasCommand command);

    /**
     * Command object containing the data needed to remove miles.
     *
     * @param milhasId the ID of the miles entry to remove
     */
    record RemoverMilhasCommand(UUID milhasId) {
        public RemoverMilhasCommand {
            Objects.requireNonNull(milhasId, "milhasId eh obrigatorio");
        }
    }
}
