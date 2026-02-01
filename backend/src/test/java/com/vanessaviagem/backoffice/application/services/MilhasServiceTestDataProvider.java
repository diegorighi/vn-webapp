package com.vanessaviagem.backoffice.application.services;

import com.vanessaviagem.backoffice.application.services.MilhasServiceScenarios.AtualizarMilhasScenario;
import com.vanessaviagem.backoffice.application.services.MilhasServiceScenarios.BuscarMilhasPorClienteScenario;
import com.vanessaviagem.backoffice.application.services.MilhasServiceScenarios.BuscarMilhasPorProgramaScenario;
import com.vanessaviagem.backoffice.application.services.MilhasServiceScenarios.BuscarMilhasScenario;
import com.vanessaviagem.backoffice.application.services.MilhasServiceScenarios.CalcularCustoMedioMilheiroScenario;
import com.vanessaviagem.backoffice.application.services.MilhasServiceScenarios.CalcularSaldoTotalScenario;
import com.vanessaviagem.backoffice.application.services.MilhasServiceScenarios.RegistrarMilhasScenario;
import com.vanessaviagem.backoffice.application.services.MilhasServiceScenarios.RemoverMilhasScenario;
import com.vanessaviagem.backoffice.domain.exceptions.MilhasNaoEncontradaException;
import com.vanessaviagem.backoffice.domain.model.Milhas;
import com.vanessaviagem.backoffice.domain.model.enums.TipoProgramaMilhas;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;
import org.junit.jupiter.params.provider.Arguments;

/**
 * External data provider for MilhasService parameterized tests.
 * Contains static methods that provide test scenarios as Stream of Arguments.
 */
public final class MilhasServiceTestDataProvider {

    // Fixed UUIDs for consistent test data
    private static final UUID CLIENTE_ID_1 = UUID.fromString("550e8400-e29b-41d4-a716-446655440001");
    private static final UUID CLIENTE_ID_2 = UUID.fromString("550e8400-e29b-41d4-a716-446655440002");
    private static final UUID MILHAS_ID_1 = UUID.fromString("550e8400-e29b-41d4-a716-446655440010");
    private static final UUID MILHAS_ID_2 = UUID.fromString("550e8400-e29b-41d4-a716-446655440011");
    private static final UUID MILHAS_ID_3 = UUID.fromString("550e8400-e29b-41d4-a716-446655440012");
    private static final UUID MILHAS_ID_INEXISTENTE = UUID.fromString("550e8400-e29b-41d4-a716-446655440099");

    private MilhasServiceTestDataProvider() {
        // Utility class - prevent instantiation
    }

    // --- Factory Methods for Test Data ---

    private static Milhas criarMilhasSemId(TipoProgramaMilhas programa, int quantidade, BigDecimal valor) {
        return Milhas.criar(programa, quantidade, valor);
    }

    private static Milhas criarMilhasComId(UUID id, TipoProgramaMilhas programa, int quantidade, BigDecimal valor) {
        return Milhas.comId(id, programa, quantidade, valor);
    }

    private static Milhas milhasSmiles10000() {
        return criarMilhasComId(MILHAS_ID_1, TipoProgramaMilhas.SMILES, 10000, new BigDecimal("450.00"));
    }

    private static Milhas milhasLatamPass5000() {
        return criarMilhasComId(MILHAS_ID_2, TipoProgramaMilhas.LATAM_PASS, 5000, new BigDecimal("200.00"));
    }

    private static Milhas milhasAzul8000() {
        return criarMilhasComId(MILHAS_ID_3, TipoProgramaMilhas.AZUL_FIDELIDADE, 8000, new BigDecimal("320.00"));
    }

    private static Milhas milhasLivelo15000() {
        return criarMilhasComId(
                UUID.fromString("550e8400-e29b-41d4-a716-446655440013"),
                TipoProgramaMilhas.LIVELO,
                15000,
                new BigDecimal("600.00")
        );
    }

    private static Milhas milhasSmilesAlternativo() {
        return criarMilhasComId(
                UUID.fromString("550e8400-e29b-41d4-a716-446655440014"),
                TipoProgramaMilhas.SMILES,
                20000,
                new BigDecimal("900.00")
        );
    }

