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

    // 🔥 OPTIONAL (debugging ke liye useful)
    private String rawResponse;

    private String timestamp;

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

    // ✅ SUCCESS
    public static <T> BiometricResponseDTO<T> success(T data, String message, String rawResponse) {
        return BiometricResponseDTO.<T>builder()
                .success(true)
                .message(message)
                .data(data)
                .rawResponse(rawResponse) // optional
                .timestamp(currentTime())
                .build();
    }

    // ✅ ERROR
    public static <T> BiometricResponseDTO<T> error(String message, String rawResponse) {
        return BiometricResponseDTO.<T>builder()
                .success(false)
                .message(message)
                .rawResponse(rawResponse)
                .timestamp(currentTime())
                .build();
    }

    // 🔥 COMMON METHOD (code clean)
    private static String currentTime() {
        return LocalDateTime.now().format(FORMATTER);
    }
}