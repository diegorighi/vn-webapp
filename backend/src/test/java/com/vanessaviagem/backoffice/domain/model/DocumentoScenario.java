package com.vanessaviagem.backoffice.domain.model;

import com.vanessaviagem.backoffice.domain.model.enums.TipoDocumento;
import java.time.LocalDate;

public record DocumentoScenario(
        String descricao,
        TipoDocumento tipo,
        String numero,
        LocalDate validade,
        LocalDate emitidoEm,
        Class<? extends Exception> expectedException,
        String expectedMessage
) {
    @Override
    public String toString() {
        return descricao;
    }
}
