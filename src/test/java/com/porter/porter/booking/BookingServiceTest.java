package com.porter.porter.booking;

import com.porter.porter.booking.dto.*;
import com.porter.porter.booking.entity.Booking;
import com.porter.porter.booking.enums.*;
import com.porter.porter.booking.mapper.BookingMapper;
import com.porter.porter.booking.repository.BookingRepository;
import com.porter.porter.booking.service.BookingService;
import com.porter.porter.booking.service.PricingService;
import com.porter.porter.booking.util.TrackingTokenGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private PricingService pricingService;

    @InjectMocks
    private com.porter.porter.booking.service.impl.BookingServiceImpl bookingService;

    private PricePreviewRequest pricePreviewRequest;
    private CreateBookingRequest createBookingRequest;
    private PricePreviewResponse pricePreviewResponse;
    private Booking booking;

    @BeforeEach
    void setUp() {
        pricePreviewRequest = new PricePreviewRequest(
                "King Fahd Road, Dammam",
                26.4207,
                50.0888,
                "Al Faisaliyah",
                "Al Shati, Dammam",
                26.4512,
                50.1122,
                "Al Shati",
                CargoType.CONSTRUCTION_MATERIAL,
                WeightRange.KG_500_1000,
                List.of(SpecialRequirement.LOADING_ASSISTANCE)
        );

        createBookingRequest = new CreateBookingRequest(
                "King Fahd Road, Dammam",
                26.4207,
                50.0888,
                "Al Faisaliyah",
                "Al Shati, Dammam",
                26.4512,
                50.1122,
                "Al Shati",
                CargoType.CONSTRUCTION_MATERIAL,
                "100 cement bags",
                WeightRange.KG_500_1000,
                List.of(SpecialRequirement.LOADING_ASSISTANCE),
                "Ahmed",
                "+966501234567",
                "Mohammed",
                "+966509999999",
                VehicleType.PICKUP_TRUCK,
                PaymentMethod.CASH_TO_DRIVER
        );

        pricePreviewResponse = PricePreviewResponse.builder()
                .pickupZone("ZONE_A")
                .dropZone("ZONE_B")
                .basePrice(120.0)
                .additionalCharges(20.0)
                .totalPrice(140.0)
                .currency("SAR")
                .availableVehicleTypes(List.of(
                        VehicleTypeOption.builder()
                                .vehicleType(VehicleType.PICKUP_TRUCK)
                                .capacity("Up to 1 Ton")
                                .estimatedAvailability("Available Now")
                                .price(140.0)
                                .build()
                ))
                .build();

        booking = Booking.builder()
                .id(1001L)
                .bookingNumber("CG-2026-1234")
                .customerId(1L)
                .pickupAddress("King Fahd Road, Dammam")
                .pickupLatitude(26.4207)
                .pickupLongitude(50.0888)
                .pickupDistrict("Al Faisaliyah")
                .pickupZone("ZONE_A")
                .dropAddress("Al Shati, Dammam")
                .dropLatitude(26.4512)
                .dropLongitude(50.1122)
                .dropDistrict("Al Shati")
                .dropZone("ZONE_B")
                .cargoType(CargoType.CONSTRUCTION_MATERIAL)
                .cargoDescription("100 cement bags")
                .weightRange(WeightRange.KG_500_1000)
                .selectedVehicleType(VehicleType.PICKUP_TRUCK)
                .basePrice(120.0)
                .additionalCharges(20.0)
                .totalPrice(140.0)
                .currency("SAR")
                .paymentMethod(PaymentMethod.CASH_TO_DRIVER)
                .status(BookingStatus.PENDING_ASSIGNMENT)
                .trackingToken("abc123xyz789")
                .pickupContactName("Ahmed")
                .pickupContactPhone("+966501234567")
                .dropContactName("Mohammed")
                .dropContactPhone("+966509999999")
                .specialRequirements(List.of())
                .build();
    }

    @Test
    void testGetPricePreview_Success() {
        when(pricingService.calculatePricePreview(any(PricePreviewRequest.class)))
                .thenReturn(pricePreviewResponse);

        PricePreviewResponse response = bookingService.getPricePreview(pricePreviewRequest);

        assertNotNull(response);
        assertEquals("ZONE_A", response.getPickupZone());
        assertEquals("ZONE_B", response.getDropZone());
        assertEquals(140.0, response.getTotalPrice());
        assertEquals("SAR", response.getCurrency());
        assertEquals(1, response.getAvailableVehicleTypes().size());

        verify(pricingService, times(1)).calculatePricePreview(any(PricePreviewRequest.class));
    }

    @Test
    void testCreateBooking_Success() {
        when(pricingService.mapDistrictToZone("Al Faisaliyah")).thenReturn("ZONE_A");
        when(pricingService.mapDistrictToZone("Al Shati")).thenReturn("ZONE_B");
        when(pricingService.calculateTotalPrice(eq("ZONE_A"), eq("ZONE_B"), eq(VehicleType.PICKUP_TRUCK), any()))
                .thenReturn(140.0);
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        CreateBookingResponse response = bookingService.createBooking(createBookingRequest, 1L);

        assertNotNull(response);
        assertEquals(1001L, response.getBookingId());
        assertEquals("CG-2026-1234", response.getBookingNumber());
        assertEquals(BookingStatus.PENDING_ASSIGNMENT, response.getStatus());
        assertEquals(140.0, response.getTotalPrice());
        assertEquals(PaymentMethod.CASH_TO_DRIVER, response.getPaymentMethod());

        verify(bookingRepository, times(1)).save(any(Booking.class));
    }

    @Test
    void testCreateBooking_PickupDistrictNotServiceable() {
        when(pricingService.mapDistrictToZone("Al Faisaliyah")).thenReturn("ZONE_A");
        when(pricingService.mapDistrictToZone("Al Shati")).thenReturn(null);

        assertThrows(com.porter.porter.booking.exception.DistrictNotServiceableException.class,
                () -> bookingService.createBooking(createBookingRequest, 1L));
    }

    @Test
    void testCreateBooking_DropDistrictNotServiceable() {
        when(pricingService.mapDistrictToZone("Al Faisaliyah")).thenReturn(null);

        assertThrows(com.porter.porter.booking.exception.DistrictNotServiceableException.class,
                () -> bookingService.createBooking(createBookingRequest, 1L));
    }

    @Test
    void testGetMyBookings_Success() {
        when(bookingRepository.findByCustomerId(1L)).thenReturn(List.of(booking));

        List<BookingResponse> responses = bookingService.getMyBookings(1L);

        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals(1001L, responses.get(0).getId());
        assertEquals("CG-2026-1234", responses.get(0).getBookingNumber());

        verify(bookingRepository, times(1)).findByCustomerId(1L);
    }

    @Test
    void testGetMyBookings_Empty() {
        when(bookingRepository.findByCustomerId(1L)).thenReturn(List.of());

        List<BookingResponse> responses = bookingService.getMyBookings(1L);

        assertNotNull(responses);
        assertEquals(0, responses.size());

        verify(bookingRepository, times(1)).findByCustomerId(1L);
    }

    @Test
    void testGetBookingById_Success() {
        when(bookingRepository.findById(1001L)).thenReturn(Optional.of(booking));

        BookingResponse response = bookingService.getBookingById(1001L, 1L);

        assertNotNull(response);
        assertEquals(1001L, response.getId());
        assertEquals("CG-2026-1234", response.getBookingNumber());
        assertEquals(1L, response.getCustomerId());

        verify(bookingRepository, times(1)).findById(1001L);
    }

    @Test
    void testGetBookingById_NotFound() {
        when(bookingRepository.findById(1001L)).thenReturn(Optional.empty());

        assertThrows(com.porter.porter.booking.exception.BookingNotFoundException.class,
                () -> bookingService.getBookingById(1001L, 1L));
    }

    @Test
    void testGetBookingById_AccessDenied() {
        Booking otherBooking = Booking.builder()
                .id(1002L)
                .bookingNumber("CG-2026-5678")
                .customerId(2L) // Different customer
                .build();

        when(bookingRepository.findById(1002L)).thenReturn(Optional.of(otherBooking));

        assertThrows(com.porter.porter.booking.exception.AccessDeniedException.class,
                () -> bookingService.getBookingById(1002L, 1L));
    }
}
