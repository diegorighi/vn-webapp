package com.vanessaviagem.backoffice.domain.exceptions;

import java.util.UUID;
import java.util.stream.Stream;
import org.junit.jupiter.params.provider.Arguments;

public final class SaldoMilhasInsuficienteExceptionTestDataProvider {

    private static final UUID PROGRAMA_LATAM = UUID.fromString("550e8400-e29b-41d4-a716-446655440030");
    private static final UUID PROGRAMA_SMILES = UUID.fromString("550e8400-e29b-41d4-a716-446655440031");

    private SaldoMilhasInsuficienteExceptionTestDataProvider() {
    }

    public static Stream<Arguments> cenarios() {
        return Stream.of(
                Arguments.of(new SaldoMilhasInsuficienteExceptionScenario(
                        "should calculate deficit when saldo is 5000 and solicitado is 10000",
                        PROGRAMA_LATAM,
                        5000L,
                        10000L,
                        5000L
                )),
                Arguments.of(new SaldoMilhasInsuficienteExceptionScenario(
                        "should calculate deficit when saldo is zero",
                        PROGRAMA_SMILES,
                        0L,
                        15000L,
                        15000L
                )),
                Arguments.of(new SaldoMilhasInsuficienteExceptionScenario(
                        "should calculate small deficit",
                        PROGRAMA_LATAM,
                        9500L,
                        10000L,
                        500L
                ))
        );
    }
}
