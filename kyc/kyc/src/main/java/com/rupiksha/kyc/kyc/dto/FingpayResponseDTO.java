package com.rupiksha.kyc.kyc.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true) // 🔥 extra fields ignore
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FingpayResponseDTO {

    private boolean status;
    private String message;
    private Object data; // later strongly type kar sakte ho

    private Integer statusCode; // 🔥 long se better (API int hi deta hai)

    // ✅ Helper methods (debugging easy)
    public boolean isSuccess() {
        return status && statusCode != null && statusCode == 10000;
    }

    public boolean isFailure() {
        return !isSuccess();
    }
}