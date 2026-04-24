package com.rupiksha.fingpayaeps.faeps.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class EkycStatusRequestDTO {

    @NotBlank(message = "merchantLoginId is required")
    private String merchantLoginId;
}