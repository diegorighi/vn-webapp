package com.vanessaviagem.backoffice.application.services;

import com.vanessaviagem.backoffice.application.services.ClienteServiceScenario.AdicionarDependenteScenario;
import com.vanessaviagem.backoffice.application.services.ClienteServiceScenario.AtualizarDependenteScenario;
import com.vanessaviagem.backoffice.application.services.ClienteServiceScenario.AtualizarTitularScenario;
import com.vanessaviagem.backoffice.application.services.ClienteServiceScenario.AtualizarViagemScenario;
import com.vanessaviagem.backoffice.application.services.ClienteServiceScenario.BuscarDependenteScenario;
import com.vanessaviagem.backoffice.application.services.ClienteServiceScenario.BuscarDependentesPorTitularScenario;
import com.vanessaviagem.backoffice.application.services.ClienteServiceScenario.BuscarTitularScenario;
import com.vanessaviagem.backoffice.application.services.ClienteServiceScenario.BuscarTodosTitularesScenario;
import com.vanessaviagem.backoffice.application.services.ClienteServiceScenario.BuscarViagemScenario;
import com.vanessaviagem.backoffice.application.services.ClienteServiceScenario.BuscarViagensPorTitularScenario;
import com.vanessaviagem.backoffice.application.services.ClienteServiceScenario.CancelarViagemScenario;
import com.vanessaviagem.backoffice.application.services.ClienteServiceScenario.DesativarTitularScenario;
import com.vanessaviagem.backoffice.application.services.ClienteServiceScenario.RegistrarTitularScenario;
import com.vanessaviagem.backoffice.application.services.ClienteServiceScenario.RegistrarViagemScenario;
import com.vanessaviagem.backoffice.application.services.ClienteServiceScenario.RemoverDependenteScenario;
import com.vanessaviagem.backoffice.domain.exceptions.ClienteNaoEncontradoException;
import com.vanessaviagem.backoffice.domain.exceptions.TitularInativoException;
import com.vanessaviagem.backoffice.domain.exceptions.ViagemNaoEncontradaException;
import com.vanessaviagem.backoffice.domain.model.cliente.ClienteDependente;
import com.vanessaviagem.backoffice.domain.model.cliente.ClienteTitular;
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
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;
import org.junit.jupiter.params.provider.Arguments;

/**
 * External data provider for ClienteService tests.
 * All test data is centralized here following the parameterized test pattern.
 */
public final class ClienteServiceTestDataProvider {

    // ========== FIXED UUIDs FOR CONSISTENT TESTING ==========
    private static final UUID TITULAR_ID = UUID.fromString("550e8400-e29b-41d4-a716-446655440010");
    private static final UUID TITULAR_ID_2 = UUID.fromString("550e8400-e29b-41d4-a716-446655440020");
    private static final UUID PESSOA_ID = UUID.fromString("550e8400-e29b-41d4-a716-446655440011");
    private static final UUID PESSOA_ID_2 = UUID.fromString("550e8400-e29b-41d4-a716-446655440021");
    private static final UUID DEPENDENTE_ID = UUID.fromString("550e8400-e29b-41d4-a716-446655440012");
    private static final UUID DEPENDENTE_ID_2 = UUID.fromString("550e8400-e29b-41d4-a716-446655440022");
    private static final UUID VIAGEM_ID = UUID.fromString("550e8400-e29b-41d4-a716-446655440013");
    private static final UUID VIAGEM_ID_2 = UUID.fromString("550e8400-e29b-41d4-a716-446655440023");
    private static final UUID NON_EXISTENT_ID = UUID.fromString("550e8400-e29b-41d4-a716-446655440099");

    private ClienteServiceTestDataProvider() {
        // Utility class - no instantiation
    }

    // ========== FACTORY METHODS ==========

    private static Pessoa criarPessoaTitular() {
        return new Pessoa(
                PESSOA_ID,
                "Carlos",
                "Eduardo",
                "Pereira",
                LocalDate.of(1980, 12, 5),
                Sexo.MASCULINO,
                List.of(new Documento(
                        TipoDocumento.CPF,
                        "987.654.321-00",
                        null,
                        LocalDate.of(2019, 3, 10)
                )),
                List.of(new Endereco(
                        true,
                        "Avenida Brasil",
                        500,
                        "Jardins",
                        "04567-890",
                        "Sao Paulo",
                        "SP",
                        "Brasil"
                )),
                List.of(new Contato(
                        true,
                        TipoContato.EMAIL,
                        "carlos@example.com"
                )),
                LocalDate.of(2024, 1, 1),
                LocalDate.of(2024, 1, 1)
        );
    }

