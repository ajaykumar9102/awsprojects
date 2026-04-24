package com.rupiksha.fingpayaeps.faeps.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class ResendOtpDTO {

    private Integer superMerchantId; // set in service

    @NotBlank
    private String merchantLoginId;

    @NotNull
    private Integer primaryKeyId;

    @NotBlank
    private String encodeFPTxnId;
}