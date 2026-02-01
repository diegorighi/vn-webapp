package com.vanessaviagem.backoffice.domain.model;

import java.util.stream.Stream;
import org.junit.jupiter.params.provider.Arguments;

public final class EnderecoTestDataProvider {

    private EnderecoTestDataProvider() {
    }

    public static Stream<Arguments> criacaoValida() {
        return Stream.of(
                Arguments.of(new EnderecoScenario(
                        "should create endereco principal completo",
                        true,
                        "Rua das Flores",
                        123,
                        "Centro",
                        "01234-567",
                        "Sao Paulo",
                        "SP",
                        "Brasil",
                        null,
                        null
                )),
                Arguments.of(new EnderecoScenario(
                        "should create endereco secundario sem numero",
                        false,
                        "Avenida Paulista",
                        null,
                        "Bela Vista",
                        "01310-100",
                        "Sao Paulo",
                        "SP",
                        "Brasil",
                        null,
                        null
                )),
                Arguments.of(new EnderecoScenario(
                        "should create endereco internacional",
                        false,
                        "123 Main Street",
                        456,
                        "Downtown",
                        "10001",
                        "New York",
                        "NY",
                        "Estados Unidos",
                        null,
                        null
                ))
        );
    }

    public static Stream<Arguments> validacaoNulos() {
        return Stream.of(
                Arguments.of(new EnderecoScenario(
                        "should throw when logradouro is null",
                        true,
                        null,
                        123,
                        "Centro",
                        "01234-567",
                        "Sao Paulo",
                        "SP",
                        "Brasil",
                        NullPointerException.class,
                        "logradouro eh obrigatorio"
                )),
                Arguments.of(new EnderecoScenario(
                        "should throw when bairro is null",
                        true,
                        "Rua das Flores",
                        123,
                        null,
                        "01234-567",
                        "Sao Paulo",
                        "SP",
                        "Brasil",
                        NullPointerException.class,
                        "bairro eh obrigatorio"
                )),
                Arguments.of(new EnderecoScenario(
                        "should throw when cep is null",
                        true,
                        "Rua das Flores",
                        123,
                        "Centro",
                        null,
                        "Sao Paulo",
                        "SP",
                        "Brasil",
                        NullPointerException.class,
                        "cep eh obrigatorio"
                )),
                Arguments.of(new EnderecoScenario(
                        "should throw when cidade is null",
                        true,
                        "Rua das Flores",
                        123,
                        "Centro",
                        "01234-567",
                        null,
                        "SP",
                        "Brasil",
                        NullPointerException.class,
                        "cidade eh obrigatorio"
                )),
                Arguments.of(new EnderecoScenario(
                        "should throw when estado is null",
                        true,
                        "Rua das Flores",
                        123,
                        "Centro",
                        "01234-567",
                        "Sao Paulo",
                        null,
                        "Brasil",
                        NullPointerException.class,
                        "estado eh obrigatorio"
                )),
                Arguments.of(new EnderecoScenario(
                        "should throw when pais is null",
                        true,
                        "Rua das Flores",
                        123,
                        "Centro",
                        "01234-567",
                        "Sao Paulo",
                        "SP",
                        null,
                        NullPointerException.class,
                        "pais eh obrigatorio"
                ))
        );
    }
}
