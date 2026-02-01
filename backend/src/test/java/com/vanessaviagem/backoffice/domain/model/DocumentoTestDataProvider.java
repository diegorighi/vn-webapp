package com.vanessaviagem.backoffice.domain.model;

import com.vanessaviagem.backoffice.domain.model.enums.TipoDocumento;
import java.time.LocalDate;
import java.util.stream.Stream;
import org.junit.jupiter.params.provider.Arguments;

public final class DocumentoTestDataProvider {

    private DocumentoTestDataProvider() {
    }

    public static Stream<Arguments> criacaoValida() {
        return Stream.of(
                Arguments.of(new DocumentoScenario(
                        "should create documento CPF with validade",
                        TipoDocumento.CPF,
                        "123.456.789-00",
                        LocalDate.of(2030, 12, 31),
                        LocalDate.of(2020, 1, 15),
                        null,
                        null
                )),
                Arguments.of(new DocumentoScenario(
                        "should create documento RG without validade",
                        TipoDocumento.RG,
                        "12.345.678-9",
                        null,
                        LocalDate.of(2015, 6, 10),
                        null,
                        null
                )),
                Arguments.of(new DocumentoScenario(
                        "should create documento PASSAPORTE with validade",
                        TipoDocumento.PASSAPORTE,
                        "AB123456",
                        LocalDate.of(2028, 5, 20),
                        LocalDate.of(2023, 5, 20),
                        null,
                        null
                )),
                Arguments.of(new DocumentoScenario(
                        "should create documento CNH with validade",
                        TipoDocumento.CNH,
                        "04567890123",
                        LocalDate.of(2027, 8, 15),
                        LocalDate.of(2022, 8, 15),
                        null,
                        null
                ))
        );
    }

    public static Stream<Arguments> validacaoNulos() {
        return Stream.of(
                Arguments.of(new DocumentoScenario(
                        "should throw when tipo is null",
                        null,
                        "123.456.789-00",
                        LocalDate.of(2030, 12, 31),
                        LocalDate.of(2020, 1, 15),
                        NullPointerException.class,
                        "tipo eh obrigatorio"
                )),
                Arguments.of(new DocumentoScenario(
                        "should throw when numero is null",
                        TipoDocumento.CPF,
                        null,
                        LocalDate.of(2030, 12, 31),
                        LocalDate.of(2020, 1, 15),
                        NullPointerException.class,
                        "numero eh obrigatorio"
                )),
                Arguments.of(new DocumentoScenario(
                        "should throw when emitidoEm is null",
                        TipoDocumento.CPF,
                        "123.456.789-00",
                        LocalDate.of(2030, 12, 31),
                        null,
                        NullPointerException.class,
                        "emitidoEm eh obrigatorio"
                ))
        );
    }
}
