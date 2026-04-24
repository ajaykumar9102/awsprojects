package com.rupiksha.fingpayaeps.faeps.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class OnboardRequestDTO {

    private String username;   // set in service
    private String password;   // MD5 password

    // 🔥 REMOVE (Fingpay header se use karta hai)
    // private String timestamp;

    private String ipAddress;

    private Double latitude;
    private Double longitude;

    // 🔥 CRITICAL FIX (field name mapping)
    @JsonProperty("supermerchantId")
    private Integer superMerchantId;

    @Valid
    @NotNull(message = "Merchant required")
    private MerchantDTO merchant;
}