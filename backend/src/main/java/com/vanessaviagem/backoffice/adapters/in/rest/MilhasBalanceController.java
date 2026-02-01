package com.vanessaviagem.backoffice.adapters.in.rest;

import com.vanessaviagem.backoffice.application.ports.out.MilhasBalancePort;
import com.vanessaviagem.backoffice.application.services.MilhasBalanceService;
import com.vanessaviagem.backoffice.application.services.TwoFactorAuthService;
import com.vanessaviagem.backoffice.domain.model.SaldoMilhasExterno;
import com.vanessaviagem.backoffice.domain.model.enums.TipoProgramaMilhas;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Controller REST para consulta de saldo de milhas.
 */
@RestController
@RequestMapping("/api/v1/milhas")
public class MilhasBalanceController {

    private static final Logger log = LoggerFactory.getLogger(MilhasBalanceController.class);

    private final MilhasBalanceService milhasBalanceService;
    private final TwoFactorAuthService twoFactorAuthService;

    public MilhasBalanceController(
            MilhasBalanceService milhasBalanceService,
            TwoFactorAuthService twoFactorAuthService
    ) {
        this.milhasBalanceService = milhasBalanceService;
        this.twoFactorAuthService = twoFactorAuthService;
    }

    /**
     * Consulta o saldo de milhas de uma credencial específica.
     */
    @GetMapping("/saldo/{credencialId}")
    public ResponseEntity<?> consultarSaldo(@PathVariable UUID credencialId) {
        try {
            SaldoMilhasExterno saldo = milhasBalanceService.consultarSaldo(credencialId);
            return ResponseEntity.ok(SaldoResponse.from(saldo));

        } catch (MilhasBalancePort.MilhasBalanceException e) {
            if (e.getMessage().contains("2FA")) {
                return ResponseEntity.status(428)
                        .body(Map.of(
                                "error", "2FA_REQUIRED",
                                "message", e.getMessage(),
                                "programa", e.getPrograma().name()
                        ));
            }

            log.error("Erro ao consultar saldo: {}", e.getMessage());
            return ResponseEntity.status(e.isRecuperavel() ? 503 : 400)
                    .body(Map.of(
                            "error", "BALANCE_ERROR",
                            "message", e.getMessage(),
                            "recoverable", e.isRecuperavel()
                    ));
        }
    }

    /**
     * Consulta saldos de todas as credenciais de um cliente.
     */
    @GetMapping("/saldo/cliente/{clienteId}")
    public ResponseEntity<List<SaldoResponse>> consultarSaldosPorCliente(@PathVariable UUID clienteId) {
        List<SaldoMilhasExterno> saldos = milhasBalanceService.consultarSaldosPorCliente(clienteId);
        List<SaldoResponse> response = saldos.stream()
                .map(SaldoResponse::from)
                .toList();
        return ResponseEntity.ok(response);
    }

    /**
     * Lista challenges de 2FA pendentes.
     */
    @GetMapping("/2fa/pendentes")
    public ResponseEntity<List<ChallengeResponse>> listarChallengesPendentes() {
        List<TwoFactorAuthService.PendingChallenge> challenges =
                twoFactorAuthService.listarChallengesPendentes();

        List<ChallengeResponse> response = challenges.stream()
                .map(ChallengeResponse::from)
                .toList();

        return ResponseEntity.ok(response);
    }

    /**
     * Submete código OTP para um challenge de 2FA.
     */
    @PostMapping("/2fa/{challengeKey}/submit")
    public ResponseEntity<?> submeterCodigo2FA(
            @PathVariable String challengeKey,
            @RequestBody SubmitCodeRequest request
    ) {
        boolean accepted = twoFactorAuthService.submeterCodigo(challengeKey, request.code());

        if (accepted) {
            return ResponseEntity.ok(Map.of(
                    "status", "ACCEPTED",
                    "message", "Código recebido. Processando autenticação..."
            ));
        } else {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "CHALLENGE_NOT_FOUND",
                    "message", "Challenge não encontrado ou expirado"
            ));
        }
    }

    /**
     * Cancela um challenge de 2FA.
     */
    @DeleteMapping("/2fa/{challengeKey}")
    public ResponseEntity<?> cancelarChallenge(@PathVariable String challengeKey) {
        twoFactorAuthService.removerChallenge(challengeKey);
        return ResponseEntity.ok(Map.of(
                "status", "CANCELLED",
                "message", "Challenge cancelado"
        ));
    }

    // DTOs

    public record SaldoResponse(
            UUID id,
            TipoProgramaMilhas programa,
            long saldoMilhas,
            long milhasAExpirar,
            String dataExpiracao,
            String nivelFidelidade,
            String fonte,
            String consultadoEm,
            int tempoRespostaMs
    ) {
        public static SaldoResponse from(SaldoMilhasExterno saldo) {
            return new SaldoResponse(
                    saldo.id(),
                    saldo.programa(),
                    saldo.saldoMilhas(),
                    saldo.milhasAExpirar(),
                    saldo.dataExpiracao() != null ? saldo.dataExpiracao().toString() : null,
                    saldo.nivelFidelidade(),
                    saldo.fonte().name(),
                    saldo.consultadoEm().toString(),
                    saldo.tempoRespostaMs()
            );
        }
    }

    public record ChallengeResponse(
            String challengeKey,
            TipoProgramaMilhas programa,
            String tipo,
            String destinoMascarado,
            long segundosRestantes
    ) {
        public static ChallengeResponse from(TwoFactorAuthService.PendingChallenge challenge) {
            return new ChallengeResponse(
                    challenge.challengeKey(),
                    challenge.programa(),
                    challenge.type().name(),
                    challenge.maskedDestination(),
                    challenge.segundosRestantes()
            );
        }
    }

    public record SubmitCodeRequest(String code) {}
}
