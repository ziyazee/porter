package com.porter.porter.customer.repository;

import com.porter.porter.customer.entity.Customer;

import java.util.Optional;

public interface CustomerRepository {
    Customer save(Customer customer);
    Optional<Customer> findByPhoneNumber(String phoneNumber);
    Optional<Customer> findById(Long id);
    void delete(Customer customer);
    boolean existsByPhoneNumber(String phoneNumber);
}
