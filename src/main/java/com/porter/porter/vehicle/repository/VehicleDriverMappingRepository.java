package com.porter.porter.vehicle.repository;

import com.porter.porter.vehicle.entity.VehicleDriverMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface VehicleDriverMappingRepository extends JpaRepository<VehicleDriverMapping, Long> {
    Optional<VehicleDriverMapping> findByVehicleIdAndActiveTrue(Long vehicleId);
    Optional<VehicleDriverMapping> findByDriverIdAndActiveTrue(Long driverId);
    List<VehicleDriverMapping> findByVehicleId(Long vehicleId);
    List<VehicleDriverMapping> findByDriverId(Long driverId);

    @Modifying
    @Transactional
    @Query("UPDATE VehicleDriverMapping v SET v.active = false WHERE v.vehicleId = :vehicleId AND v.active = true")
    void deactivateByVehicleId(@Param("vehicleId") Long vehicleId);

    @Modifying
    @Transactional
    @Query("UPDATE VehicleDriverMapping v SET v.active = false WHERE v.driverId = :driverId AND v.active = true")
    void deactivateByDriverId(@Param("driverId") Long driverId);
}
