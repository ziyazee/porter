package com.porter.porter.booking.repository;

import com.porter.porter.booking.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    Optional<Booking> findByBookingNumber(String bookingNumber);
    Optional<Booking> findByTrackingToken(String trackingToken);
    List<Booking> findByCustomerId(Long customerId);
}
