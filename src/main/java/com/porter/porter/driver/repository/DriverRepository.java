package com.porter.porter.driver.repository;

import com.porter.porter.driver.entity.Driver;
import com.porter.porter.driver.enums.DriverStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DriverRepository extends JpaRepository<Driver, Long> {
    Optional<Driver> findByPhoneNumber(String phoneNumber);
    Optional<Driver> findByIqamaNumber(String iqamaNumber);
    List<Driver> findByStatus(DriverStatus status);
    List<Driver> findByActive(Boolean active);
}
