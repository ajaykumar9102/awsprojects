package com.rupiksha.fingpayaeps.faeps.util;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;

@Component
@Slf4j
public class FingpayEncryptionUtil {

    private PublicKey publicKey;

    @PostConstruct
    public void init() throws Exception {

        // ✅ Ensure BC provider added only once
        if (Security.getProvider("BC") == null) {
            Security.addProvider(new BouncyCastleProvider());
        }

        try (InputStream is = getClass().getClassLoader()
                .getResourceAsStream("fingpay_public.cer")) {

            if (is == null) {
                throw new RuntimeException("❌ fingpay_public.cer not found");
            }

            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            Certificate cert = cf.generateCertificate(is);
            publicKey = cert.getPublicKey();

            log.info("✅ Fingpay Public Key Loaded");

        } catch (Exception e) {
            log.error("❌ Failed to load public key", e);
            throw e;
        }
    }

    // ✅ AES 128 session key
    public SecretKey generateSessionKey() throws Exception {
        KeyGenerator kg = KeyGenerator.getInstance("AES");
        kg.init(128);
        return kg.generateKey();
    }

    // ✅ AES Encryption (BC Provider FIXED)
    public String encryptBody(String json, SecretKey key) throws Exception {

        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding", "BC"); // 🔥 FIX
        cipher.init(Cipher.ENCRYPT_MODE, key);

        byte[] encrypted = cipher.doFinal(json.getBytes(StandardCharsets.UTF_8));

        return Base64.getEncoder().encodeToString(encrypted);
    }

    // ✅ RSA Encryption (BC Provider FIXED)
    public String encryptSessionKey(SecretKey key) throws Exception {

        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding", "BC"); // 🔥 FIX
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);

        byte[] encryptedKey = cipher.doFinal(key.getEncoded());

        return Base64.getEncoder().encodeToString(encryptedKey);
    }

    // ✅ SHA-256 HASH (on encrypted body)
    public String generateHash(String input) throws Exception {

        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] digest = md.digest(input.trim().getBytes(StandardCharsets.UTF_8));

        return Base64.getEncoder().encodeToString(digest);
    }

    // ✅ TIMESTAMP FORMAT
    public String timestamp() {

        return LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
    }
}