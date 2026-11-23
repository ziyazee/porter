package com.porter.porter.booking.controller;

import com.porter.porter.booking.dto.*;
import com.porter.porter.booking.service.BookingService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/bookings")
public class BookingController {
    
    @Autowired
    private BookingService bookingService;
    
    @PostMapping("/price-preview")
    public ResponseEntity<PricePreviewResponse> getPricePreview(@Valid @RequestBody PricePreviewRequest request) {
        PricePreviewResponse response = bookingService.getPricePreview(request);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping
    public ResponseEntity<CreateBookingResponse> createBooking(@Valid @RequestBody CreateBookingRequest request, Authentication authentication) {
        Long customerId = (Long) authentication.getPrincipal();
        CreateBookingResponse response = bookingService.createBooking(request, customerId);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/my")
    public ResponseEntity<List<BookingResponse>> getMyBookings(Authentication authentication) {
        Long customerId = (Long) authentication.getPrincipal();
        List<BookingResponse> response = bookingService.getMyBookings(customerId);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{bookingId}")
    public ResponseEntity<BookingResponse> getBookingById(@PathVariable Long bookingId, Authentication authentication) {
        Long customerId = (Long) authentication.getPrincipal();
        BookingResponse response = bookingService.getBookingById(bookingId, customerId);
        return ResponseEntity.ok(response);
    }
}
