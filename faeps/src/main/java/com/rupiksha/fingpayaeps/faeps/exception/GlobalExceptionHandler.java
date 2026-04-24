package com.rupiksha.fingpayaeps.faeps.exception;


import com.rupiksha.fingpayaeps.faeps.dto.BiometricResponseDTO;
import com.rupiksha.fingpayaeps.faeps.exception.FingpayException;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

    // =========================
    // ✅ VALIDATION ERROR
    // =========================
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<BiometricResponseDTO<Object>> handleValidationException(
            MethodArgumentNotValidException ex) {

        String errorMsg = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .findFirst()
                .orElse("Validation error");

        log.warn("❌ Validation Error: {}", errorMsg);

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(BiometricResponseDTO.<Object>builder()
                        .success(false)
                        .message(errorMsg)
                        .timestamp(LocalDateTime.now().format(FORMATTER))
                        .build());
    }

    // =========================
    // ✅ ILLEGAL ARGUMENT
    // =========================
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<BiometricResponseDTO<Object>> handleIllegalArgument(IllegalArgumentException ex) {

        log.warn("❌ Illegal Argument: {}", ex.getMessage());

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(BiometricResponseDTO.error(ex.getMessage(), null));
    }

    // =========================
    // 🔥 FINGPAY ERROR (OTP + KYC)
    // =========================
    @ExceptionHandler(FingpayException.class)
    public ResponseEntity<BiometricResponseDTO<Object>> handleFingpay(FingpayException e){

        log.error("❌ Fingpay Error:", e);

        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(
                BiometricResponseDTO.<Object>builder()
                        .success(false)
                        .message(e.getMessage())
                        .rawResponse("errorCode=" + e.getErrorCode() + ", txnId=" + e.getTxnId())
                        .timestamp(LocalDateTime.now().format(FORMATTER))
                        .build()
        );
    }

    // =========================
    // 🔥 RUNTIME ERROR
    // =========================
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<BiometricResponseDTO<Object>> handleRuntimeException(RuntimeException ex) {

        log.error("❌ Runtime Exception:", ex);

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(BiometricResponseDTO.error(ex.getMessage(), null));
    }

    // =========================
    // 🔥 GENERIC ERROR
    // =========================
    @ExceptionHandler(Exception.class)
    public ResponseEntity<BiometricResponseDTO<Object>> handleGenericException(Exception ex) {

        log.error("❌ Unexpected Exception:", ex);

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(BiometricResponseDTO.error("Something went wrong", null));
    }
}