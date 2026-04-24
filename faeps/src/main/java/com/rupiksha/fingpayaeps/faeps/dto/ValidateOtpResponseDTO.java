package com.rupiksha.fingpayaeps.faeps.dto;

import lombok.Data;

@Data
public class ValidateOtpResponseDTO {

    private boolean status;
    private String message;
    private DataBlock data;
    private int statusCode;

    @Data
    public static class DataBlock {
        private Integer primaryKeyId;
        private String encodeFPTxnId;
    }
}