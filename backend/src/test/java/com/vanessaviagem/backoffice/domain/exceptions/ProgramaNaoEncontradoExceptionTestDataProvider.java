package com.vanessaviagem.backoffice.domain.exceptions;

import com.vanessaviagem.backoffice.domain.exceptions.ProgramaNaoEncontradoExceptionScenario.CriacaoComBrandScenario;
import com.vanessaviagem.backoffice.domain.exceptions.ProgramaNaoEncontradoExceptionScenario.CriacaoComIdEMensagemScenario;
import com.vanessaviagem.backoffice.domain.exceptions.ProgramaNaoEncontradoExceptionScenario.CriacaoComIdScenario;
import java.util.UUID;
import java.util.stream.Stream;
import org.junit.jupiter.params.provider.Arguments;

/**
 * Test data provider for ProgramaNaoEncontradoException tests.
 */
public final class ProgramaNaoEncontradoExceptionTestDataProvider {

    private static final UUID PROGRAMA_ID_1 = UUID.fromString("11111111-1111-1111-1111-111111111111");
    private static final UUID PROGRAMA_ID_2 = UUID.fromString("22222222-2222-2222-2222-222222222222");
    private static final UUID PROGRAMA_ID_3 = UUID.fromString("33333333-3333-3333-3333-333333333333");

    private ProgramaNaoEncontradoExceptionTestDataProvider() {
    }

    public static Stream<Arguments> criacaoComId() {
        return Stream.of(
                Arguments.of(new CriacaoComIdScenario(
                        "should create exception with programa ID 1",
                        PROGRAMA_ID_1,
                        "Programa de milhas nao encontrado: 11111111-1111-1111-1111-111111111111"
                )),
                Arguments.of(new CriacaoComIdScenario(
                        "should create exception with programa ID 2",
                        PROGRAMA_ID_2,
                        "Programa de milhas nao encontrado: 22222222-2222-2222-2222-222222222222"
                )),
                Arguments.of(new CriacaoComIdScenario(
                        "should create exception with programa ID 3",
                        PROGRAMA_ID_3,
                        "Programa de milhas nao encontrado: 33333333-3333-3333-3333-333333333333"
                ))
        );
    }

    public static Stream<Arguments> criacaoComBrand() {
        return Stream.of(
                Arguments.of(new CriacaoComBrandScenario(
                        "should create exception with brand Smiles",
                        "Smiles",
                        "Programa de milhas nao encontrado para brand: Smiles"
                )),
                Arguments.of(new CriacaoComBrandScenario(
                        "should create exception with brand LATAM Pass",
                        "LATAM Pass",
                        "Programa de milhas nao encontrado para brand: LATAM Pass"
                )),
                Arguments.of(new CriacaoComBrandScenario(
                        "should create exception with brand Azul Fidelidade",
                        "Azul Fidelidade",
                        "Programa de milhas nao encontrado para brand: Azul Fidelidade"
                )),
                Arguments.of(new CriacaoComBrandScenario(
                        "should create exception with brand Livelo",
                        "Livelo",
                        "Programa de milhas nao encontrado para brand: Livelo"
                ))
        );
    }

    public static Stream<Arguments> criacaoComIdEMensagem() {
        return Stream.of(
                Arguments.of(new CriacaoComIdEMensagemScenario(
                        "should create exception with custom message for inactive programa",
                        PROGRAMA_ID_1,
                        "Programa inativo ou removido do catalogo",
                        "Programa inativo ou removido do catalogo"
                )),
                Arguments.of(new CriacaoComIdEMensagemScenario(
                        "should create exception with detailed message",
                        PROGRAMA_ID_2,
                        "Programa nao disponivel para a regiao informada",
                        "Programa nao disponivel para a regiao informada"
                )),
                Arguments.of(new CriacaoComIdEMensagemScenario(
                        "should create exception with technical message",
                        PROGRAMA_ID_3,
                        "Programa ID invalido ou expirado no sistema",
                        "Programa ID invalido ou expirado no sistema"
                ))
        );
    }
}
