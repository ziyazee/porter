package com.porter.porter.assignment.service;

import com.porter.porter.assignment.dto.BookingAssignmentRequest;
import com.porter.porter.assignment.dto.BookingAssignmentResponse;

public interface BookingAssignmentService {
    BookingAssignmentResponse assignBooking(Long bookingId, BookingAssignmentRequest request, Long assignedBy);
}
