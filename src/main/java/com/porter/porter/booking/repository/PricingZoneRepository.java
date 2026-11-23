package com.porter.porter.booking.repository;

import com.porter.porter.booking.entity.PricingZone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PricingZoneRepository extends JpaRepository<PricingZone, Long> {
    Optional<PricingZone> findByDistrictName(String districtName);
}
