package com.vanessaviagem.backoffice.domain.exceptions;

import java.util.stream.Stream;
import org.junit.jupiter.params.provider.Arguments;

public final class DomainExceptionTestDataProvider {

    private DomainExceptionTestDataProvider() {
    }

    public static Stream<Arguments> criacaoComMensagem() {
        return Stream.of(
                Arguments.of(new DomainExceptionScenario(
                        "should create exception with message",
                        "Erro de dominio",
                        null,
                        "Erro de dominio"
                )),
                Arguments.of(new DomainExceptionScenario(
                        "should create exception with detailed message",
                        "Operacao invalida para o estado atual",
                        null,
                        "Operacao invalida para o estado atual"
                ))
        );
    }

    public static Stream<Arguments> criacaoComMensagemECausa() {
        return Stream.of(
                Arguments.of(new DomainExceptionScenario(
                        "should create exception with message and cause",
                        "Erro de processamento",
                        new RuntimeException("Causa original"),
                        "Erro de processamento"
                )),
                Arguments.of(new DomainExceptionScenario(
                        "should create exception with null pointer cause",
                        "Valor obrigatorio nulo",
                        new NullPointerException("campo obrigatorio"),
                        "Valor obrigatorio nulo"
                ))
        );
    }
}
