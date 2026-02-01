package com.vanessaviagem.backoffice.application.services;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.vanessaviagem.backoffice.adapters.out.external.AuthenticationResult;
import com.vanessaviagem.backoffice.domain.model.TenantContext;
import com.vanessaviagem.backoffice.domain.model.enums.TipoProgramaMilhas;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * Serviço para gerenciar autenticação de dois fatores (2FA).
 *
 * <p>Fluxo:</p>
 * <ol>
 *   <li>Quando 2FA é requerido, um challenge é criado e armazenado</li>
 *   <li>O usuário é notificado que precisa inserir o código</li>
 *   <li>O usuário submete o código via API</li>
 *   <li>O código é validado e a autenticação completa</li>
 * </ol>
 */
@Service
public class TwoFactorAuthService {

    private static final Logger log = LoggerFactory.getLogger(TwoFactorAuthService.class);

    private final Cache<String, PendingChallenge> pendingChallenges;
    private final Cache<String, CompletableFuture<String>> waitingForCode;

    public TwoFactorAuthService() {
        this.pendingChallenges = Caffeine.newBuilder()
                .expireAfterWrite(Duration.ofMinutes(10))
                .maximumSize(1000)
                .build();

        this.waitingForCode = Caffeine.newBuilder()
                .expireAfterWrite(Duration.ofMinutes(10))
                .maximumSize(1000)
                .build();
    }

    /**
     * Registra um challenge de 2FA pendente.
     *
     * @param credencialId ID da credencial
     * @param programa     Programa de milhas
     * @param requires2FA  Resultado da autenticação que requer 2FA
     * @return ID do challenge para referência
     */
    public String registrarChallenge(
            UUID credencialId,
            TipoProgramaMilhas programa,
            AuthenticationResult.Requires2FA requires2FA
    ) {
        TenantContext ctx = TenantContext.current();

        String challengeKey = generateChallengeKey(ctx.tenantId(), credencialId);

        PendingChallenge challenge = new PendingChallenge(
                challengeKey,
                ctx.tenantId(),
                credencialId,
                programa,
                requires2FA.challengeId(),
                requires2FA.type(),
                requires2FA.maskedDestination(),
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(10)
        );

        pendingChallenges.put(challengeKey, challenge);

        log.info("2FA challenge registrado: {} para {} ({})",
                challengeKey, programa, requires2FA.maskedDestination());

        return challengeKey;
    }

    /**
     * Obtém um challenge pendente.
     *
     * @param challengeKey Chave do challenge
     * @return O challenge se existir e não expirado
     */
    public Optional<PendingChallenge> obterChallenge(String challengeKey) {
        PendingChallenge challenge = pendingChallenges.getIfPresent(challengeKey);
        if (challenge == null || challenge.isExpirado()) {
            return Optional.empty();
        }
        return Optional.of(challenge);
    }

    /**
     * Submete o código OTP para um challenge.
     *
     * @param challengeKey Chave do challenge
     * @param otpCode      Código OTP inserido pelo usuário
     * @return true se o código foi aceito para processamento
     */
    public boolean submeterCodigo(String challengeKey, String otpCode) {
        PendingChallenge challenge = pendingChallenges.getIfPresent(challengeKey);
        if (challenge == null || challenge.isExpirado()) {
            log.warn("Challenge não encontrado ou expirado: {}", challengeKey);
            return false;
        }

        // Notificar qualquer processo esperando pelo código
        CompletableFuture<String> future = waitingForCode.getIfPresent(challengeKey);
        if (future != null) {
            future.complete(otpCode);
            log.debug("Código OTP entregue para challenge: {}", challengeKey);
        }

        return true;
    }

    /**
     * Aguarda o usuário inserir o código OTP.
     *
     * @param challengeKey Chave do challenge
     * @param timeoutSeconds Tempo máximo de espera em segundos
     * @return O código OTP quando inserido
     */
    public Optional<String> aguardarCodigo(String challengeKey, int timeoutSeconds) {
        CompletableFuture<String> future = new CompletableFuture<>();
        waitingForCode.put(challengeKey, future);

        try {
            String code = future.get(timeoutSeconds, TimeUnit.SECONDS);
            return Optional.ofNullable(code);
        } catch (Exception e) {
            log.debug("Timeout aguardando código OTP para: {}", challengeKey);
            return Optional.empty();
        } finally {
            waitingForCode.invalidate(challengeKey);
        }
    }

    /**
     * Remove um challenge (após sucesso ou cancelamento).
     */
    public void removerChallenge(String challengeKey) {
        pendingChallenges.invalidate(challengeKey);
        waitingForCode.invalidate(challengeKey);
    }

    /**
     * Lista challenges pendentes para o tenant atual.
     */
    public java.util.List<PendingChallenge> listarChallengesPendentes() {
        TenantContext ctx = TenantContext.current();
        return pendingChallenges.asMap().values().stream()
                .filter(c -> c.tenantId().equals(ctx.tenantId()))
                .filter(c -> !c.isExpirado())
                .toList();
    }

    private String generateChallengeKey(UUID tenantId, UUID credencialId) {
        return tenantId.toString().substring(0, 8) + "-" +
                credencialId.toString().substring(0, 8) + "-" +
                System.currentTimeMillis();
    }

    /**
     * Representa um challenge de 2FA pendente.
     */
    public record PendingChallenge(
            String challengeKey,
            UUID tenantId,
            UUID credencialId,
            TipoProgramaMilhas programa,
            String externalChallengeId,
            AuthenticationResult.Requires2FA.TwoFactorType type,
            String maskedDestination,
            LocalDateTime criadoEm,
            LocalDateTime expiraEm
    ) {
        public boolean isExpirado() {
            return LocalDateTime.now().isAfter(expiraEm);
        }

        public long segundosRestantes() {
            return java.time.Duration.between(LocalDateTime.now(), expiraEm).getSeconds();
        }
    }
}
