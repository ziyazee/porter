package com.porter.porter.customer;

import com.porter.porter.customer.dto.*;
import com.porter.porter.customer.entity.Customer;
import com.porter.porter.customer.enums.CustomerStatus;
import com.porter.porter.customer.mapper.CustomerMapper;
import com.porter.porter.customer.repository.CustomerRepository;
import com.porter.porter.customer.service.impl.CustomerServiceImpl;
import com.porter.porter.otp.enums.OtpPurpose;
import com.porter.porter.otp.repository.OtpRepository;
import com.porter.porter.otp.service.OtpService;
import com.porter.porter.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private OtpRepository otpRepository;

    @Mock
    private OtpService otpService;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private CustomerServiceImpl customerService;

    private CustomerRegistrationRequest registrationRequest;
    private CustomerLoginRequest loginRequest;
    private OtpVerificationRequest otpVerificationRequest;
    private ResendOtpRequest resendOtpRequest;
    private Customer customer;

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

        customer = Customer.builder()
                .id(1L)
                .fullName("Ahmed Ali")
                .phoneNumber("+966501234567")
                .email("ahmed@example.com")
                .companyName("ABC Contracting")
                .status(CustomerStatus.PENDING_VERIFICATION)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    void testRegister_NewCustomer_Success() {
        when(customerRepository.existsByPhoneNumber("+966501234567")).thenReturn(false);
        when(customerRepository.save(any(Customer.class))).thenReturn(customer);
        when(otpService.generateAndSendOtp(anyString(), any(OtpPurpose.class))).thenReturn("123456");

        OtpResponse response = customerService.register(registrationRequest);

        assertNotNull(response);
        assertEquals("OTP sent successfully", response.getMessage());
        assertEquals("+966501234567", response.getPhoneNumber());
        assertEquals(300, response.getOtpExpiresInSeconds());
        
        verify(customerRepository, times(1)).save(any(Customer.class));
        verify(otpService, times(1)).generateAndSendOtp(anyString(), eq(OtpPurpose.REGISTRATION));
    }

    @Test
    void testRegister_ExistingActiveCustomer_ThrowsException() {
        Customer activeCustomer = Customer.builder()
                .id(1L)
                .fullName("Ahmed Ali")
                .phoneNumber("+966501234567")
                .status(CustomerStatus.ACTIVE)
                .build();

        when(customerRepository.existsByPhoneNumber("+966501234567")).thenReturn(true);
        when(customerRepository.findByPhoneNumber("+966501234567")).thenReturn(java.util.Optional.of(activeCustomer));

        assertThrows(com.porter.porter.common.exception.CustomerAlreadyExistsException.class, 
                () -> customerService.register(registrationRequest));
    }

    @Test
    void testRegister_ExistingPendingCustomer_Replaces() {
        Customer pendingCustomer = Customer.builder()
                .id(1L)
                .fullName("Ahmed Ali")
                .phoneNumber("+966501234567")
                .status(CustomerStatus.PENDING_VERIFICATION)
                .build();

        when(customerRepository.existsByPhoneNumber("+966501234567")).thenReturn(true);
        when(customerRepository.findByPhoneNumber("+966501234567")).thenReturn(java.util.Optional.of(pendingCustomer));
        when(customerRepository.save(any(Customer.class))).thenReturn(customer);
        when(otpService.generateAndSendOtp(anyString(), any(OtpPurpose.class))).thenReturn("123456");

        OtpResponse response = customerService.register(registrationRequest);

        assertNotNull(response);
        verify(customerRepository, times(1)).delete(any(Customer.class));
        verify(otpRepository, times(1)).deleteByPhoneNumber("+966501234567");
    }

    @Test
    void testLogin_Success() {
        Customer activeCustomer = Customer.builder()
                .id(1L)
                .fullName("Ahmed Ali")
                .phoneNumber("+966501234567")
                .status(CustomerStatus.ACTIVE)
                .build();

        when(customerRepository.findByPhoneNumber("+966501234567")).thenReturn(java.util.Optional.of(activeCustomer));
        when(otpService.generateAndSendOtp(anyString(), any(OtpPurpose.class))).thenReturn("123456");

        OtpResponse response = customerService.login(loginRequest);

        assertNotNull(response);
        assertEquals("OTP sent successfully", response.getMessage());
        verify(otpService, times(1)).generateAndSendOtp(anyString(), eq(OtpPurpose.LOGIN));
    }

    @Test
    void testLogin_CustomerNotFound_ThrowsException() {
        when(customerRepository.findByPhoneNumber("+966501234567")).thenReturn(java.util.Optional.empty());

        assertThrows(com.porter.porter.common.exception.CustomerNotFoundException.class, 
                () -> customerService.login(loginRequest));
    }

    @Test
    void testLogin_BlockedCustomer_ThrowsException() {
        Customer blockedCustomer = Customer.builder()
                .id(1L)
                .fullName("Ahmed Ali")
                .phoneNumber("+966501234567")
                .status(CustomerStatus.BLOCKED)
                .build();

        when(customerRepository.findByPhoneNumber("+966501234567")).thenReturn(java.util.Optional.of(blockedCustomer));

        assertThrows(RuntimeException.class, () -> customerService.login(loginRequest));
    }

    @Test
    void testVerifyOtp_Success() {
        Customer pendingCustomer = Customer.builder()
                .id(1L)
                .fullName("Ahmed Ali")
                .phoneNumber("+966501234567")
                .status(CustomerStatus.PENDING_VERIFICATION)
                .build();

        when(otpService.verifyOtp(anyString(), anyString(), any(OtpPurpose.class))).thenReturn(true);
        when(customerRepository.findByPhoneNumber("+966501234567")).thenReturn(java.util.Optional.of(pendingCustomer));
        when(jwtUtil.generateToken(anyString(), anyLong(), anyString())).thenReturn("jwt-token");
        when(customerRepository.save(any(Customer.class))).thenReturn(pendingCustomer);

        AuthResponse response = customerService.verifyOtp(otpVerificationRequest);

        assertNotNull(response);
        assertEquals("jwt-token", response.getAccessToken());
        assertEquals("Bearer", response.getTokenType());
        assertNotNull(response.getCustomer());
        assertEquals(CustomerStatus.ACTIVE, response.getCustomer().getStatus());
        
        verify(otpRepository, times(1)).deleteByPhoneNumber("+966501234567");
    }

    @Test
    void testVerifyOtp_InvalidOtp_ThrowsException() {
        when(otpService.verifyOtp(anyString(), anyString(), any(OtpPurpose.class))).thenReturn(false);

        assertThrows(com.porter.porter.common.exception.InvalidOtpException.class, 
                () -> customerService.verifyOtp(otpVerificationRequest));
    }

    @Test
    void testResendOtp_Success() {
        Customer activeCustomer = Customer.builder()
                .id(1L)
                .fullName("Ahmed Ali")
                .phoneNumber("+966501234567")
                .status(CustomerStatus.ACTIVE)
                .build();

        when(customerRepository.findByPhoneNumber("+966501234567")).thenReturn(java.util.Optional.of(activeCustomer));
        doNothing().when(otpService).resendOtp(anyString(), any(OtpPurpose.class));

        OtpResponse response = customerService.resendOtp(resendOtpRequest);

        assertNotNull(response);
        assertEquals("OTP resent successfully", response.getMessage());
        verify(otpService, times(1)).resendOtp(anyString(), eq(OtpPurpose.LOGIN));
    }

    @Test
    void testResendOtp_CustomerNotFound_ThrowsException() {
        when(customerRepository.findByPhoneNumber("+966501234567")).thenReturn(java.util.Optional.empty());

        assertThrows(com.porter.porter.common.exception.CustomerNotFoundException.class, 
                () -> customerService.resendOtp(resendOtpRequest));
    }

    @Test
    void testResendOtp_PendingCustomer_UsesRegistrationPurpose() {
        Customer pendingCustomer = Customer.builder()
                .id(1L)
                .fullName("Ahmed Ali")
                .phoneNumber("+966501234567")
                .status(CustomerStatus.PENDING_VERIFICATION)
                .build();

        when(customerRepository.findByPhoneNumber("+966501234567")).thenReturn(java.util.Optional.of(pendingCustomer));
        doNothing().when(otpService).resendOtp(anyString(), any(OtpPurpose.class));

        OtpResponse response = customerService.resendOtp(resendOtpRequest);

        assertNotNull(response);
        verify(otpService, times(1)).resendOtp(anyString(), eq(OtpPurpose.REGISTRATION));
    }
}
