package com.porter.porter.vehicle.service;

import com.porter.porter.vehicle.dto.VehicleRequest;
import com.porter.porter.vehicle.dto.VehicleResponse;
import com.porter.porter.vehicle.dto.VehicleStatusUpdateRequest;
import com.porter.porter.vehicle.enums.VehicleStatus;

import java.util.List;

public interface VehicleService {
    VehicleResponse createVehicle(VehicleRequest request);
    VehicleResponse getVehicleById(Long id);
    List<VehicleResponse> getAllVehicles();
    VehicleResponse updateVehicle(Long id, VehicleRequest request);
    VehicleResponse updateVehicleStatus(Long id, VehicleStatusUpdateRequest request);
    void deleteVehicle(Long id);
}
