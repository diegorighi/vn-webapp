package com.vanessaviagem.backoffice.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vanessaviagem.backoffice.adapters.out.external.AuthenticationResult;
import com.vanessaviagem.backoffice.adapters.out.external.SessionData;
import com.vanessaviagem.backoffice.adapters.out.external.smiles.SmilesBalanceAdapter;
import com.vanessaviagem.backoffice.adapters.out.external.latam.LatamPassBalanceAdapter;
import com.vanessaviagem.backoffice.application.ports.out.MilhasBalancePort.ConsultaSaldoRequest;
import com.vanessaviagem.backoffice.application.ports.out.SessionRepository;
import com.vanessaviagem.backoffice.domain.model.SaldoMilhasExterno;
import com.vanessaviagem.backoffice.domain.model.TenantContext;
import com.vanessaviagem.backoffice.domain.model.enums.TipoProgramaMilhas;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

/**
 * Teste de integração manual para adapters de milhas.
 *
 * <p>IMPORTANTE: Este teste usa credenciais reais e deve ser executado
 * apenas em ambiente de desenvolvimento controlado.</p>
 *
 * <p>Executar via: ./gradlew test --tests MilhasIntegrationTest -i</p>
 */
public class MilhasIntegrationTest {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    // Mock SessionRepository para testes
    private static final SessionRepository mockSessionRepo = new SessionRepository() {
        private SessionData currentSession;

        @Override
        public SessionData salvar(SessionData session) {
            this.currentSession = session;
            System.out.println("[Session] Salvando sessão para " + session.programa());
            return session;
        }

        @Override
        public Optional<SessionData> buscarPorCredencial(UUID credencialId) {
            if (currentSession != null && !currentSession.isExpirada()) {
                return Optional.of(currentSession);
            }
            return Optional.empty();
        }

        @Override
        public Optional<SessionData> buscarPorClienteEPrograma(UUID clienteId, TipoProgramaMilhas programa) {
            return Optional.empty();
        }

        @Override
        public void atualizar(SessionData session) {
            this.currentSession = session;
        }

        @Override
        public void remover(UUID sessionId) {
            this.currentSession = null;
        }

        @Override
        public int removerExpiradas() {
            return 0;
        }
    };

    public static void main(String[] args) {
        if (args.length < 3) {
            System.out.println("Uso: MilhasIntegrationTest <programa> <cpf> <senha>");
            System.out.println("Programas: SMILES, LATAM, AZUL");
            System.out.println();
            System.out.println("Exemplo:");
            System.out.println("  java MilhasIntegrationTest SMILES 35330301807 1234");
            return;
        }

        String programa = args[0].toUpperCase();
        String cpf = args[1];
        String senha = args[2];

        // Configurar contexto de tenant
        UUID tenantId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID clienteId = UUID.randomUUID();
        UUID credencialId = UUID.randomUUID();

        TenantContext.set(tenantId, userId, Set.of("READ", "WRITE"));

        try {
            switch (programa) {
                case "SMILES" -> testarSmiles(cpf, senha, tenantId, clienteId, credencialId);
                case "LATAM" -> testarLatam(cpf, senha, tenantId, clienteId, credencialId);
                case "AZUL" -> testarAzul(cpf, senha, tenantId, clienteId, credencialId);
                default -> System.out.println("Programa desconhecido: " + programa);
            }
        } finally {
            TenantContext.clear();
        }
    }

    private static void testarSmiles(String cpf, String senha, UUID tenantId, UUID clienteId, UUID credencialId) {
        System.out.println("=".repeat(60));
        System.out.println("Testando SMILES");
        System.out.println("CPF: " + cpf);
        System.out.println("=".repeat(60));

        SmilesBalanceAdapter adapter = new SmilesBalanceAdapter(objectMapper, mockSessionRepo);

        try {
            ConsultaSaldoRequest request = ConsultaSaldoRequest.comSenha(
                    tenantId, clienteId, credencialId,
                    TipoProgramaMilhas.SMILES,
                    cpf, senha
            );

            SaldoMilhasExterno saldo = adapter.consultarSaldo(request);

            System.out.println();
            System.out.println("SUCESSO!");
            System.out.println("-".repeat(40));
            System.out.println("Saldo: " + String.format("%,d", saldo.saldoMilhas()) + " milhas");
            System.out.println("Nivel: " + saldo.nivelFidelidade());
            System.out.println("Tempo: " + saldo.tempoRespostaMs() + "ms");
            System.out.println("Fonte: " + saldo.fonte());

        } catch (Exception e) {
            System.out.println();
            System.out.println("ERRO: " + e.getMessage());

            if (e.getMessage().contains("2FA")) {
                System.out.println();
                System.out.println("2FA NECESSARIO!");
                System.out.println("Verifique seu celular/email para o codigo de verificacao.");
            }
        }
    }