    private static Pessoa criarPessoaTitular2() {
        return new Pessoa(
                PESSOA_ID_2,
                "Maria",
                null,
                "Santos",
                LocalDate.of(1990, 6, 15),
                Sexo.FEMININO,
                List.of(new Documento(
                        TipoDocumento.CPF,
                        "123.456.789-00",
                        null,
                        LocalDate.of(2020, 1, 5)
                )),
                List.of(new Endereco(
                        true,
                        "Rua das Flores",
                        100,
                        "Centro",
                        "01234-567",
                        "Rio de Janeiro",
                        "RJ",
                        "Brasil"
                )),
                List.of(new Contato(
                        true,
                        TipoContato.CELULAR,
                        "+55 21 99999-8888"
                )),
                LocalDate.of(2024, 2, 1),
                LocalDate.of(2024, 2, 1)
        );
    }

    private static Pessoa criarPessoaDependente() {
        return new Pessoa(
                UUID.fromString("550e8400-e29b-41d4-a716-446655440014"),
                "Ana",
                null,
                "Pereira",
                LocalDate.of(2010, 4, 22),
                Sexo.FEMININO,
                List.of(new Documento(
                        TipoDocumento.RG,
                        "11.222.333-4",
                        null,
                        LocalDate.of(2021, 7, 15)
                )),
                List.of(new Endereco(
                        true,
                        "Avenida Brasil",
                        500,
                        "Jardins",
                        "04567-890",
                        "Sao Paulo",
                        "SP",
                        "Brasil"
                )),
                List.of(new Contato(
                        true,
                        TipoContato.CELULAR,
                        "+55 11 97777-6666"
                )),
                LocalDate.of(2024, 2, 1),
                LocalDate.of(2024, 2, 1)
        );
    }

    private static Pessoa criarPessoaDependente2() {
        return new Pessoa(
                UUID.fromString("550e8400-e29b-41d4-a716-446655440024"),
                "Pedro",
                "Henrique",
                "Pereira",
                LocalDate.of(2015, 8, 10),
                Sexo.MASCULINO,
                List.of(new Documento(
                        TipoDocumento.RG,
                        "22.333.444-5",
                        null,
                        LocalDate.of(2022, 3, 20)
                )),
                List.of(new Endereco(
                        true,
                        "Avenida Brasil",
                        500,
                        "Jardins",
                        "04567-890",
                        "Sao Paulo",
                        "SP",
                        "Brasil"
                )),
                List.of(new Contato(
                        true,
                        TipoContato.CELULAR,
                        "+55 11 96666-5555"
                )),
                LocalDate.of(2024, 3, 1),
                LocalDate.of(2024, 3, 1)
        );
    }

    private static ClienteTitular criarTitularValido() {
        return new ClienteTitular(
                TITULAR_ID,
                criarPessoaTitular(),
                Collections.emptyList(),
                true,
                Collections.emptyList()
        );
    }

    private static ClienteTitular criarTitularValido2() {
        return new ClienteTitular(
                TITULAR_ID_2,
                criarPessoaTitular2(),
                Collections.emptyList(),
                true,
                Collections.emptyList()
        );
    }

    private static ClienteTitular criarTitularComViagens() {
        return new ClienteTitular(
                TITULAR_ID,
                criarPessoaTitular(),
                List.of(criarViagemValida()),
                true,
                Collections.emptyList()
        );
    }

    private static ClienteDependente criarDependenteValido() {
        return new ClienteDependente(
                DEPENDENTE_ID,
                TITULAR_ID,
                Parentesco.FILHA,
                criarPessoaDependente(),
                Collections.emptyList(),
                true
        );
    }

    private static ClienteDependente criarDependenteValido2() {
        return new ClienteDependente(
                DEPENDENTE_ID_2,
                TITULAR_ID,
                Parentesco.FILHO,
                criarPessoaDependente2(),
                Collections.emptyList(),
                true
        );
    }

