package com.rupiksha.fingpayaeps.faeps.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class SettlementDTO {

    @NotBlank(message = "Account number required")
    private String companyBankAccountNumber;

    @NotBlank(message = "IFSC required")
    @Pattern(regexp = "^[A-Z]{4}0[A-Z0-9]{6}$", message = "Invalid IFSC")
    private String bankIfscCode;

    @NotBlank(message = "Bank name required")
    private String companyBankName;

    // 🔥 Optional (safe)
    private String bankBranchName;

    @NotBlank(message = "Account holder name required")
    private String bankAccountName;
}