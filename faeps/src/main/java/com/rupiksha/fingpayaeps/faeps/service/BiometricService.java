package com.rupiksha.fingpayaeps.faeps.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rupiksha.fingpayaeps.faeps.dto.*;
import com.rupiksha.fingpayaeps.faeps.util.AadhaarValidator;
import com.rupiksha.fingpayaeps.faeps.util.EncryptionUtil;
import com.rupiksha.fingpayaeps.faeps.util.PidXmlParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.text.SimpleDateFormat;
import java.util.Date;

@Service
public class BiometricService {

    private static final Logger log = LoggerFactory.getLogger(BiometricService.class);

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ObjectMapper mapper;

    @Value("${fingpay.ekyc.biometric-url}")
    private String biometricUrl;

    @Value("${fingpay.supermerchant.id}")
    private int superMerchantId;

    // 🔥 FRONTEND ENTRY
    public BiometricResponseDTO<Object> processFrontendBiometric(BiometricFrontendRequest frontendReq) {

        log.info("🔥 processFrontendBiometric CALLED");

        // ✅ Aadhaar Validation
        if (!AadhaarValidator.isValid(frontendReq.getAadhaarNumber())) {
            throw new IllegalArgumentException("Invalid Aadhaar Number");
        }

        // ✅ Merchant Validation
        if (frontendReq.getMerchantLoginId() == null || frontendReq.getMerchantLoginId().isEmpty()) {
            throw new RuntimeException("merchantLoginId is required");
        }

        // ✅ CARD
        P2CardnumberORUID card = new P2CardnumberORUID();
        card.setAdhaarNumber(frontendReq.getAadhaarNumber());
        card.setIndicatorforUID(Integer.parseInt(frontendReq.getIndicatorforUID()));
        card.setNationalBankIdentificationNumber(frontendReq.getNationalBankIdentificationNumber());

        // 🔥 XML → OBJECT
        CaptureResponse capture = PidXmlParser.parse(frontendReq.getPidXml());

        // 🔍 DEBUG
        log.info("========== BIOMETRIC DEBUG ==========");
        log.info("Aadhaar: ****{}", frontendReq.getAadhaarNumber().substring(8));
        log.info("errCode: {}", capture.getErrCode());
        log.info("fType: {}", capture.getFType());
        log.info("iCount: {}", capture.getICount());
        log.info("qScore: {}", capture.getQScore());
        log.info("HMAC: {}", capture.getHmac() != null ? "PRESENT" : "MISSING");
        log.info("SessionKey: {}", capture.getSessionKey() != null ? "PRESENT" : "MISSING");
        log.info("=====================================");

        // ✅ REQUEST BUILD
        BiometricRequestDTO req = BiometricRequestDTO.builder()
                .merchantLoginId(frontendReq.getMerchantLoginId()) // 🔥 dynamic
                .superMerchantId(superMerchantId)
                .primaryKeyId(frontendReq.getPrimaryKeyId())
                .encodeFPTxnId(frontendReq.getEncodeFPTxnId())
                .requestRemarks("ekyc")
                .cardnumberORUID(card)
                .captureResponse(capture)
                .build();

        return processBiometric(req);
    }

    // 🔐 CORE API
    public BiometricResponseDTO<Object> processBiometric(BiometricRequestDTO request) {

        log.info("🔥 processBiometric CALLED");

        String rawResponse = null;

        try {
            // =========================
            // ✅ STEP 1: JSON
            // =========================
            String json = mapper.writeValueAsString(request);

            log.info("\n========== REQUEST JSON ==========");
            log.info("{}", json);
            log.info("=================================");

            // =========================
            // 🔐 STEP 2: ENCRYPT
            // =========================
            EncryptionUtil.EncryptionResult enc = EncryptionUtil.encryptRequest(json);

            log.info("\n========== ENCRYPTED ==========");
            log.info("HASH: {}", enc.getHash());
            log.info("ESKEY: {}", enc.getEskey());

            String bodyPreview = enc.getBody().length() > 200
                    ? enc.getBody().substring(0, 200)
                    : enc.getBody();

            log.info("BODY (preview): {}", bodyPreview);
            log.info("================================");

            // =========================
            // ⏱️ TIMESTAMP
            // =========================
            String timestamp = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss")
                    .format(new Date());

            // =========================
            // 📦 HEADERS
            // =========================
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("trnTimestamp", timestamp);
            headers.set("hash", enc.getHash());
            headers.set("deviceIMEI", "352801082418919");
            headers.set("eskey", enc.getEskey());

            log.info("➡️ API CALL: {}", biometricUrl);

            HttpEntity<String> entity = new HttpEntity<>(enc.getBody(), headers);

            // =========================
            // 🚀 API CALL
            // =========================
            ResponseEntity<String> response = restTemplate.exchange(
                    biometricUrl,
                    HttpMethod.POST,
                    entity,
                    String.class
            );

            rawResponse = response.getBody();

            // =========================
            // 📥 RAW RESPONSE
            // =========================
            log.info("\n========== RAW RESPONSE ==========");
            log.info("{}", rawResponse);
            log.info("=================================");

            if (rawResponse == null || rawResponse.isEmpty()) {
                return BiometricResponseDTO.error("Empty response from Fingpay", null);
            }

            // =========================
            // ✅ PARSE RESPONSE
            // =========================
            FingpayResponseDTO fingpay =
                    mapper.readValue(rawResponse, FingpayResponseDTO.class);

            log.info("\n========== PARSED RESPONSE ==========");
            log.info("Status: {}", fingpay.isStatus());
            log.info("Message: {}", fingpay.getMessage());
            log.info("StatusCode: {}", fingpay.getStatusCode());
            log.info("=====================================");

            // 🔥 FINAL CONDITION FIX
            if (fingpay.isStatus() && fingpay.getStatusCode() == 10000) {
                return BiometricResponseDTO.success(
                        null,
                        fingpay.getMessage(),
                        rawResponse   // ✅ IMPORTANT
                );
            } else {
                return BiometricResponseDTO.error(
                        fingpay.getMessage(),
                        rawResponse
                );
            }

        } catch (Exception e) {
            log.error("❌ Biometric Error", e);

            return BiometricResponseDTO.error(
                    "Biometric EKYC Failed: " + e.getMessage(),
                    rawResponse
            );
        }
    }
}