    private static ClienteTitular criarTitularInativo() {
        return new ClienteTitular(
                TITULAR_ID,
                criarPessoaTitular(),
                Collections.emptyList(),
                false,
                Collections.emptyList()
        );
    }

    private static Viagem criarViagemValida() {
        return new Viagem(
                VIAGEM_ID,
                "DEF789",
                List.of("GRU", "LIS", "CDG"),
                OffsetDateTime.parse("2024-09-15T08:00:00Z"),
                "3A",
                List.of("TAP", "AF"),
                "EUR",
                new BigDecimal("3500.00"),
                ViagemStatus.EMITIDO,
                OffsetDateTime.parse("2024-03-01T12:00:00Z"),
                OffsetDateTime.parse("2024-03-01T12:00:00Z")
        );
    }

    private static Viagem criarViagemDomestica() {
        return new Viagem(
                VIAGEM_ID_2,
                "GHI012",
                List.of("GRU", "BSB"),
                OffsetDateTime.parse("2024-10-01T06:00:00Z"),
                "12C",
                List.of("GOL"),
                "BRL",
                new BigDecimal("900.00"),
                ViagemStatus.EMITIDO,
                OffsetDateTime.parse("2024-04-01T09:00:00Z"),
                OffsetDateTime.parse("2024-04-01T09:00:00Z")
        );
    }

    // ========== REGISTRAR TITULAR SCENARIOS ==========

    public static Stream<Arguments> registrarTitularScenarios() {
        ClienteTitular titular = criarTitularValido();
        ClienteTitular titularComViagens = criarTitularComViagens();

        return Stream.of(
                Arguments.of(new RegistrarTitularScenario(
                        "should register titular successfully",
                        titular,
                        titular,
                        null,
                        null
                )),
                Arguments.of(new RegistrarTitularScenario(
                        "should register titular with viagens",
                        titularComViagens,
                        titularComViagens,
                        null,
                        null
                ))
        );
    }

    public static Stream<Arguments> registrarTitularNullScenarios() {
        return Stream.of(
                Arguments.of(new RegistrarTitularScenario(
                        "should throw NullPointerException when titular is null",
                        null,
                        null,
                        NullPointerException.class,
                        "titular eh obrigatorio"
                ))
        );
    }

    // ========== BUSCAR TITULAR SCENARIOS ==========

    public static Stream<Arguments> buscarTitularScenarios() {
        ClienteTitular titular = criarTitularValido();

        return Stream.of(
                Arguments.of(new BuscarTitularScenario(
                        "should find titular when exists",
                        TITULAR_ID,
                        Optional.of(titular),
                        Optional.of(titular)
                )),
                Arguments.of(new BuscarTitularScenario(
                        "should return empty when titular not found",
                        NON_EXISTENT_ID,
                        Optional.empty(),
                        Optional.empty()
                ))
        );
    }

    // ========== BUSCAR TODOS TITULARES SCENARIOS ==========

    public static Stream<Arguments> buscarTodosTitularesScenarios() {
        ClienteTitular titular1 = criarTitularValido();
        ClienteTitular titular2 = criarTitularValido2();

        return Stream.of(
                Arguments.of(new BuscarTodosTitularesScenario(
                        "should return empty list when no titulares exist",
                        Collections.emptyList(),
                        Collections.emptyList()
                )),
                Arguments.of(new BuscarTodosTitularesScenario(
                        "should return list with one titular",
                        List.of(titular1),
                        List.of(titular1)
                )),
                Arguments.of(new BuscarTodosTitularesScenario(
                        "should return list with multiple titulares",
                        List.of(titular1, titular2),
                        List.of(titular1, titular2)
                ))
        );
    }

    // ========== ATUALIZAR TITULAR SCENARIOS ==========

    public static Stream<Arguments> atualizarTitularScenarios() {
        ClienteTitular titularExistente = criarTitularValido();
        ClienteTitular titularAtualizado = new ClienteTitular(
                TITULAR_ID,
                criarPessoaTitular(),
                List.of(criarViagemValida()),
                true,
                Collections.emptyList()
        );

        return Stream.of(
                Arguments.of(new AtualizarTitularScenario(
                        "should update titular successfully when exists",
                        titularAtualizado,
                        Optional.of(titularExistente),
                        titularAtualizado,
                        null,
                        null
                )),
                Arguments.of(new AtualizarTitularScenario(
                        "should throw ClienteNaoEncontradoException when titular not found",
                        titularAtualizado,
                        Optional.empty(),
                        null,
                        ClienteNaoEncontradoException.class,
                        String.format("Cliente nao encontrado: %s", TITULAR_ID)
                ))
        );
    }

