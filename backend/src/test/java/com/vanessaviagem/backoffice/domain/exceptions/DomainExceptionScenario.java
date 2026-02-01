package com.vanessaviagem.backoffice.domain.exceptions;

public record DomainExceptionScenario(
        String descricao,
        String message,
        Throwable cause,
        String expectedMessage
) {
    @Override
    public String toString() {
        return descricao;
    }
}
