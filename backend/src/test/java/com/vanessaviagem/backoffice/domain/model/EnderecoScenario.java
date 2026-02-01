package com.vanessaviagem.backoffice.domain.model;

public record EnderecoScenario(
        String descricao,
        boolean principal,
        String logradouro,
        Integer numero,
        String bairro,
        String cep,
        String cidade,
        String estado,
        String pais,
        Class<? extends Exception> expectedException,
        String expectedMessage
) {
    @Override
    public String toString() {
        return descricao;
    }
}
