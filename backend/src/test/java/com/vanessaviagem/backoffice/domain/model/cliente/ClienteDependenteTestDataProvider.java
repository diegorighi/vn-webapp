package com.vanessaviagem.backoffice.domain.model.cliente;

import com.vanessaviagem.backoffice.domain.model.Contato;
import com.vanessaviagem.backoffice.domain.model.Documento;
import com.vanessaviagem.backoffice.domain.model.Endereco;
import com.vanessaviagem.backoffice.domain.model.Pessoa;
import com.vanessaviagem.backoffice.domain.model.Viagem;
import com.vanessaviagem.backoffice.domain.model.enums.Parentesco;
import com.vanessaviagem.backoffice.domain.model.enums.Sexo;
import com.vanessaviagem.backoffice.domain.model.enums.TipoContato;
import com.vanessaviagem.backoffice.domain.model.enums.TipoDocumento;
import com.vanessaviagem.backoffice.domain.model.enums.ViagemStatus;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;
import org.junit.jupiter.params.provider.Arguments;

public final class ClienteDependenteTestDataProvider {

    private static final UUID DEPENDENTE_ID = UUID.fromString("550e8400-e29b-41d4-a716-446655440005");
    private static final UUID TITULAR_ID = UUID.fromString("550e8400-e29b-41d4-a716-446655440001");
    private static final UUID PESSOA_ID = UUID.fromString("550e8400-e29b-41d4-a716-446655440006");
    private static final UUID VIAGEM_ID = UUID.fromString("550e8400-e29b-41d4-a716-446655440007");

    private ClienteDependenteTestDataProvider() {
    }

    private static Pessoa criarPessoaConjuge() {
        return new Pessoa(
                PESSOA_ID,
                "Ana",
                null,
                "Silva",
                LocalDate.of(1987, 5, 15),
                Sexo.FEMININO,
                List.of(new Documento(
                        TipoDocumento.CPF,
                        "987.654.321-00",
                        null,
                        LocalDate.of(2020, 6, 15)
                )),
                List.of(new Endereco(
                        true,
                        "Rua das Flores",
                        100,
                        "Centro",
                        "01234-567",
                        "Sao Paulo",
                        "SP",
                        "Brasil"
                )),
                List.of(new Contato(
                        true,
                        TipoContato.CELULAR,
                        "+55 11 99999-8888"
                )),
                LocalDate.of(2024, 1, 1),
                LocalDate.of(2024, 1, 1)
        );
    }

    private static Pessoa criarPessoaFilho() {
        return new Pessoa(
                UUID.fromString("550e8400-e29b-41d4-a716-446655440008"),
                "Pedro",
                null,
                "Silva",
                LocalDate.of(2015, 8, 10),
                Sexo.MASCULINO,
                List.of(new Documento(
                        TipoDocumento.RG,
                        "12.345.678-9",
                        null,
                        LocalDate.of(2020, 6, 15)
                )),
                List.of(new Endereco(
                        true,
                        "Rua das Flores",
                        100,
                        "Centro",
                        "01234-567",
                        "Sao Paulo",
                        "SP",
                        "Brasil"
                )),
                List.of(new Contato(
                        true,
                        TipoContato.CELULAR,
                        "+55 11 99999-7777"
                )),
                LocalDate.of(2024, 1, 1),
                LocalDate.of(2024, 1, 1)
        );
    }

    private static Pessoa criarPessoaFilha() {
        return new Pessoa(
                UUID.fromString("550e8400-e29b-41d4-a716-446655440009"),
                "Julia",
                null,
                "Silva",
                LocalDate.of(2018, 3, 22),
                Sexo.FEMININO,
                List.of(new Documento(
                        TipoDocumento.RG,
                        "98.765.432-1",
                        null,
                        LocalDate.of(2023, 1, 10)
                )),
                List.of(new Endereco(
                        true,
                        "Rua das Flores",
                        100,
                        "Centro",
                        "01234-567",
                        "Sao Paulo",
                        "SP",
                        "Brasil"
                )),
                List.of(new Contato(
                        true,
                        TipoContato.CELULAR,
                        "+55 11 99999-6666"
                )),
                LocalDate.of(2024, 1, 1),
                LocalDate.of(2024, 1, 1)
        );
    }

