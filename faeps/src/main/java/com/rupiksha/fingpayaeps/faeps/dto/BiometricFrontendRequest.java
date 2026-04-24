package com.rupiksha.fingpayaeps.faeps.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class BiometricFrontendRequest {

    @NotBlank(message = "Merchant Login ID is required")
    private String merchantLoginId;

    @NotBlank(message = "Aadhaar is required")
    @Pattern(regexp = "\\d{12}", message = "Aadhaar must be 12 digits")
    private String aadhaarNumber;

    // ✅ STRING रखना सही है (frontend से आ रहा है)
    @NotBlank(message = "Indicator is required")
    @Pattern(regexp = "[02]", message = "indicator must be 0 (Aadhaar) or 2 (VID)")
    private String indicatorforUID;

    @NotBlank(message = "IIN (Bank code) is required")
    @Pattern(regexp = "\\d{6}", message = "IIN must be 6 digits")
    private String nationalBankIdentificationNumber;

    @NotNull(message = "PrimaryKeyId is required")
    private Integer primaryKeyId;

    @NotBlank(message = "encodeFPTxnId is required")
    private String encodeFPTxnId;

    // 🔥 IMPORTANT: PID XML validation
    @NotBlank(message = "PID XML is required")
    @Size(min = 100, message = "Invalid PID XML (too small)")
    private String pidXml;
}