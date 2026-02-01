package com.vanessaviagem.backoffice.config;

import com.vanessaviagem.backoffice.application.services.TenantCryptoService;
import com.vanessaviagem.backoffice.domain.exceptions.CriptografiaException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Configuração do serviço de KMS (Key Management Service).
 *
 * <p>Suporta múltiplos backends:</p>
 * <ul>
 *   <li>AWS KMS (produção)</li>
 *   <li>HashiCorp Vault (produção)</li>
 *   <li>Local (desenvolvimento - NÃO usar em produção)</li>
 * </ul>
 */
@Configuration
public class KmsConfig {

    private static final Logger log = LoggerFactory.getLogger(KmsConfig.class);

    /**
     * Bean de KmsService para desenvolvimento local.
     * USA uma chave mestre local (KEK) - NÃO USAR EM PRODUÇÃO!
     */
    @Bean
    @ConditionalOnProperty(name = "kms.provider", havingValue = "local", matchIfMissing = true)
    @ConditionalOnMissingBean(TenantCryptoService.KmsService.class)
    public TenantCryptoService.KmsService localKmsService(KmsProperties properties) {
        log.warn("Usando KMS LOCAL - NAO USAR EM PRODUCAO!");
        return new LocalKmsService(properties.getLocalMasterKey());
    }

    /**
     * Implementação local de KMS para desenvolvimento.
     * Criptografa DEKs usando uma chave mestre local.
     */
    static class LocalKmsService implements TenantCryptoService.KmsService {

        private static final String ALGORITHM = "AES/GCM/NoPadding";
        private static final int GCM_IV_LENGTH = 12;
        private static final int GCM_TAG_LENGTH = 128;

        private final SecretKey masterKey;
        private final SecureRandom secureRandom;

        LocalKmsService(String masterKeyBase64) {
            if (masterKeyBase64 == null || masterKeyBase64.isBlank()) {
                log.warn("Master key não configurada - gerando chave temporária");
                byte[] keyBytes = new byte[32];
                new SecureRandom().nextBytes(keyBytes);
                this.masterKey = new SecretKeySpec(keyBytes, "AES");
            } else {
                byte[] keyBytes = Base64.getDecoder().decode(masterKeyBase64);
                this.masterKey = new SecretKeySpec(keyBytes, "AES");
            }
            this.secureRandom = new SecureRandom();
        }

        @Override
        public byte[] encrypt(byte[] plaintext) {
            try {
                Cipher cipher = Cipher.getInstance(ALGORITHM);
                byte[] iv = new byte[GCM_IV_LENGTH];
                secureRandom.nextBytes(iv);
                cipher.init(Cipher.ENCRYPT_MODE, masterKey, new GCMParameterSpec(GCM_TAG_LENGTH, iv));

                byte[] ciphertext = cipher.doFinal(plaintext);

                return ByteBuffer.allocate(iv.length + ciphertext.length)
                        .put(iv)
                        .put(ciphertext)
                        .array();
            } catch (Exception e) {
                throw new CriptografiaException("Erro ao criptografar com KEK", e);
            }
        }

        @Override
        public byte[] decrypt(byte[] ciphertext) {
            try {
                ByteBuffer buffer = ByteBuffer.wrap(ciphertext);
                byte[] iv = new byte[GCM_IV_LENGTH];
                buffer.get(iv);
                byte[] encryptedData = new byte[buffer.remaining()];
                buffer.get(encryptedData);

                Cipher cipher = Cipher.getInstance(ALGORITHM);
                cipher.init(Cipher.DECRYPT_MODE, masterKey, new GCMParameterSpec(GCM_TAG_LENGTH, iv));

                return cipher.doFinal(encryptedData);
            } catch (Exception e) {
                throw new CriptografiaException("Erro ao descriptografar com KEK", e);
            }
        }
    }

    @Component
    @ConfigurationProperties(prefix = "kms")
    public static class KmsProperties {

        /**
         * Provider do KMS: local, aws, vault
         */
        private String provider = "local";

        /**
         * Chave mestre local (Base64) - apenas para desenvolvimento
         */
        private String localMasterKey;

        /**
         * ARN da chave mestre no AWS KMS
         */
        private String awsKeyArn;

        /**
         * Path da chave no HashiCorp Vault
         */
        private String vaultKeyPath;

        public String getProvider() {
            return provider;
        }

        public void setProvider(String provider) {
            this.provider = provider;
        }

        public String getLocalMasterKey() {
            return localMasterKey;
        }

        public void setLocalMasterKey(String localMasterKey) {
            this.localMasterKey = localMasterKey;
        }

        public String getAwsKeyArn() {
            return awsKeyArn;
        }

        public void setAwsKeyArn(String awsKeyArn) {
            this.awsKeyArn = awsKeyArn;
        }

        public String getVaultKeyPath() {
            return vaultKeyPath;
        }

        public void setVaultKeyPath(String vaultKeyPath) {
            this.vaultKeyPath = vaultKeyPath;
        }
    }
}
