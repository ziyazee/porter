package com.porter.porter.customer.controller;

import com.porter.porter.customer.dto.*;
import com.porter.porter.customer.service.CustomerService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/customers")
public class CustomerController {
    
    @Autowired
    private CustomerService customerService;
    
    @PostMapping("/register")
    public ResponseEntity<OtpResponse> register(@Valid @RequestBody CustomerRegistrationRequest request) {
        OtpResponse response = customerService.register(request);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/login")
    public ResponseEntity<OtpResponse> login(@Valid @RequestBody CustomerLoginRequest request) {
        OtpResponse response = customerService.login(request);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/verify-otp")
    public ResponseEntity<AuthResponse> verifyOtp(@Valid @RequestBody OtpVerificationRequest request) {
        AuthResponse response = customerService.verifyOtp(request);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/resend-otp")
    public ResponseEntity<OtpResponse> resendOtp(@Valid @RequestBody ResendOtpRequest request) {
        OtpResponse response = customerService.resendOtp(request);
        return ResponseEntity.ok(response);
    }
}
