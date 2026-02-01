package com.vanessaviagem.backoffice.application.services;

import com.vanessaviagem.backoffice.application.ports.out.AuditLogRepository;
import com.vanessaviagem.backoffice.domain.model.AuditLog;
import com.vanessaviagem.backoffice.domain.model.TenantContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Serviço de auditoria para rastreamento de operações sensíveis.
 *
 * <p>Características:</p>
 * <ul>
 *   <li>Operações assíncronas para não bloquear a operação principal</li>
 *   <li>Logs imutáveis e append-only</li>
 *   <li>Registra todos os acessos a credenciais</li>
 *   <li>Registra tentativas de acesso não autorizado</li>
 * </ul>
 */
@Service
public class AuditService {

    private static final Logger log = LoggerFactory.getLogger(AuditService.class);

    private final AuditLogRepository auditLogRepository;

    public AuditService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    /**
     * Registra uma operação de sucesso de forma assíncrona.
     *
     * @param acao      Tipo da ação realizada
     * @param recurso   Tipo do recurso acessado
     * @param recursoId ID do recurso
     * @param detalhes  Detalhes adicionais (não sensíveis)
     */
    @Async
    public void registrar(String acao, String recurso, UUID recursoId, Map<String, Object> detalhes) {
        try {
            TenantContext ctx = TenantContext.current();

            AuditLog auditLog = AuditLog.sucesso(
                    ctx.tenantId(),
                    ctx.userId(),
                    acao,
                    recurso,
                    recursoId,
                    sanitizarDetalhes(detalhes)
            );

            auditLogRepository.salvar(auditLog);

            log.debug("Audit log registrado: {} {} {} para tenant {}",
                    acao, recurso, recursoId, ctx.tenantId());
        } catch (Exception e) {
            log.error("Erro ao registrar audit log: {} {} {}", acao, recurso, recursoId, e);
        }
    }

    /**
     * Registra uma operação de sucesso com contexto HTTP.
     *
     * @param acao      Tipo da ação realizada
     * @param recurso   Tipo do recurso acessado
     * @param recursoId ID do recurso
     * @param ipOrigem  IP de origem da requisição
     * @param userAgent User-Agent do cliente
     * @param detalhes  Detalhes adicionais (não sensíveis)
     */
    @Async
    public void registrarComContexto(
            String acao,
            String recurso,
            UUID recursoId,
            String ipOrigem,
            String userAgent,
            Map<String, Object> detalhes
    ) {
        try {
            TenantContext ctx = TenantContext.current();

            AuditLog auditLog = AuditLog.sucessoComContexto(
                    ctx.tenantId(),
                    ctx.userId(),
                    acao,
                    recurso,
                    recursoId,
                    ipOrigem,
                    userAgent,
                    sanitizarDetalhes(detalhes)
            );

            auditLogRepository.salvar(auditLog);

            log.debug("Audit log registrado com contexto: {} {} {} de {}",
                    acao, recurso, recursoId, ipOrigem);
        } catch (Exception e) {
            log.error("Erro ao registrar audit log com contexto: {} {} {}", acao, recurso, recursoId, e);
        }
    }

    /**
     * Registra uma falha de operação.
     *
     * @param acao         Tipo da ação que falhou
     * @param recurso      Tipo do recurso
     * @param recursoId    ID do recurso
     * @param mensagemErro Mensagem de erro
     * @param detalhes     Detalhes adicionais (não sensíveis)
     */
    @Async
    public void registrarFalha(
            String acao,
            String recurso,
            UUID recursoId,
            String mensagemErro,
            Map<String, Object> detalhes
    ) {
        try {
            TenantContext ctx = TenantContext.current();

            AuditLog auditLog = AuditLog.falha(
                    ctx.tenantId(),
                    ctx.userId(),
                    acao,
                    recurso,
                    recursoId,
                    mensagemErro,
                    sanitizarDetalhes(detalhes)
            );

            auditLogRepository.salvar(auditLog);

            log.warn("Audit log de falha registrado: {} {} {} - {}",
                    acao, recurso, recursoId, mensagemErro);
        } catch (Exception e) {
            log.error("Erro ao registrar audit log de falha: {} {} {}", acao, recurso, recursoId, e);
        }
    }

    /**
     * Registra uma tentativa de acesso não autorizado.
     * SEMPRE registrado de forma síncrona para garantir registro.
     *
     * @param tenantSolicitante ID do tenant que tentou o acesso
     * @param recurso           Tipo do recurso
     * @param recursoId         ID do recurso
     * @param ipOrigem          IP de origem
     * @param detalhes          Detalhes adicionais
     */
    public void registrarAcessoNaoAutorizado(
            UUID tenantSolicitante,
            UUID userId,
            String recurso,
            UUID recursoId,
            String ipOrigem,
            Map<String, Object> detalhes
    ) {
        try {
            AuditLog auditLog = AuditLog.acessoNaoAutorizado(
                    tenantSolicitante,
                    userId,
                    recurso,
                    recursoId,
                    ipOrigem,
                    sanitizarDetalhes(detalhes)
            );

            auditLogRepository.salvar(auditLog);

            log.warn("ALERTA DE SEGURANCA: Tentativa de acesso não autorizado - tenant {} tentou acessar {} {} de {}",
                    tenantSolicitante, recurso, recursoId, ipOrigem);
        } catch (Exception e) {
            log.error("CRITICO: Erro ao registrar tentativa de acesso não autorizado: {} {} {}",
                    tenantSolicitante, recurso, recursoId, e);
        }
    }

    /**
     * Consulta logs de auditoria por período.
     *
     * @param inicio Data/hora inicial
     * @param fim    Data/hora final
     * @return Lista de logs no período
     */
    public List<AuditLog> consultarPorPeriodo(LocalDateTime inicio, LocalDateTime fim) {
        TenantContext ctx = TenantContext.current();
        return auditLogRepository.buscarPorTenantEPeriodo(ctx.tenantId(), inicio, fim);
    }

    /**
     * Consulta logs de auditoria por recurso.
     *
     * @param recurso   Tipo do recurso
     * @param recursoId ID do recurso
     * @return Lista de logs do recurso
     */
    public List<AuditLog> consultarPorRecurso(String recurso, UUID recursoId) {
        TenantContext ctx = TenantContext.current();
        return auditLogRepository.buscarPorRecurso(ctx.tenantId(), recurso, recursoId);
    }

    /**
     * Consulta tentativas de acesso não autorizado.
     *
     * @param inicio Data/hora inicial
     * @param fim    Data/hora final
     * @return Lista de tentativas
     */
    public List<AuditLog> consultarAcessosNaoAutorizados(LocalDateTime inicio, LocalDateTime fim) {
        TenantContext ctx = TenantContext.current();
        return auditLogRepository.buscarAcessosNaoAutorizados(ctx.tenantId(), inicio, fim);
    }

    /**
     * Remove dados sensíveis dos detalhes antes de salvar.
     */
    private Map<String, Object> sanitizarDetalhes(Map<String, Object> detalhes) {
        if (detalhes == null) {
            return Map.of();
        }

        return detalhes.entrySet().stream()
                .filter(e -> !isCampoSensivel(e.getKey()))
                .collect(java.util.stream.Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue
                ));
    }

    private boolean isCampoSensivel(String campo) {
        String lower = campo.toLowerCase();
        return lower.contains("senha") ||
                lower.contains("password") ||
                lower.contains("secret") ||
                lower.contains("token") ||
                lower.contains("key") ||
                lower.contains("credential");
    }
}
