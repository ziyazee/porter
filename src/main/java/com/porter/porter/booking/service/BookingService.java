package com.porter.porter.booking.service;

import com.porter.porter.booking.dto.*;

import java.util.List;

public interface BookingService {
    PricePreviewResponse getPricePreview(PricePreviewRequest request);
    CreateBookingResponse createBooking(CreateBookingRequest request, Long customerId);
    List<BookingResponse> getMyBookings(Long customerId);
    BookingResponse getBookingById(Long bookingId, Long customerId);
}
