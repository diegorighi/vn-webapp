package com.vanessaviagem.backoffice.domain.model;

import com.vanessaviagem.backoffice.domain.model.enums.Sexo;
import com.vanessaviagem.backoffice.domain.model.enums.TipoContato;
import com.vanessaviagem.backoffice.domain.model.enums.TipoDocumento;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;
import org.junit.jupiter.params.provider.Arguments;

public final class PessoaTestDataProvider {

    private static final UUID PESSOA_ID = UUID.fromString("550e8400-e29b-41d4-a716-446655440001");
    private static final LocalDate DATA_NASCIMENTO = LocalDate.of(1990, 5, 15);
    private static final LocalDate CREATED_AT = LocalDate.of(2024, 1, 15);
    private static final LocalDate UPDATED_AT = LocalDate.of(2024, 1, 15);

    private PessoaTestDataProvider() {
    }

    private static Documento documentoCpf() {
        return new Documento(
                TipoDocumento.CPF,
                "123.456.789-00",
                null,
                LocalDate.of(2020, 1, 1)
        );
    }

    private static Endereco enderecoPrincipal() {
        return new Endereco(
                true,
                "Rua das Flores",
                123,
                "Centro",
                "01234-567",
                "Sao Paulo",
                "SP",
                "Brasil"
        );
    }

    private static Contato contatoEmail() {
        return new Contato(
                true,
                TipoContato.EMAIL,
                "pessoa@example.com"
        );
    }

    public static Stream<Arguments> criacaoValida() {
        return Stream.of(
                Arguments.of(new PessoaScenario(
                        "should create pessoa completa masculina",
                        PESSOA_ID,
                        "Joao",
                        "Carlos",
                        "Silva",
                        DATA_NASCIMENTO,
                        Sexo.MASCULINO,
                        List.of(documentoCpf()),
                        List.of(enderecoPrincipal()),
                        List.of(contatoEmail()),
                        CREATED_AT,
                        UPDATED_AT,
                        null,
                        null
                )),
                Arguments.of(new PessoaScenario(
                        "should create pessoa feminina sem nome do meio",
                        PESSOA_ID,
                        "Maria",
                        null,
                        "Santos",
                        DATA_NASCIMENTO,
                        Sexo.FEMININO,
                        List.of(documentoCpf()),
                        List.of(enderecoPrincipal()),
                        List.of(contatoEmail()),
                        CREATED_AT,
                        null,
                        null,
                        null
                )),
                Arguments.of(new PessoaScenario(
                        "should create pessoa com listas vazias",
                        PESSOA_ID,
                        "Pedro",
                        null,
                        "Oliveira",
                        DATA_NASCIMENTO,
                        Sexo.MASCULINO,
                        Collections.emptyList(),
                        Collections.emptyList(),
                        Collections.emptyList(),
                        null,
                        null,
                        null,
                        null
                ))
        );
    }

    public static Stream<Arguments> validacaoNulos() {
        return Stream.of(
                Arguments.of(new PessoaScenario(
                        "should throw when pessoaId is null",
                        null,
                        "Joao",
                        "Carlos",
                        "Silva",
                        DATA_NASCIMENTO,
                        Sexo.MASCULINO,
                        List.of(documentoCpf()),
                        List.of(enderecoPrincipal()),
                        List.of(contatoEmail()),
                        CREATED_AT,
                        UPDATED_AT,
                        NullPointerException.class,
                        "pessoaId eh obrigatorio"
                )),
                Arguments.of(new PessoaScenario(
                        "should throw when primeiroNome is null",
                        PESSOA_ID,
                        null,
                        "Carlos",
                        "Silva",
                        DATA_NASCIMENTO,
                        Sexo.MASCULINO,
                        List.of(documentoCpf()),
                        List.of(enderecoPrincipal()),
                        List.of(contatoEmail()),
                        CREATED_AT,
                        UPDATED_AT,
                        NullPointerException.class,
                        "primeiroNome eh obrigatorio"
                )),
                Arguments.of(new PessoaScenario(
                        "should throw when sobrenome is null",
                        PESSOA_ID,
                        "Joao",
                        "Carlos",
                        null,
                        DATA_NASCIMENTO,
                        Sexo.MASCULINO,
                        List.of(documentoCpf()),
                        List.of(enderecoPrincipal()),
                        List.of(contatoEmail()),
                        CREATED_AT,
                        UPDATED_AT,
                        NullPointerException.class,
                        "sobrenome eh obrigatorio"
                )),
                Arguments.of(new PessoaScenario(
                        "should throw when dataNascimento is null",
                        PESSOA_ID,
                        "Joao",
                        "Carlos",
                        "Silva",
                        null,
                        Sexo.MASCULINO,
                        List.of(documentoCpf()),
                        List.of(enderecoPrincipal()),
                        List.of(contatoEmail()),
                        CREATED_AT,
                        UPDATED_AT,
                        NullPointerException.class,
                        "dataNascimento eh obrigatorio"
                )),
                Arguments.of(new PessoaScenario(
                        "should throw when sexo is null",
                        PESSOA_ID,
                        "Joao",
                        "Carlos",
                        "Silva",
                        DATA_NASCIMENTO,
                        null,
                        List.of(documentoCpf()),
                        List.of(enderecoPrincipal()),
                        List.of(contatoEmail()),
                        CREATED_AT,
                        UPDATED_AT,
                        NullPointerException.class,
                        "sexo eh obrigatorio"
                )),
                Arguments.of(new PessoaScenario(
                        "should throw when documentos is null",
                        PESSOA_ID,
                        "Joao",
                        "Carlos",
                        "Silva",
                        DATA_NASCIMENTO,
                        Sexo.MASCULINO,
                        null,
                        List.of(enderecoPrincipal()),
                        List.of(contatoEmail()),
                        CREATED_AT,
                        UPDATED_AT,
                        NullPointerException.class,
                        "documentos eh obrigatorio"
                )),
                Arguments.of(new PessoaScenario(
                        "should throw when enderecos is null",
                        PESSOA_ID,
                        "Joao",
                        "Carlos",
                        "Silva",
                        DATA_NASCIMENTO,
                        Sexo.MASCULINO,
                        List.of(documentoCpf()),
                        null,
                        List.of(contatoEmail()),
                        CREATED_AT,
                        UPDATED_AT,
                        NullPointerException.class,
                        "enderecos eh obrigatorio"
                )),
                Arguments.of(new PessoaScenario(
                        "should throw when contatos is null",
                        PESSOA_ID,
                        "Joao",
                        "Carlos",
                        "Silva",
                        DATA_NASCIMENTO,
                        Sexo.MASCULINO,
                        List.of(documentoCpf()),
                        List.of(enderecoPrincipal()),
                        null,
                        CREATED_AT,
                        UPDATED_AT,
                        NullPointerException.class,
                        "contatos eh obrigatorio"
                ))
        );
    }
}
