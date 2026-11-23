package com.porter.porter.vehicle;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.porter.porter.booking.enums.VehicleType;
import com.porter.porter.vehicle.dto.VehicleRequest;
import com.porter.porter.vehicle.dto.VehicleResponse;
import com.porter.porter.vehicle.dto.VehicleStatusUpdateRequest;
import com.porter.porter.vehicle.enums.VehicleStatus;
import com.porter.porter.vehicle.service.VehicleService;
import com.porter.porter.security.TestSecurityConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestSecurityConfig.class)
public class VehicleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private VehicleService vehicleService;

    private VehicleRequest vehicleRequest;
    private VehicleResponse vehicleResponse;

    @BeforeEach
    void setUp() {
        vehicleRequest = VehicleRequest.builder()
                .vehicleNumber("KSA1234")
                .vehicleType(VehicleType.PICKUP_TRUCK)
                .capacityKg(1000.0)
                .wialonUnitId("unit_12345")
                .build();

        vehicleResponse = VehicleResponse.builder()
                .id(1L)
                .vehicleNumber("KSA1234")
                .vehicleType(VehicleType.PICKUP_TRUCK)
                .capacityKg(1000.0)
                .status(VehicleStatus.AVAILABLE)
                .active(true)
                .wialonUnitId("unit_12345")
                .currentDriverId(null)
                .build();
    }

    @Test
    void testCreateVehicle_Success() throws Exception {
        when(vehicleService.createVehicle(any(VehicleRequest.class))).thenReturn(vehicleResponse);

        mockMvc.perform(post("/api/v1/admin/vehicles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(vehicleRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.vehicleNumber").value("KSA1234"))
                .andExpect(jsonPath("$.vehicleType").value("PICKUP_TRUCK"))
                .andExpect(jsonPath("$.status").value("AVAILABLE"));
    }

    @Test
    void testCreateVehicle_ValidationError() throws Exception {
        VehicleRequest invalidRequest = VehicleRequest.builder()
                .vehicleNumber("")
                .build();

        mockMvc.perform(post("/api/v1/admin/vehicles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetAllVehicles_Success() throws Exception {
        when(vehicleService.getAllVehicles()).thenReturn(Collections.singletonList(vehicleResponse));

        mockMvc.perform(get("/api/v1/admin/vehicles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].vehicleNumber").value("KSA1234"));
    }

    @Test
    void testGetVehicleById_Success() throws Exception {
        when(vehicleService.getVehicleById(1L)).thenReturn(vehicleResponse);

        mockMvc.perform(get("/api/v1/admin/vehicles/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.vehicleNumber").value("KSA1234"));
    }

    @Test
    void testUpdateVehicle_Success() throws Exception {
        when(vehicleService.updateVehicle(eq(1L), any(VehicleRequest.class))).thenReturn(vehicleResponse);

        mockMvc.perform(put("/api/v1/admin/vehicles/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(vehicleRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void testUpdateVehicleStatus_Success() throws Exception {
        VehicleStatusUpdateRequest statusRequest = VehicleStatusUpdateRequest.builder()
                .status(VehicleStatus.MAINTENANCE)
                .build();

        VehicleResponse updatedResponse = VehicleResponse.builder()
                .id(1L)
                .vehicleNumber("KSA1234")
                .vehicleType(VehicleType.PICKUP_TRUCK)
                .capacityKg(1000.0)
                .status(VehicleStatus.MAINTENANCE)
                .active(true)
                .build();

        when(vehicleService.updateVehicleStatus(eq(1L), any(VehicleStatusUpdateRequest.class)))
                .thenReturn(updatedResponse);

        mockMvc.perform(patch("/api/v1/admin/vehicles/1/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(statusRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("MAINTENANCE"));
    }

    @Test
    void testDeleteVehicle_Success() throws Exception {
        mockMvc.perform(delete("/api/v1/admin/vehicles/1"))
                .andExpect(status().isNoContent());
    }
}
