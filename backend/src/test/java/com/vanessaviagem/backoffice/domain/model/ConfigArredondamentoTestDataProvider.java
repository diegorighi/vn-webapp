package com.vanessaviagem.backoffice.domain.model;

import com.vanessaviagem.backoffice.domain.model.ConfigArredondamentoScenario.ComCasasDecimaisScenario;
import com.vanessaviagem.backoffice.domain.model.ConfigArredondamentoScenario.CriacaoInvalidaScenario;
import com.vanessaviagem.backoffice.domain.model.ConfigArredondamentoScenario.CriacaoValidaScenario;
import com.vanessaviagem.backoffice.domain.model.ConfigArredondamentoScenario.DefaultConstantScenario;
import java.math.RoundingMode;
import java.util.stream.Stream;
import org.junit.jupiter.params.provider.Arguments;

/**
 * Test data provider for ConfigArredondamento tests.
 */
public final class ConfigArredondamentoTestDataProvider {

    private ConfigArredondamentoTestDataProvider() {
    }

    public static Stream<Arguments> criacaoValida() {
        return Stream.of(
                Arguments.of(new CriacaoValidaScenario(
                        "should create config with 0 decimal places and HALF_UP",
                        0,
                        RoundingMode.HALF_UP
                )),
                Arguments.of(new CriacaoValidaScenario(
                        "should create config with 2 decimal places and CEILING",
                        2,
                        RoundingMode.CEILING
                )),
                Arguments.of(new CriacaoValidaScenario(
                        "should create config with 4 decimal places and FLOOR",
                        4,
                        RoundingMode.FLOOR
                )),
                Arguments.of(new CriacaoValidaScenario(
                        "should create config with 6 decimal places and DOWN",
                        6,
                        RoundingMode.DOWN
                )),
                Arguments.of(new CriacaoValidaScenario(
                        "should create config with 1 decimal place and UP",
                        1,
                        RoundingMode.UP
                )),
                Arguments.of(new CriacaoValidaScenario(
                        "should create config with 3 decimal places and HALF_DOWN",
                        3,
                        RoundingMode.HALF_DOWN
                )),
                Arguments.of(new CriacaoValidaScenario(
                        "should create config with 5 decimal places and HALF_EVEN",
                        5,
                        RoundingMode.HALF_EVEN
                ))
        );
    }

    public static Stream<Arguments> criacaoInvalida() {
        return Stream.of(
                Arguments.of(new CriacaoInvalidaScenario(
                        "should throw when casasDecimais is negative",
                        -1,
                        RoundingMode.HALF_UP,
                        IllegalArgumentException.class,
                        "casasDecimais deve estar entre 0 e 6"
                )),
                Arguments.of(new CriacaoInvalidaScenario(
                        "should throw when casasDecimais is greater than 6",
                        7,
                        RoundingMode.HALF_UP,
                        IllegalArgumentException.class,
                        "casasDecimais deve estar entre 0 e 6"
                )),
                Arguments.of(new CriacaoInvalidaScenario(
                        "should throw when casasDecimais is 10",
                        10,
                        RoundingMode.CEILING,
                        IllegalArgumentException.class,
                        "casasDecimais deve estar entre 0 e 6"
                )),
                Arguments.of(new CriacaoInvalidaScenario(
                        "should throw when modoArredondamento is null",
                        4,
                        null,
                        NullPointerException.class,
                        "modoArredondamento eh obrigatorio"
                )),
                Arguments.of(new CriacaoInvalidaScenario(
                        "should throw when casasDecimais is -100",
                        -100,
                        RoundingMode.FLOOR,
                        IllegalArgumentException.class,
                        "casasDecimais deve estar entre 0 e 6"
                ))
        );
    }

    public static Stream<Arguments> comCasasDecimais() {
        return Stream.of(
                Arguments.of(new ComCasasDecimaisScenario(
                        "should create config with 0 decimal places using factory",
                        0,
                        RoundingMode.HALF_UP
                )),
                Arguments.of(new ComCasasDecimaisScenario(
                        "should create config with 2 decimal places using factory",
                        2,
                        RoundingMode.HALF_UP
                )),
                Arguments.of(new ComCasasDecimaisScenario(
                        "should create config with 4 decimal places using factory",
                        4,
                        RoundingMode.HALF_UP
                )),
                Arguments.of(new ComCasasDecimaisScenario(
                        "should create config with 6 decimal places using factory",
                        6,
                        RoundingMode.HALF_UP
                ))
        );
    }

    public static Stream<Arguments> defaultConstant() {
        return Stream.of(
                Arguments.of(new DefaultConstantScenario(
                        "should have default with 4 decimal places and HALF_UP",
                        4,
                        RoundingMode.HALF_UP
                ))
        );
    }
}
