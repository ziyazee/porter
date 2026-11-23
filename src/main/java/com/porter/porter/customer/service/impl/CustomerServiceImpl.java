package com.porter.porter.customer.service.impl;

import com.porter.porter.common.exception.CustomerAlreadyExistsException;
import com.porter.porter.common.exception.CustomerNotFoundException;
import com.porter.porter.common.exception.InvalidOtpException;
import com.porter.porter.customer.dto.*;
import com.porter.porter.customer.entity.Customer;
import com.porter.porter.customer.enums.CustomerStatus;
import com.porter.porter.customer.mapper.CustomerMapper;
import com.porter.porter.customer.repository.CustomerRepository;
import com.porter.porter.customer.service.CustomerService;
import com.porter.porter.otp.enums.OtpPurpose;
import com.porter.porter.otp.repository.OtpRepository;
import com.porter.porter.otp.service.OtpService;
import com.porter.porter.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class CustomerServiceImpl implements CustomerService {
    
    @Autowired
    private CustomerRepository customerRepository;
    
    @Autowired
    private OtpRepository otpRepository;
    
    @Autowired
    private OtpService otpService;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Override
    public OtpResponse register(CustomerRegistrationRequest request) {
        // Check if phone number already exists
        if (customerRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            Customer existingCustomer = customerRepository.findByPhoneNumber(request.getPhoneNumber())
                    .orElseThrow();
            
            if (existingCustomer.getStatus() == CustomerStatus.ACTIVE) {
                throw new CustomerAlreadyExistsException("Phone number already registered and active");
            }
            
            // If pending verification, delete and recreate
            customerRepository.delete(existingCustomer);
            otpRepository.deleteByPhoneNumber(request.getPhoneNumber());
        }
        
        // Create new customer with PENDING_VERIFICATION status
        Customer customer = Customer.builder()
                .fullName(request.getFullName())
                .phoneNumber(request.getPhoneNumber())
                .email(request.getEmail())
                .companyName(request.getCompanyName())
                .status(CustomerStatus.PENDING_VERIFICATION)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        
        customerRepository.save(customer);
        
        // Generate and send OTP
        otpService.generateAndSendOtp(request.getPhoneNumber(), OtpPurpose.REGISTRATION);
        
        return OtpResponse.builder()
                .message("OTP sent successfully")
                .phoneNumber(request.getPhoneNumber())
                .otpExpiresInSeconds(300)
                .build();
    }
    
    @Override
    public OtpResponse login(CustomerLoginRequest request) {
        // Check if customer exists
        Customer customer = customerRepository.findByPhoneNumber(request.getPhoneNumber())
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found. Please register first."));
        
        // Check if customer is blocked
        if (customer.getStatus() == CustomerStatus.BLOCKED) {
            throw new RuntimeException("Your account has been blocked. Please contact support.");
        }
        
        // Cleanup expired OTP if any
        otpService.cleanupExpiredOtp(request.getPhoneNumber());
        
        // Generate and send OTP
        otpService.generateAndSendOtp(request.getPhoneNumber(), OtpPurpose.LOGIN);
        
        return OtpResponse.builder()
                .message("OTP sent successfully")
                .phoneNumber(request.getPhoneNumber())
                .otpExpiresInSeconds(300)
                .build();
    }
    
    @Override
    public AuthResponse verifyOtp(OtpVerificationRequest request) {
        // Cleanup expired OTP
        otpService.cleanupExpiredOtp(request.getPhoneNumber());
        
        // Verify OTP
        boolean isValid = otpService.verifyOtp(request.getPhoneNumber(), request.getOtp(), OtpPurpose.REGISTRATION);
        
        if (!isValid) {
            // Try login purpose as well
            isValid = otpService.verifyOtp(request.getPhoneNumber(), request.getOtp(), OtpPurpose.LOGIN);
            if (!isValid) {
                throw new InvalidOtpException("Invalid OTP or OTP expired");
            }
        }
        
        // Get customer
        Customer customer = customerRepository.findByPhoneNumber(request.getPhoneNumber())
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found"));
        
        // Update customer status to ACTIVE if it was PENDING_VERIFICATION
        if (customer.getStatus() == CustomerStatus.PENDING_VERIFICATION) {
            customer.setStatus(CustomerStatus.ACTIVE);
            customer.setUpdatedAt(LocalDateTime.now());
            customerRepository.save(customer);
        }
        
        // Update last login time
        customer.setLastLoginAt(LocalDateTime.now());
        customerRepository.save(customer);
        
        // Generate JWT token
        String token = jwtUtil.generateToken(customer.getPhoneNumber(), customer.getId(), "CUSTOMER");
        
        // Clean up OTP after successful verification
        otpRepository.deleteByPhoneNumber(request.getPhoneNumber());
        
        return AuthResponse.builder()
                .accessToken(token)
                .tokenType("Bearer")
                .customer(CustomerMapper.toResponse(customer))
                .build();
    }
    
    @Override
    public OtpResponse resendOtp(ResendOtpRequest request) {
        // Check if customer exists
        Customer customer = customerRepository.findByPhoneNumber(request.getPhoneNumber())
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found. Please register first."));
        
        // Determine purpose based on customer status
        OtpPurpose purpose = customer.getStatus() == CustomerStatus.PENDING_VERIFICATION 
                ? OtpPurpose.REGISTRATION 
                : OtpPurpose.LOGIN;
        
        // Resend OTP
        otpService.resendOtp(request.getPhoneNumber(), purpose);
        
        return OtpResponse.builder()
                .message("OTP resent successfully")
                .phoneNumber(request.getPhoneNumber())
                .otpExpiresInSeconds(300)
                .build();
    }
}
