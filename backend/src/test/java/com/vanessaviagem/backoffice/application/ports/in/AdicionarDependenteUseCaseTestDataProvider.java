package com.vanessaviagem.backoffice.application.ports.in;

import com.vanessaviagem.backoffice.application.ports.in.AdicionarDependenteUseCaseScenario.CommandInvalidScenario;
import com.vanessaviagem.backoffice.application.ports.in.AdicionarDependenteUseCaseScenario.CommandValidScenario;
import com.vanessaviagem.backoffice.application.ports.in.AdicionarDependenteUseCaseScenario.ResultInvalidScenario;
import com.vanessaviagem.backoffice.application.ports.in.AdicionarDependenteUseCaseScenario.ResultValidScenario;
import com.vanessaviagem.backoffice.domain.model.cliente.ClienteDependente;
import com.vanessaviagem.backoffice.domain.model.Contato;
import com.vanessaviagem.backoffice.domain.model.Documento;
import com.vanessaviagem.backoffice.domain.model.Endereco;
import com.vanessaviagem.backoffice.domain.model.Pessoa;
import com.vanessaviagem.backoffice.domain.model.enums.Parentesco;
import com.vanessaviagem.backoffice.domain.model.enums.Sexo;
import com.vanessaviagem.backoffice.domain.model.enums.TipoContato;
import com.vanessaviagem.backoffice.domain.model.enums.TipoDocumento;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;
import org.junit.jupiter.params.provider.Arguments;

public final class AdicionarDependenteUseCaseTestDataProvider {

    private static final UUID TITULAR_ID = UUID.fromString("550e8400-e29b-41d4-a716-446655440050");
    private static final UUID PESSOA_ID = UUID.fromString("550e8400-e29b-41d4-a716-446655440051");
    private static final UUID DEPENDENTE_ID = UUID.fromString("550e8400-e29b-41d4-a716-446655440052");

    private AdicionarDependenteUseCaseTestDataProvider() {
    }

    private static Pessoa criarPessoaDependente() {
        return new Pessoa(
                PESSOA_ID,
                "Lucas",
                null,
                "Almeida",
                LocalDate.of(2012, 3, 25),
                Sexo.MASCULINO,
                List.of(new Documento(
                        TipoDocumento.RG,
                        "55.666.777-8",
                        null,
                        LocalDate.of(2020, 8, 10)
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
                        TipoContato.CELULAR,
                        "+55 19 98888-7777"
                )),
                LocalDate.of(2024, 1, 1),
                LocalDate.of(2024, 1, 1)
        );
    }

    private static ClienteDependente criarDependenteValido() {
        return new ClienteDependente(
                DEPENDENTE_ID,
                TITULAR_ID,
                Parentesco.FILHO,
                criarPessoaDependente(),
                Collections.emptyList(),
                true
        );
    }

    public static Stream<Arguments> commandValido() {
        return Stream.of(
                Arguments.of(new CommandValidScenario(
                        "should create command with valid data for FILHO",
                        TITULAR_ID,
                        Parentesco.FILHO,
                        criarPessoaDependente()
                )),
                Arguments.of(new CommandValidScenario(
                        "should create command with valid data for CONJUGE",
                        TITULAR_ID,
                        Parentesco.CONJUGE,
                        criarPessoaDependente()
                ))
        );
    }

    public static Stream<Arguments> commandInvalido() {
        return Stream.of(
                Arguments.of(new CommandInvalidScenario(
                        "should throw when titularId is null",
                        null,
                        Parentesco.FILHO,
                        criarPessoaDependente(),
                        NullPointerException.class,
                        "titularId eh obrigatorio"
                )),
                Arguments.of(new CommandInvalidScenario(
                        "should throw when parentesco is null",
                        TITULAR_ID,
                        null,
                        criarPessoaDependente(),
                        NullPointerException.class,
                        "parentesco eh obrigatorio"
                )),
                Arguments.of(new CommandInvalidScenario(
                        "should throw when dadosPessoais is null",
                        TITULAR_ID,
                        Parentesco.FILHO,
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
                        DEPENDENTE_ID,
                        criarDependenteValido()
                ))
        );
    }

    public static Stream<Arguments> resultInvalido() {
        return Stream.of(
                Arguments.of(new ResultInvalidScenario(
                        "should throw when dependenteId is null in result",
                        null,
                        criarDependenteValido(),
                        NullPointerException.class,
                        "dependenteId eh obrigatorio"
                )),
                Arguments.of(new ResultInvalidScenario(
                        "should throw when dependente is null in result",
                        DEPENDENTE_ID,
                        null,
                        NullPointerException.class,
                        "dependente eh obrigatorio"
                ))
        );
    }
}
