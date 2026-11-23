package com.porter.porter.otp.service.impl;

import com.porter.porter.common.exception.MaxOtpAttemptsExceededException;
import com.porter.porter.common.exception.OtpExpiredException;
import com.porter.porter.common.exception.OtpResendCooldownException;
import com.porter.porter.otp.entity.CustomerOtp;
import com.porter.porter.otp.enums.OtpPurpose;
import com.porter.porter.otp.repository.OtpRepository;
import com.porter.porter.otp.service.OtpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.Random;
import java.util.logging.Logger;

@Service
public class OtpServiceImpl implements OtpService {
    
    private static final Logger logger = Logger.getLogger(OtpServiceImpl.class.getName());
    private static final int OTP_LENGTH = 6;
    private static final int OTP_EXPIRY_MINUTES = 5;
    private static final int MAX_ATTEMPTS = 3;
    private static final int RESEND_COOLDOWN_SECONDS = 60;
    
    @Autowired
    private OtpRepository otpRepository;
    
    @Override
    public String generateAndSendOtp(String phoneNumber, OtpPurpose purpose) {
        // Generate 6-digit OTP
        String otp = generateOtp();
        String otpHash = hashOtp(otp);
        
        // Calculate expiry time
        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(OTP_EXPIRY_MINUTES);
        LocalDateTime resendAvailableAt = LocalDateTime.now().plusSeconds(RESEND_COOLDOWN_SECONDS);
        
        // Create and save OTP record
        CustomerOtp customerOtp = CustomerOtp.builder()
                .phoneNumber(phoneNumber)
                .otpHash(otpHash)
                .purpose(purpose)
                .expiresAt(expiresAt)
                .attempts(0)
                .resendAvailableAt(resendAvailableAt)
                .verified(false)
                .createdAt(LocalDateTime.now())
                .build();
        
        otpRepository.save(customerOtp);
        
        // Log OTP for local development (in production, this would be sent via SMS)
        logger.info("OTP for " + phoneNumber + ": " + otp + " (Purpose: " + purpose + ")");
        
        return otp;
    }
    
    @Override
    public boolean verifyOtp(String phoneNumber, String otp, OtpPurpose purpose) {
        CustomerOtp customerOtp = otpRepository.findByPhoneNumber(phoneNumber)
                .orElse(null);
        
        if (customerOtp == null) {
            return false;
        }
        
        // Check if OTP is expired
        if (LocalDateTime.now().isAfter(customerOtp.getExpiresAt())) {
            throw new OtpExpiredException("OTP has expired. Please request a new OTP.");
        }
        
        // Check if max attempts exceeded
        if (customerOtp.getAttempts() >= MAX_ATTEMPTS) {
            throw new MaxOtpAttemptsExceededException("Maximum OTP attempts exceeded. Please request a new OTP.");
        }
        
        // Check if purpose matches
        if (customerOtp.getPurpose() != purpose) {
            return false;
        }
        
        // Verify OTP hash
        String otpHash = hashOtp(otp);
        if (!otpHash.equals(customerOtp.getOtpHash())) {
            // Increment attempts
            customerOtp.setAttempts(customerOtp.getAttempts() + 1);
            otpRepository.save(customerOtp);
            return false;
        }
        
        // Mark as verified
        customerOtp.setVerified(true);
        otpRepository.save(customerOtp);
        
        return true;
    }
    
    @Override
    public void resendOtp(String phoneNumber, OtpPurpose purpose) {
        CustomerOtp existingOtp = otpRepository.findByPhoneNumber(phoneNumber)
                .orElse(null);
        
        if (existingOtp != null) {
            // Check if resend cooldown has passed
            if (LocalDateTime.now().isBefore(existingOtp.getResendAvailableAt())) {
                throw new OtpResendCooldownException("Please wait before resending OTP");
            }
            
            // Delete existing OTP
            otpRepository.deleteByPhoneNumber(phoneNumber);
        }
        
        // Generate new OTP
        generateAndSendOtp(phoneNumber, purpose);
    }
    
    @Override
    public void cleanupExpiredOtp(String phoneNumber) {
        CustomerOtp customerOtp = otpRepository.findByPhoneNumber(phoneNumber)
                .orElse(null);
        
        if (customerOtp != null && LocalDateTime.now().isAfter(customerOtp.getExpiresAt())) {
            otpRepository.deleteByPhoneNumber(phoneNumber);
        }
    }
    
    private String generateOtp() {
        Random random = new Random();
        StringBuilder otp = new StringBuilder();
        for (int i = 0; i < OTP_LENGTH; i++) {
            otp.append(random.nextInt(10));
        }
        return otp.toString();
    }
    
    private String hashOtp(String otp) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(otp.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error hashing OTP", e);
        }
    }
}
