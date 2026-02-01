package com.vanessaviagem.backoffice.domain.model;

import com.vanessaviagem.backoffice.domain.model.enums.TipoDocumento;
import java.time.LocalDate;
import java.util.Objects;

public record Documento(
        TipoDocumento tipo,
        String numero,
        LocalDate validade,
        LocalDate emitidoEm
) {

    public Documento {
        Objects.requireNonNull(tipo, "tipo eh obrigatorio");
        Objects.requireNonNull(numero, "numero eh obrigatorio");
        Objects.requireNonNull(emitidoEm, "emitidoEm eh obrigatorio");
    }
}
