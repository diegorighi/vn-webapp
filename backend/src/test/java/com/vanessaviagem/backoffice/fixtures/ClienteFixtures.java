package com.vanessaviagem.backoffice.fixtures;

import com.vanessaviagem.backoffice.domain.model.Contato;
import com.vanessaviagem.backoffice.domain.model.Documento;
import com.vanessaviagem.backoffice.domain.model.Endereco;
import com.vanessaviagem.backoffice.domain.model.Pessoa;
import com.vanessaviagem.backoffice.domain.model.Viagem;
import com.vanessaviagem.backoffice.domain.model.cliente.ClienteDependente;
import com.vanessaviagem.backoffice.domain.model.cliente.ClienteTitular;
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

/**
 * Centralized fixtures for Cliente domain tests.
 * All test data is defined here and reused across test classes.
 *
 * <p>Naming convention:
 * <ul>
 *   <li>UUIDs: [ENTITY]_[VARIANT]_ID (e.g., TITULAR_JOAO_ID)</li>
 *   <li>Factories: [entity][Variant]() (e.g., pessoaJoao(), titularAtivoSemDependentes())</li>
 * </ul>
 */
public final class ClienteFixtures {

    private ClienteFixtures() {
    }

    // ========== FIXED UUIDs ==========

    // Titulares
    public static final UUID TITULAR_JOAO_ID = UUID.fromString("550e8400-e29b-41d4-a716-446655440001");
    public static final UUID TITULAR_MARIA_ID = UUID.fromString("550e8400-e29b-41d4-a716-446655440002");
    public static final UUID TITULAR_CARLOS_ID = UUID.fromString("550e8400-e29b-41d4-a716-446655440003");

    // Pessoas
    public static final UUID PESSOA_JOAO_ID = UUID.fromString("550e8400-e29b-41d4-a716-446655440010");
    public static final UUID PESSOA_MARIA_ID = UUID.fromString("550e8400-e29b-41d4-a716-446655440011");
    public static final UUID PESSOA_ANA_ID = UUID.fromString("550e8400-e29b-41d4-a716-446655440012");
    public static final UUID PESSOA_PEDRO_ID = UUID.fromString("550e8400-e29b-41d4-a716-446655440013");
    public static final UUID PESSOA_JULIA_ID = UUID.fromString("550e8400-e29b-41d4-a716-446655440014");
    public static final UUID PESSOA_CARLOS_ID = UUID.fromString("550e8400-e29b-41d4-a716-446655440015");

    // Dependentes
    public static final UUID DEPENDENTE_ANA_ID = UUID.fromString("550e8400-e29b-41d4-a716-446655440020");
    public static final UUID DEPENDENTE_PEDRO_ID = UUID.fromString("550e8400-e29b-41d4-a716-446655440021");
    public static final UUID DEPENDENTE_JULIA_ID = UUID.fromString("550e8400-e29b-41d4-a716-446655440022");

    // Viagens
    public static final UUID VIAGEM_EUROPA_ID = UUID.fromString("550e8400-e29b-41d4-a716-446655440030");
    public static final UUID VIAGEM_USA_ID = UUID.fromString("550e8400-e29b-41d4-a716-446655440031");
    public static final UUID VIAGEM_DOMESTICA_ID = UUID.fromString("550e8400-e29b-41d4-a716-446655440032");

    // Non-existent (for error scenarios)
    public static final UUID NON_EXISTENT_ID = UUID.fromString("550e8400-e29b-41d4-a716-446655440099");

    // ========== DOCUMENTOS ==========

    public static Documento documentoCpf(String numero) {
        return new Documento(TipoDocumento.CPF, numero, null, LocalDate.of(2020, 1, 1));
    }

    public static Documento documentoRg(String numero) {
        return new Documento(TipoDocumento.RG, numero, null, LocalDate.of(2020, 1, 1));
    }

    public static Documento documentoPassaporte(String numero) {
        return new Documento(TipoDocumento.PASSAPORTE, numero, LocalDate.of(2030, 1, 1), LocalDate.of(2020, 1, 1));
    }

    // ========== ENDERECOS ==========

    public static Endereco enderecoSaoPaulo() {
        return new Endereco(true, "Av. Paulista", 1000, "Bela Vista", "01310-100", "Sao Paulo", "SP", "Brasil");
    }

    public static Endereco enderecoRioDeJaneiro() {
        return new Endereco(true, "Av. Atlantica", 500, "Copacabana", "22070-000", "Rio de Janeiro", "RJ", "Brasil");
    }

    public static Endereco enderecoCampinas() {
        return new Endereco(true, "Rua Barao de Jaguara", 200, "Centro", "13015-000", "Campinas", "SP", "Brasil");
    }

    // ========== CONTATOS ==========

    public static Contato contatoEmail(String email) {
        return new Contato(true, TipoContato.EMAIL, email);
    }

    public static Contato contatoCelular(String numero) {
        return new Contato(false, TipoContato.CELULAR, numero);
    }

    public static Contato contatoWhatsapp(String numero) {
        return new Contato(false, TipoContato.WHATSAPP, numero);
    }

    // ========== PESSOAS ==========

    public static Pessoa pessoaJoao() {
        return new Pessoa(
                PESSOA_JOAO_ID,
                "Joao",
                "Carlos",
                "Silva",
                LocalDate.of(1985, 3, 20),
                Sexo.MASCULINO,
                List.of(documentoCpf("123.456.789-00")),
                List.of(enderecoSaoPaulo()),
                List.of(contatoEmail("joao@example.com")),
                LocalDate.of(2024, 1, 1),
                LocalDate.of(2024, 1, 1)
        );
    }

