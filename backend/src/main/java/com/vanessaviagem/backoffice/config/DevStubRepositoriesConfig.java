package com.vanessaviagem.backoffice.config;

import com.vanessaviagem.backoffice.application.ports.out.*;
import com.vanessaviagem.backoffice.application.services.TenantCryptoService.KmsService;
import com.vanessaviagem.backoffice.domain.model.*;
import com.vanessaviagem.backoffice.domain.model.Tenant;
import com.vanessaviagem.backoffice.domain.model.cliente.ClienteDependente;
import com.vanessaviagem.backoffice.domain.model.cliente.ClienteTitular;
import com.vanessaviagem.backoffice.domain.model.enums.TipoProgramaMilhas;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.time.LocalDateTime;
import java.util.*;

/**
 * Stub implementations for repositories not yet implemented.
 * Only active in 'dev' profile to allow application startup.
 */
@Configuration
@Profile("dev")
public class DevStubRepositoriesConfig {

    private static final Logger log = LoggerFactory.getLogger(DevStubRepositoriesConfig.class);

    @Bean
    @ConditionalOnMissingBean(ClienteRepository.class)
    public ClienteRepository stubClienteRepository() {
        log.warn("Using STUB ClienteRepository - data will be stored in memory only");
        return new StubClienteRepository();
    }

    @Bean
    @ConditionalOnMissingBean(ViagemRepository.class)
    public ViagemRepository stubViagemRepository() {
        log.warn("Using STUB ViagemRepository - data will be stored in memory only");
        return new StubViagemRepository();
    }

    @Bean
    @ConditionalOnMissingBean(MilhasRepository.class)
    public MilhasRepository stubMilhasRepository() {
        log.warn("Using STUB MilhasRepository - data will be stored in memory only");
        return new StubMilhasRepository();
    }

    @Bean
    @ConditionalOnMissingBean(ProgramaMilhasRepository.class)
    public ProgramaMilhasRepository stubProgramaMilhasRepository() {
        log.warn("Using STUB ProgramaMilhasRepository - data will be stored in memory only");
        return new StubProgramaMilhasRepository();
    }

    @Bean
    @ConditionalOnMissingBean(CredencialRepository.class)
    public CredencialRepository stubCredencialRepository() {
        log.warn("Using STUB CredencialRepository");
        return new StubCredencialRepository();
    }

    @Bean
    @ConditionalOnMissingBean(AuditLogRepository.class)
    public AuditLogRepository stubAuditLogRepository() {
        log.warn("Using STUB AuditLogRepository - audit logs will only be written to console");
        return new StubAuditLogRepository();
    }

    @Bean
    @ConditionalOnMissingBean(TenantKeyRepository.class)
    public TenantKeyRepository stubTenantKeyRepository() {
        log.warn("Using STUB TenantKeyRepository");
        return new StubTenantKeyRepository();
    }

    @Bean
    @ConditionalOnMissingBean(KmsService.class)
    public KmsService stubKmsService() {
        log.warn("Using STUB KmsService - NO ENCRYPTION WILL BE APPLIED");
        return new StubKmsService();
    }

    @Bean
    @ConditionalOnMissingBean(MilhasBalancePort.class)
    public MilhasBalancePort stubMilhasBalancePort() {
        log.warn("Using STUB MilhasBalancePort - balance queries will return empty");
        return new StubMilhasBalancePort();
    }

    @Bean
    @ConditionalOnMissingBean(TenantRepository.class)
    public TenantRepository stubTenantRepository() {
        log.warn("Using STUB TenantRepository");
        return new StubTenantRepository();
    }

    // ==================== Stub Implementations ====================

    private static class StubClienteRepository implements ClienteRepository {
        @Override
        public ClienteTitular salvarTitular(ClienteTitular titular) { return titular; }
        @Override
        public ClienteDependente salvarDependente(ClienteDependente dependente) { return dependente; }
        @Override
        public Optional<ClienteTitular> buscarTitular(UUID clienteId) { return Optional.empty(); }
        @Override
        public List<ClienteTitular> buscarTodosTitulares() { return List.of(); }
        @Override
        public ClienteTitular atualizarTitular(ClienteTitular titular) { return titular; }
        @Override
        public void excluirTitular(UUID clienteId) {}
        @Override
        public Optional<ClienteDependente> buscarDependente(UUID dependenteId) { return Optional.empty(); }
        @Override
        public List<ClienteDependente> buscarDependentesPorTitular(UUID titularId) { return List.of(); }
        @Override
        public ClienteDependente atualizarDependente(ClienteDependente dependente) { return dependente; }
        @Override
        public void excluirDependente(UUID dependenteId) {}
    }

    private static class StubViagemRepository implements ViagemRepository {
        @Override
        public Viagem salvar(UUID tenantId, UUID clienteTitularId, Viagem viagem) { return viagem; }
        @Override
        public Optional<Viagem> buscarPorId(UUID tenantId, UUID viagemId) { return Optional.empty(); }
        @Override
        public List<Viagem> buscarPorTitular(UUID tenantId, UUID titularId) { return List.of(); }
        @Override
        public List<Viagem> buscarTodos(UUID tenantId) { return List.of(); }
        @Override
        public Viagem atualizar(UUID tenantId, Viagem viagem) { return viagem; }
        @Override
        public void excluir(UUID tenantId, UUID viagemId) {}
    }

