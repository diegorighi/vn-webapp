package com.vanessaviagem.backoffice.domain.exceptions;

import com.vanessaviagem.backoffice.domain.exceptions.MilhasNaoEncontradaExceptionScenario.CriacaoComIdScenario;
import java.util.UUID;
import java.util.stream.Stream;
import org.junit.jupiter.params.provider.Arguments;

/**
 * Test data provider for MilhasNaoEncontradaException tests.
 */
public final class MilhasNaoEncontradaExceptionTestDataProvider {

    private static final UUID MILHAS_ID_1 = UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa");
    private static final UUID MILHAS_ID_2 = UUID.fromString("bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb");
    private static final UUID MILHAS_ID_3 = UUID.fromString("cccccccc-cccc-cccc-cccc-cccccccccccc");

    private MilhasNaoEncontradaExceptionTestDataProvider() {
    }

    public static Stream<Arguments> criacaoComId() {
        return Stream.of(
                Arguments.of(new CriacaoComIdScenario(
                        "should create exception with milhas ID 1",
                        MILHAS_ID_1,
                        "Milhas nao encontrada com id: aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa"
                )),
                Arguments.of(new CriacaoComIdScenario(
                        "should create exception with milhas ID 2",
                        MILHAS_ID_2,
                        "Milhas nao encontrada com id: bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb"
                )),
                Arguments.of(new CriacaoComIdScenario(
                        "should create exception with milhas ID 3",
                        MILHAS_ID_3,
                        "Milhas nao encontrada com id: cccccccc-cccc-cccc-cccc-cccccccccccc"
                ))
        );
    }
}
