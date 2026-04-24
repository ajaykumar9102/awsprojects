package com.rupiksha.kyc.kyc.controller;

import com.rupiksha.kyc.kyc.dto.BiometricFrontendRequest;
import com.rupiksha.kyc.kyc.dto.BiometricResponseDTO;
import com.rupiksha.kyc.kyc.service.BiometricService;
import com.rupiksha.kyc.kyc.util.AadhaarValidator;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/aeps/ekyc")
@CrossOrigin(origins = "*")
public class BiometricController {

    private static final Logger log = LoggerFactory.getLogger(BiometricController.class);

    private final BiometricService biometricService;

    public BiometricController(BiometricService biometricService) {
        this.biometricService = biometricService;
    }

    @PostMapping("/biometric")
    public ResponseEntity<BiometricResponseDTO<Object>> biometricEkyc(
            @Valid @RequestBody BiometricFrontendRequest request) {

        // 🔥 UNIQUE REQUEST ID (VERY IMPORTANT)
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