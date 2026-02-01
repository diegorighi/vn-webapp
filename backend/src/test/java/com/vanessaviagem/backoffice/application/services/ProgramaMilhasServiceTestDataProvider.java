package com.vanessaviagem.backoffice.application.services;

import com.vanessaviagem.backoffice.application.services.ProgramaMilhasServiceScenarios.AtivarProgramaScenario;
import com.vanessaviagem.backoffice.application.services.ProgramaMilhasServiceScenarios.AtualizarProgramaScenario;
import com.vanessaviagem.backoffice.application.services.ProgramaMilhasServiceScenarios.BuscarProgramaPorBrandScenario;
import com.vanessaviagem.backoffice.application.services.ProgramaMilhasServiceScenarios.BuscarProgramaScenario;
import com.vanessaviagem.backoffice.application.services.ProgramaMilhasServiceScenarios.CadastrarProgramaScenario;
import com.vanessaviagem.backoffice.application.services.ProgramaMilhasServiceScenarios.DesativarProgramaScenario;
import com.vanessaviagem.backoffice.application.services.ProgramaMilhasServiceScenarios.ListarProgramasAtivosScenario;
import com.vanessaviagem.backoffice.application.services.ProgramaMilhasServiceScenarios.ListarProgramasScenario;
import com.vanessaviagem.backoffice.domain.exceptions.ProgramaNaoEncontradoException;
import com.vanessaviagem.backoffice.domain.model.ConfigArredondamento;
import com.vanessaviagem.backoffice.domain.model.ProgramaDeMilhas;
import com.vanessaviagem.backoffice.domain.model.enums.StatusPrograma;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;
import org.junit.jupiter.params.provider.Arguments;

/**
 * External data provider for ProgramaMilhasService parameterized tests.
 * Contains all test fixtures and scenario configurations.
 */
public final class ProgramaMilhasServiceTestDataProvider {

    // Fixed UUIDs for predictable test data
    private static final UUID SMILES_ID = UUID.fromString("550e8400-e29b-41d4-a716-446655440001");
    private static final UUID LATAM_ID = UUID.fromString("550e8400-e29b-41d4-a716-446655440002");
    private static final UUID AZUL_ID = UUID.fromString("550e8400-e29b-41d4-a716-446655440003");
    private static final UUID NONEXISTENT_ID = UUID.fromString("550e8400-e29b-41d4-a716-446655440999");

    // Brand names
    private static final String BRAND_SMILES = "Smiles";
    private static final String BRAND_LATAM = "LATAM Pass";
    private static final String BRAND_AZUL = "Azul Fidelidade";
    private static final String BRAND_NONEXISTENT = "Programa Inexistente";

    // Default currency
    private static final String MOEDA_BRL = "BRL";
    private static final String MOEDA_USD = "USD";

    private ProgramaMilhasServiceTestDataProvider() {
        // Utility class
    }

    // ========================================================================
    // Test Fixtures
    // ========================================================================

    private static ProgramaDeMilhas criarProgramaSmiles() {
        return new ProgramaDeMilhas(
                SMILES_ID,
                BRAND_SMILES,
                StatusPrograma.ATIVO,
                MOEDA_BRL,
                ConfigArredondamento.DEFAULT
        );
    }

    private static ProgramaDeMilhas criarProgramaLatam() {
        return new ProgramaDeMilhas(
                LATAM_ID,
                BRAND_LATAM,
                StatusPrograma.ATIVO,
                MOEDA_BRL,
                new ConfigArredondamento(2, RoundingMode.HALF_UP)
        );
    }

    private static ProgramaDeMilhas criarProgramaAzulInativo() {
        return new ProgramaDeMilhas(
                AZUL_ID,
                BRAND_AZUL,
                StatusPrograma.INATIVO,
                MOEDA_BRL,
                ConfigArredondamento.DEFAULT
        );
    }

    private static ProgramaDeMilhas criarProgramaSmilesInativo() {
        return new ProgramaDeMilhas(
                SMILES_ID,
                BRAND_SMILES,
                StatusPrograma.INATIVO,
                MOEDA_BRL,
                ConfigArredondamento.DEFAULT
        );
    }

