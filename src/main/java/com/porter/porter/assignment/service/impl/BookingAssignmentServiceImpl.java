package com.porter.porter.assignment.service.impl;

import com.porter.porter.assignment.dto.BookingAssignmentRequest;
import com.porter.porter.assignment.dto.BookingAssignmentResponse;
import com.porter.porter.assignment.entity.BookingAssignment;
import com.porter.porter.assignment.exception.BookingAlreadyAssignedException;
import com.porter.porter.assignment.exception.BookingAssignmentException;
import com.porter.porter.assignment.repository.BookingAssignmentRepository;
import com.porter.porter.booking.entity.Booking;
import com.porter.porter.booking.enums.BookingStatus;
import com.porter.porter.booking.exception.BookingNotFoundException;
import com.porter.porter.booking.repository.BookingRepository;
import com.porter.porter.driver.entity.Driver;
import com.porter.porter.driver.enums.DriverStatus;
import com.porter.porter.driver.exception.DriverNotFoundException;
import com.porter.porter.driver.exception.DriverUnavailableException;
import com.porter.porter.driver.repository.DriverRepository;
import com.porter.porter.vehicle.entity.Vehicle;
import com.porter.porter.vehicle.entity.VehicleDriverMapping;
import com.porter.porter.vehicle.enums.VehicleStatus;
import com.porter.porter.vehicle.exception.VehicleNotFoundException;
import com.porter.porter.vehicle.exception.VehicleUnavailableException;
import com.porter.porter.vehicle.repository.VehicleDriverMappingRepository;
import com.porter.porter.vehicle.repository.VehicleRepository;
import com.porter.porter.assignment.service.BookingAssignmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BookingAssignmentServiceImpl implements BookingAssignmentService {
    
    @Autowired
    private BookingRepository bookingRepository;
    
    @Autowired
    private VehicleRepository vehicleRepository;
    
    @Autowired
    private DriverRepository driverRepository;
    
    @Autowired
    private BookingAssignmentRepository bookingAssignmentRepository;
    
    @Autowired
    private VehicleDriverMappingRepository vehicleDriverMappingRepository;
    
    @Override
    @Transactional
    public BookingAssignmentResponse assignBooking(Long bookingId, BookingAssignmentRequest request, Long assignedBy) {
        // Get booking
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException("Booking not found"));
        
        // Validate booking status
        if (booking.getStatus() != BookingStatus.PENDING_ASSIGNMENT) {
            throw new BookingAlreadyAssignedException("Booking is not in PENDING_ASSIGNMENT status");
        }
        
        // Get vehicle
        Vehicle vehicle = vehicleRepository.findById(request.getVehicleId())
                .orElseThrow(() -> new VehicleNotFoundException("Vehicle not found"));
        
        // Validate vehicle is active
        if (!vehicle.getActive()) {
            throw new VehicleUnavailableException("Vehicle is not active");
        }
        
        // Validate vehicle is available
        if (vehicle.getStatus() != VehicleStatus.AVAILABLE) {
            throw new VehicleUnavailableException("Vehicle is not available");
        }
        
        // Check if vehicle is already assigned to an active booking
        bookingAssignmentRepository.findByVehicleId(request.getVehicleId())
                .stream()
                .filter(a -> {
                    Booking b = bookingRepository.findById(a.getBookingId()).orElse(null);
                    return b != null && 
                           (b.getStatus() == BookingStatus.DRIVER_ASSIGNED || 
                            b.getStatus() == BookingStatus.AT_PICKUP || 
                            b.getStatus() == BookingStatus.IN_TRANSIT);
                })
                .findFirst()
                .ifPresent(a -> {
                    throw new VehicleUnavailableException("Vehicle is already assigned to an active booking");
                });
        
        // Get driver
        Driver driver = driverRepository.findById(request.getDriverId())
                .orElseThrow(() -> new DriverNotFoundException("Driver not found"));
        
        // Validate driver is active
        if (!driver.getActive()) {
            throw new DriverUnavailableException("Driver is not active");
        }
        
        // Validate driver is available
        if (driver.getStatus() != DriverStatus.AVAILABLE) {
            throw new DriverUnavailableException("Driver is not available");
        }
        
        // Check if driver is already assigned to an active booking
        bookingAssignmentRepository.findByDriverId(request.getDriverId())
                .stream()
                .filter(a -> {
                    Booking b = bookingRepository.findById(a.getBookingId()).orElse(null);
                    return b != null && 
                           (b.getStatus() == BookingStatus.DRIVER_ASSIGNED || 
                            b.getStatus() == BookingStatus.AT_PICKUP || 
                            b.getStatus() == BookingStatus.IN_TRANSIT);
                })
                .findFirst()
                .ifPresent(a -> {
                    throw new DriverUnavailableException("Driver is already assigned to an active booking");
                });
        
        // Create booking assignment
        BookingAssignment assignment = BookingAssignment.builder()
                .bookingId(bookingId)
                .vehicleId(request.getVehicleId())
                .driverId(request.getDriverId())
                .assignedBy(assignedBy)
                .build();
        
        bookingAssignmentRepository.save(assignment);
        
        // Update booking status
        booking.setStatus(BookingStatus.DRIVER_ASSIGNED);
        bookingRepository.save(booking);
        
        // Update vehicle status
        vehicle.setStatus(VehicleStatus.ASSIGNED);
        vehicle.setCurrentDriverId(request.getDriverId());
        vehicleRepository.save(vehicle);
        
        // Update driver status
        driver.setStatus(DriverStatus.ASSIGNED);
        driverRepository.save(driver);
        
        // Deactivate any existing active mappings for this vehicle
        vehicleDriverMappingRepository.deactivateByVehicleId(request.getVehicleId());
        
        // Deactivate any existing active mappings for this driver
        vehicleDriverMappingRepository.deactivateByDriverId(request.getDriverId());
        
        // Create vehicle-driver mapping
        VehicleDriverMapping mapping = VehicleDriverMapping.builder()
                .vehicleId(request.getVehicleId())
                .driverId(request.getDriverId())
                .active(true)
                .build();
        
        vehicleDriverMappingRepository.save(mapping);
        
        // Build response
        return BookingAssignmentResponse.builder()
                .bookingId(booking.getId())
                .bookingNumber(booking.getBookingNumber())
                .status(booking.getStatus())
                .vehicle(BookingAssignmentResponse.VehicleInfo.builder()
                        .id(vehicle.getId())
                        .vehicleNumber(vehicle.getVehicleNumber())
                        .build())
                .driver(BookingAssignmentResponse.DriverInfo.builder()
                        .id(driver.getId())
                        .fullName(driver.getFullName())
                        .build())
                .build();
    }
}
