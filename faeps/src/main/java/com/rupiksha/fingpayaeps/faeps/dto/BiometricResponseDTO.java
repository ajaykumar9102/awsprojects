package com.rupiksha.fingpayaeps.faeps.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BiometricResponseDTO<T> {

    private boolean success;
    private String message;
    private T data;
    private String rawResponse;
    private String timestamp;

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

    // ✅ SUCCESS (UPDATED)
    public static <T> BiometricResponseDTO<T> success(T data, String message, String rawResponse) {
        return BiometricResponseDTO.<T>builder()
                .success(true)
                .message(message)
                .data(data)
                .rawResponse(rawResponse) // ✅ FIXED
                .timestamp(LocalDateTime.now().format(FORMATTER))
                .build();
    }

    // ✅ ERROR (ALREADY CORRECT)
    public static <T> BiometricResponseDTO<T> error(String message, String rawResponse) {
        return BiometricResponseDTO.<T>builder()
                .success(false)
                .message(message)
                .rawResponse(rawResponse)
                .timestamp(LocalDateTime.now().format(FORMATTER))
                .build();
    }
}