    // ========== DESATIVAR TITULAR SCENARIOS ==========

    public static Stream<Arguments> desativarTitularScenarios() {
        ClienteTitular titularAtivo = criarTitularValido();

        return Stream.of(
                Arguments.of(new DesativarTitularScenario(
                        "should deactivate titular successfully when exists without dependentes",
                        TITULAR_ID,
                        Optional.of(titularAtivo),
                        Collections.emptyList(),
                        null,
                        null
                )),
                Arguments.of(new DesativarTitularScenario(
                        "should throw ClienteNaoEncontradoException when titular not found",
                        NON_EXISTENT_ID,
                        Optional.empty(),
                        Collections.emptyList(),
                        ClienteNaoEncontradoException.class,
                        String.format("Cliente nao encontrado: %s", NON_EXISTENT_ID)
                ))
        );
    }

    public static Stream<Arguments> desativarTitularComDependentesScenarios() {
        ClienteTitular titularAtivo = criarTitularValido();
        ClienteDependente dep1 = criarDependenteValido();
        ClienteDependente dep2 = criarDependenteValido2();

        return Stream.of(
                Arguments.of(new DesativarTitularScenario(
                        "should deactivate titular and propagate to one dependente",
                        TITULAR_ID,
                        Optional.of(titularAtivo),
                        List.of(dep1),
                        null,
                        null
                )),
                Arguments.of(new DesativarTitularScenario(
                        "should deactivate titular and propagate to multiple dependentes",
                        TITULAR_ID,
                        Optional.of(titularAtivo),
                        List.of(dep1, dep2),
                        null,
                        null
                ))
        );
    }

    // ========== ADICIONAR DEPENDENTE SCENARIOS ==========

    public static Stream<Arguments> adicionarDependenteScenarios() {
        ClienteTitular titularExistente = criarTitularValido();
        Pessoa dependenteDados = criarPessoaDependente();
        ClienteDependente dependenteEsperado = new ClienteDependente(
                DEPENDENTE_ID,
                TITULAR_ID,
                Parentesco.FILHA,
                dependenteDados,
                List.of(),
                true
        );

        return Stream.of(
                Arguments.of(new AdicionarDependenteScenario(
                        "should add dependente FILHA when titular exists and is active",
                        TITULAR_ID,
                        Parentesco.FILHA,
                        dependenteDados,
                        Optional.of(titularExistente),
                        dependenteEsperado,
                        null,
                        null
                )),
                Arguments.of(new AdicionarDependenteScenario(
                        "should throw ClienteNaoEncontradoException when titular not found",
                        NON_EXISTENT_ID,
                        Parentesco.FILHO,
                        dependenteDados,
                        Optional.empty(),
                        null,
                        ClienteNaoEncontradoException.class,
                        String.format("Cliente nao encontrado: %s", NON_EXISTENT_ID)
                ))
        );
    }

    public static Stream<Arguments> adicionarDependenteTitularInativoScenarios() {
        ClienteTitular titularInativo = criarTitularInativo();
        Pessoa dependenteDados = criarPessoaDependente();

        return Stream.of(
                Arguments.of(new AdicionarDependenteScenario(
                        "should throw TitularInativoException when titular is inactive",
                        TITULAR_ID,
                        Parentesco.FILHO,
                        dependenteDados,
                        Optional.of(titularInativo),
                        null,
                        TitularInativoException.class,
                        String.format("Titular inativo: %s. Nao e possivel adicionar dependente a titular inativo.", TITULAR_ID)
                ))
        );
    }

    // ========== BUSCAR DEPENDENTE SCENARIOS ==========

    public static Stream<Arguments> buscarDependenteScenarios() {
        ClienteDependente dependente = criarDependenteValido();

        return Stream.of(
                Arguments.of(new BuscarDependenteScenario(
                        "should find dependente when exists",
                        DEPENDENTE_ID,
                        Optional.of(dependente),
                        Optional.of(dependente)
                )),
                Arguments.of(new BuscarDependenteScenario(
                        "should return empty when dependente not found",
                        NON_EXISTENT_ID,
                        Optional.empty(),
                        Optional.empty()
                ))
        );
    }

