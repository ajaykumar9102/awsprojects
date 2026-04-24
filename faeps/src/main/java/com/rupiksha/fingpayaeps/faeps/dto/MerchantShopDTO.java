package com.rupiksha.fingpayaeps.faeps.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class MerchantShopDTO {

    @NotBlank(message = "Shop address required")
    private String shopAddress;

    @NotBlank(message = "Shop city required")
    private String shopCity;

    @NotBlank(message = "Shop district required")
    private String shopDistrict;

    @NotNull(message = "Shop state required")
    private Integer shopState; // Fingpay stateId

    @NotBlank(message = "Shop pincode required")
    @Pattern(regexp = "^[1-9][0-9]{5}$", message = "Invalid pincode")
    private String shopPincode;

    @NotNull(message = "Latitude required")
    @DecimalMin(value = "-90.0", message = "Invalid latitude")
    @DecimalMax(value = "90.0", message = "Invalid latitude")
    private Double shopLatitude;

    @NotNull(message = "Longitude required")
    @DecimalMin(value = "-180.0", message = "Invalid longitude")
    @DecimalMax(value = "180.0", message = "Invalid longitude")
    private Double shopLongitude;

    // 🔥 OPTIONAL (Fingpay sensitive field)
    private String backgroundImageOfShop;
}