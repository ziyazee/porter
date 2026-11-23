package com.porter.porter.driver.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DriverRequest {
    @NotBlank(message = "Full name is required")
    private String fullName;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^\\+966[0-9]{9}$", message = "Phone number must be in Saudi format (+966XXXXXXXXX)")
    private String phoneNumber;

    @NotBlank(message = "Iqama number is required")
    private String iqamaNumber;

    @NotBlank(message = "Driving license number is required")
    private String drivingLicenseNumber;
}
