package com.vanessaviagem.backoffice.domain.model;

import com.vanessaviagem.backoffice.domain.model.ProgramaDeMilhasScenario.AtivarScenario;
import com.vanessaviagem.backoffice.domain.model.ProgramaDeMilhasScenario.ComRegrasArredondamentoScenario;
import com.vanessaviagem.backoffice.domain.model.ProgramaDeMilhasScenario.CriacaoInvalidaScenario;
import com.vanessaviagem.backoffice.domain.model.ProgramaDeMilhasScenario.CriacaoValidaScenario;
import com.vanessaviagem.backoffice.domain.model.ProgramaDeMilhasScenario.DesativarScenario;
import com.vanessaviagem.backoffice.domain.model.ProgramaDeMilhasScenario.FactoryCriarComMoedaScenario;
import com.vanessaviagem.backoffice.domain.model.ProgramaDeMilhasScenario.FactoryCriarScenario;
import com.vanessaviagem.backoffice.domain.model.ProgramaDeMilhasScenario.IsAtivoScenario;
import com.vanessaviagem.backoffice.domain.model.enums.StatusPrograma;
import java.math.RoundingMode;
import java.util.UUID;
import java.util.stream.Stream;
import org.junit.jupiter.params.provider.Arguments;

/**
 * Test data provider for ProgramaDeMilhas tests.
 */
public final class ProgramaDeMilhasTestDataProvider {

    private static final UUID SMILES_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");
    private static final UUID LATAM_ID = UUID.fromString("22222222-2222-2222-2222-222222222222");
    private static final UUID AZUL_ID = UUID.fromString("33333333-3333-3333-3333-333333333333");

    private ProgramaDeMilhasTestDataProvider() {
    }

    public static Stream<Arguments> criacaoValida() {
        return Stream.of(
                Arguments.of(new CriacaoValidaScenario(
                        "should create programa with all valid fields",
                        SMILES_ID,
                        "Smiles",
                        StatusPrograma.ATIVO,
                        "BRL",
                        ConfigArredondamento.DEFAULT
                )),
                Arguments.of(new CriacaoValidaScenario(
                        "should create programa with USD currency",
                        LATAM_ID,
                        "LATAM Pass",
                        StatusPrograma.ATIVO,
                        "USD",
                        new ConfigArredondamento(2, RoundingMode.HALF_UP)
                )),
                Arguments.of(new CriacaoValidaScenario(
                        "should create programa with INATIVO status",
                        AZUL_ID,
                        "Azul Fidelidade",
                        StatusPrograma.INATIVO,
                        "BRL",
                        ConfigArredondamento.DEFAULT
                )),
                Arguments.of(new CriacaoValidaScenario(
                        "should create programa with EUR currency and custom rounding",
                        UUID.fromString("44444444-4444-4444-4444-444444444444"),
                        "Flying Blue",
                        StatusPrograma.ATIVO,
                        "EUR",
                        new ConfigArredondamento(6, RoundingMode.CEILING)
                ))
        );
    }

    public static Stream<Arguments> criacaoInvalida() {
        return Stream.of(
                Arguments.of(new CriacaoInvalidaScenario(
                        "should throw when id is null",
                        null,
                        "Smiles",
                        StatusPrograma.ATIVO,
                        "BRL",
                        ConfigArredondamento.DEFAULT,
                        NullPointerException.class,
                        "id eh obrigatorio"
                )),
                Arguments.of(new CriacaoInvalidaScenario(
                        "should throw when brand is null",
                        SMILES_ID,
                        null,
                        StatusPrograma.ATIVO,
                        "BRL",
                        ConfigArredondamento.DEFAULT,
                        NullPointerException.class,
                        "brand eh obrigatorio"
                )),
                Arguments.of(new CriacaoInvalidaScenario(
                        "should throw when brand is blank",
                        SMILES_ID,
                        "   ",
                        StatusPrograma.ATIVO,
                        "BRL",
                        ConfigArredondamento.DEFAULT,
                        IllegalArgumentException.class,
                        "brand nao pode estar vazio"
                )),
                Arguments.of(new CriacaoInvalidaScenario(
                        "should throw when brand is empty",
                        SMILES_ID,
                        "",
                        StatusPrograma.ATIVO,
                        "BRL",
                        ConfigArredondamento.DEFAULT,
                        IllegalArgumentException.class,
                        "brand nao pode estar vazio"
                )),
                Arguments.of(new CriacaoInvalidaScenario(
                        "should throw when status is null",
                        SMILES_ID,
                        "Smiles",
                        null,
                        "BRL",
                        ConfigArredondamento.DEFAULT,
                        NullPointerException.class,
                        "status eh obrigatorio"
                )),
                Arguments.of(new CriacaoInvalidaScenario(
                        "should throw when moeda is null",
                        SMILES_ID,
                        "Smiles",
                        StatusPrograma.ATIVO,
                        null,
                        ConfigArredondamento.DEFAULT,
                        NullPointerException.class,
                        "moeda eh obrigatorio"
                )),
                Arguments.of(new CriacaoInvalidaScenario(
                        "should throw when moeda is blank",
                        SMILES_ID,
                        "Smiles",
                        StatusPrograma.ATIVO,
                        "   ",
                        ConfigArredondamento.DEFAULT,
                        IllegalArgumentException.class,
                        "moeda nao pode estar vazia"
                )),
                Arguments.of(new CriacaoInvalidaScenario(
                        "should throw when moeda has less than 3 characters",
                        SMILES_ID,
                        "Smiles",
                        StatusPrograma.ATIVO,
                        "BR",
                        ConfigArredondamento.DEFAULT,
                        IllegalArgumentException.class,
                        "moeda deve ter exatamente 3 caracteres (codigo ISO)"
                )),
                Arguments.of(new CriacaoInvalidaScenario(
                        "should throw when moeda has more than 3 characters",
                        SMILES_ID,
                        "Smiles",
                        StatusPrograma.ATIVO,
                        "BRLX",
                        ConfigArredondamento.DEFAULT,
                        IllegalArgumentException.class,
                        "moeda deve ter exatamente 3 caracteres (codigo ISO)"
                )),
                Arguments.of(new CriacaoInvalidaScenario(
                        "should throw when regrasArredondamento is null",
                        SMILES_ID,
                        "Smiles",
                        StatusPrograma.ATIVO,
                        "BRL",
                        null,
                        NullPointerException.class,
                        "regrasArredondamento eh obrigatorio"
                ))
        );
    }

