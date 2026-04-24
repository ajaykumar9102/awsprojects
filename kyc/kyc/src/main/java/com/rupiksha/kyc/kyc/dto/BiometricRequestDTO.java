package com.rupiksha.kyc.kyc.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BiometricRequestDTO {

    @JsonProperty("merchantLoginId")
    private String merchantLoginId;

    @JsonProperty("superMerchantId")
    private Integer superMerchantId;

    @JsonProperty("primaryKeyId")
    private Integer primaryKeyId;

    @JsonProperty("encodeFPTxnId")
    private String encodeFPTxnId;

    @JsonProperty("requestRemarks")
    private String requestRemarks;

    @JsonProperty("cardnumberORUID")
    private P2CardnumberORUID cardnumberORUID;

    @JsonProperty("captureResponse")
    private CaptureResponse captureResponse;
}