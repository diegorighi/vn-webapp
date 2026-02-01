package com.vanessaviagem.backoffice.application.ports.in;

import com.vanessaviagem.backoffice.application.ports.in.RegistrarClienteUseCaseScenario.CommandInvalidScenario;
import com.vanessaviagem.backoffice.application.ports.in.RegistrarClienteUseCaseScenario.CommandValidScenario;
import com.vanessaviagem.backoffice.application.ports.in.RegistrarClienteUseCaseScenario.ResultInvalidScenario;
import com.vanessaviagem.backoffice.application.ports.in.RegistrarClienteUseCaseScenario.ResultValidScenario;
import com.vanessaviagem.backoffice.domain.model.cliente.ClienteTitular;
import com.vanessaviagem.backoffice.domain.model.Contato;
import com.vanessaviagem.backoffice.domain.model.Documento;
import com.vanessaviagem.backoffice.domain.model.Endereco;
import com.vanessaviagem.backoffice.domain.model.Pessoa;
import com.vanessaviagem.backoffice.domain.model.enums.Sexo;
import com.vanessaviagem.backoffice.domain.model.enums.TipoContato;
import com.vanessaviagem.backoffice.domain.model.enums.TipoDocumento;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;
import org.junit.jupiter.params.provider.Arguments;

public final class RegistrarClienteUseCaseTestDataProvider {

    private static final UUID PESSOA_ID = UUID.fromString("550e8400-e29b-41d4-a716-446655440040");
    private static final UUID CLIENTE_ID = UUID.fromString("550e8400-e29b-41d4-a716-446655440041");

    private RegistrarClienteUseCaseTestDataProvider() {
    }

    private static Pessoa criarPessoaValida() {
        return new Pessoa(
                PESSOA_ID,
                "Roberto",
                "Carlos",
                "Almeida",
                LocalDate.of(1975, 7, 12),
                Sexo.MASCULINO,
                List.of(new Documento(
                        TipoDocumento.CPF,
                        "111.222.333-44",
                        null,
                        LocalDate.of(2018, 5, 20)
                )),
                List.of(new Endereco(
                        true,
                        "Rua Principal",
                        200,
                        "Vila Nova",
                        "12345-678",
                        "Campinas",
                        "SP",
                        "Brasil"
                )),
                List.of(new Contato(
                        true,
                        TipoContato.EMAIL,
                        "roberto@example.com"
                )),
                LocalDate.of(2024, 1, 1),
                LocalDate.of(2024, 1, 1)
        );
    }

    private static ClienteTitular criarClienteValido() {
        return new ClienteTitular(
                CLIENTE_ID,
                criarPessoaValida(),
                Collections.emptyList(),
                true,
                Collections.emptyList()
        );
    }

    public static Stream<Arguments> commandValido() {
        return Stream.of(
                Arguments.of(new CommandValidScenario(
                        "should create command with valid pessoa",
                        criarPessoaValida()
                ))
        );
    }

    public static Stream<Arguments> commandInvalido() {
        return Stream.of(
                Arguments.of(new CommandInvalidScenario(
                        "should throw when dadosPessoais is null",
                        null,
                        NullPointerException.class,
                        "dadosPessoais eh obrigatorio"
                ))
        );
    }

    public static Stream<Arguments> resultValido() {
        return Stream.of(
                Arguments.of(new ResultValidScenario(
                        "should create result with valid data",
                        CLIENTE_ID,
                        criarClienteValido()
                ))
        );
    }

    public static Stream<Arguments> resultInvalido() {
        return Stream.of(
                Arguments.of(new ResultInvalidScenario(
                        "should throw when clienteId is null in result",
                        null,
                        criarClienteValido(),
                        NullPointerException.class,
                        "clienteId eh obrigatorio"
                )),
                Arguments.of(new ResultInvalidScenario(
                        "should throw when cliente is null in result",
                        CLIENTE_ID,
                        null,
                        NullPointerException.class,
                        "cliente eh obrigatorio"
                ))
        );
    }
}
