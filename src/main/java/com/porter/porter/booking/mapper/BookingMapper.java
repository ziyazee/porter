package com.porter.porter.booking.mapper;

import com.porter.porter.booking.dto.BookingResponse;
import com.porter.porter.booking.entity.Booking;
import com.porter.porter.booking.entity.BookingSpecialRequirement;
import com.porter.porter.booking.enums.SpecialRequirement;

import java.util.List;
import java.util.stream.Collectors;

public class BookingMapper {

    public static BookingResponse toResponse(Booking booking) {
        List<SpecialRequirement> specialRequirements = null;
        if (booking.getSpecialRequirements() != null) {
            specialRequirements = booking.getSpecialRequirements().stream()
                    .map(BookingSpecialRequirement::getRequirement)
                    .collect(Collectors.toList());
        }

        return BookingResponse.builder()
                .id(booking.getId())
                .bookingNumber(booking.getBookingNumber())
                .customerId(booking.getCustomerId())
                .pickupAddress(booking.getPickupAddress())
                .pickupLatitude(booking.getPickupLatitude())
                .pickupLongitude(booking.getPickupLongitude())
                .pickupDistrict(booking.getPickupDistrict())
                .pickupZone(booking.getPickupZone())
                .dropAddress(booking.getDropAddress())
                .dropLatitude(booking.getDropLatitude())
                .dropLongitude(booking.getDropLongitude())
                .dropDistrict(booking.getDropDistrict())
                .dropZone(booking.getDropZone())
                .cargoType(booking.getCargoType())
                .cargoDescription(booking.getCargoDescription())
                .weightRange(booking.getWeightRange())
                .selectedVehicleType(booking.getSelectedVehicleType())
                .basePrice(booking.getBasePrice())
                .additionalCharges(booking.getAdditionalCharges())
                .totalPrice(booking.getTotalPrice())
                .currency(booking.getCurrency())
                .paymentMethod(booking.getPaymentMethod())
                .status(booking.getStatus())
                .trackingToken(booking.getTrackingToken())
                .trackingUrl(null) // Will be set separately
                .pickupContactName(booking.getPickupContactName())
                .pickupContactPhone(booking.getPickupContactPhone())
                .dropContactName(booking.getDropContactName())
                .dropContactPhone(booking.getDropContactPhone())
                .specialRequirements(specialRequirements)
                .createdAt(booking.getCreatedAt())
                .updatedAt(booking.getUpdatedAt())
                .build();
    }
}