    private static Viagem criarViagemValida() {
        return new Viagem(
                VIAGEM_ID,
                "XYZ456",
                List.of("GRU", "MIA"),
                OffsetDateTime.parse("2024-07-20T14:00:00Z"),
                "15B",
                List.of("AA"),
                "USD",
                new BigDecimal("2000.00"),
                ViagemStatus.EMITIDO,
                OffsetDateTime.parse("2024-02-10T10:00:00Z"),
                OffsetDateTime.parse("2024-02-10T10:00:00Z")
        );
    }

    public static Stream<Arguments> criacaoValida() {
        return Stream.of(
                Arguments.of(new ClienteDependenteScenario(
                        "should create dependente CONJUGE ativo com viagens",
                        DEPENDENTE_ID,
                        TITULAR_ID,
                        Parentesco.CONJUGE,
                        criarPessoaConjuge(),
                        List.of(criarViagemValida()),
                        true,
                        null,
                        null
                )),
                Arguments.of(new ClienteDependenteScenario(
                        "should create dependente FILHO ativo sem viagens",
                        UUID.fromString("550e8400-e29b-41d4-a716-446655440010"),
                        TITULAR_ID,
                        Parentesco.FILHO,
                        criarPessoaFilho(),
                        Collections.emptyList(),
                        true,
                        null,
                        null
                )),
                Arguments.of(new ClienteDependenteScenario(
                        "should create dependente FILHA inativo sem viagens",
                        UUID.fromString("550e8400-e29b-41d4-a716-446655440011"),
                        TITULAR_ID,
                        Parentesco.FILHA,
                        criarPessoaFilha(),
                        Collections.emptyList(),
                        false,
                        null,
                        null
                )),
                Arguments.of(new ClienteDependenteScenario(
                        "should create dependente PAI ativo",
                        UUID.fromString("550e8400-e29b-41d4-a716-446655440012"),
                        TITULAR_ID,
                        Parentesco.PAI,
                        criarPessoaConjuge(),
                        Collections.emptyList(),
                        true,
                        null,
                        null
                )),
                Arguments.of(new ClienteDependenteScenario(
                        "should create dependente MAE ativo",
                        UUID.fromString("550e8400-e29b-41d4-a716-446655440013"),
                        TITULAR_ID,
                        Parentesco.MAE,
                        criarPessoaConjuge(),
                        Collections.emptyList(),
                        true,
                        null,
                        null
                )),
                Arguments.of(new ClienteDependenteScenario(
                        "should create dependente OUTRO ativo",
                        UUID.fromString("550e8400-e29b-41d4-a716-446655440014"),
                        TITULAR_ID,
                        Parentesco.OUTRO,
                        criarPessoaConjuge(),
                        Collections.emptyList(),
                        true,
                        null,
                        null
                ))
        );
    }

    public static Stream<Arguments> validacaoNulos() {
        return Stream.of(
                Arguments.of(new ClienteDependenteScenario(
                        "should throw when clienteId is null",
                        null,
                        TITULAR_ID,
                        Parentesco.FILHO,
                        criarPessoaFilho(),
                        Collections.emptyList(),
                        true,
                        NullPointerException.class,
                        "clienteId eh obrigatorio"
                )),
                Arguments.of(new ClienteDependenteScenario(
                        "should throw when titularId is null",
                        DEPENDENTE_ID,
                        null,
                        Parentesco.FILHO,
                        criarPessoaFilho(),
                        Collections.emptyList(),
                        true,
                        NullPointerException.class,
                        "titularId eh obrigatorio"
                )),
                Arguments.of(new ClienteDependenteScenario(
                        "should throw when parentesco is null",
                        DEPENDENTE_ID,
                        TITULAR_ID,
                        null,
                        criarPessoaFilho(),
                        Collections.emptyList(),
                        true,
                        NullPointerException.class,
                        "parentesco eh obrigatorio"
                )),
                Arguments.of(new ClienteDependenteScenario(
                        "should throw when dadosPessoais is null",
                        DEPENDENTE_ID,
                        TITULAR_ID,
                        Parentesco.FILHO,
                        null,
                        Collections.emptyList(),
                        true,
                        NullPointerException.class,
                        "dadosPessoais eh obrigatorio"
                )),
                Arguments.of(new ClienteDependenteScenario(
                        "should throw when viagens is null",
                        DEPENDENTE_ID,
                        TITULAR_ID,
                        Parentesco.FILHO,
                        criarPessoaFilho(),
                        null,
                        true,
                        NullPointerException.class,
                        "viagens eh obrigatorio"
                ))
        );
    }
}
