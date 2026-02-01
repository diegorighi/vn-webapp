package com.vanessaviagem.backoffice.domain.model;

import com.vanessaviagem.backoffice.domain.model.enums.TipoContato;
import java.util.stream.Stream;
import org.junit.jupiter.params.provider.Arguments;

public final class ContatoTestDataProvider {

    private ContatoTestDataProvider() {
    }

    public static Stream<Arguments> criacaoValida() {
        return Stream.of(
                Arguments.of(new ContatoScenario(
                        "should create contato principal with email",
                        true,
                        TipoContato.EMAIL,
                        "cliente@example.com",
                        null,
                        null
                )),
                Arguments.of(new ContatoScenario(
                        "should create contato secundario with celular",
                        false,
                        TipoContato.CELULAR,
                        "+55 11 99999-9999",
                        null,
                        null
                )),
                Arguments.of(new ContatoScenario(
                        "should create contato with telefone fixo",
                        false,
                        TipoContato.TELEFONE,
                        "+55 11 3333-4444",
                        null,
                        null
                ))
        );
    }

    public static Stream<Arguments> validacaoNulos() {
        return Stream.of(
                Arguments.of(new ContatoScenario(
                        "should throw when tipo is null",
                        true,
                        null,
                        "cliente@example.com",
                        NullPointerException.class,
                        "tipo eh obrigatorio"
                )),
                Arguments.of(new ContatoScenario(
                        "should throw when valor is null",
                        true,
                        TipoContato.EMAIL,
                        null,
                        NullPointerException.class,
                        "valor eh obrigatorio"
                ))
        );
    }
}
