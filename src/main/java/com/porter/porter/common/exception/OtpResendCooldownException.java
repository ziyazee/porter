package com.porter.porter.common.exception;

public class OtpResendCooldownException extends RuntimeException {
    public OtpResendCooldownException(String message) {
        super(message);
    }
}
