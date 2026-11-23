package com.porter.porter.booking.service;

import com.porter.porter.booking.dto.PricePreviewRequest;
import com.porter.porter.booking.dto.PricePreviewResponse;
import com.porter.porter.booking.dto.VehicleTypeOption;

import java.util.List;

public interface PricingService {
    PricePreviewResponse calculatePricePreview(PricePreviewRequest request);
    Double calculateTotalPrice(String pickupZone, String dropZone, com.porter.porter.booking.enums.VehicleType vehicleType, List<com.porter.porter.booking.enums.SpecialRequirement> specialRequirements);
    String mapDistrictToZone(String districtName);
    void initializeSamplePricingData();
}
