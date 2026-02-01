package com.vanessaviagem.backoffice.domain.model.cliente;

import com.vanessaviagem.backoffice.domain.model.enums.TipoDocumento;

import java.util.Objects;
import java.util.UUID;

/**
 * Value object representing a customer document.
 * Documents are identified by type and number.
 */
public record ClienteDocumento(
        UUID id,
        TipoDocumento tipo,
        String numero,
        boolean principal,
        String arquivoUrl,
        String nomeArquivo
) {

    public ClienteDocumento {
        Objects.requireNonNull(id, "id eh obrigatorio");
        Objects.requireNonNull(tipo, "tipo eh obrigatorio");
        Objects.requireNonNull(numero, "numero eh obrigatorio");
        if (numero.isBlank()) {
            throw new IllegalArgumentException("numero nao pode ser vazio");
        }
    }

    /**
     * Creates a new document with generated ID.
     */
    public static ClienteDocumento criar(
            TipoDocumento tipo,
            String numero,
            boolean principal,
            String arquivoUrl,
            String nomeArquivo
    ) {
        return new ClienteDocumento(
                UUID.randomUUID(),
                tipo,
                numero,
                principal,
                arquivoUrl,
                nomeArquivo
        );
    }

    /**
     * Creates a copy with a new ID (for persistence).
     */
    public ClienteDocumento withId(UUID newId) {
        return new ClienteDocumento(newId, tipo, numero, principal, arquivoUrl, nomeArquivo);
    }
}
