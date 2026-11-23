package com.porter.porter.booking.dto;

import com.porter.porter.booking.enums.CargoType;
import com.porter.porter.booking.enums.SpecialRequirement;
import com.porter.porter.booking.enums.WeightRange;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PricePreviewRequest {
    @NotBlank(message = "Pickup address is required")
    private String pickupAddress;
    
    @NotNull(message = "Pickup latitude is required")
    private Double pickupLatitude;
    
    @NotNull(message = "Pickup longitude is required")
    private Double pickupLongitude;
    
    @NotBlank(message = "Pickup district is required")
    private String pickupDistrict;
    
    @NotBlank(message = "Drop address is required")
    private String dropAddress;
    
    @NotNull(message = "Drop latitude is required")
    private Double dropLatitude;
    
    @NotNull(message = "Drop longitude is required")
    private Double dropLongitude;
    
    @NotBlank(message = "Drop district is required")
    private String dropDistrict;
    
    @NotNull(message = "Cargo type is required")
    private CargoType cargoType;
    
    @NotNull(message = "Weight range is required")
    private WeightRange weightRange;
    
    private List<SpecialRequirement> specialRequirements;
}
