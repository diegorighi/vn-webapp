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

public final class ClienteTitularTestDataProvider {

    private static final UUID TITULAR_ID = UUID.fromString("550e8400-e29b-41d4-a716-446655440001");
    private static final UUID PESSOA_TITULAR_ID = UUID.fromString("550e8400-e29b-41d4-a716-446655440003");
    private static final UUID VIAGEM_ID = UUID.fromString("550e8400-e29b-41d4-a716-446655440004");

    private ClienteTitularTestDataProvider() {
    }

    private static Pessoa criarPessoaTitular() {
        return new Pessoa(
                PESSOA_TITULAR_ID,
                "Joao",
                "Carlos",
                "Silva",
                LocalDate.of(1985, 3, 20),
                Sexo.MASCULINO,
                List.of(new Documento(
                        TipoDocumento.CPF,
                        "123.456.789-00",
                        null,
                        LocalDate.of(2020, 1, 1)
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
                        TipoContato.EMAIL,
                        "joao@example.com"
                )),
                LocalDate.of(2024, 1, 1),
                LocalDate.of(2024, 1, 1)
        );
    }

    private static Viagem criarViagemValida() {
        return new Viagem(
                VIAGEM_ID,
                "ABC123",
                List.of("GRU", "GIG"),
                OffsetDateTime.parse("2024-06-15T10:00:00Z"),
                "14A",
                List.of("LATAM"),
                "BRL",
                new BigDecimal("1500.00"),
                ViagemStatus.EMITIDO,
                OffsetDateTime.parse("2024-01-15T08:00:00Z"),
                OffsetDateTime.parse("2024-01-15T08:00:00Z")
        );
    }

    private static ClienteDependente criarDependenteConjuge() {
        return new ClienteDependente(
                UUID.fromString("550e8400-e29b-41d4-a716-446655440011"),
                TITULAR_ID,
                Parentesco.CONJUGE,
                new Pessoa(
                        UUID.fromString("550e8400-e29b-41d4-a716-446655440021"),
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
                                TipoContato.EMAIL,
                                "ana@example.com"
                        )),
                        LocalDate.of(2024, 1, 1),
                        LocalDate.of(2024, 1, 1)
                ),
                Collections.emptyList(),
                true
        );
    }

    private static ClienteDependente criarDependenteFilho() {
        return new ClienteDependente(
                UUID.fromString("550e8400-e29b-41d4-a716-446655440012"),
                TITULAR_ID,
                Parentesco.FILHO,
                new Pessoa(
                        UUID.fromString("550e8400-e29b-41d4-a716-446655440022"),
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
                ),
                Collections.emptyList(),
                true
        );
    }

    private static ClienteDependente criarDependenteFilha() {
        return new ClienteDependente(
                UUID.fromString("550e8400-e29b-41d4-a716-446655440013"),
                TITULAR_ID,
                Parentesco.FILHA,
                new Pessoa(
                        UUID.fromString("550e8400-e29b-41d4-a716-446655440023"),
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
                ),
                Collections.emptyList(),
                true
        );
    }

    public static Stream<Arguments> criacaoValida() {
        return Stream.of(
                Arguments.of(new ClienteTitularScenario(
                        "should create cliente titular ativo com viagens sem dependentes",
                        TITULAR_ID,
                        criarPessoaTitular(),
                        List.of(criarViagemValida()),
                        true,
                        Collections.emptyList(),
                        null,
                        null
                )),
                Arguments.of(new ClienteTitularScenario(
                        "should create cliente titular inativo sem viagens sem dependentes",
                        TITULAR_ID,
                        criarPessoaTitular(),
                        Collections.emptyList(),
                        false,
                        Collections.emptyList(),
                        null,
                        null
                )),
                Arguments.of(new ClienteTitularScenario(
                        "should create cliente titular com 1 dependente CONJUGE",
                        TITULAR_ID,
                        criarPessoaTitular(),
                        Collections.emptyList(),
                        true,
                        List.of(criarDependenteConjuge()),
                        null,
                        null
                )),
                Arguments.of(new ClienteTitularScenario(
                        "should create cliente titular com familia completa (esposa e 2 filhos)",
                        TITULAR_ID,
                        criarPessoaTitular(),
                        List.of(criarViagemValida()),
                        true,
                        List.of(
                                criarDependenteConjuge(),
                                criarDependenteFilho(),
                                criarDependenteFilha()
                        ),
                        null,
                        null
                )),
                Arguments.of(new ClienteTitularScenario(
                        "should create cliente titular apenas com filhos (sem conjuge)",
                        TITULAR_ID,
                        criarPessoaTitular(),
                        Collections.emptyList(),
                        true,
                        List.of(
                                criarDependenteFilho(),
                                criarDependenteFilha()
                        ),
                        null,
                        null
                ))
        );
    }

    public static Stream<Arguments> validacaoNulos() {
        return Stream.of(
                Arguments.of(new ClienteTitularScenario(
                        "should throw when clienteId is null",
                        null,
                        criarPessoaTitular(),
                        Collections.emptyList(),
                        true,
                        Collections.emptyList(),
                        NullPointerException.class,
                        "clienteId eh obrigatorio"
                )),
                Arguments.of(new ClienteTitularScenario(
                        "should throw when dadosPessoais is null",
                        TITULAR_ID,
                        null,
                        Collections.emptyList(),
                        true,
                        Collections.emptyList(),
                        NullPointerException.class,
                        "dadosPessoais eh obrigatorio"
                )),
                Arguments.of(new ClienteTitularScenario(
                        "should throw when viagens is null",
                        TITULAR_ID,
                        criarPessoaTitular(),
                        null,
                        true,
                        Collections.emptyList(),
                        NullPointerException.class,
                        "viagens eh obrigatorio"
                )),
                Arguments.of(new ClienteTitularScenario(
                        "should throw when dependentes is null",
                        TITULAR_ID,
                        criarPessoaTitular(),
                        Collections.emptyList(),
                        true,
                        null,
                        NullPointerException.class,
                        "dependentes eh obrigatorio"
                ))
        );
    }
}
