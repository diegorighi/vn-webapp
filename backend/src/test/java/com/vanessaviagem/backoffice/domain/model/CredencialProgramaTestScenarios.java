package com.vanessaviagem.backoffice.domain.model;

import com.vanessaviagem.backoffice.domain.model.enums.StatusCredencial;
import com.vanessaviagem.backoffice.domain.model.enums.TipoProgramaMilhas;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Cenários de teste para CredencialPrograma.
 */
public final class CredencialProgramaTestScenarios {

    private CredencialProgramaTestScenarios() {
    }

    public record CriarComSenhaScenario(
            String description,
            UUID tenantId,
            UUID clienteId,
            TipoProgramaMilhas programa,
            byte[] usuarioCriptografado,
            byte[] senhaCriptografada
    ) {
        @Override
        public String toString() {
            return description;
        }
    }

    public record CriarComOAuthScenario(
            String description,
            UUID tenantId,
            UUID clienteId,
            TipoProgramaMilhas programa,
            byte[] accessToken,
            byte[] refreshToken,
            LocalDateTime tokenExpiraEm
    ) {
        @Override
        public String toString() {
            return description;
        }
    }

    public record TransicaoStatusScenario(
            String description,
            StatusCredencial statusInicial,
            String operacao,
            StatusCredencial statusEsperado
    ) {
        @Override
        public String toString() {
            return description;
        }
    }

    public record TokenExpiradoScenario(
            String description,
            LocalDateTime tokenExpiraEm,
            boolean expectedExpirado
    ) {
        @Override
        public String toString() {
            return description;
        }
    }
}