    private static ProgramaDeMilhas criarProgramaSmilesAtualizado() {
        return new ProgramaDeMilhas(
                SMILES_ID,
                BRAND_SMILES,
                StatusPrograma.ATIVO,
                MOEDA_USD,
                new ConfigArredondamento(6, RoundingMode.CEILING)
        );
    }

    // ========================================================================
    // Provider Methods - cadastrarPrograma
    // ========================================================================

    /**
     * Provides scenarios for cadastrarPrograma operation.
     *
     * @return stream of test arguments
     */
    public static Stream<Arguments> cadastrarProgramaScenarios() {
        ProgramaDeMilhas smilesPrograma = criarProgramaSmiles();

        return Stream.of(
                Arguments.of(new CadastrarProgramaScenario(
                        "should register program successfully when brand does not exist",
                        smilesPrograma,
                        false,
                        smilesPrograma,
                        null,
                        null
                )),
                Arguments.of(new CadastrarProgramaScenario(
                        "should throw IllegalStateException when brand already exists",
                        smilesPrograma,
                        true,
                        null,
                        IllegalStateException.class,
                        String.format("Ja existe um programa com o brand: %s", BRAND_SMILES)
                ))
        );
    }

    // ========================================================================
    // Provider Methods - buscarPrograma
    // ========================================================================

    /**
     * Provides scenarios for buscarPrograma operation.
     *
     * @return stream of test arguments
     */
    public static Stream<Arguments> buscarProgramaScenarios() {
        ProgramaDeMilhas smilesPrograma = criarProgramaSmiles();

        return Stream.of(
                Arguments.of(new BuscarProgramaScenario(
                        "should return program when found by ID",
                        SMILES_ID,
                        Optional.of(smilesPrograma),
                        true
                )),
                Arguments.of(new BuscarProgramaScenario(
                        "should return empty when program not found by ID",
                        NONEXISTENT_ID,
                        Optional.empty(),
                        false
                ))
        );
    }

    // ========================================================================
    // Provider Methods - buscarProgramaPorBrand
    // ========================================================================

    /**
     * Provides scenarios for buscarProgramaPorBrand operation.
     *
     * @return stream of test arguments
     */
    public static Stream<Arguments> buscarProgramaPorBrandScenarios() {
        ProgramaDeMilhas smilesPrograma = criarProgramaSmiles();

        return Stream.of(
                Arguments.of(new BuscarProgramaPorBrandScenario(
                        "should return program when found by brand",
                        BRAND_SMILES,
                        Optional.of(smilesPrograma),
                        true
                )),
                Arguments.of(new BuscarProgramaPorBrandScenario(
                        "should return empty when brand not found",
                        BRAND_NONEXISTENT,
                        Optional.empty(),
                        false
                ))
        );
    }

    // ========================================================================
    // Provider Methods - listarProgramas
    // ========================================================================

    /**
     * Provides scenarios for listarProgramas operation.
     *
     * @return stream of test arguments
     */
    public static Stream<Arguments> listarProgramasScenarios() {
        ProgramaDeMilhas smiles = criarProgramaSmiles();
        ProgramaDeMilhas latam = criarProgramaLatam();
        ProgramaDeMilhas azulInativo = criarProgramaAzulInativo();

        return Stream.of(
                Arguments.of(new ListarProgramasScenario(
                        "should return empty list when no programs exist",
                        Collections.emptyList(),
                        0
                )),
                Arguments.of(new ListarProgramasScenario(
                        "should return all programs including inactive",
                        List.of(smiles, latam, azulInativo),
                        3
                )),
                Arguments.of(new ListarProgramasScenario(
                        "should return single program when only one exists",
                        List.of(smiles),
                        1
                ))
        );
    }

    // ========================================================================
    // Provider Methods - listarProgramasAtivos
    // ========================================================================

