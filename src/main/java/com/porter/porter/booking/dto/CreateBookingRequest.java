package com.porter.porter.booking.dto;

import com.porter.porter.booking.enums.CargoType;
import com.porter.porter.booking.enums.PaymentMethod;
import com.porter.porter.booking.enums.SpecialRequirement;
import com.porter.porter.booking.enums.VehicleType;
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
public class CreateBookingRequest {
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
    
    private String cargoDescription;
    
    @NotNull(message = "Weight range is required")
    private WeightRange weightRange;
    
    private List<SpecialRequirement> specialRequirements;
    
    @NotBlank(message = "Pickup contact name is required")
    private String pickupContactName;
    
    @NotBlank(message = "Pickup contact phone is required")
    @Pattern(regexp = "^\\+966[0-9]{9}$", message = "Phone number must be in Saudi format +966XXXXXXXXX")
    private String pickupContactPhone;
    
    @NotBlank(message = "Drop contact name is required")
    private String dropContactName;
    
    @NotBlank(message = "Drop contact phone is required")
    @Pattern(regexp = "^\\+966[0-9]{9}$", message = "Phone number must be in Saudi format +966XXXXXXXXX")
    private String dropContactPhone;
    
    @NotNull(message = "Vehicle type is required")
    private VehicleType selectedVehicleType;
    
    @NotNull(message = "Payment method is required")
    private PaymentMethod paymentMethod;
}
