package com.porter.porter.customer.mapper;

import com.porter.porter.customer.dto.CustomerResponse;
import com.porter.porter.customer.entity.Customer;

public class CustomerMapper {
    
    public static CustomerResponse toResponse(Customer customer) {
        return CustomerResponse.builder()
                .id(customer.getId())
                .fullName(customer.getFullName())
                .phoneNumber(customer.getPhoneNumber())
                .email(customer.getEmail())
                .companyName(customer.getCompanyName())
                .status(customer.getStatus())
                .build();
    }
}
