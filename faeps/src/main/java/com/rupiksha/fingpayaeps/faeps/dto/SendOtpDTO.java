package com.rupiksha.fingpayaeps.faeps.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class SendOtpDTO {

    private Integer superMerchantId; // set in service

    @NotBlank
    private String merchantLoginId;

    private String transactionType = "EKY"; // fixed

    @NotBlank
    @Pattern(regexp = "^[6-9][0-9]{9}$")
    private String mobileNumber;

    @NotBlank
    @Pattern(regexp = "^[0-9]{12}$")
    private String aadharNumber;

    @NotBlank
    @Pattern(regexp = "^[A-Z]{5}[0-9]{4}[A-Z]{1}$")
    private String panNumber;

    private String matmSerialNumber;

    @NotNull
    private Double latitude;

    @NotNull
    private Double longitude;
}