    private static void testarLatam(String cpf, String senha, UUID tenantId, UUID clienteId, UUID credencialId) {
        System.out.println("=".repeat(60));
        System.out.println("Testando LATAM PASS");
        System.out.println("CPF: " + cpf);
        System.out.println("=".repeat(60));

        LatamPassBalanceAdapter adapter = new LatamPassBalanceAdapter(objectMapper, mockSessionRepo);

        try {
            ConsultaSaldoRequest request = ConsultaSaldoRequest.comSenha(
                    tenantId, clienteId, credencialId,
                    TipoProgramaMilhas.LATAM_PASS,
                    cpf, senha
            );

            SaldoMilhasExterno saldo = adapter.consultarSaldo(request);

            System.out.println();
            System.out.println("SUCESSO!");
            System.out.println("-".repeat(40));
            System.out.println("Saldo: " + String.format("%,d", saldo.saldoMilhas()) + " milhas");
            System.out.println("Milhas a expirar: " + String.format("%,d", saldo.milhasAExpirar()));
            if (saldo.dataExpiracao() != null) {
                System.out.println("Data expiracao: " + saldo.dataExpiracao());
            }
            System.out.println("Nivel: " + saldo.nivelFidelidade());
            System.out.println("Tempo: " + saldo.tempoRespostaMs() + "ms");
            System.out.println("Fonte: " + saldo.fonte());

        } catch (Exception e) {
            System.out.println();
            System.out.println("ERRO: " + e.getMessage());

            if (e.getMessage().contains("2FA")) {
                System.out.println();
                System.out.println("2FA NECESSARIO!");
                System.out.println("Verifique seu email para o codigo de verificacao.");
            }
        }
    }

    private static void testarAzul(String cpf, String senha, UUID tenantId, UUID clienteId, UUID credencialId) {
        System.out.println("=".repeat(60));
        System.out.println("Testando AZUL FIDELIDADE");
        System.out.println("CPF: " + cpf);
        System.out.println("=".repeat(60));

        System.out.println();
        System.out.println("NOTA: Azul usa Playwright (browser automation).");
        System.out.println("Certifique-se de ter o Playwright instalado:");
        System.out.println("  mvn exec:java -e -D exec.mainClass=com.microsoft.playwright.CLI -D exec.args=\"install chromium\"");
        System.out.println();

        // Azul requer Playwright instalado
        // Para teste real, descomente o código abaixo:
        /*
        AzulFidelidadeBalanceAdapter adapter = new AzulFidelidadeBalanceAdapter(objectMapper, mockSessionRepo);

        try {
            ConsultaSaldoRequest request = ConsultaSaldoRequest.comSenha(
                    tenantId, clienteId, credencialId,
                    TipoProgramaMilhas.AZUL_FIDELIDADE,
                    cpf, senha
            );

            SaldoMilhasExterno saldo = adapter.consultarSaldo(request);

            System.out.println();
            System.out.println("SUCESSO!");
            System.out.println("-".repeat(40));
            System.out.println("Saldo: " + String.format("%,d", saldo.saldoMilhas()) + " pontos");
            System.out.println("Nivel: " + saldo.nivelFidelidade());
            System.out.println("Tempo: " + saldo.tempoRespostaMs() + "ms");
            System.out.println("Fonte: " + saldo.fonte());

        } catch (Exception e) {
            System.out.println();
            System.out.println("ERRO: " + e.getMessage());
        }
        */

        System.out.println("Teste Azul desabilitado - requer Playwright instalado.");
    }
}
