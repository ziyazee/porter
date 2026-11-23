package com.porter.porter.driver;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.porter.porter.driver.dto.DriverRequest;
import com.porter.porter.driver.dto.DriverResponse;
import com.porter.porter.driver.dto.DriverStatusUpdateRequest;
import com.porter.porter.driver.enums.DriverStatus;
import com.porter.porter.driver.service.DriverService;
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
public class DriverControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private DriverService driverService;

    private DriverRequest driverRequest;
    private DriverResponse driverResponse;

    @BeforeEach
    void setUp() {
        driverRequest = DriverRequest.builder()
                .fullName("Ahmed Ali")
                .phoneNumber("+966501234567")
                .iqamaNumber("1234567890")
                .drivingLicenseNumber("DL123456")
                .build();

        driverResponse = DriverResponse.builder()
                .id(1L)
                .fullName("Ahmed Ali")
                .phoneNumber("+966501234567")
                .iqamaNumber("1234567890")
                .drivingLicenseNumber("DL123456")
                .status(DriverStatus.AVAILABLE)
                .active(true)
                .build();
    }

    @Test
    void testCreateDriver_Success() throws Exception {
        when(driverService.createDriver(any(DriverRequest.class))).thenReturn(driverResponse);

        mockMvc.perform(post("/api/v1/admin/drivers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(driverRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.fullName").value("Ahmed Ali"))
                .andExpect(jsonPath("$.phoneNumber").value("+966501234567"))
                .andExpect(jsonPath("$.status").value("AVAILABLE"));
    }

    @Test
    void testCreateDriver_ValidationError() throws Exception {
        DriverRequest invalidRequest = DriverRequest.builder()
                .fullName("")
                .build();

        mockMvc.perform(post("/api/v1/admin/drivers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testCreateDriver_InvalidPhoneNumber() throws Exception {
        DriverRequest invalidRequest = DriverRequest.builder()
                .fullName("Ahmed Ali")
                .phoneNumber("0501234567")
                .iqamaNumber("1234567890")
                .drivingLicenseNumber("DL123456")
                .build();

        mockMvc.perform(post("/api/v1/admin/drivers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetAllDrivers_Success() throws Exception {
        when(driverService.getAllDrivers()).thenReturn(Collections.singletonList(driverResponse));

        mockMvc.perform(get("/api/v1/admin/drivers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].fullName").value("Ahmed Ali"));
    }

    @Test
    void testGetDriverById_Success() throws Exception {
        when(driverService.getDriverById(1L)).thenReturn(driverResponse);

        mockMvc.perform(get("/api/v1/admin/drivers/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.fullName").value("Ahmed Ali"));
    }

    @Test
    void testUpdateDriver_Success() throws Exception {
        when(driverService.updateDriver(eq(1L), any(DriverRequest.class))).thenReturn(driverResponse);

        mockMvc.perform(put("/api/v1/admin/drivers/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(driverRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void testUpdateDriverStatus_Success() throws Exception {
        DriverStatusUpdateRequest statusRequest = DriverStatusUpdateRequest.builder()
                .status(DriverStatus.OFFLINE)
                .build();

        DriverResponse updatedResponse = DriverResponse.builder()
                .id(1L)
                .fullName("Ahmed Ali")
                .phoneNumber("+966501234567")
                .iqamaNumber("1234567890")
                .drivingLicenseNumber("DL123456")
                .status(DriverStatus.OFFLINE)
                .active(true)
                .build();

        when(driverService.updateDriverStatus(eq(1L), any(DriverStatusUpdateRequest.class)))
                .thenReturn(updatedResponse);

        mockMvc.perform(patch("/api/v1/admin/drivers/1/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(statusRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OFFLINE"));
    }

    @Test
    void testDeleteDriver_Success() throws Exception {
        mockMvc.perform(delete("/api/v1/admin/drivers/1"))
                .andExpect(status().isNoContent());
    }
}
