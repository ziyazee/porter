package com.porter.porter.otp.repository.impl;

import com.porter.porter.otp.entity.CustomerOtp;
import com.porter.porter.otp.repository.OtpRepository;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class OtpRepositoryImpl implements OtpRepository {
    
    private final Map<Long, CustomerOtp> otps = new ConcurrentHashMap<>();
    private final Map<String, Long> phoneNumberIndex = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);
    
    @Override
    public CustomerOtp save(CustomerOtp customerOtp) {
        if (customerOtp.getId() == null) {
            customerOtp.setId(idGenerator.getAndIncrement());
        }
        otps.put(customerOtp.getId(), customerOtp);
        phoneNumberIndex.put(customerOtp.getPhoneNumber(), customerOtp.getId());
        return customerOtp;
    }
    
    @Override
    public Optional<CustomerOtp> findByPhoneNumber(String phoneNumber) {
        Long id = phoneNumberIndex.get(phoneNumber);
        if (id != null) {
            return Optional.ofNullable(otps.get(id));
        }
        return Optional.empty();
    }
    
    @Override
    public void delete(CustomerOtp customerOtp) {
        otps.remove(customerOtp.getId());
        phoneNumberIndex.remove(customerOtp.getPhoneNumber());
    }
    
    @Override
    public void deleteByPhoneNumber(String phoneNumber) {
        Long id = phoneNumberIndex.remove(phoneNumber);
        if (id != null) {
            otps.remove(id);
        }
    }
}
