package com.rupiksha.fingpayaeps.faeps.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class BiometricFrontendRequest {

    @NotBlank
    private String merchantLoginId; // ✅ NEW (multi-user support)

    @NotBlank
    @Pattern(regexp = "\\d{12}", message = "Aadhaar must be 12 digits")
    private String aadhaarNumber;

    // ✅ FIXED (ONLY 0 or 2) → keep STRING (since frontend भेज रहा है)
    @NotBlank
    @Pattern(regexp = "[02]", message = "indicator must be 0 or 2")
    private String indicatorforUID;

    @NotBlank
    private String nationalBankIdentificationNumber;

    @NotNull
    private Integer primaryKeyId;

    @NotBlank
    private String encodeFPTxnId;

    @NotBlank
    @Size(min = 500, message = "PID XML too small")
    private String pidXml;
}