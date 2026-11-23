package com.porter.porter.driver.service.impl;

import com.porter.porter.driver.dto.DriverRequest;
import com.porter.porter.driver.dto.DriverResponse;
import com.porter.porter.driver.dto.DriverStatusUpdateRequest;
import com.porter.porter.driver.entity.Driver;
import com.porter.porter.driver.enums.DriverStatus;
import com.porter.porter.driver.exception.DriverNotFoundException;
import com.porter.porter.driver.repository.DriverRepository;
import com.porter.porter.driver.service.DriverService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DriverServiceImpl implements DriverService {
    
    @Autowired
    private DriverRepository driverRepository;
    
    @Override
    public DriverResponse createDriver(DriverRequest request) {
        Driver driver = Driver.builder()
                .fullName(request.getFullName())
                .phoneNumber(request.getPhoneNumber())
                .iqamaNumber(request.getIqamaNumber())
                .drivingLicenseNumber(request.getDrivingLicenseNumber())
                .build();
        
        Driver savedDriver = driverRepository.save(driver);
        return mapToResponse(savedDriver);
    }
    
    @Override
    public DriverResponse getDriverById(Long id) {
        Driver driver = driverRepository.findById(id)
                .orElseThrow(() -> new DriverNotFoundException("Driver not found"));
        return mapToResponse(driver);
    }
    
    @Override
    public List<DriverResponse> getAllDrivers() {
        List<Driver> drivers = driverRepository.findAll();
        return drivers.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    public DriverResponse updateDriver(Long id, DriverRequest request) {
        Driver driver = driverRepository.findById(id)
                .orElseThrow(() -> new DriverNotFoundException("Driver not found"));
        
        driver.setFullName(request.getFullName());
        driver.setPhoneNumber(request.getPhoneNumber());
        driver.setIqamaNumber(request.getIqamaNumber());
        driver.setDrivingLicenseNumber(request.getDrivingLicenseNumber());
        
        Driver savedDriver = driverRepository.save(driver);
        return mapToResponse(savedDriver);
    }
    
    @Override
    public DriverResponse updateDriverStatus(Long id, DriverStatusUpdateRequest request) {
        Driver driver = driverRepository.findById(id)
                .orElseThrow(() -> new DriverNotFoundException("Driver not found"));
        
        driver.setStatus(request.getStatus());
        
        Driver savedDriver = driverRepository.save(driver);
        return mapToResponse(savedDriver);
    }
    
    @Override
    public void deleteDriver(Long id) {
        Driver driver = driverRepository.findById(id)
                .orElseThrow(() -> new DriverNotFoundException("Driver not found"));
        driverRepository.deleteById(id);
    }
    
    private DriverResponse mapToResponse(Driver driver) {
        return DriverResponse.builder()
                .id(driver.getId())
                .fullName(driver.getFullName())
                .phoneNumber(driver.getPhoneNumber())
                .iqamaNumber(driver.getIqamaNumber())
                .drivingLicenseNumber(driver.getDrivingLicenseNumber())
                .status(driver.getStatus())
                .active(driver.getActive())
                .createdAt(driver.getCreatedAt())
                .updatedAt(driver.getUpdatedAt())
                .build();
    }
}
