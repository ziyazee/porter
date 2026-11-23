package com.porter.porter.assignment.controller;

import com.porter.porter.assignment.dto.BookingAssignmentRequest;
import com.porter.porter.assignment.dto.BookingAssignmentResponse;
import com.porter.porter.assignment.service.BookingAssignmentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/bookings")
public class BookingAssignmentController {
    
    @Autowired
    private BookingAssignmentService bookingAssignmentService;
    
    @PostMapping("/{bookingId}/assign")
    public ResponseEntity<BookingAssignmentResponse> assignBooking(
            @PathVariable Long bookingId,
            @Valid @RequestBody BookingAssignmentRequest request,
            Authentication authentication) {
        
        Long assignedBy = (Long) authentication.getPrincipal();
        BookingAssignmentResponse response = bookingAssignmentService.assignBooking(bookingId, request, assignedBy);
        return ResponseEntity.ok(response);
    }
}
