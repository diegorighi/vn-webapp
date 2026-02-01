package com.vanessaviagem.backoffice.domain.model;

import com.vanessaviagem.backoffice.domain.model.enums.ViagemStatus;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;
import org.junit.jupiter.params.provider.Arguments;

public final class ViagemTestDataProvider {

    private static final UUID VIAGEM_ID = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
    private static final OffsetDateTime DATA_VIAGEM = OffsetDateTime.parse("2024-06-15T10:30:00Z");
    private static final OffsetDateTime CREATED_AT = OffsetDateTime.parse("2024-01-15T08:00:00Z");
    private static final OffsetDateTime UPDATED_AT = OffsetDateTime.parse("2024-01-15T08:00:00Z");

    private ViagemTestDataProvider() {
    }

    public static Stream<Arguments> criacaoValida() {
        return Stream.of(
                Arguments.of(new ViagemScenario(
                        "should create viagem domestica ida e volta",
                        VIAGEM_ID,
                        "ABC123",
                        List.of("GRU", "GIG", "GRU"),
                        DATA_VIAGEM,
                        "14A",
                        List.of("LATAM"),
                        "BRL",
                        new BigDecimal("1500.00"),
                        ViagemStatus.EMITIDO,
                        CREATED_AT,
                        UPDATED_AT,
                        null,
                        null
                )),
                Arguments.of(new ViagemScenario(
                        "should create viagem internacional com conexao",
                        VIAGEM_ID,
                        "XYZ789",
                        List.of("GRU", "MIA", "LAX"),
                        DATA_VIAGEM,
                        "2B",
                        List.of("TAM", "AA"),
                        "USD",
                        new BigDecimal("2500.00"),
                        ViagemStatus.VOADO,
                        CREATED_AT,
                        UPDATED_AT,
                        null,
                        null
                )),
                Arguments.of(new ViagemScenario(
                        "should create viagem cancelada sem assento",
                        VIAGEM_ID,
                        "CANCEL01",
                        List.of("BSB", "GRU"),
                        DATA_VIAGEM,
                        null,
                        List.of("GOL"),
                        "BRL",
                        new BigDecimal("800.00"),
                        ViagemStatus.CANCELADO,
                        CREATED_AT,
                        null,
                        null,
                        null
                ))
        );
    }

    public static Stream<Arguments> validacaoNulos() {
        return Stream.of(
                Arguments.of(new ViagemScenario(
                        "should throw when viagemId is null",
                        null,
                        "ABC123",
                        List.of("GRU", "GIG"),
                        DATA_VIAGEM,
                        "14A",
                        List.of("LATAM"),
                        "BRL",
                        new BigDecimal("1500.00"),
                        ViagemStatus.EMITIDO,
                        CREATED_AT,
                        UPDATED_AT,
                        NullPointerException.class,
                        "viagemId eh obrigatorio"
                )),
                Arguments.of(new ViagemScenario(
                        "should throw when localizador is null",
                        VIAGEM_ID,
                        null,
                        List.of("GRU", "GIG"),
                        DATA_VIAGEM,
                        "14A",
                        List.of("LATAM"),
                        "BRL",
                        new BigDecimal("1500.00"),
                        ViagemStatus.EMITIDO,
                        CREATED_AT,
                        UPDATED_AT,
                        NullPointerException.class,
                        "localizador eh obrigatorio"
                )),
                Arguments.of(new ViagemScenario(
                        "should throw when trecho is null",
                        VIAGEM_ID,
                        "ABC123",
                        null,
                        DATA_VIAGEM,
                        "14A",
                        List.of("LATAM"),
                        "BRL",
                        new BigDecimal("1500.00"),
                        ViagemStatus.EMITIDO,
                        CREATED_AT,
                        UPDATED_AT,
                        NullPointerException.class,
                        "trecho eh obrigatorio"
                )),
                Arguments.of(new ViagemScenario(
                        "should throw when data is null",
                        VIAGEM_ID,
                        "ABC123",
                        List.of("GRU", "GIG"),
                        null,
                        "14A",
                        List.of("LATAM"),
                        "BRL",
                        new BigDecimal("1500.00"),
                        ViagemStatus.EMITIDO,
                        CREATED_AT,
                        UPDATED_AT,
                        NullPointerException.class,
                        "data eh obrigatorio"
                )),
                Arguments.of(new ViagemScenario(
                        "should throw when companhiaAereaList is null",
                        VIAGEM_ID,
                        "ABC123",
                        List.of("GRU", "GIG"),
                        DATA_VIAGEM,
                        "14A",
                        null,
                        "BRL",
                        new BigDecimal("1500.00"),
                        ViagemStatus.EMITIDO,
                        CREATED_AT,
                        UPDATED_AT,
                        NullPointerException.class,
                        "companhiaAereaList eh obrigatorio"
                )),
                Arguments.of(new ViagemScenario(
                        "should throw when moeda is null",
                        VIAGEM_ID,
                        "ABC123",
                        List.of("GRU", "GIG"),
                        DATA_VIAGEM,
                        "14A",
                        List.of("LATAM"),
                        null,
                        new BigDecimal("1500.00"),
                        ViagemStatus.EMITIDO,
                        CREATED_AT,
                        UPDATED_AT,
                        NullPointerException.class,
                        "moeda eh obrigatorio"
                )),
                Arguments.of(new ViagemScenario(
                        "should throw when precoTotal is null",
                        VIAGEM_ID,
                        "ABC123",
                        List.of("GRU", "GIG"),
                        DATA_VIAGEM,
                        "14A",
                        List.of("LATAM"),
                        "BRL",
                        null,
                        ViagemStatus.EMITIDO,
                        CREATED_AT,
                        UPDATED_AT,
                        NullPointerException.class,
                        "precoTotal eh obrigatorio"
                )),
                Arguments.of(new ViagemScenario(
                        "should throw when status is null",
                        VIAGEM_ID,
                        "ABC123",
                        List.of("GRU", "GIG"),
                        DATA_VIAGEM,
                        "14A",
                        List.of("LATAM"),
                        "BRL",
                        new BigDecimal("1500.00"),
                        null,
                        CREATED_AT,
                        UPDATED_AT,
                        NullPointerException.class,
                        "status eh obrigatorio"
                ))
        );
    }
}
