package com.porter.porter.customer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.porter.porter.customer.dto.*;
import com.porter.porter.customer.service.CustomerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CustomerService customerService;

    private CustomerRegistrationRequest registrationRequest;
    private CustomerLoginRequest loginRequest;
    private OtpVerificationRequest otpVerificationRequest;
    private ResendOtpRequest resendOtpRequest;
    private OtpResponse otpResponse;
    private AuthResponse authResponse;

    @BeforeEach
    void setUp() {
        registrationRequest = new CustomerRegistrationRequest(
                "Ahmed Ali",
                "+966501234567",
                "ahmed@example.com",
                "ABC Contracting"
        );

        loginRequest = new CustomerLoginRequest("+966501234567");

        otpVerificationRequest = new OtpVerificationRequest("+966501234567", "123456");

        resendOtpRequest = new ResendOtpRequest("+966501234567");

        otpResponse = OtpResponse.builder()
                .message("OTP sent successfully")
                .phoneNumber("+966501234567")
                .otpExpiresInSeconds(300)
                .build();

        OtpResponse resendOtpResponse = OtpResponse.builder()
                .message("OTP resent successfully")
                .phoneNumber("+966501234567")
                .otpExpiresInSeconds(300)
                .build();

        CustomerResponse customerResponse = CustomerResponse.builder()
                .id(1L)
                .fullName("Ahmed Ali")
                .phoneNumber("+966501234567")
                .email("ahmed@example.com")
                .companyName("ABC Contracting")
                .status(com.porter.porter.customer.enums.CustomerStatus.ACTIVE)
                .build();

        authResponse = AuthResponse.builder()
                .accessToken("test-jwt-token")
                .tokenType("Bearer")
                .customer(customerResponse)
                .build();
    }

    @Test
    void testRegister_Success() throws Exception {
        when(customerService.register(any(CustomerRegistrationRequest.class)))
                .thenReturn(otpResponse);

        mockMvc.perform(post("/api/v1/customers/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registrationRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("OTP sent successfully"))
                .andExpect(jsonPath("$.phoneNumber").value("+966501234567"))
                .andExpect(jsonPath("$.otpExpiresInSeconds").value(300));
    }

    @Test
    void testRegister_Validation_Error() throws Exception {
        CustomerRegistrationRequest invalidRequest = new CustomerRegistrationRequest(
                "", // Invalid: empty full name
                "+966501234567",
                "ahmed@example.com",
                "ABC Contracting"
        );

        mockMvc.perform(post("/api/v1/customers/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testLogin_Success() throws Exception {
        when(customerService.login(any(CustomerLoginRequest.class)))
                .thenReturn(otpResponse);

        mockMvc.perform(post("/api/v1/customers/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("OTP sent successfully"))
                .andExpect(jsonPath("$.phoneNumber").value("+966501234567"))
                .andExpect(jsonPath("$.otpExpiresInSeconds").value(300));
    }

    @Test
    void testLogin_Validation_Error() throws Exception {
        CustomerLoginRequest invalidRequest = new CustomerLoginRequest(
                "0501234567" // Invalid: wrong format
        );

        mockMvc.perform(post("/api/v1/customers/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testVerifyOtp_Success() throws Exception {
        when(customerService.verifyOtp(any(OtpVerificationRequest.class)))
                .thenReturn(authResponse);

        mockMvc.perform(post("/api/v1/customers/verify-otp")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(otpVerificationRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("test-jwt-token"))
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.customer.id").value(1))
                .andExpect(jsonPath("$.customer.fullName").value("Ahmed Ali"));
    }

    @Test
    void testVerifyOtp_Validation_Error() throws Exception {
        OtpVerificationRequest invalidRequest = new OtpVerificationRequest(
                "+966501234567",
                "12345" // Invalid: only 5 digits
        );

        mockMvc.perform(post("/api/v1/customers/verify-otp")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testResendOtp_Success() throws Exception {
        OtpResponse resendOtpResponse = OtpResponse.builder()
                .message("OTP resent successfully")
                .phoneNumber("+966501234567")
                .otpExpiresInSeconds(300)
                .build();
        when(customerService.resendOtp(any(ResendOtpRequest.class)))
                .thenReturn(resendOtpResponse);

        mockMvc.perform(post("/api/v1/customers/resend-otp")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(resendOtpRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("OTP resent successfully"))
                .andExpect(jsonPath("$.phoneNumber").value("+966501234567"))
                .andExpect(jsonPath("$.otpExpiresInSeconds").value(300));
    }

    @Test
    void testResendOtp_Validation_Error() throws Exception {
        ResendOtpRequest invalidRequest = new ResendOtpRequest(
                "0501234567" // Invalid: wrong format
        );

        mockMvc.perform(post("/api/v1/customers/resend-otp")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }
}
