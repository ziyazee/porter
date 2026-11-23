package com.porter.porter.customer.service;

import com.porter.porter.customer.dto.*;

public interface CustomerService {
    OtpResponse register(CustomerRegistrationRequest request);
    OtpResponse login(CustomerLoginRequest request);
    AuthResponse verifyOtp(OtpVerificationRequest request);
    OtpResponse resendOtp(ResendOtpRequest request);
}
