package com.vanessaviagem.backoffice.domain.model;

import com.vanessaviagem.backoffice.domain.model.MilhasScenario.ComIdFactoryInvalidaScenario;
import com.vanessaviagem.backoffice.domain.model.MilhasScenario.ComIdFactoryScenario;
import com.vanessaviagem.backoffice.domain.model.MilhasScenario.ComIdInstanceScenario;
import com.vanessaviagem.backoffice.domain.model.MilhasScenario.ComQuantidadeScenario;
import com.vanessaviagem.backoffice.domain.model.MilhasScenario.ComValorScenario;
import com.vanessaviagem.backoffice.domain.model.MilhasScenario.CriacaoInvalidaScenario;
import com.vanessaviagem.backoffice.domain.model.MilhasScenario.CriacaoValidaScenario;
import com.vanessaviagem.backoffice.domain.model.MilhasScenario.PrecoMedioScenario;
import com.vanessaviagem.backoffice.domain.model.enums.TipoProgramaMilhas;
import java.math.BigDecimal;
import java.util.UUID;
import java.util.stream.Stream;
import org.junit.jupiter.params.provider.Arguments;

public final class MilhasTestDataProvider {

    private static final UUID MILHAS_ID_1 = UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa");
    private static final UUID MILHAS_ID_2 = UUID.fromString("bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb");
    private static final UUID MILHAS_ID_3 = UUID.fromString("cccccccc-cccc-cccc-cccc-cccccccccccc");

    private MilhasTestDataProvider() {
    }

    public static Stream<Arguments> cenariosPrecoMedio() {
        return Stream.of(
                Arguments.of(new PrecoMedioScenario(
                        "milheiro a 25 em 10k",
                        10_000,
                        new BigDecimal("25.00"),
                        new BigDecimal("2.5000")
                )),
                Arguments.of(new PrecoMedioScenario(
                        "milheiro a 30 em 50k",
                        50_000,
                        new BigDecimal("30.00"),
                        new BigDecimal("0.6000")
                ))
        );
    }

    public static Stream<Arguments> criacaoValida() {
        return Stream.of(
                Arguments.of(new CriacaoValidaScenario(
                        "should create milhas LATAM_PASS",
                        TipoProgramaMilhas.LATAM_PASS,
                        10000,
                        new BigDecimal("250.00")
                )),
                Arguments.of(new CriacaoValidaScenario(
                        "should create milhas SMILES",
                        TipoProgramaMilhas.SMILES,
                        5000,
                        new BigDecimal("150.00")
                )),
                Arguments.of(new CriacaoValidaScenario(
                        "should create milhas AZUL_FIDELIDADE",
                        TipoProgramaMilhas.AZUL_FIDELIDADE,
                        1000,
                        new BigDecimal("30.00")
                ))
        );
    }

    public static Stream<Arguments> criacaoInvalida() {
        return Stream.of(
                Arguments.of(new CriacaoInvalidaScenario(
                        "should throw when programa is null",
                        null,
                        10000,
                        new BigDecimal("250.00"),
                        NullPointerException.class,
                        "programa eh obrigatorio"
                )),
                Arguments.of(new CriacaoInvalidaScenario(
                        "should throw when valor is null",
                        TipoProgramaMilhas.LATAM_PASS,
                        10000,
                        null,
                        NullPointerException.class,
                        "valor eh obrigatorio"
                )),
                Arguments.of(new CriacaoInvalidaScenario(
                        "should throw when quantidade is zero",
                        TipoProgramaMilhas.LATAM_PASS,
                        0,
                        new BigDecimal("250.00"),
                        IllegalArgumentException.class,
                        "quantidade deve ser positiva"
                )),
                Arguments.of(new CriacaoInvalidaScenario(
                        "should throw when quantidade is negative",
                        TipoProgramaMilhas.SMILES,
                        -1000,
                        new BigDecimal("100.00"),
                        IllegalArgumentException.class,
                        "quantidade deve ser positiva"
                ))
        );
    }