    // --- Provider Methods ---

    /**
     * Provides scenarios for registrarMilhas operation.
     */
    public static Stream<Arguments> registrarMilhasScenarios() {
        Milhas milhasInput = criarMilhasSemId(TipoProgramaMilhas.SMILES, 10000, new BigDecimal("450.00"));
        Milhas milhasSalva = milhasSmiles10000();

        Milhas milhasLatamInput = criarMilhasSemId(TipoProgramaMilhas.LATAM_PASS, 5000, new BigDecimal("200.00"));
        Milhas milhasLatamSalva = milhasLatamPass5000();

        return Stream.of(
                Arguments.of(new RegistrarMilhasScenario(
                        "should register milhas successfully for SMILES program",
                        CLIENTE_ID_1,
                        milhasInput,
                        milhasSalva,
                        null,
                        null
                )),
                Arguments.of(new RegistrarMilhasScenario(
                        "should register milhas successfully for LATAM_PASS program",
                        CLIENTE_ID_2,
                        milhasLatamInput,
                        milhasLatamSalva,
                        null,
                        null
                )),
                Arguments.of(new RegistrarMilhasScenario(
                        "should throw NullPointerException when clienteId is null",
                        null,
                        milhasInput,
                        null,
                        NullPointerException.class,
                        "clienteId eh obrigatorio"
                )),
                Arguments.of(new RegistrarMilhasScenario(
                        "should throw NullPointerException when milhas is null",
                        CLIENTE_ID_1,
                        null,
                        null,
                        NullPointerException.class,
                        "milhas eh obrigatorio"
                ))
        );
    }

    /**
     * Provides scenarios for buscarMilhas operation.
     */
    public static Stream<Arguments> buscarMilhasScenarios() {
        Milhas milhasExistente = milhasSmiles10000();

        return Stream.of(
                Arguments.of(new BuscarMilhasScenario(
                        "should find milhas when it exists",
                        MILHAS_ID_1,
                        Optional.of(milhasExistente),
                        Optional.of(milhasExistente)
                )),
                Arguments.of(new BuscarMilhasScenario(
                        "should return empty when milhas not found",
                        MILHAS_ID_INEXISTENTE,
                        Optional.empty(),
                        Optional.empty()
                )),
                Arguments.of(new BuscarMilhasScenario(
                        "should find milhas for LATAM_PASS program",
                        MILHAS_ID_2,
                        Optional.of(milhasLatamPass5000()),
                        Optional.of(milhasLatamPass5000())
                ))
        );
    }

    /**
     * Provides scenarios for buscarMilhasPorCliente operation.
     */
    public static Stream<Arguments> buscarMilhasPorClienteScenarios() {
        List<Milhas> listaMilhasCliente1 = List.of(milhasSmiles10000(), milhasLatamPass5000(), milhasAzul8000());
        List<Milhas> listaMilhasCliente2 = List.of(milhasLivelo15000());

        return Stream.of(
                Arguments.of(new BuscarMilhasPorClienteScenario(
                        "should return empty list when client has no milhas",
                        CLIENTE_ID_1,
                        Collections.emptyList(),
                        0
                )),
                Arguments.of(new BuscarMilhasPorClienteScenario(
                        "should return list with multiple milhas entries",
                        CLIENTE_ID_1,
                        listaMilhasCliente1,
                        3
                )),
                Arguments.of(new BuscarMilhasPorClienteScenario(
                        "should return list with single milhas entry",
                        CLIENTE_ID_2,
                        listaMilhasCliente2,
                        1
                ))
        );
    }

