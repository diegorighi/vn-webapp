package com.vanessaviagem.backoffice.domain.model;

import com.vanessaviagem.backoffice.domain.model.enums.ViagemStatus;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public record Viagem(
        UUID viagemId,
        String localizador,
        List<String> trecho,
        OffsetDateTime data,
        String assento,
        List<String> companhiaAereaList,
        String moeda,
        BigDecimal precoTotal,
        ViagemStatus status,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {

    public Viagem {
        Objects.requireNonNull(viagemId, "viagemId eh obrigatorio");
        Objects.requireNonNull(localizador, "localizador eh obrigatorio");
        Objects.requireNonNull(trecho, "trecho eh obrigatorio");
        Objects.requireNonNull(data, "data eh obrigatorio");
        Objects.requireNonNull(companhiaAereaList, "companhiaAereaList eh obrigatorio");
        Objects.requireNonNull(moeda, "moeda eh obrigatorio");
        Objects.requireNonNull(precoTotal, "precoTotal eh obrigatorio");
        Objects.requireNonNull(status, "status eh obrigatorio");
    }
}
