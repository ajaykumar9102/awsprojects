package com.rupiksha.fingpayaeps.faeps.exception;

import lombok.Getter;

@Getter
public class FingpayException extends RuntimeException {

    private String errorCode;
    private String txnId;

    public FingpayException(String message) {
        super(message);
    }

    public FingpayException(String message, Throwable cause) {
        super(message, cause);
    }

    public FingpayException(Throwable cause) {
        super(cause);
    }

    // 🔥 Custom constructor (very useful)
    public FingpayException(String message, String errorCode, String txnId) {
        super(message);
        this.errorCode = errorCode;
        this.txnId = txnId;
    }
}