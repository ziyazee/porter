package com.porter.porter.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.porter.porter.booking.dto.*;
import com.porter.porter.booking.service.BookingService;
import com.porter.porter.security.TestSecurityConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestSecurityConfig.class)
public class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BookingService bookingService;

    @MockBean
    private Authentication authentication;

    private PricePreviewRequest pricePreviewRequest;
    private CreateBookingRequest createBookingRequest;
    private PricePreviewResponse pricePreviewResponse;
    private CreateBookingResponse createBookingResponse;
    private BookingResponse bookingResponse;

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
                com.porter.porter.booking.enums.CargoType.CONSTRUCTION_MATERIAL,
                com.porter.porter.booking.enums.WeightRange.KG_500_1000,
                List.of(com.porter.porter.booking.enums.SpecialRequirement.LOADING_ASSISTANCE)
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
                com.porter.porter.booking.enums.CargoType.CONSTRUCTION_MATERIAL,
                "100 cement bags",
                com.porter.porter.booking.enums.WeightRange.KG_500_1000,
                List.of(com.porter.porter.booking.enums.SpecialRequirement.LOADING_ASSISTANCE),
                "Ahmed",
                "+966501234567",
                "Mohammed",
                "+966509999999",
                com.porter.porter.booking.enums.VehicleType.PICKUP_TRUCK,
                com.porter.porter.booking.enums.PaymentMethod.CASH_TO_DRIVER
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
                                .vehicleType(com.porter.porter.booking.enums.VehicleType.PICKUP_TRUCK)
                                .capacity("Up to 1 Ton")
                                .estimatedAvailability("Available Now")
                                .price(140.0)
                                .build()
                ))
                .build();

        createBookingResponse = CreateBookingResponse.builder()
                .bookingId(1001L)
                .bookingNumber("CG-2026-1234")
                .status(com.porter.porter.booking.enums.BookingStatus.PENDING_ASSIGNMENT)
                .pickupZone("ZONE_A")
                .dropZone("ZONE_B")
                .totalPrice(140.0)
                .currency("SAR")
                .paymentMethod(com.porter.porter.booking.enums.PaymentMethod.CASH_TO_DRIVER)
                .trackingToken("abc123xyz789")
                .trackingUrl("https://cargogo.sa/t/abc123xyz789")
                .build();

        bookingResponse = BookingResponse.builder()
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
                .cargoType(com.porter.porter.booking.enums.CargoType.CONSTRUCTION_MATERIAL)
                .cargoDescription("100 cement bags")
                .weightRange(com.porter.porter.booking.enums.WeightRange.KG_500_1000)
                .selectedVehicleType(com.porter.porter.booking.enums.VehicleType.PICKUP_TRUCK)
                .basePrice(120.0)
                .additionalCharges(20.0)
                .totalPrice(140.0)
                .currency("SAR")
                .paymentMethod(com.porter.porter.booking.enums.PaymentMethod.CASH_TO_DRIVER)
                .status(com.porter.porter.booking.enums.BookingStatus.PENDING_ASSIGNMENT)
                .trackingToken("abc123xyz789")
                .trackingUrl("https://cargogo.sa/t/abc123xyz789")
                .pickupContactName("Ahmed")
                .pickupContactPhone("+966501234567")
                .dropContactName("Mohammed")
                .dropContactPhone("+966509999999")
                .specialRequirements(List.of(com.porter.porter.booking.enums.SpecialRequirement.LOADING_ASSISTANCE))
                .build();

        when(authentication.getPrincipal()).thenReturn(1L);
        when(authentication.getAuthorities()).thenReturn((Collection) Collections.singletonList(new SimpleGrantedAuthority("ROLE_CUSTOMER")));
    }

    @Test
    void testGetPricePreview_Success() throws Exception {
        when(bookingService.getPricePreview(any(PricePreviewRequest.class)))
                .thenReturn(pricePreviewResponse);

        mockMvc.perform(post("/api/v1/bookings/price-preview")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pricePreviewRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pickupZone").value("ZONE_A"))
                .andExpect(jsonPath("$.dropZone").value("ZONE_B"))
                .andExpect(jsonPath("$.totalPrice").value(140.0))
                .andExpect(jsonPath("$.currency").value("SAR"));
    }

    @Test
    void testGetPricePreview_ValidationError() throws Exception {
        PricePreviewRequest invalidRequest = new PricePreviewRequest(
                "", // Invalid: empty pickup address
                26.4207,
                50.0888,
                "Al Faisaliyah",
                "Al Shati, Dammam",
                26.4512,
                50.1122,
                "Al Shati",
                com.porter.porter.booking.enums.CargoType.CONSTRUCTION_MATERIAL,
                com.porter.porter.booking.enums.WeightRange.KG_500_1000,
                null
        );

        mockMvc.perform(post("/api/v1/bookings/price-preview")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testCreateBooking_Success() throws Exception {
        when(bookingService.createBooking(any(CreateBookingRequest.class), eq(1L)))
                .thenReturn(createBookingResponse);

        mockMvc.perform(post("/api/v1/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createBookingRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bookingId").value(1001))
                .andExpect(jsonPath("$.bookingNumber").value("CG-2026-1234"))
                .andExpect(jsonPath("$.status").value("PENDING_ASSIGNMENT"))
                .andExpect(jsonPath("$.totalPrice").value(140.0));
    }

    @Test
    void testCreateBooking_ValidationError() throws Exception {
        CreateBookingRequest invalidRequest = new CreateBookingRequest(
                "", // Invalid: empty pickup address
                26.4207,
                50.0888,
                "Al Faisaliyah",
                "Al Shati, Dammam",
                26.4512,
                50.1122,
                "Al Shati",
                com.porter.porter.booking.enums.CargoType.CONSTRUCTION_MATERIAL,
                "100 cement bags",
                com.porter.porter.booking.enums.WeightRange.KG_500_1000,
                null,
                "Ahmed",
                "+966501234567",
                "Mohammed",
                "+966509999999",
                com.porter.porter.booking.enums.VehicleType.PICKUP_TRUCK,
                com.porter.porter.booking.enums.PaymentMethod.CASH_TO_DRIVER
        );

        mockMvc.perform(post("/api/v1/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetMyBookings_Success() throws Exception {
        when(bookingService.getMyBookings(1L))
                .thenReturn(List.of(bookingResponse));

        mockMvc.perform(get("/api/v1/bookings/my")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1001))
                .andExpect(jsonPath("$[0].bookingNumber").value("CG-2026-1234"));
    }

    @Test
    void testGetBookingById_Success() throws Exception {
        when(bookingService.getBookingById(1001L, 1L))
                .thenReturn(bookingResponse);

        mockMvc.perform(get("/api/v1/bookings/1001")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1001))
                .andExpect(jsonPath("$.bookingNumber").value("CG-2026-1234"))
                .andExpect(jsonPath("$.trackingUrl").value("https://cargogo.sa/t/abc123xyz789"));
    }
}
