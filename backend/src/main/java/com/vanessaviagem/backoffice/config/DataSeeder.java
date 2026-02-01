package com.vanessaviagem.backoffice.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Seed data for development environment only.
 *
 * IMPORTANTE: Este componente é apenas para DEV com H2 in-memory.
 * Os dados de seed principais vêm das migrations H2 (db/migration/h2/).
 * Este seeder serve como fallback caso as migrations não tenham seed data.
 *
 * Para HML/PROD com PostgreSQL, usar migrations ou scripts separados.
 */
@Component
@Profile("dev")
public class DataSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

    private static final UUID DEV_TENANT_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");

    // Programas de Milhas (IDs fixos para facilitar testes)
    private static final UUID SMILES_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");
    private static final UUID LATAM_ID = UUID.fromString("22222222-2222-2222-2222-222222222222");
    private static final UUID LIVELO_ID = UUID.fromString("33333333-3333-3333-3333-333333333333");
    private static final UUID AZUL_ID = UUID.fromString("44444444-4444-4444-4444-444444444444");
    private static final UUID ESFERA_ID = UUID.fromString("55555555-5555-5555-5555-555555555555");

    private final JdbcTemplate jdbc;

    public DataSeeder(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public void run(String... args) {
        log.info("=".repeat(60));
        log.info("INICIANDO SEED DE DADOS - AMBIENTE DEV/HML");
        log.info("=".repeat(60));

        if (hasSeededData()) {
            log.info("Dados de seed ja existem (via migrations H2). DataSeeder pulando.");
            return;
        }

        // Verifica se o tenant de dev existe (criado pelas migrations)
        if (!tenantExists()) {
            log.warn("DEV_TENANT_ID nao existe. Migrations H2 nao foram executadas corretamente?");
            return;
        }

        seedContasPrograma();
        seedTransacoes();

        log.info("=".repeat(60));
        log.info("SEED CONCLUÍDO COM SUCESSO");
        log.info("=".repeat(60));

        printSummary();
    }

    private boolean hasSeededData() {
        Integer count = jdbc.queryForObject(
            "SELECT COUNT(*) FROM conta_programa WHERE tenant_id = ?",
            Integer.class,
            DEV_TENANT_ID
        );
        return count != null && count > 0;
    }

    private boolean tenantExists() {
        Integer count = jdbc.queryForObject(
            "SELECT COUNT(*) FROM account WHERE id = ?",
            Integer.class,
            DEV_TENANT_ID
        );
        return count != null && count > 0;
    }

    private void seedContasPrograma() {
        log.info("Criando contas de programa...");

        // Diego - Smiles (10.000 milhas, custo R$ 350)
        insertContaPrograma(SMILES_ID, "Smiles", "Diego", 10000, new BigDecimal("350.00"));

        // Diego - LATAM Pass (25.000 milhas, custo R$ 750)
        insertContaPrograma(LATAM_ID, "LATAM Pass", "Diego", 25000, new BigDecimal("750.00"));

        // Diego - Livelo (50.000 pontos, custo R$ 1.200)
        insertContaPrograma(LIVELO_ID, "Livelo", "Diego", 50000, new BigDecimal("1200.00"));

        // Vanessa - Smiles (15.000 milhas, custo R$ 450)
        insertContaPrograma(
            UUID.fromString("11111111-1111-1111-1111-111111111112"),
            "Smiles", "Vanessa", 15000, new BigDecimal("450.00")
        );

        // Vanessa - Azul Fidelidade (30.000 pontos, custo R$ 900)
        insertContaPrograma(AZUL_ID, "Azul Fidelidade", "Vanessa", 30000, new BigDecimal("900.00"));

        // Pai - Esfera (20.000 pontos, custo R$ 0 - bonus cartão)
        insertContaPrograma(ESFERA_ID, "Esfera", "Pai", 20000, BigDecimal.ZERO);

        // Mãe - LATAM Pass (8.000 milhas, custo R$ 280)
        insertContaPrograma(
            UUID.fromString("22222222-2222-2222-2222-222222222223"),
            "LATAM Pass", "Mae", 8000, new BigDecimal("280.00")
        );

        log.info("7 contas de programa criadas");
    }

    private void seedTransacoes() {
        log.info("Criando transações...");

        LocalDateTime now = LocalDateTime.now();

        // Diego - Smiles: Compra inicial
        insertTransacao(SMILES_ID, "COMPRA", 10000, new BigDecimal("350.00"),
            "Compra direta Smiles", "Promoção Black Friday", now.minusDays(30));

        // Diego - LATAM Pass: Compra + Bonus
        insertTransacao(LATAM_ID, "COMPRA", 20000, new BigDecimal("600.00"),
            "Compra direta LATAM", null, now.minusDays(25));
        insertTransacao(LATAM_ID, "BONUS", 5000, BigDecimal.ZERO,
            "Bonus Cartão Itaú", "Cashback mensal", now.minusDays(20));

        // Diego - Livelo: Múltiplas compras
        insertTransacao(LIVELO_ID, "COMPRA", 30000, new BigDecimal("720.00"),
            "Compra Livelo", "R$ 24/milheiro", now.minusDays(45));
        insertTransacao(LIVELO_ID, "BONUS", 10000, BigDecimal.ZERO,
            "Bonus clube Livelo", null, now.minusDays(15));
        insertTransacao(LIVELO_ID, "COMPRA", 10000, new BigDecimal("480.00"),
            "Compra Livelo", "Promoção especial", now.minusDays(5));

        // Vanessa - Smiles
        insertTransacao(
            UUID.fromString("11111111-1111-1111-1111-111111111112"),
            "COMPRA", 15000, new BigDecimal("450.00"),
            "Compra direta Smiles", null, now.minusDays(10)
        );

        // Vanessa - Azul
        insertTransacao(AZUL_ID, "COMPRA", 20000, new BigDecimal("600.00"),
            "Compra TudoAzul", null, now.minusDays(35));
        insertTransacao(AZUL_ID, "BONUS", 10000, BigDecimal.ZERO,
            "Bonus cartão Azul", "Promoção aniversário", now.minusDays(8));

        // Pai - Esfera (só bonus do cartão)
        insertTransacao(ESFERA_ID, "BONUS", 5000, BigDecimal.ZERO,
            "Bonus Santander", "Gastos mensais", now.minusDays(60));
        insertTransacao(ESFERA_ID, "BONUS", 5000, BigDecimal.ZERO,
            "Bonus Santander", "Gastos mensais", now.minusDays(30));
        insertTransacao(ESFERA_ID, "BONUS", 10000, BigDecimal.ZERO,
            "Bonus Santander", "Promoção 2x pontos", now.minusDays(1));

        // Mãe - LATAM Pass
        insertTransacao(
            UUID.fromString("22222222-2222-2222-2222-222222222223"),
            "COMPRA", 8000, new BigDecimal("280.00"),
            "Compra direta LATAM", null, now.minusDays(15)
        );

        log.info("13 transações criadas");
    }

    private void insertContaPrograma(UUID programaId, String programaNome, String owner,
                                      long saldoMilhas, BigDecimal custoBase) {
        UUID id = UUID.randomUUID();
        BigDecimal custoMedio = saldoMilhas > 0
            ? custoBase.multiply(new BigDecimal("1000")).divide(new BigDecimal(saldoMilhas), 6, java.math.RoundingMode.HALF_UP)
            : BigDecimal.ZERO;

        LocalDateTime now = LocalDateTime.now();

        jdbc.update("""
            INSERT INTO conta_programa
            (id, tenant_id, programa_id, programa_nome, owner, saldo_milhas,
             custo_base_total_brl, custo_medio_milheiro_atual, criado_em, atualizado_em)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """,
            id, DEV_TENANT_ID, programaId, programaNome, owner, saldoMilhas,
            custoBase, custoMedio, now, now
        );
    }

    private void insertTransacao(UUID contaProgramaId, String tipo, long milhas,
                                  BigDecimal valor, String fonte, String observacao,
                                  LocalDateTime data) {
        // Buscar o ID da conta pelo programa_id
        UUID contaId = jdbc.queryForObject(
            "SELECT id FROM conta_programa WHERE programa_id = ? AND tenant_id = ?",
            UUID.class,
            contaProgramaId, DEV_TENANT_ID
        );

        jdbc.update("""
            INSERT INTO transacao
            (id, conta_programa_id, tipo, milhas, valor_brl, fonte, observacao, data, criado_em)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
            """,
            UUID.randomUUID(), contaId, tipo, milhas, valor, fonte, observacao, data, LocalDateTime.now()
        );
    }

    private void printSummary() {
        log.info("");
        log.info("RESUMO DOS DADOS DE SEED:");
        log.info("-".repeat(60));

        jdbc.query("""
            SELECT owner, COUNT(*) as contas, SUM(saldo_milhas) as total_milhas,
                   SUM(custo_base_total_brl) as total_investido
            FROM conta_programa
            WHERE tenant_id = ?
            GROUP BY owner
            ORDER BY owner
            """,
            rs -> {
                while (rs.next()) {
                    log.info("  {} -> {} contas, {} milhas, R$ {} investido",
                        rs.getString("owner"),
                        rs.getInt("contas"),
                        String.format("%,d", rs.getLong("total_milhas")),
                        rs.getBigDecimal("total_investido")
                    );
                }
            },
            DEV_TENANT_ID
        );

        Long totalMilhas = jdbc.queryForObject(
            "SELECT SUM(saldo_milhas) FROM conta_programa WHERE tenant_id = ?",
            Long.class, DEV_TENANT_ID
        );
        BigDecimal totalInvestido = jdbc.queryForObject(
            "SELECT SUM(custo_base_total_brl) FROM conta_programa WHERE tenant_id = ?",
            BigDecimal.class, DEV_TENANT_ID
        );

        log.info("-".repeat(60));
        log.info("  TOTAL: {} milhas | R$ {} investido",
            String.format("%,d", totalMilhas), totalInvestido);
        log.info("");
    }
}
