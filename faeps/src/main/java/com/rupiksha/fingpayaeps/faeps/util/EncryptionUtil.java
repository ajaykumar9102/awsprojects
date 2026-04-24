package com.rupiksha.fingpayaeps.faeps.util;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.Security;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPublicKey;
import java.util.Base64;

public class EncryptionUtil {

    private static final Logger log = LoggerFactory.getLogger(EncryptionUtil.class);

    private static final String PROVIDER = "BC";
    private static final String RSA_ALGO = "RSA/ECB/PKCS1Padding";
    private static final String AES_ALGO = "AES/ECB/PKCS5Padding";

    private static RSAPublicKey PUBLIC_KEY;

    static {
        try {
            Security.addProvider(new BouncyCastleProvider());

            try (InputStream is = new ClassPathResource("fingpay_public.cer").getInputStream()) {
                CertificateFactory cf = CertificateFactory.getInstance("X.509");
                X509Certificate cert = (X509Certificate) cf.generateCertificate(is);
                PUBLIC_KEY = (RSAPublicKey) cert.getPublicKey();
            }

            log.info("✅ Fingpay Public Key Loaded");

        } catch (Exception e) {
            log.error("❌ Failed to load public key", e);
            throw new RuntimeException(e);
        }
    }

    // 🔐 AES SESSION KEY (128-bit)
    public static SecretKey generateSessionKey() {
        try {
            KeyGenerator keyGen = KeyGenerator.getInstance("AES", PROVIDER);
            keyGen.init(128);
            return keyGen.generateKey();
        } catch (Exception e) {
            log.error("❌ AES key generation failed", e);
            throw new RuntimeException(e);
        }
    }

    // 🔐 HASH (SHA-256 → Base64)
    public static String generateHash(String json) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256", PROVIDER);
            byte[] hash = digest.digest(json.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            log.error("❌ Hash generation failed", e);
            throw new RuntimeException(e);
        }
    }

    // 🔐 AES ENCRYPT BODY
    public static String encryptBody(String json, SecretKey key) {
        try {
            Cipher cipher = Cipher.getInstance(AES_ALGO, PROVIDER);
            cipher.init(Cipher.ENCRYPT_MODE, key);

            byte[] encrypted = cipher.doFinal(json.getBytes(StandardCharsets.UTF_8));

            return Base64.getEncoder().encodeToString(encrypted);

        } catch (Exception e) {
            log.error("❌ AES encryption failed", e);
            throw new RuntimeException(e);
        }
    }

    // 🔐 RSA ENCRYPT (SESSION KEY → ESKEY)
    public static String generateEskey(SecretKey key) {
        try {
            Cipher cipher = Cipher.getInstance(RSA_ALGO, PROVIDER);
            cipher.init(Cipher.ENCRYPT_MODE, PUBLIC_KEY);

            byte[] encrypted = cipher.doFinal(key.getEncoded());

            return Base64.getEncoder().encodeToString(encrypted);

        } catch (Exception e) {
            log.error("❌ RSA encryption failed", e);
            throw new RuntimeException(e);
        }
    }

    // 🔥 COMPLETE FLOW HELPER
    public static EncryptionResult encryptRequest(String json) {

        SecretKey sessionKey = generateSessionKey();

        String hash = generateHash(json);
        String eskey = generateEskey(sessionKey);
        String encryptedBody = encryptBody(json, sessionKey);

        return new EncryptionResult(hash, eskey, encryptedBody);
    }

    // 🔥 RESPONSE HOLDER
    public static class EncryptionResult {
        private final String hash;
        private final String eskey;
        private final String body;

        public EncryptionResult(String hash, String eskey, String body) {
            this.hash = hash;
            this.eskey = eskey;
            this.body = body;
        }

        public String getHash() { return hash; }
        public String getEskey() { return eskey; }
        public String getBody() { return body; }
    }
}