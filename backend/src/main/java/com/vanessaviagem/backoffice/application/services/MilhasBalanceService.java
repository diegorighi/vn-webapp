package com.vanessaviagem.backoffice.application.services;

import com.vanessaviagem.backoffice.application.ports.out.CredencialRepository;
import com.vanessaviagem.backoffice.application.ports.out.MilhasBalancePort;
import com.vanessaviagem.backoffice.application.ports.out.MilhasBalancePort.ConsultaSaldoRequest;
import com.vanessaviagem.backoffice.domain.exceptions.AcessoNaoAutorizadoException;
import com.vanessaviagem.backoffice.domain.exceptions.CredencialNaoEncontradaException;
import com.vanessaviagem.backoffice.domain.model.AuditLog;
import com.vanessaviagem.backoffice.domain.model.CredencialPrograma;
import com.vanessaviagem.backoffice.domain.model.SaldoMilhasExterno;
import com.vanessaviagem.backoffice.domain.model.TenantContext;
import com.vanessaviagem.backoffice.domain.model.enums.TipoProgramaMilhas;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Serviço para consulta de saldo de milhas em programas externos.
 *
 * <p>Características de segurança:</p>
 * <ul>
 *   <li>Validação de tenant em todas as operações</li>
 *   <li>Descriptografia de credenciais apenas em memória</li>
 *   <li>Auditoria de todos os acessos</li>
 *   <li>Circuit breaker para resiliência</li>
 * </ul>
 */
@Service
public class MilhasBalanceService {

    private static final Logger log = LoggerFactory.getLogger(MilhasBalanceService.class);

    private final CredencialRepository credencialRepository;
    private final TenantCryptoService cryptoService;
    private final AuditService auditService;
    private final List<MilhasBalancePort> adapters;

    public MilhasBalanceService(
            CredencialRepository credencialRepository,
            TenantCryptoService cryptoService,
            AuditService auditService,
            List<MilhasBalancePort> adapters
    ) {
        this.credencialRepository = credencialRepository;
        this.cryptoService = cryptoService;
        this.auditService = auditService;
        this.adapters = adapters;
    }

    /**
     * Consulta o saldo de milhas de uma credencial específica.
     *
     * @param credencialId ID da credencial
     * @return Saldo consultado
     * @throws CredencialNaoEncontradaException Se credencial não existir
     * @throws AcessoNaoAutorizadoException     Se credencial pertencer a outro tenant
     */
    @Transactional(readOnly = true)
    public SaldoMilhasExterno consultarSaldo(UUID credencialId) {
        TenantContext ctx = TenantContext.current();
        UUID tenantId = ctx.tenantId();

        CredencialPrograma credencial = credencialRepository.buscarPorId(credencialId)
                .orElseThrow(() -> new CredencialNaoEncontradaException(credencialId));

        validarAcessoTenant(credencial, tenantId, credencialId);

        if (!credencial.isAtiva()) {
            auditService.registrarFalha(
                    AuditLog.Acoes.BALANCE_QUERY_FAILED,
                    AuditLog.Recursos.CREDENCIAL_PROGRAMA,
                    credencialId,
                    "Credencial inativa: " + credencial.status(),
                    Map.of("programa", credencial.programa().name())
            );
            throw new IllegalStateException("Credencial não está ativa: " + credencial.status());
        }

        auditService.registrar(
                AuditLog.Acoes.CREDENTIAL_DECRYPT,
                AuditLog.Recursos.CREDENCIAL_PROGRAMA,
                credencialId,
                Map.of("programa", credencial.programa().name())
        );

        ConsultaSaldoRequest request = buildConsultaRequest(credencial, tenantId);

        MilhasBalancePort adapter = findAdapter(credencial.programa());

        try {
            SaldoMilhasExterno saldo = adapter.consultarSaldo(request);

            auditService.registrar(
                    AuditLog.Acoes.BALANCE_QUERY,
                    AuditLog.Recursos.SALDO_MILHAS,
                    credencialId,
                    Map.of(
                            "programa", credencial.programa().name(),
                            "saldo", saldo.saldoMilhas(),
                            "tempoResposta", saldo.tempoRespostaMs()
                    )
            );

            credencialRepository.atualizar(credencial.registrarConsulta());

            log.info("Saldo consultado com sucesso: {} milhas em {} para cliente {}",
                    saldo.saldoMilhas(), credencial.programa(), credencial.clienteId());

            return saldo;

        } catch (MilhasBalancePort.MilhasBalanceException e) {
            auditService.registrarFalha(
                    AuditLog.Acoes.BALANCE_QUERY_FAILED,
                    AuditLog.Recursos.SALDO_MILHAS,
                    credencialId,
                    e.getMessage(),
                    Map.of(
                            "programa", credencial.programa().name(),
                            "recuperavel", e.isRecuperavel()
                    )
            );

            if (!e.isRecuperavel()) {
                credencialRepository.atualizar(credencial.marcarErroAutenticacao());
            }

            throw e;
        }
    }

