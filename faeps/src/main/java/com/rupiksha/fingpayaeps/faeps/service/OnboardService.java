package com.rupiksha.fingpayaeps.faeps.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rupiksha.fingpayaeps.faeps.config.FingpayClient;
import com.rupiksha.fingpayaeps.faeps.dto.OnboardRequestDTO;
import com.rupiksha.fingpayaeps.faeps.entity.OnboardTxn;
import com.rupiksha.fingpayaeps.faeps.exception.FingpayException;
import com.rupiksha.fingpayaeps.faeps.repository.OnboardTxnRepo;
import com.rupiksha.fingpayaeps.faeps.util.FingpayEncryptionUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class OnboardService {

    private final FingpayClient client;
    private final ObjectMapper mapper;
    private final FingpayEncryptionUtil encryptionUtil;
    private final OnboardTxnRepo repo;

    @Value("${fingpay.onboard.url}")
    private String url;

    @Value("${fingpay.username}")
    private String username;

    @Value("${fingpay.password}")
    private String password; // already MD5

    @Value("${fingpay.supermerchant.id}")
    private Integer superMerchantId;

    @Value("${fingpay.ip}")
    private String ip;

    public String onboard(OnboardRequestDTO dto) {

        String txnId = UUID.randomUUID().toString();

        // ✅ INIT SAVE
        OnboardTxn txn = new OnboardTxn();
        txn.setMerchantLoginId(dto.getMerchant().getMerchantLoginId());
        txn.setTxnId(txnId);
        txn.setStatus(OnboardTxn.Status.INIT);
        txn.setCreatedAt(LocalDateTime.now());
        repo.save(txn);

        try {

            // ✅ REQUIRED FIELDS
            dto.setUsername(username);
            dto.setPassword(password);
            dto.setSuperMerchantId(superMerchantId);
            dto.setIpAddress(ip);

            // ✅ TIMESTAMP (ONLY HEADER)
            String timestamp = encryptionUtil.timestamp();

            // ✅ JSON
            String plainJson = mapper.writeValueAsString(dto);
            log.info("===== FINAL JSON =====");
            log.info(plainJson);

            // ✅ AES SESSION KEY
            SecretKey sessionKey = encryptionUtil.generateSessionKey();

            // ✅ ENCRYPT BODY
            String encryptedBody =
                    encryptionUtil.encryptBody(plainJson, sessionKey);

            // ✅ ENCRYPT SESSION KEY
            String eskey =
                    encryptionUtil.encryptSessionKey(sessionKey);

            // ✅ HASH (on encrypted body)
            String hash =
                    encryptionUtil.generateHash(encryptedBody);

            // ✅ HEADERS
            HttpHeaders headers = new HttpHeaders();
            headers.add("trnTimestamp", timestamp);
            headers.add("hash", hash);
            headers.add("eskey", eskey);
            headers.add("X-Correlation-ID", txnId);
            headers.add(HttpHeaders.CONTENT_TYPE, "text/plain");

            // ✅ SAVE REQUEST
            txn.setRequestPayload(encryptedBody);
            txn.setStatus(OnboardTxn.Status.SENT);
            repo.save(txn);

            // ✅ API CALL
            String response =
                    client.post(url, encryptedBody, headers);

            log.info("Fingpay Response: {}", response);

            if (response == null || response.isEmpty()) {
                txn.setStatus(OnboardTxn.Status.FAILED);
                txn.setUpdatedAt(LocalDateTime.now());
                repo.save(txn);
                throw new FingpayException("Empty response from Fingpay");
            }

            // ✅ SAVE RESPONSE
            txn.setResponsePayload(response);

            // ✅ SUCCESS CHECK
            if (response.contains("\"status\":true") &&
                    response.contains("\"merchantStatus\":true")) {

                txn.setStatus(OnboardTxn.Status.SUCCESS);

            } else {
                txn.setStatus(OnboardTxn.Status.FAILED);
            }

            txn.setUpdatedAt(LocalDateTime.now());
            repo.save(txn);

            return response;

        } catch (Exception e) {

            log.error("Onboarding failed", e);

            txn.setStatus(OnboardTxn.Status.FAILED);
            txn.setUpdatedAt(LocalDateTime.now());
            repo.save(txn);

            throw new FingpayException("Onboarding Failed", e);
        }
    }
}