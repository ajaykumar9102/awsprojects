package com.rupiksha.fingpayaeps.faeps.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    // 🔥🔥🔥 MOST IMPORTANT FIX
    @JsonProperty("captureResponse")
    private String captureResponse;
}