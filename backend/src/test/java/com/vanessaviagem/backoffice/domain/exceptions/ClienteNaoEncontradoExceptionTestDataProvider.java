package com.vanessaviagem.backoffice.domain.exceptions;

import java.util.UUID;
import java.util.stream.Stream;
import org.junit.jupiter.params.provider.Arguments;

public final class ClienteNaoEncontradoExceptionTestDataProvider {

    private static final UUID CLIENTE_ID_1 = UUID.fromString("550e8400-e29b-41d4-a716-446655440020");
    private static final UUID CLIENTE_ID_2 = UUID.fromString("550e8400-e29b-41d4-a716-446655440021");

    private ClienteNaoEncontradoExceptionTestDataProvider() {
    }

    public static Stream<Arguments> criacaoComId() {
        return Stream.of(
                Arguments.of(new ClienteNaoEncontradoExceptionScenario(
                        "should create exception with cliente ID",
                        CLIENTE_ID_1,
                        null,
                        "Cliente nao encontrado: " + CLIENTE_ID_1
                )),
                Arguments.of(new ClienteNaoEncontradoExceptionScenario(
                        "should create exception with different ID",
                        CLIENTE_ID_2,
                        null,
                        "Cliente nao encontrado: " + CLIENTE_ID_2
                ))
        );
    }

    public static Stream<Arguments> criacaoComIdEMensagem() {
        return Stream.of(
                Arguments.of(new ClienteNaoEncontradoExceptionScenario(
                        "should create exception with custom message",
                        CLIENTE_ID_1,
                        "Titular nao encontrado no sistema",
                        "Titular nao encontrado no sistema"
                )),
                Arguments.of(new ClienteNaoEncontradoExceptionScenario(
                        "should create exception with another custom message",
                        CLIENTE_ID_2,
                        "Dependente invalido",
                        "Dependente invalido"
                ))
        );
    }
}