    // ========== BUSCAR DEPENDENTES POR TITULAR SCENARIOS ==========

    public static Stream<Arguments> buscarDependentesPorTitularScenarios() {
        ClienteTitular titular = criarTitularValido();
        ClienteDependente dep1 = criarDependenteValido();
        ClienteDependente dep2 = criarDependenteValido2();

        return Stream.of(
                Arguments.of(new BuscarDependentesPorTitularScenario(
                        "should return empty list when titular has no dependentes",
                        TITULAR_ID,
                        Optional.of(titular),
                        Collections.emptyList(),
                        Collections.emptyList(),
                        null,
                        null
                )),
                Arguments.of(new BuscarDependentesPorTitularScenario(
                        "should return list with dependentes when titular has dependentes",
                        TITULAR_ID,
                        Optional.of(titular),
                        List.of(dep1, dep2),
                        List.of(dep1, dep2),
                        null,
                        null
                )),
                Arguments.of(new BuscarDependentesPorTitularScenario(
                        "should throw ClienteNaoEncontradoException when titular not found",
                        NON_EXISTENT_ID,
                        Optional.empty(),
                        null,
                        null,
                        ClienteNaoEncontradoException.class,
                        String.format("Cliente nao encontrado: %s", NON_EXISTENT_ID)
                ))
        );
    }

    // ========== ATUALIZAR DEPENDENTE SCENARIOS ==========

    public static Stream<Arguments> atualizarDependenteScenarios() {
        ClienteDependente dependenteExistente = criarDependenteValido();
        ClienteDependente dependenteAtualizado = new ClienteDependente(
                DEPENDENTE_ID,
                TITULAR_ID,
                Parentesco.FILHA,
                criarPessoaDependente2(),
                Collections.emptyList(),
                true
        );

        return Stream.of(
                Arguments.of(new AtualizarDependenteScenario(
                        "should update dependente successfully when exists",
                        dependenteAtualizado,
                        Optional.of(dependenteExistente),
                        dependenteAtualizado,
                        null,
                        null
                )),
                Arguments.of(new AtualizarDependenteScenario(
                        "should throw ClienteNaoEncontradoException when dependente not found",
                        dependenteAtualizado,
                        Optional.empty(),
                        null,
                        ClienteNaoEncontradoException.class,
                        String.format("Dependente nao encontrado: %s", DEPENDENTE_ID)
                ))
        );
    }

    // ========== REMOVER DEPENDENTE SCENARIOS ==========

    public static Stream<Arguments> removerDependenteScenarios() {
        ClienteDependente dependente = criarDependenteValido();

        return Stream.of(
                Arguments.of(new RemoverDependenteScenario(
                        "should remove dependente successfully when exists",
                        DEPENDENTE_ID,
                        Optional.of(dependente),
                        null,
                        null
                )),
                Arguments.of(new RemoverDependenteScenario(
                        "should throw ClienteNaoEncontradoException when dependente not found",
                        NON_EXISTENT_ID,
                        Optional.empty(),
                        ClienteNaoEncontradoException.class,
                        String.format("Dependente nao encontrado: %s", NON_EXISTENT_ID)
                ))
        );
    }

    // ========== REGISTRAR VIAGEM SCENARIOS ==========

    public static Stream<Arguments> registrarViagemScenarios() {
        ClienteTitular titular = criarTitularValido();
        Viagem viagem = criarViagemValida();
        Viagem viagemDomestica = criarViagemDomestica();

        return Stream.of(
                Arguments.of(new RegistrarViagemScenario(
                        "should register viagem internacional for titular",
                        TITULAR_ID,
                        viagem,
                        Optional.of(titular),
                        viagem,
                        null,
                        null
                )),
                Arguments.of(new RegistrarViagemScenario(
                        "should register viagem domestica for titular",
                        TITULAR_ID,
                        viagemDomestica,
                        Optional.of(titular),
                        viagemDomestica,
                        null,
                        null
                )),
                Arguments.of(new RegistrarViagemScenario(
                        "should throw ClienteNaoEncontradoException when titular not found",
                        NON_EXISTENT_ID,
                        viagem,
                        Optional.empty(),
                        null,
                        ClienteNaoEncontradoException.class,
                        String.format("Cliente nao encontrado: %s", NON_EXISTENT_ID)
                ))
        );
    }

