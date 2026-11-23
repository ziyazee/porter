package com.porter.porter.otp.repository;

import com.porter.porter.otp.entity.CustomerOtp;

import java.util.Optional;

public interface OtpRepository {
    CustomerOtp save(CustomerOtp customerOtp);
    Optional<CustomerOtp> findByPhoneNumber(String phoneNumber);
    void delete(CustomerOtp customerOtp);
    void deleteByPhoneNumber(String phoneNumber);
}