    public static Stream<Arguments> comIdFactory() {
        return Stream.of(
                Arguments.of(new ComIdFactoryScenario(
                        "should create milhas with ID for LATAM_PASS",
                        MILHAS_ID_1,
                        TipoProgramaMilhas.LATAM_PASS,
                        10000,
                        new BigDecimal("250.00")
                )),
                Arguments.of(new ComIdFactoryScenario(
                        "should create milhas with ID for SMILES",
                        MILHAS_ID_2,
                        TipoProgramaMilhas.SMILES,
                        5000,
                        new BigDecimal("150.00")
                )),
                Arguments.of(new ComIdFactoryScenario(
                        "should create milhas with ID for AZUL_FIDELIDADE",
                        MILHAS_ID_3,
                        TipoProgramaMilhas.AZUL_FIDELIDADE,
                        1000,
                        new BigDecimal("30.00")
                ))
        );
    }

    public static Stream<Arguments> comIdFactoryInvalida() {
        return Stream.of(
                Arguments.of(new ComIdFactoryInvalidaScenario(
                        "should throw when id is null in comId factory",
                        null,
                        TipoProgramaMilhas.LATAM_PASS,
                        10000,
                        new BigDecimal("250.00"),
                        NullPointerException.class,
                        "id eh obrigatorio"
                )),
                Arguments.of(new ComIdFactoryInvalidaScenario(
                        "should throw when programa is null in comId factory",
                        MILHAS_ID_1,
                        null,
                        10000,
                        new BigDecimal("250.00"),
                        NullPointerException.class,
                        "programa eh obrigatorio"
                )),
                Arguments.of(new ComIdFactoryInvalidaScenario(
                        "should throw when quantidade is zero in comId factory",
                        MILHAS_ID_2,
                        TipoProgramaMilhas.SMILES,
                        0,
                        new BigDecimal("150.00"),
                        IllegalArgumentException.class,
                        "quantidade deve ser positiva"
                ))
        );
    }

    public static Stream<Arguments> comQuantidade() {
        return Stream.of(
                Arguments.of(new ComQuantidadeScenario(
                        "should update quantidade to 5000",
                        Milhas.criar(TipoProgramaMilhas.LATAM_PASS, 10000, new BigDecimal("250.00")),
                        5000
                )),
                Arguments.of(new ComQuantidadeScenario(
                        "should update quantidade to 20000",
                        Milhas.criar(TipoProgramaMilhas.SMILES, 10000, new BigDecimal("250.00")),
                        20000
                )),
                Arguments.of(new ComQuantidadeScenario(
                        "should update quantidade to 1",
                        Milhas.criar(TipoProgramaMilhas.AZUL_FIDELIDADE, 5000, new BigDecimal("100.00")),
                        1
                ))
        );
    }

    public static Stream<Arguments> comValor() {
        return Stream.of(
                Arguments.of(new ComValorScenario(
                        "should update valor to 500.00",
                        Milhas.criar(TipoProgramaMilhas.LATAM_PASS, 10000, new BigDecimal("250.00")),
                        new BigDecimal("500.00")
                )),
                Arguments.of(new ComValorScenario(
                        "should update valor to 0.01",
                        Milhas.criar(TipoProgramaMilhas.SMILES, 10000, new BigDecimal("250.00")),
                        new BigDecimal("0.01")
                )),
                Arguments.of(new ComValorScenario(
                        "should update valor to 99999.99",
                        Milhas.criar(TipoProgramaMilhas.AZUL_FIDELIDADE, 5000, new BigDecimal("100.00")),
                        new BigDecimal("99999.99")
                ))
        );
    }

    public static Stream<Arguments> comIdInstance() {
        return Stream.of(
                Arguments.of(new ComIdInstanceScenario(
                        "should update id using instance method",
                        Milhas.criar(TipoProgramaMilhas.LATAM_PASS, 10000, new BigDecimal("250.00")),
                        MILHAS_ID_1
                )),
                Arguments.of(new ComIdInstanceScenario(
                        "should update id on SMILES instance",
                        Milhas.criar(TipoProgramaMilhas.SMILES, 5000, new BigDecimal("150.00")),
                        MILHAS_ID_2
                )),
                Arguments.of(new ComIdInstanceScenario(
                        "should update id on existing milhas with id",
                        Milhas.comId(MILHAS_ID_1, TipoProgramaMilhas.AZUL_FIDELIDADE, 1000, new BigDecimal("30.00")),
                        MILHAS_ID_3
                ))
        );
    }
}