    // ========== BUSCAR VIAGEM SCENARIOS ==========

    public static Stream<Arguments> buscarViagemScenarios() {
        Viagem viagem = criarViagemValida();

        return Stream.of(
                Arguments.of(new BuscarViagemScenario(
                        "should find viagem when exists",
                        VIAGEM_ID,
                        Optional.of(viagem),
                        Optional.of(viagem)
                )),
                Arguments.of(new BuscarViagemScenario(
                        "should return empty when viagem not found",
                        NON_EXISTENT_ID,
                        Optional.empty(),
                        Optional.empty()
                ))
        );
    }

    // ========== BUSCAR VIAGENS POR TITULAR SCENARIOS ==========

    public static Stream<Arguments> buscarViagensPorTitularScenarios() {
        ClienteTitular titular = criarTitularValido();
        Viagem viagem1 = criarViagemValida();
        Viagem viagem2 = criarViagemDomestica();

        return Stream.of(
                Arguments.of(new BuscarViagensPorTitularScenario(
                        "should return empty list when titular has no viagens",
                        TITULAR_ID,
                        Optional.of(titular),
                        Collections.emptyList(),
                        Collections.emptyList(),
                        null,
                        null
                )),
                Arguments.of(new BuscarViagensPorTitularScenario(
                        "should return list with viagens when titular has viagens",
                        TITULAR_ID,
                        Optional.of(titular),
                        List.of(viagem1, viagem2),
                        List.of(viagem1, viagem2),
                        null,
                        null
                )),
                Arguments.of(new BuscarViagensPorTitularScenario(
                        "should throw ClienteNaoEncontradoException when titular not found",
                        NON_EXISTENT_ID,
                        Optional.empty(),
                        null,
                        null,
                        ClienteNaoEncontradoException.class,
                        String.format("Cliente nao encontrado: %s", NON_EXISTENT_ID)
                ))
        );
    }

    // ========== ATUALIZAR VIAGEM SCENARIOS ==========

    public static Stream<Arguments> atualizarViagemScenarios() {
        Viagem viagemExistente = criarViagemValida();
        Viagem viagemAtualizada = new Viagem(
                VIAGEM_ID,
                "DEF789",
                List.of("GRU", "LIS", "CDG"),
                OffsetDateTime.parse("2024-09-15T08:00:00Z"),
                "5B",
                List.of("TAP", "AF"),
                "EUR",
                new BigDecimal("3800.00"),
                ViagemStatus.VOADO,
                OffsetDateTime.parse("2024-03-01T12:00:00Z"),
                OffsetDateTime.parse("2024-06-01T10:00:00Z")
        );

        return Stream.of(
                Arguments.of(new AtualizarViagemScenario(
                        "should update viagem successfully when exists",
                        viagemAtualizada,
                        Optional.of(viagemExistente),
                        viagemAtualizada,
                        null,
                        null
                )),
                Arguments.of(new AtualizarViagemScenario(
                        "should throw ViagemNaoEncontradaException when viagem not found",
                        viagemAtualizada,
                        Optional.empty(),
                        null,
                        ViagemNaoEncontradaException.class,
                        String.format("Viagem nao encontrada: %s", VIAGEM_ID)
                ))
        );
    }

    // ========== CANCELAR VIAGEM SCENARIOS ==========

    public static Stream<Arguments> cancelarViagemScenarios() {
        Viagem viagem = criarViagemValida();

        return Stream.of(
                Arguments.of(new CancelarViagemScenario(
                        "should cancel viagem successfully when exists",
                        VIAGEM_ID,
                        Optional.of(viagem),
                        null,
                        null
                )),
                Arguments.of(new CancelarViagemScenario(
                        "should throw ViagemNaoEncontradaException when viagem not found",
                        NON_EXISTENT_ID,
                        Optional.empty(),
                        ViagemNaoEncontradaException.class,
                        String.format("Viagem nao encontrada: %s", NON_EXISTENT_ID)
                ))
        );
    }
}
