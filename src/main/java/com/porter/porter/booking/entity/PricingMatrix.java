package com.porter.porter.booking.entity;

import com.porter.porter.booking.enums.VehicleType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "pricing_matrix")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PricingMatrix {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "from_zone", nullable = false)
    private String fromZone;

    @Column(name = "to_zone", nullable = false)
    private String toZone;

    @Enumerated(EnumType.STRING)
    @Column(name = "vehicle_type", nullable = false)
    private VehicleType vehicleType;

    @Column(name = "base_price", nullable = false)
    private Double basePrice;

    @Column(name = "active", nullable = false)
    private Boolean active;
}
