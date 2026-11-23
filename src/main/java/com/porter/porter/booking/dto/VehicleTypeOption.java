package com.porter.porter.booking.dto;

import com.porter.porter.booking.enums.VehicleType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VehicleTypeOption {
    private VehicleType vehicleType;
    private String capacity;
    private String estimatedAvailability;
    private Double price;
}
