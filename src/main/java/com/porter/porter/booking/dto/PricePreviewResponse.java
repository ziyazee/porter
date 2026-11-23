package com.porter.porter.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PricePreviewResponse {
    private String pickupZone;
    private String dropZone;
    private Double basePrice;
    private Double additionalCharges;
    private Double totalPrice;
    private String currency;
    private List<VehicleTypeOption> availableVehicleTypes;
}
