package com.rupiksha.kyc.kyc.exception;

import com.rupiksha.kyc.kyc.dto.BiometricResponseDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // ✅ 1. VALIDATION ERROR
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
                        .timestamp(LocalDateTime.now().toString())
                        .build());
    }

    // ✅ 2. ILLEGAL ARGUMENT (🔥 IMPORTANT ADD)
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<BiometricResponseDTO<Object>> handleIllegalArgument(IllegalArgumentException ex) {

        log.warn("❌ Illegal Argument: {}", ex.getMessage());

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(BiometricResponseDTO.error(ex.getMessage(), null));
    }

    // ✅ 3. BUSINESS / SERVICE ERROR
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<BiometricResponseDTO<Object>> handleRuntimeException(RuntimeException ex) {

        log.error("❌ Runtime Exception:", ex);

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(BiometricResponseDTO.error(ex.getMessage(), null));
    }

    // ✅ 4. GENERIC ERROR (Fallback)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<BiometricResponseDTO<Object>> handleGenericException(Exception ex) {

        log.error("❌ Unexpected Exception:", ex);

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(BiometricResponseDTO.error("Something went wrong", null));
    }
}