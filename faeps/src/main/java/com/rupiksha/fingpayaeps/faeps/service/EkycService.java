package com.rupiksha.fingpayaeps.faeps.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rupiksha.fingpayaeps.faeps.config.FingpayClient;
import com.rupiksha.fingpayaeps.faeps.dto.*;
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
public class EkycService {

    private final FingpayClient client;
    private final ObjectMapper mapper;
    private final FingpayEncryptionUtil encryptionUtil;
    private final OnboardTxnRepo repo;


    @Value("${fingpay.supermerchant.id}")
    private Integer superMerchantId;

    @Value("${fingpay.ekyc.send-otp-url}")
    private String sendOtpUrl;

    public Object sendOtp(SendOtpDTO dto) {

        String txnId = UUID.randomUUID().toString();

        try {

            // ✅ SET REQUIRED FIELDS
            dto.setSuperMerchantId(superMerchantId);
            dto.setTransactionType("EKY");

            String deviceImei = "352801082418919";
            dto.setMatmSerialNumber(deviceImei);

            // ✅ CLEAN JSON (🔥 IMPORTANT)
            String cleanJson = mapper.writeValueAsString(dto).trim();

            log.info("===== EKYC REQUEST JSON =====");
            log.info(cleanJson);
            log.info("JSON LENGTH: {}", cleanJson.length());

            // ✅ ENCRYPTION
            SecretKey sessionKey = encryptionUtil.generateSessionKey();

            String encryptedBody =
                    encryptionUtil.encryptBody(cleanJson, sessionKey);

            String eskey =
                    encryptionUtil.encryptSessionKey(sessionKey);

            // ✅ HASH (🔥 SAME STRING)
            String hash =
                    encryptionUtil.generateHash(cleanJson);

            String timestamp = encryptionUtil.timestamp();

            // ✅ HEADERS
            HttpHeaders headers = new HttpHeaders();
            headers.add("trnTimestamp", timestamp);
            headers.add("hash", hash);
            headers.add("eskey", eskey);
            headers.add("deviceIMEI", deviceImei);
            headers.add("Content-Type", "text/plain");

            log.info("===== HEADERS =====");
            log.info("hash: {}", hash);
            log.info("timestamp: {}", timestamp);

            // ✅ API CALL
            String response =
                    client.post(sendOtpUrl, encryptedBody, headers);

            log.info("===== EKYC RESPONSE =====");
            log.info(response);

            // ✅ PARSE RESPONSE
            SendOtpResponseDTO res =
                    mapper.readValue(response, SendOtpResponseDTO.class);

            // ✅ SAVE encodeFPTxnId
            if (res.isStatus() && res.getData() != null) {

                String fpTxnId = res.getData().getEncodeFPTxnId();

                OnboardTxn txn = new OnboardTxn();
                txn.setTxnId(txnId);
                txn.setMerchantLoginId(dto.getMerchantLoginId());
                txn.setRemarks(fpTxnId);
                txn.setStatus(OnboardTxn.Status.SUCCESS);
                txn.setCreatedAt(LocalDateTime.now());

                repo.save(txn);
            }

            return res;

        } catch (Exception e) {

            log.error("Send OTP Failed", e);

            throw new FingpayException("Send OTP Failed", e);
        }
    }
    //********************************
    @Value("${fingpay.ekyc.validate-otp-url}")
    private String validateOtpUrl;

    public Object validateOtp(ValidateOtpDTO dto) {

        String txnId = UUID.randomUUID().toString();

        try {

            // ✅ REQUIRED FIELDS
            dto.setSuperMerchantId(superMerchantId);
            dto.setTransactionType("EKY");

            String deviceImei = "352801082418919";
            dto.setMatmSerialNumber(deviceImei);

            // ✅ CLEAN JSON (IMPORTANT)
            String cleanJson = mapper.writeValueAsString(dto).trim();

            log.info("===== VALIDATE OTP REQUEST =====");
            log.info(cleanJson);

            // ✅ ENCRYPTION
            SecretKey sessionKey = encryptionUtil.generateSessionKey();

            String encryptedBody =
                    encryptionUtil.encryptBody(cleanJson, sessionKey);

            String eskey =
                    encryptionUtil.encryptSessionKey(sessionKey);

            // 🔥 HASH (EKYC RULE)
            String hash =
                    encryptionUtil.generateHash(cleanJson);

            String timestamp = encryptionUtil.timestamp();

            // ✅ HEADERS
            HttpHeaders headers = new HttpHeaders();
            headers.add("trnTimestamp", timestamp);
            headers.add("hash", hash);
            headers.add("eskey", eskey);
            headers.add("deviceIMEI", deviceImei);
            headers.add("Content-Type", "text/plain");

            // ✅ API CALL
            String response =
                    client.post(validateOtpUrl, encryptedBody, headers);

            log.info("===== VALIDATE OTP RESPONSE =====");
            log.info(response);

            // ✅ PARSE RESPONSE
            ValidateOtpResponseDTO res =
                    mapper.readValue(response, ValidateOtpResponseDTO.class);

            // ✅ SAVE RESULT
            OnboardTxn txn = new OnboardTxn();
            txn.setTxnId(txnId);
            txn.setMerchantLoginId(dto.getMerchantLoginId());
            txn.setRequestPayload(encryptedBody);
            txn.setResponsePayload(response);
            txn.setCreatedAt(LocalDateTime.now());

            if (res.isStatus()) {
                txn.setStatus(OnboardTxn.Status.SUCCESS);
                txn.setRemarks("OTP VERIFIED");
            } else {
                txn.setStatus(OnboardTxn.Status.FAILED);
                txn.setRemarks(res.getMessage());
            }

            repo.save(txn);

            return res;

        } catch (Exception e) {

            log.error("Validate OTP Failed", e);

            throw new FingpayException("Validate OTP Failed", e);
        }
    }
    //**********************************
    @Value("${fingpay.ekyc.resend-otp-url}")
    private String resendOtpUrl;

    public Object resendOtp(ResendOtpDTO dto) {

        try {

            // ✅ SET REQUIRED
            dto.setSuperMerchantId(superMerchantId);

            String deviceImei = "352801082418919";

            // ✅ CLEAN JSON
            String cleanJson = mapper.writeValueAsString(dto).trim();

            log.info("===== RESEND OTP REQUEST =====");
            log.info(cleanJson);

            // ✅ ENCRYPTION
            SecretKey sessionKey = encryptionUtil.generateSessionKey();

            String encryptedBody =
                    encryptionUtil.encryptBody(cleanJson, sessionKey);

            String eskey =
                    encryptionUtil.encryptSessionKey(sessionKey);

            // 🔥 HASH (same EKYC rule)
            String hash =
                    encryptionUtil.generateHash(cleanJson);

            String timestamp = encryptionUtil.timestamp();

            // ✅ HEADERS
            HttpHeaders headers = new HttpHeaders();
            headers.add("trnTimestamp", timestamp);
            headers.add("hash", hash);
            headers.add("eskey", eskey);
            headers.add("deviceIMEI", deviceImei);
            headers.add("Content-Type", "text/plain");

            // ✅ API CALL
            String response =
                    client.post(resendOtpUrl, encryptedBody, headers);

            log.info("===== RESEND OTP RESPONSE =====");
            log.info(response);

            return mapper.readValue(response, Object.class);

        } catch (Exception e) {

            log.error("Resend OTP Failed", e);
            throw new FingpayException("Resend OTP Failed", e);
        }
    }
    //***************************


}