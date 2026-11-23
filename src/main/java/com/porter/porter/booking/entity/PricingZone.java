package com.porter.porter.booking.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "pricing_zones")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PricingZone {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "district_name", unique = true, nullable = false)
    private String districtName;

    @Column(name = "zone_code", nullable = false)
    private String zoneCode;

    @Column(name = "active", nullable = false)
    private Boolean active;
}
