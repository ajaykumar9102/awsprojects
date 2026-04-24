package com.rupiksha.fingpayaeps.faeps.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class KycDTO {

    @NotBlank(message = "PAN is required")
    private String userPan;

    @NotBlank(message = "Aadhaar is required")
    private String aadhaarNumber;

    // 🔥 OPTIONAL → DO NOT SEND EMPTY STRING IN REQUEST
    private String gstinNumber;

    @NotBlank(message = "Company/Shop PAN is required")
    private String companyOrShopPan;

    // 🔥 BASE64 IMAGES (MANDATORY)
    @NotBlank(message = "PAN image required")
    private String merchantPanImage;

    @NotBlank(message = "Masked Aadhaar image required")
    private String maskedAadharImage;

    // 🔥 MUST BE "true" / "false" STRING (NOT BOOLEAN)
    @NotBlank(message = "shopAndPanImage flag required")
    private String shopAndPanImage;
}