package com.rupiksha.fingpayaeps.faeps.controller;

import com.rupiksha.fingpayaeps.faeps.dto.*;
import com.rupiksha.fingpayaeps.faeps.service.BiometricService;
import com.rupiksha.fingpayaeps.faeps.service.EkycService;


import com.rupiksha.fingpayaeps.faeps.util.AadhaarValidator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/aeps/ekyc")
@RequiredArgsConstructor
@CrossOrigin("*")
public class EkycController {

    private static final Logger log = LoggerFactory.getLogger(EkycController.class);

    private final EkycService service;          // OTP service
    private final BiometricService biometricService; // 🔥 NEW

    // =========================
    // 🔹 OTP APIs
    // =========================

    @PostMapping("/send-otp")
    public Object sendOtp(@RequestBody @Valid SendOtpDTO dto) {
        return service.sendOtp(dto);
    }

    @PostMapping("/validate-otp")
    public Object validateOtp(@RequestBody @Valid ValidateOtpDTO dto) {
        return service.validateOtp(dto);
    }

    @PostMapping("/resend-otp")
    public Object resendOtp(@RequestBody @Valid ResendOtpDTO dto) {
        return service.resendOtp(dto);
    }

    // =========================
    // 🔥 BIOMETRIC EKYC API
    // =========================

    @PostMapping("/biometric")
    public ResponseEntity<BiometricResponseDTO<Object>> biometricEkyc(
            @Valid @RequestBody BiometricFrontendRequest request) {

        // 🔥 UNIQUE REQUEST ID
        String requestId = UUID.randomUUID().toString();

        // 🔐 SAFE LOGGING
        log.info("📥 [{}] Incoming Biometric Request", requestId);
        log.info("📥 [{}] Aadhaar: {}", requestId,
                AadhaarValidator.mask(request.getAadhaarNumber()));

        log.info("📥 [{}] PID XML Length: {}", requestId,
                request.getPidXml() != null ? request.getPidXml().length() : 0);

        // 🔹 PROCESS
        BiometricResponseDTO<Object> response =
                biometricService.processFrontendBiometric(request);

        // 🔹 RESPONSE LOG
        log.info("📤 [{}] Response: success={}, message={}",
                requestId,
                response.isSuccess(),
                response.getMessage()
        );

        return ResponseEntity.ok(response);
    }
}