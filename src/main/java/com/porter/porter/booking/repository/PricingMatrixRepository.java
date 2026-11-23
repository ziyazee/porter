package com.porter.porter.booking.repository;

import com.porter.porter.booking.entity.PricingMatrix;
import com.porter.porter.booking.enums.VehicleType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PricingMatrixRepository extends JpaRepository<PricingMatrix, Long> {
    Optional<PricingMatrix> findByFromZoneAndToZoneAndVehicleTypeAndActiveTrue(String fromZone, String toZone, VehicleType vehicleType);
    List<PricingMatrix> findByActiveTrue();
}
