package com.porter.porter.driver.service;

import com.porter.porter.driver.dto.DriverRequest;
import com.porter.porter.driver.dto.DriverResponse;
import com.porter.porter.driver.dto.DriverStatusUpdateRequest;
import com.porter.porter.driver.enums.DriverStatus;

import java.util.List;

public interface DriverService {
    DriverResponse createDriver(DriverRequest request);
    DriverResponse getDriverById(Long id);
    List<DriverResponse> getAllDrivers();
    DriverResponse updateDriver(Long id, DriverRequest request);
    DriverResponse updateDriverStatus(Long id, DriverStatusUpdateRequest request);
    void deleteDriver(Long id);
}
