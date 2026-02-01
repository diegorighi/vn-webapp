package com.vanessaviagem.backoffice.domain.model;

import com.vanessaviagem.backoffice.domain.model.enums.TipoContato;
import java.util.Objects;

public record Contato(
        boolean principal,
        TipoContato tipo,
        String valor
) {

    public Contato {
        Objects.requireNonNull(tipo, "tipo eh obrigatorio");
        Objects.requireNonNull(valor, "valor eh obrigatorio");
    }
}
