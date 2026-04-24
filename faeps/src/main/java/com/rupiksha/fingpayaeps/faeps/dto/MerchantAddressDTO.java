package com.rupiksha.fingpayaeps.faeps.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class MerchantAddressDTO {

    @NotBlank(message = "Address1 is required")
    private String merchantAddress1;

    // 🔥 OPTIONAL (important fix)
    private String merchantAddress2;

    @NotNull(message = "State is required")
    private Integer merchantState; // Fingpay stateId

    @NotBlank(message = "City is required")
    private String merchantCityName;

    @NotBlank(message = "District is required")
    private String merchantDistrictName;

    @NotBlank(message = "Pincode is required")
    @Pattern(regexp = "^[1-9][0-9]{5}$", message = "Invalid pincode")
    private String merchantPinCode;
}