    /**
     * Provides scenarios for buscarMilhasPorPrograma operation.
     */
    public static Stream<Arguments> buscarMilhasPorProgramaScenarios() {
        List<Milhas> listaSmiles = List.of(milhasSmiles10000(), milhasSmilesAlternativo());
        List<Milhas> listaLatam = List.of(milhasLatamPass5000());

        return Stream.of(
                Arguments.of(new BuscarMilhasPorProgramaScenario(
                        "should return empty list when no milhas for program",
                        TipoProgramaMilhas.AADVANTAGE,
                        Collections.emptyList(),
                        0
                )),
                Arguments.of(new BuscarMilhasPorProgramaScenario(
                        "should return list with multiple milhas for SMILES program",
                        TipoProgramaMilhas.SMILES,
                        listaSmiles,
                        2
                )),
                Arguments.of(new BuscarMilhasPorProgramaScenario(
                        "should return list with single milhas for LATAM_PASS program",
                        TipoProgramaMilhas.LATAM_PASS,
                        listaLatam,
                        1
                )),
                Arguments.of(new BuscarMilhasPorProgramaScenario(
                        "should return empty list for ESFERA program",
                        TipoProgramaMilhas.ESFERA,
                        Collections.emptyList(),
                        0
                )),
                Arguments.of(new BuscarMilhasPorProgramaScenario(
                        "should return list for AZUL_FIDELIDADE program",
                        TipoProgramaMilhas.AZUL_FIDELIDADE,
                        List.of(milhasAzul8000()),
                        1
                ))
        );
    }

    /**
     * Provides scenarios for atualizarMilhas operation.
     */
    public static Stream<Arguments> atualizarMilhasScenarios() {
        Milhas milhasExistente = milhasSmiles10000();
        Milhas milhasAtualizada = Milhas.comId(MILHAS_ID_1, TipoProgramaMilhas.SMILES, 15000, new BigDecimal("675.00"));

        Milhas milhasLatamAtualizada = Milhas.comId(MILHAS_ID_2, TipoProgramaMilhas.LATAM_PASS, 7500, new BigDecimal("300.00"));

        Milhas milhasInexistente = Milhas.comId(MILHAS_ID_INEXISTENTE, TipoProgramaMilhas.LIVELO, 5000, new BigDecimal("200.00"));

        return Stream.of(
                Arguments.of(new AtualizarMilhasScenario(
                        "should update milhas successfully when it exists",
                        milhasAtualizada,
                        true,
                        milhasAtualizada,
                        milhasAtualizada,
                        null,
                        null
                )),
                Arguments.of(new AtualizarMilhasScenario(
                        "should update milhas for LATAM_PASS program",
                        milhasLatamAtualizada,
                        true,
                        milhasLatamAtualizada,
                        milhasLatamAtualizada,
                        null,
                        null
                )),
                Arguments.of(new AtualizarMilhasScenario(
                        "should throw MilhasNaoEncontradaException when milhas not found",
                        milhasInexistente,
                        false,
                        null,
                        null,
                        MilhasNaoEncontradaException.class,
                        "Milhas nao encontrada com id: " + MILHAS_ID_INEXISTENTE
                )),
                Arguments.of(new AtualizarMilhasScenario(
                        "should throw NullPointerException when milhas is null",
                        null,
                        false,
                        null,
                        null,
                        NullPointerException.class,
                        "milhas eh obrigatorio"
                ))
        );
    }

    /**
     * Provides scenarios for removerMilhas operation.
     */
    public static Stream<Arguments> removerMilhasScenarios() {
        return Stream.of(
                Arguments.of(new RemoverMilhasScenario(
                        "should remove milhas successfully when it exists",
                        MILHAS_ID_1,
                        true,
                        null,
                        null
                )),
                Arguments.of(new RemoverMilhasScenario(
                        "should remove milhas for different ID",
                        MILHAS_ID_2,
                        true,
                        null,
                        null
                )),
                Arguments.of(new RemoverMilhasScenario(
                        "should throw MilhasNaoEncontradaException when milhas not found",
                        MILHAS_ID_INEXISTENTE,
                        false,
                        MilhasNaoEncontradaException.class,
                        "Milhas nao encontrada com id: " + MILHAS_ID_INEXISTENTE
                ))
        );
    }

