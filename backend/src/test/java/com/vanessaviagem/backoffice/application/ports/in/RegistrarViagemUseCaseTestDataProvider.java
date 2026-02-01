package com.vanessaviagem.backoffice.application.ports.in;

import com.vanessaviagem.backoffice.application.ports.in.RegistrarViagemUseCaseScenario.CommandInvalidScenario;
import com.vanessaviagem.backoffice.application.ports.in.RegistrarViagemUseCaseScenario.CommandValidScenario;
import com.vanessaviagem.backoffice.application.ports.in.RegistrarViagemUseCaseScenario.ResultInvalidScenario;
import com.vanessaviagem.backoffice.application.ports.in.RegistrarViagemUseCaseScenario.ResultValidScenario;
import com.vanessaviagem.backoffice.domain.model.Viagem;
import com.vanessaviagem.backoffice.domain.model.enums.ViagemStatus;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;
import org.junit.jupiter.params.provider.Arguments;

public final class RegistrarViagemUseCaseTestDataProvider {

    private static final UUID CLIENTE_ID = UUID.fromString("550e8400-e29b-41d4-a716-446655440060");
    private static final UUID VIAGEM_ID = UUID.fromString("550e8400-e29b-41d4-a716-446655440061");
    private static final OffsetDateTime DATA_VIAGEM = OffsetDateTime.parse("2024-08-20T15:00:00Z");
    private static final OffsetDateTime CREATED_AT = OffsetDateTime.parse("2024-05-01T10:00:00Z");

    private RegistrarViagemUseCaseTestDataProvider() {
    }

    private static Viagem criarViagemValida() {
        return new Viagem(
                VIAGEM_ID,
                "JKL345",
                List.of("GRU", "EZE"),
                DATA_VIAGEM,
                "5F",
                List.of("AR"),
                "ARS",
                new BigDecimal("1200.00"),
                ViagemStatus.EMITIDO,
                CREATED_AT,
                CREATED_AT
        );
    }

    public static Stream<Arguments> commandValido() {
        return Stream.of(
                Arguments.of(new CommandValidScenario(
                        "should create command with all fields",
                        CLIENTE_ID,
                        "JKL345",
                        List.of("GRU", "EZE"),
                        DATA_VIAGEM,
                        "5F",
                        List.of("AR"),
                        "ARS",
                        new BigDecimal("1200.00")
                )),
                Arguments.of(new CommandValidScenario(
                        "should create command with null assento",
                        CLIENTE_ID,
                        "MNO678",
                        List.of("GRU", "MIA", "LAX"),
                        DATA_VIAGEM,
                        null,
                        List.of("AA", "LA"),
                        "USD",
                        new BigDecimal("2500.00")
                ))
        );
    }

    public static Stream<Arguments> commandInvalido() {
        return Stream.of(
                Arguments.of(new CommandInvalidScenario(
                        "should throw when clienteId is null",
                        null,
                        "JKL345",
                        List.of("GRU", "EZE"),
                        DATA_VIAGEM,
                        "5F",
                        List.of("AR"),
                        "ARS",
                        new BigDecimal("1200.00"),
                        NullPointerException.class,
                        "clienteId eh obrigatorio"
                )),
                Arguments.of(new CommandInvalidScenario(
                        "should throw when localizador is null",
                        CLIENTE_ID,
                        null,
                        List.of("GRU", "EZE"),
                        DATA_VIAGEM,
                        "5F",
                        List.of("AR"),
                        "ARS",
                        new BigDecimal("1200.00"),
                        NullPointerException.class,
                        "localizador eh obrigatorio"
                )),
                Arguments.of(new CommandInvalidScenario(
                        "should throw when trecho is null",
                        CLIENTE_ID,
                        "JKL345",
                        null,
                        DATA_VIAGEM,
                        "5F",
                        List.of("AR"),
                        "ARS",
                        new BigDecimal("1200.00"),
                        NullPointerException.class,
                        "trecho eh obrigatorio"
                )),
                Arguments.of(new CommandInvalidScenario(
                        "should throw when trecho is empty",
                        CLIENTE_ID,
                        "JKL345",
                        Collections.emptyList(),
                        DATA_VIAGEM,
                        "5F",
                        List.of("AR"),
                        "ARS",
                        new BigDecimal("1200.00"),
                        IllegalArgumentException.class,
                        "trecho nao pode ser vazio"
                )),
                Arguments.of(new CommandInvalidScenario(
                        "should throw when data is null",
                        CLIENTE_ID,
                        "JKL345",
                        List.of("GRU", "EZE"),
                        null,
                        "5F",
                        List.of("AR"),
                        "ARS",
                        new BigDecimal("1200.00"),
                        NullPointerException.class,
                        "data eh obrigatorio"
                )),
                Arguments.of(new CommandInvalidScenario(
                        "should throw when companhiaAereaList is null",
                        CLIENTE_ID,
                        "JKL345",
                        List.of("GRU", "EZE"),
                        DATA_VIAGEM,
                        "5F",
                        null,
                        "ARS",
                        new BigDecimal("1200.00"),
                        NullPointerException.class,
                        "companhiaAereaList eh obrigatorio"
                )),
                Arguments.of(new CommandInvalidScenario(
                        "should throw when companhiaAereaList is empty",
                        CLIENTE_ID,
                        "JKL345",
                        List.of("GRU", "EZE"),
                        DATA_VIAGEM,
                        "5F",
                        Collections.emptyList(),
                        "ARS",
                        new BigDecimal("1200.00"),
                        IllegalArgumentException.class,
                        "companhiaAereaList nao pode ser vazio"
                )),
                Arguments.of(new CommandInvalidScenario(
                        "should throw when moeda is null",
                        CLIENTE_ID,
                        "JKL345",
                        List.of("GRU", "EZE"),
                        DATA_VIAGEM,
                        "5F",
                        List.of("AR"),
                        null,
                        new BigDecimal("1200.00"),
                        NullPointerException.class,
                        "moeda eh obrigatorio"
                )),
                Arguments.of(new CommandInvalidScenario(
                        "should throw when precoTotal is null",
                        CLIENTE_ID,
                        "JKL345",
                        List.of("GRU", "EZE"),
                        DATA_VIAGEM,
                        "5F",
                        List.of("AR"),
                        "ARS",
                        null,
                        NullPointerException.class,
                        "precoTotal eh obrigatorio"
                )),
                Arguments.of(new CommandInvalidScenario(
                        "should throw when precoTotal is negative",
                        CLIENTE_ID,
                        "JKL345",
                        List.of("GRU", "EZE"),
                        DATA_VIAGEM,
                        "5F",
                        List.of("AR"),
                        "ARS",
                        new BigDecimal("-100.00"),
                        IllegalArgumentException.class,
                        "precoTotal nao pode ser negativo"
                ))
        );
    }

    public static Stream<Arguments> resultValido() {
        return Stream.of(
                Arguments.of(new ResultValidScenario(
                        "should create result with valid data",
                        VIAGEM_ID,
                        criarViagemValida()
                ))
        );
    }

    public static Stream<Arguments> resultInvalido() {
        return Stream.of(
                Arguments.of(new ResultInvalidScenario(
                        "should throw when viagemId is null in result",
                        null,
                        criarViagemValida(),
                        NullPointerException.class,
                        "viagemId eh obrigatorio"
                )),
                Arguments.of(new ResultInvalidScenario(
                        "should throw when viagem is null in result",
                        VIAGEM_ID,
                        null,
                        NullPointerException.class,
                        "viagem eh obrigatorio"
                ))
        );
    }
}
