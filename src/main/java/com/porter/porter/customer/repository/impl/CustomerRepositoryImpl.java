package com.porter.porter.customer.repository.impl;

import com.porter.porter.customer.entity.Customer;
import com.porter.porter.customer.repository.CustomerRepository;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class CustomerRepositoryImpl implements CustomerRepository {
    
    private final Map<Long, Customer> customers = new ConcurrentHashMap<>();
    private final Map<String, Long> phoneNumberIndex = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);
    
    @Override
    public Customer save(Customer customer) {
        if (customer.getId() == null) {
            customer.setId(idGenerator.getAndIncrement());
        }
        customers.put(customer.getId(), customer);
        phoneNumberIndex.put(customer.getPhoneNumber(), customer.getId());
        return customer;
    }
    
    @Override
    public Optional<Customer> findByPhoneNumber(String phoneNumber) {
        Long id = phoneNumberIndex.get(phoneNumber);
        if (id != null) {
            return Optional.ofNullable(customers.get(id));
        }
        return Optional.empty();
    }
    
    @Override
    public Optional<Customer> findById(Long id) {
        return Optional.ofNullable(customers.get(id));
    }
    
    @Override
    public void delete(Customer customer) {
        customers.remove(customer.getId());
        phoneNumberIndex.remove(customer.getPhoneNumber());
    }
    
    @Override
    public boolean existsByPhoneNumber(String phoneNumber) {
        return phoneNumberIndex.containsKey(phoneNumber);
    }
}
