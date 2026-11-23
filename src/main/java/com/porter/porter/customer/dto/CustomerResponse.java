package com.porter.porter.customer.dto;

import com.porter.porter.customer.enums.CustomerStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerResponse {
    private Long id;
    private String fullName;
    private String phoneNumber;
    private String email;
    private String companyName;
    private CustomerStatus status;
}