    public static Pessoa pessoaMaria() {
        return new Pessoa(
                PESSOA_MARIA_ID,
                "Maria",
                null,
                "Santos",
                LocalDate.of(1990, 6, 15),
                Sexo.FEMININO,
                List.of(documentoCpf("987.654.321-00")),
                List.of(enderecoRioDeJaneiro()),
                List.of(contatoEmail("maria@example.com"), contatoCelular("+55 21 99999-8888")),
                LocalDate.of(2024, 1, 1),
                LocalDate.of(2024, 1, 1)
        );
    }

    public static Pessoa pessoaCarlos() {
        return new Pessoa(
                PESSOA_CARLOS_ID,
                "Carlos",
                "Eduardo",
                "Pereira",
                LocalDate.of(1980, 12, 5),
                Sexo.MASCULINO,
                List.of(documentoCpf("111.222.333-44"), documentoPassaporte("BR123456")),
                List.of(enderecoCampinas()),
                List.of(contatoEmail("carlos@example.com")),
                LocalDate.of(2024, 1, 1),
                LocalDate.of(2024, 1, 1)
        );
    }

    public static Pessoa pessoaAna() {
        return new Pessoa(
                PESSOA_ANA_ID,
                "Ana",
                null,
                "Silva",
                LocalDate.of(1987, 5, 15),
                Sexo.FEMININO,
                List.of(documentoCpf("222.333.444-55")),
                List.of(enderecoSaoPaulo()),
                List.of(contatoEmail("ana@example.com")),
                LocalDate.of(2024, 1, 1),
                LocalDate.of(2024, 1, 1)
        );
    }

    public static Pessoa pessoaPedro() {
        return new Pessoa(
                PESSOA_PEDRO_ID,
                "Pedro",
                null,
                "Silva",
                LocalDate.of(2015, 8, 10),
                Sexo.MASCULINO,
                List.of(documentoRg("12.345.678-9")),
                List.of(enderecoSaoPaulo()),
                List.of(contatoCelular("+55 11 99999-7777")),
                LocalDate.of(2024, 1, 1),
                LocalDate.of(2024, 1, 1)
        );
    }

    public static Pessoa pessoaJulia() {
        return new Pessoa(
                PESSOA_JULIA_ID,
                "Julia",
                null,
                "Silva",
                LocalDate.of(2018, 3, 22),
                Sexo.FEMININO,
                List.of(documentoRg("98.765.432-1")),
                List.of(enderecoSaoPaulo()),
                List.of(contatoCelular("+55 11 99999-6666")),
                LocalDate.of(2024, 1, 1),
                LocalDate.of(2024, 1, 1)
        );
    }

    // ========== VIAGENS ==========

    public static Viagem viagemEuropaEmitida() {
        return new Viagem(
                VIAGEM_EUROPA_ID,
                "EUR123",
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

    public static Viagem viagemUsaEmitida() {
        return new Viagem(
                VIAGEM_USA_ID,
                "USA456",
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

    public static Viagem viagemDomesticaEmitida() {
        return new Viagem(
                VIAGEM_DOMESTICA_ID,
                "DOM789",
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

    // ========== DEPENDENTES ==========

    public static ClienteDependente dependenteAnaConjuge() {
        return new ClienteDependente(
                DEPENDENTE_ANA_ID,
                TITULAR_JOAO_ID,
                Parentesco.CONJUGE,
                pessoaAna(),
                Collections.emptyList(),
                true
        );
    }

    public static ClienteDependente dependentePedroFilho() {
        return new ClienteDependente(
                DEPENDENTE_PEDRO_ID,
                TITULAR_JOAO_ID,
                Parentesco.FILHO,
                pessoaPedro(),
                Collections.emptyList(),
                true
        );
    }

    public static ClienteDependente dependenteJuliaFilha() {
        return new ClienteDependente(
                DEPENDENTE_JULIA_ID,
                TITULAR_JOAO_ID,
                Parentesco.FILHA,
                pessoaJulia(),
                Collections.emptyList(),
                true
        );
    }

    public static ClienteDependente dependenteInativo(ClienteDependente original) {
        return original.comStatus(false);
    }

    // ========== TITULARES ==========

    public static ClienteTitular titularJoaoAtivoSemDependentes() {
        return new ClienteTitular(
                TITULAR_JOAO_ID,
                pessoaJoao(),
                Collections.emptyList(),
                true,
                Collections.emptyList()
        );
    }

    public static ClienteTitular titularJoaoAtivoComViagem() {
        return new ClienteTitular(
                TITULAR_JOAO_ID,
                pessoaJoao(),
                List.of(viagemEuropaEmitida()),
                true,
                Collections.emptyList()
        );
    }

    public static ClienteTitular titularJoaoAtivoComFamilia() {
        return new ClienteTitular(
                TITULAR_JOAO_ID,
                pessoaJoao(),
                List.of(viagemEuropaEmitida()),
                true,
                List.of(dependenteAnaConjuge(), dependentePedroFilho(), dependenteJuliaFilha())
        );
    }

    public static ClienteTitular titularJoaoInativo() {
        return new ClienteTitular(
                TITULAR_JOAO_ID,
                pessoaJoao(),
                Collections.emptyList(),
                false,
                Collections.emptyList()
        );
    }

    public static ClienteTitular titularMariaAtiva() {
        return new ClienteTitular(
                TITULAR_MARIA_ID,
                pessoaMaria(),
                List.of(viagemUsaEmitida()),
                true,
                Collections.emptyList()
        );
    }

    public static ClienteTitular titularCarlosAtivo() {
        return new ClienteTitular(
                TITULAR_CARLOS_ID,
                pessoaCarlos(),
                List.of(viagemDomesticaEmitida()),
                true,
                Collections.emptyList()
        );
    }
}