    /**
     * Provides scenarios for calcularSaldoTotal operation.
     */
    public static Stream<Arguments> calcularSaldoTotalScenarios() {
        // 10000 + 5000 + 8000 = 23000
        List<Milhas> listaMilhasVariadas = List.of(milhasSmiles10000(), milhasLatamPass5000(), milhasAzul8000());

        // 15000 only
        List<Milhas> listaMilhasUnica = List.of(milhasLivelo15000());

        // 10000 + 20000 = 30000
        List<Milhas> listaMilhasSmilesMultiplas = List.of(milhasSmiles10000(), milhasSmilesAlternativo());

        return Stream.of(
                Arguments.of(new CalcularSaldoTotalScenario(
                        "should return zero when client has no milhas",
                        CLIENTE_ID_1,
                        Collections.emptyList(),
                        0
                )),
                Arguments.of(new CalcularSaldoTotalScenario(
                        "should sum all milhas quantities across programs",
                        CLIENTE_ID_1,
                        listaMilhasVariadas,
                        23000
                )),
                Arguments.of(new CalcularSaldoTotalScenario(
                        "should return single milhas quantity",
                        CLIENTE_ID_2,
                        listaMilhasUnica,
                        15000
                )),
                Arguments.of(new CalcularSaldoTotalScenario(
                        "should sum milhas from same program",
                        CLIENTE_ID_1,
                        listaMilhasSmilesMultiplas,
                        30000
                ))
        );
    }

    /**
     * Provides scenarios for calcularCustoMedioMilheiro operation.
     * Formula: totalValor / (totalQuantidade / 1000)
     */
    public static Stream<Arguments> calcularCustoMedioMilheiroScenarios() {
        // Single entry: 10000 milhas, R$ 450.00
        // CustoMedio = 450 / 10 = R$ 45.00 per milheiro
        List<Milhas> listaSingleSmiles = List.of(milhasSmiles10000());

        // Multiple entries:
        // SMILES: 10000 milhas, R$ 450.00
        // LATAM: 5000 milhas, R$ 200.00
        // AZUL: 8000 milhas, R$ 320.00
        // Total: 23000 milhas, R$ 970.00
        // CustoMedio = 970 / 23 = R$ 42.1739 per milheiro
        List<Milhas> listaMilhasVariadas = List.of(milhasSmiles10000(), milhasLatamPass5000(), milhasAzul8000());

        // Single entry Livelo: 15000 milhas, R$ 600.00
        // CustoMedio = 600 / 15 = R$ 40.00 per milheiro
        List<Milhas> listaSingleLivelo = List.of(milhasLivelo15000());

        // Two SMILES entries:
        // 10000 milhas, R$ 450.00
        // 20000 milhas, R$ 900.00
        // Total: 30000 milhas, R$ 1350.00
        // CustoMedio = 1350 / 30 = R$ 45.00 per milheiro
        List<Milhas> listaMilhasSmilesMultiplas = List.of(milhasSmiles10000(), milhasSmilesAlternativo());

        return Stream.of(
                Arguments.of(new CalcularCustoMedioMilheiroScenario(
                        "should return zero when client has no milhas",
                        CLIENTE_ID_1,
                        Collections.emptyList(),
                        BigDecimal.ZERO
                )),
                Arguments.of(new CalcularCustoMedioMilheiroScenario(
                        "should calculate custo medio for single milhas entry",
                        CLIENTE_ID_1,
                        listaSingleSmiles,
                        new BigDecimal("45.0000")
                )),
                Arguments.of(new CalcularCustoMedioMilheiroScenario(
                        "should calculate weighted average across multiple programs",
                        CLIENTE_ID_1,
                        listaMilhasVariadas,
                        new BigDecimal("42.1739")
                )),
                Arguments.of(new CalcularCustoMedioMilheiroScenario(
                        "should calculate custo medio for Livelo entry",
                        CLIENTE_ID_2,
                        listaSingleLivelo,
                        new BigDecimal("40.0000")
                )),
                Arguments.of(new CalcularCustoMedioMilheiroScenario(
                        "should calculate custo medio for multiple same program entries",
                        CLIENTE_ID_1,
                        listaMilhasSmilesMultiplas,
                        new BigDecimal("45.0000")
                ))
        );
    }
}