    public static Stream<Arguments> factoryCriar() {
        return Stream.of(
                Arguments.of(new FactoryCriarScenario(
                        "should create programa Smiles with defaults",
                        SMILES_ID,
                        "Smiles",
                        StatusPrograma.ATIVO,
                        "BRL"
                )),
                Arguments.of(new FactoryCriarScenario(
                        "should create programa LATAM Pass with defaults",
                        LATAM_ID,
                        "LATAM Pass",
                        StatusPrograma.ATIVO,
                        "BRL"
                )),
                Arguments.of(new FactoryCriarScenario(
                        "should create programa Azul Fidelidade with defaults",
                        AZUL_ID,
                        "Azul Fidelidade",
                        StatusPrograma.ATIVO,
                        "BRL"
                ))
        );
    }

    public static Stream<Arguments> factoryCriarComMoeda() {
        return Stream.of(
                Arguments.of(new FactoryCriarComMoedaScenario(
                        "should create programa with USD currency",
                        SMILES_ID,
                        "Smiles",
                        "USD",
                        StatusPrograma.ATIVO
                )),
                Arguments.of(new FactoryCriarComMoedaScenario(
                        "should create programa with EUR currency",
                        LATAM_ID,
                        "Flying Blue",
                        "EUR",
                        StatusPrograma.ATIVO
                )),
                Arguments.of(new FactoryCriarComMoedaScenario(
                        "should create programa with GBP currency",
                        AZUL_ID,
                        "British Airways",
                        "GBP",
                        StatusPrograma.ATIVO
                ))
        );
    }

    public static Stream<Arguments> isAtivo() {
        return Stream.of(
                Arguments.of(new IsAtivoScenario(
                        "should return true when status is ATIVO",
                        ProgramaDeMilhas.criar(SMILES_ID, "Smiles"),
                        true
                )),
                Arguments.of(new IsAtivoScenario(
                        "should return false when status is INATIVO",
                        ProgramaDeMilhas.criar(LATAM_ID, "LATAM Pass").desativar(),
                        false
                ))
        );
    }

    public static Stream<Arguments> desativar() {
        return Stream.of(
                Arguments.of(new DesativarScenario(
                        "should return INATIVO when desativating active programa",
                        ProgramaDeMilhas.criar(SMILES_ID, "Smiles"),
                        StatusPrograma.INATIVO
                )),
                Arguments.of(new DesativarScenario(
                        "should return INATIVO when desativating already inactive programa",
                        ProgramaDeMilhas.criar(LATAM_ID, "LATAM Pass").desativar(),
                        StatusPrograma.INATIVO
                ))
        );
    }

    public static Stream<Arguments> ativar() {
        return Stream.of(
                Arguments.of(new AtivarScenario(
                        "should return ATIVO when activating inactive programa",
                        ProgramaDeMilhas.criar(SMILES_ID, "Smiles").desativar(),
                        StatusPrograma.ATIVO
                )),
                Arguments.of(new AtivarScenario(
                        "should return ATIVO when activating already active programa",
                        ProgramaDeMilhas.criar(LATAM_ID, "LATAM Pass"),
                        StatusPrograma.ATIVO
                ))
        );
    }

    public static Stream<Arguments> comRegrasArredondamento() {
        return Stream.of(
                Arguments.of(new ComRegrasArredondamentoScenario(
                        "should update to 2 decimal places with CEILING",
                        ProgramaDeMilhas.criar(SMILES_ID, "Smiles"),
                        new ConfigArredondamento(2, RoundingMode.CEILING)
                )),
                Arguments.of(new ComRegrasArredondamentoScenario(
                        "should update to 6 decimal places with FLOOR",
                        ProgramaDeMilhas.criar(LATAM_ID, "LATAM Pass"),
                        new ConfigArredondamento(6, RoundingMode.FLOOR)
                )),
                Arguments.of(new ComRegrasArredondamentoScenario(
                        "should update to 0 decimal places with DOWN",
                        ProgramaDeMilhas.criar(AZUL_ID, "Azul"),
                        new ConfigArredondamento(0, RoundingMode.DOWN)
                ))
        );
    }
}