    /**
     * Provides scenarios for listarProgramasAtivos operation.
     *
     * @return stream of test arguments
     */
    public static Stream<Arguments> listarProgramasAtivosScenarios() {
        ProgramaDeMilhas smiles = criarProgramaSmiles();
        ProgramaDeMilhas latam = criarProgramaLatam();

        return Stream.of(
                Arguments.of(new ListarProgramasAtivosScenario(
                        "should return empty list when no active programs exist",
                        Collections.emptyList(),
                        0
                )),
                Arguments.of(new ListarProgramasAtivosScenario(
                        "should return only active programs",
                        List.of(smiles, latam),
                        2
                )),
                Arguments.of(new ListarProgramasAtivosScenario(
                        "should return single active program",
                        List.of(smiles),
                        1
                ))
        );
    }

    // ========================================================================
    // Provider Methods - atualizarPrograma
    // ========================================================================

    /**
     * Provides scenarios for atualizarPrograma operation.
     *
     * @return stream of test arguments
     */
    public static Stream<Arguments> atualizarProgramaScenarios() {
        ProgramaDeMilhas smilesExistente = criarProgramaSmiles();
        ProgramaDeMilhas smilesAtualizado = criarProgramaSmilesAtualizado();
        ProgramaDeMilhas programaInexistente = new ProgramaDeMilhas(
                NONEXISTENT_ID,
                BRAND_NONEXISTENT,
                StatusPrograma.ATIVO,
                MOEDA_BRL,
                ConfigArredondamento.DEFAULT
        );

        return Stream.of(
                Arguments.of(new AtualizarProgramaScenario(
                        "should update program successfully when it exists",
                        smilesAtualizado,
                        Optional.of(smilesExistente),
                        smilesAtualizado,
                        null,
                        null
                )),
                Arguments.of(new AtualizarProgramaScenario(
                        "should throw ProgramaNaoEncontradoException when program not found",
                        programaInexistente,
                        Optional.empty(),
                        null,
                        ProgramaNaoEncontradoException.class,
                        String.format("Programa de milhas nao encontrado: %s", NONEXISTENT_ID)
                ))
        );
    }

    // ========================================================================
    // Provider Methods - desativarPrograma
    // ========================================================================

    /**
     * Provides scenarios for desativarPrograma operation.
     *
     * @return stream of test arguments
     */
    public static Stream<Arguments> desativarProgramaScenarios() {
        ProgramaDeMilhas smilesAtivo = criarProgramaSmiles();
        ProgramaDeMilhas azulInativo = criarProgramaAzulInativo();

        return Stream.of(
                Arguments.of(new DesativarProgramaScenario(
                        "should deactivate active program successfully",
                        SMILES_ID,
                        Optional.of(smilesAtivo),
                        null,
                        null
                )),
                Arguments.of(new DesativarProgramaScenario(
                        "should throw ProgramaNaoEncontradoException when program not found",
                        NONEXISTENT_ID,
                        Optional.empty(),
                        ProgramaNaoEncontradoException.class,
                        String.format("Programa de milhas nao encontrado: %s", NONEXISTENT_ID)
                )),
                Arguments.of(new DesativarProgramaScenario(
                        "should allow deactivating already inactive program (idempotent)",
                        AZUL_ID,
                        Optional.of(azulInativo),
                        null,
                        null
                ))
        );
    }

    // ========================================================================
    // Provider Methods - ativarPrograma
    // ========================================================================

    /**
     * Provides scenarios for ativarPrograma operation.
     *
     * @return stream of test arguments
     */
    public static Stream<Arguments> ativarProgramaScenarios() {
        ProgramaDeMilhas azulInativo = criarProgramaAzulInativo();
        ProgramaDeMilhas smilesAtivo = criarProgramaSmiles();

        return Stream.of(
                Arguments.of(new AtivarProgramaScenario(
                        "should activate inactive program successfully",
                        AZUL_ID,
                        Optional.of(azulInativo),
                        null,
                        null
                )),
                Arguments.of(new AtivarProgramaScenario(
                        "should throw ProgramaNaoEncontradoException when program not found",
                        NONEXISTENT_ID,
                        Optional.empty(),
                        ProgramaNaoEncontradoException.class,
                        String.format("Programa de milhas nao encontrado: %s", NONEXISTENT_ID)
                )),
                Arguments.of(new AtivarProgramaScenario(
                        "should allow activating already active program (idempotent)",
                        SMILES_ID,
                        Optional.of(smilesAtivo),
                        null,
                        null
                ))
        );
    }
}
