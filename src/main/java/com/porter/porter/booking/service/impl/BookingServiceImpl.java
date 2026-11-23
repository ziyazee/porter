package com.porter.porter.booking.service.impl;

import com.porter.porter.booking.dto.*;
import com.porter.porter.booking.entity.Booking;
import com.porter.porter.booking.entity.BookingSpecialRequirement;
import com.porter.porter.booking.enums.BookingStatus;
import com.porter.porter.booking.enums.SpecialRequirement;
import com.porter.porter.booking.exception.AccessDeniedException;
import com.porter.porter.booking.exception.BookingNotFoundException;
import com.porter.porter.booking.exception.DistrictNotServiceableException;
import com.porter.porter.booking.mapper.BookingMapper;
import com.porter.porter.booking.repository.BookingRepository;
import com.porter.porter.booking.service.BookingService;
import com.porter.porter.booking.service.PricingService;
import com.porter.porter.booking.util.TrackingTokenGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookingServiceImpl implements BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private PricingService pricingService;

    private static final String CURRENCY = "SAR";

    @Override
    public PricePreviewResponse getPricePreview(PricePreviewRequest request) {
        return pricingService.calculatePricePreview(request);
    }

    @Override
    @Transactional
    public CreateBookingResponse createBooking(CreateBookingRequest request, Long customerId) {
        // Validate pickup and drop districts are serviceable
        String pickupZone = pricingService.mapDistrictToZone(request.getPickupDistrict());
        String dropZone = pricingService.mapDistrictToZone(request.getDropDistrict());

        if (pickupZone == null) {
            throw new DistrictNotServiceableException("Pickup district '" + request.getPickupDistrict() + "' is not serviceable");
        }

        if (dropZone == null) {
            throw new DistrictNotServiceableException("Drop district '" + request.getDropDistrict() + "' is not serviceable");
        }

        // Calculate pricing
        Double totalPrice = pricingService.calculateTotalPrice(
                pickupZone,
                dropZone,
                request.getSelectedVehicleType(),
                request.getSpecialRequirements()
        );

        // Generate booking number and tracking token
        String bookingNumber = TrackingTokenGenerator.generateBookingNumber();
        String trackingToken = TrackingTokenGenerator.generateTrackingToken();
        String trackingUrl = TrackingTokenGenerator.generateTrackingUrl(trackingToken);

        // Calculate additional charges
        Double basePrice = totalPrice - calculateSpecialRequirementCharges(request.getSpecialRequirements());
        Double additionalCharges = calculateSpecialRequirementCharges(request.getSpecialRequirements());

        // Create booking
        Booking booking = Booking.builder()
                .bookingNumber(bookingNumber)
                .customerId(customerId)
                .pickupAddress(request.getPickupAddress())
                .pickupLatitude(request.getPickupLatitude())
                .pickupLongitude(request.getPickupLongitude())
                .pickupDistrict(request.getPickupDistrict())
                .pickupZone(pickupZone)
                .dropAddress(request.getDropAddress())
                .dropLatitude(request.getDropLatitude())
                .dropLongitude(request.getDropLongitude())
                .dropDistrict(request.getDropDistrict())
                .dropZone(dropZone)
                .cargoType(request.getCargoType())
                .cargoDescription(request.getCargoDescription())
                .weightRange(request.getWeightRange())
                .selectedVehicleType(request.getSelectedVehicleType())
                .basePrice(basePrice)
                .additionalCharges(additionalCharges)
                .totalPrice(totalPrice)
                .currency(CURRENCY)
                .paymentMethod(request.getPaymentMethod())
                .status(BookingStatus.PENDING_ASSIGNMENT)
                .trackingToken(trackingToken)
                .pickupContactName(request.getPickupContactName())
                .pickupContactPhone(request.getPickupContactPhone())
                .dropContactName(request.getDropContactName())
                .dropContactPhone(request.getDropContactPhone())
                .build();

        // Handle special requirements
        if (request.getSpecialRequirements() != null && !request.getSpecialRequirements().isEmpty()) {
            List<BookingSpecialRequirement> specialRequirements = request.getSpecialRequirements().stream()
                    .map(req -> BookingSpecialRequirement.builder()
                            .booking(booking)
                            .requirement(req)
                            .build())
                    .collect(Collectors.toList());
            booking.setSpecialRequirements(specialRequirements);
        }

        Booking savedBooking = bookingRepository.save(booking);

        return CreateBookingResponse.builder()
                .bookingId(savedBooking.getId())
                .bookingNumber(booking.getBookingNumber())
                .status(booking.getStatus())
                .pickupZone(booking.getPickupZone())
                .dropZone(booking.getDropZone())
                .totalPrice(booking.getTotalPrice())
                .currency(booking.getCurrency())
                .paymentMethod(booking.getPaymentMethod())
                .trackingToken(booking.getTrackingToken())
                .trackingUrl(trackingUrl)
                .build();
    }

    @Override
    public List<BookingResponse> getMyBookings(Long customerId) {
        List<Booking> bookings = bookingRepository.findByCustomerId(customerId);
        return bookings.stream()
                .map(booking -> {
                    BookingResponse response = BookingMapper.toResponse(booking);
                    response.setTrackingUrl(TrackingTokenGenerator.generateTrackingUrl(booking.getTrackingToken()));
                    return response;
                })
                .collect(Collectors.toList());
    }

    @Override
    public BookingResponse getBookingById(Long bookingId, Long customerId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException("Booking not found"));

        // Security check: ensure customer can only access their own bookings
        if (!booking.getCustomerId().equals(customerId)) {
            throw new AccessDeniedException("Access denied: You can only view your own bookings");
        }

        BookingResponse response = BookingMapper.toResponse(booking);
        response.setTrackingUrl(TrackingTokenGenerator.generateTrackingUrl(booking.getTrackingToken()));
        return response;
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
}
