package com.rupiksha.fingpayaeps.faeps.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class MerchantDTO {

    @NotBlank(message = "LoginId required")
    private String merchantLoginId;

    @NotBlank(message = "Login PIN required")
    private String merchantLoginPin;

    @NotBlank(message = "First name required")
    @Size(max = 40)
    private String firstName;

    @NotBlank(message = "Last name required")
    private String lastName;

    private String middleName;

    @NotBlank(message = "Phone required")
    @Pattern(regexp = "^[6-9][0-9]{9}$", message = "Invalid mobile number")
    private String merchantPhoneNumber;

    @Valid
    @NotNull(message = "Address required")
    private MerchantAddressDTO merchantAddress;

    private String companyLegalName;

    // 🔥 Validation removed (Fingpay handle karega)
    private String dateOfIncorporation;

    @NotBlank(message = "UserType required")
    private String userType; // MUST be "individual"

    @NotNull(message = "CompanyType required")
    private Integer companyType; // MCC Code (e.g. 5411)

    @Email(message = "Invalid email")
    private String emailId;

    // 🔥 MUST NOT BE NULL
    @NotBlank(message = "Certificate flag required")
    private String certificateOfIncorporationImage;

    @Valid
    @NotNull(message = "KYC required")
    private KycDTO kyc;

    @Valid
    @NotNull(message = "Settlement required")
    private SettlementDTO settlementV1;

    // 🔥 ALL MUST BE PRESENT (STRING "true"/"false")
    @NotBlank
    private String tradeBusinessProof;

    @NotBlank
    private String termsConditionCheck;

    @NotBlank
    private String cancelledChequeImages;

    @NotBlank
    private String physicalVerification;

    @NotBlank
    private String videoKycWithLatLongData;

    @Valid
    @NotNull(message = "Shop address required")
    private MerchantShopDTO merchantKycAddressData;
}