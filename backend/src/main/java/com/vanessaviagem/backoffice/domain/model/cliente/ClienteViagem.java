package com.vanessaviagem.backoffice.domain.model.cliente;

import com.vanessaviagem.backoffice.domain.model.enums.ViagemStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Represents a trip/flight for a customer.
 */
public record ClienteViagem(
        UUID id,
        UUID clienteId,
        String localizador,
        List<String> aeroportos,
        LocalDateTime dataEmbarque,
        String assento,
        List<String> companhias,
        String moeda,
        BigDecimal valor,
        ViagemStatus status,
        LocalDateTime criadoEm,
        LocalDateTime atualizadoEm
) {

    public ClienteViagem {
        Objects.requireNonNull(id, "id eh obrigatorio");
        Objects.requireNonNull(clienteId, "clienteId eh obrigatorio");
        Objects.requireNonNull(localizador, "localizador eh obrigatorio");
        Objects.requireNonNull(aeroportos, "aeroportos eh obrigatorio");
        Objects.requireNonNull(dataEmbarque, "dataEmbarque eh obrigatorio");
        Objects.requireNonNull(companhias, "companhias eh obrigatorio");
        Objects.requireNonNull(moeda, "moeda eh obrigatorio");
        Objects.requireNonNull(valor, "valor eh obrigatorio");
        Objects.requireNonNull(status, "status eh obrigatorio");
        if (localizador.isBlank()) {
            throw new IllegalArgumentException("localizador nao pode ser vazio");
        }
        if (aeroportos.isEmpty()) {
            throw new IllegalArgumentException("aeroportos nao pode ser vazio");
        }
    }

    /**
     * Creates a new viagem with generated ID and timestamps.
     */
    public static ClienteViagem criar(
            UUID clienteId,
            String localizador,
            List<String> aeroportos,
            LocalDateTime dataEmbarque,
            String assento,
            List<String> companhias,
            String moeda,
            BigDecimal valor
    ) {
        LocalDateTime now = LocalDateTime.now();
        return new ClienteViagem(
                UUID.randomUUID(),
                clienteId,
                localizador,
                aeroportos,
                dataEmbarque,
                assento,
                companhias,
                moeda,
                valor,
                ViagemStatus.RESERVADO,
                now,
                now
        );
    }

    /**
     * Creates a copy with updated status.
     */
    public ClienteViagem comStatus(ViagemStatus novoStatus) {
        return new ClienteViagem(
                id, clienteId, localizador, aeroportos, dataEmbarque, assento,
                companhias, moeda, valor, novoStatus, criadoEm, LocalDateTime.now()
        );
    }
}
