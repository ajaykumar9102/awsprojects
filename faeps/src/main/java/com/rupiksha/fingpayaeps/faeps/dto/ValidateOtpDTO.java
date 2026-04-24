package com.rupiksha.fingpayaeps.faeps.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class ValidateOtpDTO {

    private Integer superMerchantId; // set in service

    @NotBlank
    private String merchantLoginId;

    @NotBlank
    private String otp;

    @NotNull
    private Integer primaryKeyId;

    @NotBlank
    private String encodeFPTxnId;

    // fixed
    private String transactionType = "EKY";

    private String matmSerialNumber;
}