package com.porter.porter.otp.service;

import com.porter.porter.otp.enums.OtpPurpose;

public interface OtpService {
    String generateAndSendOtp(String phoneNumber, OtpPurpose purpose);
    boolean verifyOtp(String phoneNumber, String otp, OtpPurpose purpose);
    void resendOtp(String phoneNumber, OtpPurpose purpose);
    void cleanupExpiredOtp(String phoneNumber);
}
