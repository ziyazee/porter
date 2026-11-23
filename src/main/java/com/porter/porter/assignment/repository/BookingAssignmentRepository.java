package com.porter.porter.assignment.repository;

import com.porter.porter.assignment.entity.BookingAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookingAssignmentRepository extends JpaRepository<BookingAssignment, Long> {
    Optional<BookingAssignment> findByBookingId(Long bookingId);
    List<BookingAssignment> findByVehicleId(Long vehicleId);
    List<BookingAssignment> findByDriverId(Long driverId);
}
