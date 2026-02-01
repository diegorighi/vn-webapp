package com.vanessaviagem.backoffice.domain.exceptions;

import com.vanessaviagem.backoffice.domain.exceptions.ViagemNaoEncontradaExceptionScenario.CriacaoComIdEMensagemScenario;
import com.vanessaviagem.backoffice.domain.exceptions.ViagemNaoEncontradaExceptionScenario.CriacaoComIdScenario;
import java.util.UUID;
import java.util.stream.Stream;
import org.junit.jupiter.params.provider.Arguments;

/**
 * Test data provider for ViagemNaoEncontradaException tests.
 */
public final class ViagemNaoEncontradaExceptionTestDataProvider {

    private static final UUID VIAGEM_ID_1 = UUID.fromString("11111111-1111-1111-1111-111111111111");
    private static final UUID VIAGEM_ID_2 = UUID.fromString("22222222-2222-2222-2222-222222222222");
    private static final UUID VIAGEM_ID_3 = UUID.fromString("33333333-3333-3333-3333-333333333333");

    private ViagemNaoEncontradaExceptionTestDataProvider() {
    }

    public static Stream<Arguments> criacaoComId() {
        return Stream.of(
                Arguments.of(new CriacaoComIdScenario(
                        "should create exception with viagem ID 1",
                        VIAGEM_ID_1,
                        "Viagem nao encontrada: 11111111-1111-1111-1111-111111111111"
                )),
                Arguments.of(new CriacaoComIdScenario(
                        "should create exception with viagem ID 2",
                        VIAGEM_ID_2,
                        "Viagem nao encontrada: 22222222-2222-2222-2222-222222222222"
                )),
                Arguments.of(new CriacaoComIdScenario(
                        "should create exception with viagem ID 3",
                        VIAGEM_ID_3,
                        "Viagem nao encontrada: 33333333-3333-3333-3333-333333333333"
                ))
        );
    }

    public static Stream<Arguments> criacaoComIdEMensagem() {
        return Stream.of(
                Arguments.of(new CriacaoComIdEMensagemScenario(
                        "should create exception with custom message for viagem cancelada",
                        VIAGEM_ID_1,
                        "Viagem cancelada ou removida",
                        "Viagem cancelada ou removida"
                )),
                Arguments.of(new CriacaoComIdEMensagemScenario(
                        "should create exception with detailed message",
                        VIAGEM_ID_2,
                        "Viagem nao encontrada para o cliente informado",
                        "Viagem nao encontrada para o cliente informado"
                )),
                Arguments.of(new CriacaoComIdEMensagemScenario(
                        "should create exception with technical message",
                        VIAGEM_ID_3,
                        "Viagem ID invalido ou expirado no sistema",
                        "Viagem ID invalido ou expirado no sistema"
                ))
        );
    }
}
