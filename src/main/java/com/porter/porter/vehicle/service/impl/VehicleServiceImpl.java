package com.porter.porter.vehicle.service.impl;

import com.porter.porter.vehicle.dto.VehicleRequest;
import com.porter.porter.vehicle.dto.VehicleResponse;
import com.porter.porter.vehicle.dto.VehicleStatusUpdateRequest;
import com.porter.porter.vehicle.entity.Vehicle;
import com.porter.porter.vehicle.enums.VehicleStatus;
import com.porter.porter.vehicle.exception.VehicleNotFoundException;
import com.porter.porter.vehicle.repository.VehicleRepository;
import com.porter.porter.vehicle.service.VehicleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class VehicleServiceImpl implements VehicleService {
    
    @Autowired
    private VehicleRepository vehicleRepository;
    
    @Override
    public VehicleResponse createVehicle(VehicleRequest request) {
        Vehicle vehicle = Vehicle.builder()
                .vehicleNumber(request.getVehicleNumber())
                .vehicleType(request.getVehicleType())
                .capacityKg(request.getCapacityKg())
                .wialonUnitId(request.getWialonUnitId())
                .build();
        
        Vehicle savedVehicle = vehicleRepository.save(vehicle);
        return mapToResponse(savedVehicle);
    }
    
    @Override
    public VehicleResponse getVehicleById(Long id) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new VehicleNotFoundException("Vehicle not found"));
        return mapToResponse(vehicle);
    }
    
    @Override
    public List<VehicleResponse> getAllVehicles() {
        List<Vehicle> vehicles = vehicleRepository.findAll();
        return vehicles.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    public VehicleResponse updateVehicle(Long id, VehicleRequest request) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new VehicleNotFoundException("Vehicle not found"));
        
        vehicle.setVehicleNumber(request.getVehicleNumber());
        vehicle.setVehicleType(request.getVehicleType());
        vehicle.setCapacityKg(request.getCapacityKg());
        vehicle.setWialonUnitId(request.getWialonUnitId());
        
        Vehicle savedVehicle = vehicleRepository.save(vehicle);
        return mapToResponse(savedVehicle);
    }
    
    @Override
    public VehicleResponse updateVehicleStatus(Long id, VehicleStatusUpdateRequest request) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new VehicleNotFoundException("Vehicle not found"));
        
        vehicle.setStatus(request.getStatus());
        
        Vehicle savedVehicle = vehicleRepository.save(vehicle);
        return mapToResponse(savedVehicle);
    }
    
    @Override
    public void deleteVehicle(Long id) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new VehicleNotFoundException("Vehicle not found"));
        vehicleRepository.deleteById(id);
    }
    
    private VehicleResponse mapToResponse(Vehicle vehicle) {
        return VehicleResponse.builder()
                .id(vehicle.getId())
                .vehicleNumber(vehicle.getVehicleNumber())
                .vehicleType(vehicle.getVehicleType())
                .capacityKg(vehicle.getCapacityKg())
                .status(vehicle.getStatus())
                .active(vehicle.getActive())
                .wialonUnitId(vehicle.getWialonUnitId())
                .currentDriverId(vehicle.getCurrentDriverId())
                .createdAt(vehicle.getCreatedAt())
                .updatedAt(vehicle.getUpdatedAt())
                .build();
    }
}
