package com.vanessaviagem.backoffice.application.services;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.vanessaviagem.backoffice.application.ports.out.TenantKeyRepository;
import com.vanessaviagem.backoffice.domain.exceptions.CriptografiaException;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.time.Duration;
import java.util.UUID;

/**
 * Serviço de criptografia por tenant usando envelope encryption.
 *
 * <p>Hierarquia de chaves:</p>
 * <ul>
 *   <li>KEK (Key Encryption Key) - Armazenada no KMS (AWS KMS, HashiCorp Vault)</li>
 *   <li>DEK (Data Encryption Key) - Uma por tenant, criptografada com KEK</li>
 * </ul>
 *
 * <p>Vantagens:</p>
 * <ul>
 *   <li>Vazamento de DEK afeta apenas um tenant</li>
 *   <li>Rotação de chaves sem re-criptografar todos os dados</li>
 *   <li>Revogação de tenant = deletar DEK</li>
 * </ul>
 */
@Service
public class TenantCryptoService {

    private static final String ALGORITHM = "AES/GCM/NoPadding";
    private static final int GCM_IV_LENGTH = 12;
    private static final int GCM_TAG_LENGTH = 128;
    private static final int AES_KEY_SIZE = 256;

    private final TenantKeyRepository keyRepository;
    private final KmsService kmsService;
    private final Cache<UUID, SecretKey> dekCache;
    private final SecureRandom secureRandom;

    public TenantCryptoService(TenantKeyRepository keyRepository, KmsService kmsService) {
        this.keyRepository = keyRepository;
        this.kmsService = kmsService;
        this.dekCache = Caffeine.newBuilder()
                .expireAfterAccess(Duration.ofMinutes(5))
                .maximumSize(1000)
                .build();
        this.secureRandom = new SecureRandom();
    }

    /**
     * Obtém a DEK (Data Encryption Key) do tenant.
     * A DEK é cacheada em memória por 5 minutos.
     *
     * @param tenantId ID do tenant
     * @return A DEK do tenant
     * @throws CriptografiaException Se a DEK não existir ou não puder ser descriptografada
     */
    public SecretKey getDekForTenant(UUID tenantId) {
        return dekCache.get(tenantId, this::loadAndDecryptDek);
    }

    private SecretKey loadAndDecryptDek(UUID tenantId) {
        byte[] encryptedDek = keyRepository.buscarDekCriptografada(tenantId)
                .orElseThrow(() -> new CriptografiaException(
                        "DEK não encontrada para tenant: " + tenantId));

        try {
            byte[] dekBytes = kmsService.decrypt(encryptedDek);
            return new SecretKeySpec(dekBytes, "AES");
        } catch (Exception e) {
            throw new CriptografiaException("Erro ao descriptografar DEK do tenant: " + tenantId, e);
        }
    }

    /**
     * Criptografa dados sensíveis usando a DEK do tenant.
     *
     * @param tenantId  ID do tenant
     * @param plaintext Texto em claro a ser criptografado
     * @return Dados criptografados (IV + ciphertext)
     */
    public byte[] encrypt(UUID tenantId, String plaintext) {
        if (plaintext == null || plaintext.isEmpty()) {
            return null;
        }

        try {
            SecretKey dek = getDekForTenant(tenantId);

            Cipher cipher = Cipher.getInstance(ALGORITHM);
            byte[] iv = generateSecureIv();
            cipher.init(Cipher.ENCRYPT_MODE, dek, new GCMParameterSpec(GCM_TAG_LENGTH, iv));

            byte[] ciphertext = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));

            return ByteBuffer.allocate(iv.length + ciphertext.length)
                    .put(iv)
                    .put(ciphertext)
                    .array();
        } catch (Exception e) {
            throw new CriptografiaException("Erro ao criptografar dados", e);
        }
    }

    /**
     * Descriptografa dados sensíveis usando a DEK do tenant.
     *
     * @param tenantId      ID do tenant
     * @param encryptedData Dados criptografados (IV + ciphertext)
     * @return Texto em claro
     */
    public String decrypt(UUID tenantId, byte[] encryptedData) {
        if (encryptedData == null || encryptedData.length == 0) {
            return null;
        }

        try {
            SecretKey dek = getDekForTenant(tenantId);

            ByteBuffer buffer = ByteBuffer.wrap(encryptedData);
            byte[] iv = new byte[GCM_IV_LENGTH];
            buffer.get(iv);
            byte[] ciphertext = new byte[buffer.remaining()];
            buffer.get(ciphertext);

            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, dek, new GCMParameterSpec(GCM_TAG_LENGTH, iv));

            return new String(cipher.doFinal(ciphertext), StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new CriptografiaException("Erro ao descriptografar dados", e);
        }
    }

    /**
     * Gera e salva uma nova DEK para um tenant.
     *
     * @param tenantId ID do tenant
     */
    public void gerarDekParaTenant(UUID tenantId) {
        try {
            KeyGenerator keyGen = KeyGenerator.getInstance("AES");
            keyGen.init(AES_KEY_SIZE, secureRandom);
            SecretKey dek = keyGen.generateKey();

            byte[] encryptedDek = kmsService.encrypt(dek.getEncoded());

            keyRepository.salvarDek(tenantId, encryptedDek);

            dekCache.put(tenantId, dek);
        } catch (Exception e) {
            throw new CriptografiaException("Erro ao gerar DEK para tenant: " + tenantId, e);
        }
    }

    /**
     * Rotaciona a DEK de um tenant.
     * Gera nova DEK e invalida o cache.
     *
     * @param tenantId ID do tenant
     * @param versao   Nova versão da DEK
     */
    public void rotacionarDek(UUID tenantId, int versao) {
        try {
            KeyGenerator keyGen = KeyGenerator.getInstance("AES");
            keyGen.init(AES_KEY_SIZE, secureRandom);
            SecretKey newDek = keyGen.generateKey();

            byte[] encryptedDek = kmsService.encrypt(newDek.getEncoded());

            keyRepository.rotacionarDek(tenantId, encryptedDek, versao);

            dekCache.invalidate(tenantId);
        } catch (Exception e) {
            throw new CriptografiaException("Erro ao rotacionar DEK do tenant: " + tenantId, e);
        }
    }

    /**
     * Invalida o cache de DEK para um tenant.
     * Útil após rotação de chaves.
     *
     * @param tenantId ID do tenant
     */
    public void invalidarCache(UUID tenantId) {
        dekCache.invalidate(tenantId);
    }

    private byte[] generateSecureIv() {
        byte[] iv = new byte[GCM_IV_LENGTH];
        secureRandom.nextBytes(iv);
        return iv;
    }

    /**
     * Interface para integração com KMS (AWS KMS, HashiCorp Vault, etc.).
     */
    public interface KmsService {
        byte[] encrypt(byte[] plaintext);

        byte[] decrypt(byte[] ciphertext);
    }
}
