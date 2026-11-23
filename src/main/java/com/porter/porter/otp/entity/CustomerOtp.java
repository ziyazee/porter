package com.porter.porter.otp.entity;

import com.porter.porter.otp.enums.OtpPurpose;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerOtp {
    private Long id;
    private String phoneNumber;
    private String otpHash;
    private OtpPurpose purpose;
    private LocalDateTime expiresAt;
    private Integer attempts;
    private LocalDateTime resendAvailableAt;
    private Boolean verified;
    private LocalDateTime createdAt;
}
