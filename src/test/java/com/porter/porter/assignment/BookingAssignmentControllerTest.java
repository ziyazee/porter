package com.porter.porter.assignment;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.porter.porter.assignment.dto.BookingAssignmentRequest;
import com.porter.porter.assignment.dto.BookingAssignmentResponse;
import com.porter.porter.assignment.service.BookingAssignmentService;
import com.porter.porter.booking.enums.BookingStatus;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestSecurityConfig.class)
public class BookingAssignmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BookingAssignmentService bookingAssignmentService;

    @MockBean
    private Authentication authentication;

    private BookingAssignmentRequest assignmentRequest;
    private BookingAssignmentResponse assignmentResponse;

    @BeforeEach
    void setUp() {
        assignmentRequest = BookingAssignmentRequest.builder()
                .vehicleId(10L)
                .driverId(5L)
                .build();

        assignmentResponse = BookingAssignmentResponse.builder()
                .bookingId(1001L)
                .bookingNumber("CG-2026-1001")
                .status(BookingStatus.DRIVER_ASSIGNED)
                .vehicle(BookingAssignmentResponse.VehicleInfo.builder()
                        .id(10L)
                        .vehicleNumber("KSA1234")
                        .build())
                .driver(BookingAssignmentResponse.DriverInfo.builder()
                        .id(5L)
                        .fullName("Ahmed Ali")
                        .build())
                .build();

        when(authentication.getPrincipal()).thenReturn(1L);
        when(authentication.getAuthorities()).thenReturn((Collection) Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN")));
    }

    @Test
    void testAssignBooking_Success() throws Exception {
        when(bookingAssignmentService.assignBooking(eq(1001L), any(BookingAssignmentRequest.class), eq(1L)))
                .thenReturn(assignmentResponse);

        mockMvc.perform(post("/api/v1/admin/bookings/1001/assign")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(assignmentRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bookingId").value(1001))
                .andExpect(jsonPath("$.bookingNumber").value("CG-2026-1001"))
                .andExpect(jsonPath("$.status").value("DRIVER_ASSIGNED"))
                .andExpect(jsonPath("$.vehicle.id").value(10))
                .andExpect(jsonPath("$.vehicle.vehicleNumber").value("KSA1234"))
                .andExpect(jsonPath("$.driver.id").value(5))
                .andExpect(jsonPath("$.driver.fullName").value("Ahmed Ali"));
    }

    @Test
    void testAssignBooking_ValidationError() throws Exception {
        BookingAssignmentRequest invalidRequest = BookingAssignmentRequest.builder()
                .build();

        mockMvc.perform(post("/api/v1/admin/bookings/1001/assign")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testAssignBooking_MissingVehicleId() throws Exception {
        BookingAssignmentRequest invalidRequest = BookingAssignmentRequest.builder()
                .driverId(5L)
                .build();

        mockMvc.perform(post("/api/v1/admin/bookings/1001/assign")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testAssignBooking_MissingDriverId() throws Exception {
        BookingAssignmentRequest invalidRequest = BookingAssignmentRequest.builder()
                .vehicleId(10L)
                .build();

        mockMvc.perform(post("/api/v1/admin/bookings/1001/assign")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }
}
