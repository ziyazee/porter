package com.porter.porter.vehicle.dto;

import com.porter.porter.booking.enums.VehicleType;
import com.porter.porter.vehicle.enums.VehicleStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VehicleResponse {
    private Long id;
    private String vehicleNumber;
    private VehicleType vehicleType;
    private Double capacityKg;
    private VehicleStatus status;
    private Boolean active;
    private String wialonUnitId;
    private Long currentDriverId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
