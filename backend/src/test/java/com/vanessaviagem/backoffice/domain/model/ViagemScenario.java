package com.vanessaviagem.backoffice.domain.model;

import com.vanessaviagem.backoffice.domain.model.enums.ViagemStatus;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record ViagemScenario(
        String descricao,
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
        OffsetDateTime updatedAt,
        Class<? extends Exception> expectedException,
        String expectedMessage
) {
    @Override
    public String toString() {
        return descricao;
    }
}
