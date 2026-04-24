package com.rupiksha.fingpayaeps.faeps.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class P2CardnumberORUID {

    // ✅ EXACT FIELD NAME (DO NOT CHANGE)
    @NotBlank(message = "Aadhaar number is required")
    @Pattern(regexp = "\\d{12}", message = "Aadhaar must be 12 digits")
    private String adhaarNumber;

    // ✅ FIXED TYPE
    @Min(value = 0, message = "indicator must be 0 or 2")
    @Max(value = 2, message = "indicator must be 0 or 2")
    private int indicatorforUID;

    // ✅ NBIN
    private String nationalBankIdentificationNumber;

    // ❌ DO NOT SEND IN JSON
    @JsonIgnore
    public boolean isAadhaarMode() {
        return indicatorforUID == 0;
    }

    @JsonIgnore
    public boolean isVIDMode() {
        return indicatorforUID == 2;
    }
}