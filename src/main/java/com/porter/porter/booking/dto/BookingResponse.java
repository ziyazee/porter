package com.porter.porter.booking.dto;

import com.porter.porter.booking.enums.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingResponse {
    private Long id;
    private String bookingNumber;
    private Long customerId;
    private String pickupAddress;
    private Double pickupLatitude;
    private Double pickupLongitude;
    private String pickupDistrict;
    private String pickupZone;
    private String dropAddress;
    private Double dropLatitude;
    private Double dropLongitude;
    private String dropDistrict;
    private String dropZone;
    private CargoType cargoType;
    private String cargoDescription;
    private WeightRange weightRange;
    private VehicleType selectedVehicleType;
    private Double basePrice;
    private Double additionalCharges;
    private Double totalPrice;
    private String currency;
    private PaymentMethod paymentMethod;
    private BookingStatus status;
    private String trackingToken;
    private String trackingUrl;
    private String pickupContactName;
    private String pickupContactPhone;
    private String dropContactName;
    private String dropContactPhone;
    private List<SpecialRequirement> specialRequirements;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