    private static class StubMilhasRepository implements MilhasRepository {
        @Override
        public Milhas salvar(UUID tenantId, UUID clienteId, Milhas milhas) { return milhas; }
        @Override
        public Optional<Milhas> buscarPorId(UUID tenantId, UUID milhasId) { return Optional.empty(); }
        @Override
        public List<Milhas> buscarPorCliente(UUID tenantId, UUID clienteId) { return List.of(); }
        @Override
        public List<Milhas> buscarPorPrograma(UUID tenantId, TipoProgramaMilhas programa) { return List.of(); }
        @Override
        public Milhas atualizar(UUID tenantId, Milhas milhas) { return milhas; }
        @Override
        public void excluir(UUID tenantId, UUID milhasId) {}
        @Override
        public boolean existePorId(UUID tenantId, UUID milhasId) { return false; }
    }

    private static class StubProgramaMilhasRepository implements ProgramaMilhasRepository {
        @Override
        public ProgramaDeMilhas salvar(ProgramaDeMilhas programa) { return programa; }
        @Override
        public Optional<ProgramaDeMilhas> buscarPorId(UUID id) { return Optional.empty(); }
        @Override
        public Optional<ProgramaDeMilhas> buscarPorBrand(String brand) { return Optional.empty(); }
        @Override
        public List<ProgramaDeMilhas> buscarTodos() { return List.of(); }
        @Override
        public List<ProgramaDeMilhas> buscarAtivos() { return List.of(); }
        @Override
        public ProgramaDeMilhas atualizar(ProgramaDeMilhas programa) { return programa; }
        @Override
        public void excluir(UUID id) {}
        @Override
        public boolean existePorBrand(String brand) { return false; }
    }

    private static class StubCredencialRepository implements CredencialRepository {
        @Override
        public CredencialPrograma salvar(CredencialPrograma credencial) { return credencial; }
        @Override
        public Optional<CredencialPrograma> buscarPorId(UUID id) { return Optional.empty(); }
        @Override
        public List<CredencialPrograma> buscarPorCliente(UUID clienteId) { return List.of(); }
        @Override
        public Optional<CredencialPrograma> buscarPorClienteEPrograma(UUID clienteId, TipoProgramaMilhas programa) { return Optional.empty(); }
        @Override
        public void atualizar(CredencialPrograma credencial) {}
        @Override
        public void excluir(UUID id) {}
        @Override
        public boolean existeParaClienteEPrograma(UUID clienteId, TipoProgramaMilhas programa) { return false; }
        @Override
        public List<CredencialPrograma> listarAtivas() { return List.of(); }
    }

    private static class StubAuditLogRepository implements AuditLogRepository {
        private static final Logger auditLog = LoggerFactory.getLogger("AUDIT");

        @Override
        public void salvar(AuditLog log) {
            String status = log.sucesso() ? "SUCCESS" : "FAILURE";
            auditLog.info("[{}] {} {} {} - tenant={} user={}",
                    status, log.acao(), log.recurso(), log.recursoId(),
                    log.tenantId(), log.userId());
        }

        @Override
        public List<AuditLog> buscarPorTenantEPeriodo(UUID tenantId, LocalDateTime inicio, LocalDateTime fim) {
            return List.of();
        }

        @Override
        public List<AuditLog> buscarPorRecurso(UUID tenantId, String recurso, UUID recursoId) {
            return List.of();
        }

        @Override
        public List<AuditLog> buscarPorAcao(UUID tenantId, String acao, LocalDateTime inicio, LocalDateTime fim) {
            return List.of();
        }

        @Override
        public List<AuditLog> buscarAcessosNaoAutorizados(UUID tenantId, LocalDateTime inicio, LocalDateTime fim) {
            return List.of();
        }

        @Override
        public long contarPorAcao(UUID tenantId, String acao, LocalDateTime inicio, LocalDateTime fim) {
            return 0L;
        }
    }

    private static class StubTenantKeyRepository implements TenantKeyRepository {
        private final Map<UUID, byte[]> keys = new HashMap<>();

        @Override
        public Optional<byte[]> buscarDekCriptografada(UUID tenantId) {
            return Optional.ofNullable(keys.get(tenantId));
        }

        @Override
        public void salvarDek(UUID tenantId, byte[] encryptedDek) {
            keys.put(tenantId, encryptedDek);
        }

        @Override
        public void rotacionarDek(UUID tenantId, byte[] encryptedDek, int versao) {
            keys.put(tenantId, encryptedDek);
        }

        @Override
        public boolean existeDek(UUID tenantId) {
            return keys.containsKey(tenantId);
        }

        @Override
        public void removerDek(UUID tenantId) {
            keys.remove(tenantId);
        }
    }

    private static class StubKmsService implements KmsService {
        @Override
        public byte[] encrypt(byte[] plaintext) {
            return plaintext;
        }

        @Override
        public byte[] decrypt(byte[] ciphertext) {
            return ciphertext;
        }
    }

    private static class StubMilhasBalancePort implements MilhasBalancePort {
        @Override
        public SaldoMilhasExterno consultarSaldo(ConsultaSaldoRequest request) {
            throw new MilhasBalanceException(request.programa(), "Balance query not available in dev mode", false);
        }

        @Override
        public boolean suportaPrograma(TipoProgramaMilhas programa) {
            return false;
        }
    }

    private static class StubTenantRepository implements TenantRepository {
        private static final UUID DEV_TENANT_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");

        @Override
        public Tenant salvar(Tenant tenant) { return tenant; }
        @Override
        public Optional<Tenant> buscarPorId(UUID tenantId) { return Optional.empty(); }
        @Override
        public Optional<Tenant> buscarPorCnpj(String cnpj) { return Optional.empty(); }
        @Override
        public void atualizar(Tenant tenant) {}
        @Override
        public boolean existeEAtivo(UUID tenantId) {
            return DEV_TENANT_ID.equals(tenantId);
        }
        @Override
        public boolean existePorCnpj(String cnpj) { return false; }
    }
}
