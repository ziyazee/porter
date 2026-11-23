package com.porter.porter.booking.service.impl;

import com.porter.porter.booking.dto.PricePreviewRequest;
import com.porter.porter.booking.dto.PricePreviewResponse;
import com.porter.porter.booking.dto.VehicleTypeOption;
import com.porter.porter.booking.entity.PricingMatrix;
import com.porter.porter.booking.entity.PricingZone;
import com.porter.porter.booking.enums.SpecialRequirement;
import com.porter.porter.booking.enums.VehicleType;
import com.porter.porter.booking.exception.DistrictNotServiceableException;
import com.porter.porter.booking.exception.PricingNotAvailableException;
import com.porter.porter.booking.repository.PricingMatrixRepository;
import com.porter.porter.booking.repository.PricingZoneRepository;
import com.porter.porter.booking.service.PricingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class PricingServiceImpl implements PricingService {
    
    @Autowired
    private PricingZoneRepository pricingZoneRepository;
    
    @Autowired
    private PricingMatrixRepository pricingMatrixRepository;
    
    private static final String CURRENCY = "SAR";
    
    @Override
    @PostConstruct
    public void initializeSamplePricingData() {
        // Check if pricing zones already exist
        if (pricingZoneRepository.count() > 0) {
            return; // Data already initialized
        }

        // Initialize sample pricing zones
        pricingZoneRepository.save(PricingZone.builder()
                .districtName("Al Faisaliyah")
                .zoneCode("ZONE_A")
                .active(true)
                .build());

        pricingZoneRepository.save(PricingZone.builder()
                .districtName("Al Shati")
                .zoneCode("ZONE_B")
                .active(true)
                .build());

        pricingZoneRepository.save(PricingZone.builder()
                .districtName("Al Manar")
                .zoneCode("ZONE_A")
                .active(true)
                .build());

        pricingZoneRepository.save(PricingZone.builder()
                .districtName("Al Khobar")
                .zoneCode("ZONE_C")
                .active(true)
                .build());

        // Initialize sample pricing matrix
        pricingMatrixRepository.save(PricingMatrix.builder()
                .fromZone("ZONE_A")
                .toZone("ZONE_B")
                .vehicleType(VehicleType.MINI_TRUCK)
                .basePrice(100.0)
                .active(true)
                .build());

        pricingMatrixRepository.save(PricingMatrix.builder()
                .fromZone("ZONE_A")
                .toZone("ZONE_B")
                .vehicleType(VehicleType.PICKUP_TRUCK)
                .basePrice(120.0)
                .active(true)
                .build());

        pricingMatrixRepository.save(PricingMatrix.builder()
                .fromZone("ZONE_A")
                .toZone("ZONE_B")
                .vehicleType(VehicleType.FLATBED_TRUCK)
                .basePrice(150.0)
                .active(true)
                .build());

        pricingMatrixRepository.save(PricingMatrix.builder()
                .fromZone("ZONE_A")
                .toZone("ZONE_B")
                .vehicleType(VehicleType.HEAVY_TRUCK)
                .basePrice(200.0)
                .active(true)
                .build());

        pricingMatrixRepository.save(PricingMatrix.builder()
                .fromZone("ZONE_A")
                .toZone("ZONE_C")
                .vehicleType(VehicleType.MINI_TRUCK)
                .basePrice(150.0)
                .active(true)
                .build());

        pricingMatrixRepository.save(PricingMatrix.builder()
                .fromZone("ZONE_A")
                .toZone("ZONE_C")
                .vehicleType(VehicleType.PICKUP_TRUCK)
                .basePrice(180.0)
                .active(true)
                .build());

        pricingMatrixRepository.save(PricingMatrix.builder()
                .fromZone("ZONE_B")
                .toZone("ZONE_A")
                .vehicleType(VehicleType.MINI_TRUCK)
                .basePrice(100.0)
                .active(true)
                .build());

        pricingMatrixRepository.save(PricingMatrix.builder()
                .fromZone("ZONE_B")
                .toZone("ZONE_A")
                .vehicleType(VehicleType.PICKUP_TRUCK)
                .basePrice(120.0)
                .active(true)
                .build());
    }
    
    @Override
    public PricePreviewResponse calculatePricePreview(PricePreviewRequest request) {
        // Map districts to zones
        String pickupZone = mapDistrictToZone(request.getPickupDistrict());
        String dropZone = mapDistrictToZone(request.getDropDistrict());
        
        if (pickupZone == null) {
            throw new DistrictNotServiceableException("Pickup district '" + request.getPickupDistrict() + "' is not serviceable");
        }
        
        if (dropZone == null) {
            throw new DistrictNotServiceableException("Drop district '" + request.getDropDistrict() + "' is not serviceable");
        }
        
        // Get available vehicle types and their prices
        List<VehicleTypeOption> vehicleTypeOptions = new ArrayList<>();
        for (VehicleType vehicleType : VehicleType.values()) {
            Optional<PricingMatrix> pricing = pricingMatrixRepository
                    .findByFromZoneAndToZoneAndVehicleTypeAndActiveTrue(pickupZone, dropZone, vehicleType);

            if (pricing.isPresent()) {
                Double basePrice = pricing.get().getBasePrice();
                Double additionalCharges = calculateSpecialRequirementCharges(request.getSpecialRequirements());
                Double totalPrice = basePrice + additionalCharges;

                vehicleTypeOptions.add(VehicleTypeOption.builder()
                        .vehicleType(vehicleType)
                        .capacity(getVehicleCapacity(vehicleType))
                        .estimatedAvailability("Available Now")
                        .price(totalPrice)
                        .build());
            }
        }
        
        if (vehicleTypeOptions.isEmpty()) {
            throw new PricingNotAvailableException("No vehicle types available for this route");
        }
        
        // Use the first vehicle type's price for the summary
        Double basePrice = vehicleTypeOptions.get(0).getPrice();
        Double additionalCharges = calculateSpecialRequirementCharges(request.getSpecialRequirements());
        Double totalPrice = basePrice + additionalCharges;
        
        return PricePreviewResponse.builder()
                .pickupZone(pickupZone)
                .dropZone(dropZone)
                .basePrice(basePrice)
                .additionalCharges(additionalCharges)
                .totalPrice(totalPrice)
                .currency(CURRENCY)
                .availableVehicleTypes(vehicleTypeOptions)
                .build();
    }
    
    @Override
    public Double calculateTotalPrice(String pickupZone, String dropZone, VehicleType vehicleType, List<SpecialRequirement> specialRequirements) {
        Optional<PricingMatrix> pricing = pricingMatrixRepository
                .findByFromZoneAndToZoneAndVehicleTypeAndActiveTrue(pickupZone, dropZone, vehicleType);

        if (!pricing.isPresent()) {
            throw new PricingNotAvailableException("No pricing available for this route and vehicle type");
        }

        Double basePrice = pricing.get().getBasePrice();
        Double additionalCharges = calculateSpecialRequirementCharges(specialRequirements);

        return basePrice + additionalCharges;
    }
    
    @Override
    public String mapDistrictToZone(String districtName) {
        Optional<PricingZone> pricingZone = pricingZoneRepository.findByDistrictName(districtName);
        return pricingZone.map(PricingZone::getZoneCode).orElse(null);
    }
    
    private Double calculateSpecialRequirementCharges(List<SpecialRequirement> specialRequirements) {
        if (specialRequirements == null || specialRequirements.isEmpty()) {
            return 0.0;
        }
        
        Double charges = 0.0;
        for (SpecialRequirement requirement : specialRequirements) {
            switch (requirement) {
                case LOADING_ASSISTANCE:
                    charges += 10.0;
                    break;
                case UNLOADING_ASSISTANCE:
                    charges += 10.0;
                    break;
                case FRAGILE_GOODS:
                    charges += 15.0;
                    break;
                case URGENT_DELIVERY:
                    charges += 25.0;
                    break;
                case CRANE_REQUIRED:
                    charges += 100.0;
                    break;
                case TAIL_LIFT_REQUIRED:
                    charges += 30.0;
                    break;
            }
        }
        return charges;
    }
    
    private String getVehicleCapacity(VehicleType vehicleType) {
        switch (vehicleType) {
            case MINI_TRUCK:
                return "Up to 500 KG";
            case PICKUP_TRUCK:
                return "Up to 1 Ton";
            case FLATBED_TRUCK:
                return "Up to 3 Tons";
            case HEAVY_TRUCK:
                return "Up to 10 Tons";
            default:
                return "Unknown";
        }
    }
}
