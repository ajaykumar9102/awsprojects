package com.rupiksha.fingpayaeps.faeps.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class CaptureResponse {

    private String errCode;
    private String errInfo;

    @JsonProperty("fCount")
    private String fCount;

    @JsonProperty("fType")
    private String fType;

    @JsonProperty("iCount")
    private String iCount;

    @JsonProperty("iType")
    private String iType;

    @JsonProperty("pCount")
    private String pCount;

    @JsonProperty("pType")
    private String pType;

    private String nmPoints;
    private String qScore;
    private String dpID;
    private String rdsID;
    private String rdsVer;
    private String dc;
    private String mi;
    private String mc;
    private String ci;

    private String sessionKey;
    private String hmac;

    @JsonProperty("pidDatatype")
    private String pidDatatype;

    @JsonProperty("piddata")
    private String piddata;
}