    /**
     * Consulta saldos de todas as credenciais de um cliente.
     *
     * @param clienteId ID do cliente
     * @return Lista de saldos consultados
     */
    @Transactional(readOnly = true)
    public List<SaldoMilhasExterno> consultarSaldosPorCliente(UUID clienteId) {
        TenantContext ctx = TenantContext.current();

        List<CredencialPrograma> credenciais = credencialRepository.buscarPorCliente(clienteId);

        return credenciais.stream()
                .filter(CredencialPrograma::isAtiva)
                .map(cred -> {
                    try {
                        return consultarSaldo(cred.id());
                    } catch (Exception e) {
                        log.warn("Erro ao consultar saldo de {}: {}",
                                cred.programa(), e.getMessage());
                        return null;
                    }
                })
                .filter(java.util.Objects::nonNull)
                .toList();
    }

    private void validarAcessoTenant(CredencialPrograma credencial, UUID tenantId, UUID credencialId) {
        if (!credencial.tenantId().equals(tenantId)) {
            TenantContext ctx = TenantContext.current();

            auditService.registrarAcessoNaoAutorizado(
                    tenantId,
                    ctx.userId(),
                    AuditLog.Recursos.CREDENCIAL_PROGRAMA,
                    credencialId,
                    null,
                    Map.of(
                            "tenantRecurso", credencial.tenantId(),
                            "programa", credencial.programa().name()
                    )
            );

            throw new AcessoNaoAutorizadoException(
                    tenantId,
                    credencialId,
                    AuditLog.Recursos.CREDENCIAL_PROGRAMA
            );
        }
    }

    private ConsultaSaldoRequest buildConsultaRequest(CredencialPrograma credencial, UUID tenantId) {
        if (credencial.isOAuth()) {
            String accessToken = cryptoService.decrypt(tenantId, credencial.accessTokenCriptografado());
            String refreshToken = cryptoService.decrypt(tenantId, credencial.refreshTokenCriptografado());

            return ConsultaSaldoRequest.comOAuth(
                    tenantId,
                    credencial.clienteId(),
                    credencial.id(),
                    credencial.programa(),
                    accessToken,
                    refreshToken
            );
        } else {
            String usuario = cryptoService.decrypt(tenantId, credencial.usuarioCriptografado());
            String senha = cryptoService.decrypt(tenantId, credencial.senhaCriptografada());

            return ConsultaSaldoRequest.comSenha(
                    tenantId,
                    credencial.clienteId(),
                    credencial.id(),
                    credencial.programa(),
                    usuario,
                    senha
            );
        }
    }

    private MilhasBalancePort findAdapter(TipoProgramaMilhas programa) {
        return adapters.stream()
                .filter(adapter -> adapter.suportaPrograma(programa))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(
                        "Nenhum adapter disponível para programa: " + programa));
    }
}
