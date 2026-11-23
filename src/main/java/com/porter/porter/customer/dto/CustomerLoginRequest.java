package com.porter.porter.customer.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerLoginRequest {
    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^\\+966[0-9]{9}$", message = "Phone number must be in Saudi format +966XXXXXXXXX")
    private String phoneNumber;
}
