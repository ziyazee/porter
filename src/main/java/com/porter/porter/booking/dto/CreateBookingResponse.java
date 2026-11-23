package com.porter.porter.booking.dto;

import com.porter.porter.booking.enums.BookingStatus;
import com.porter.porter.booking.enums.PaymentMethod;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateBookingResponse {
    private Long bookingId;
    private String bookingNumber;
    private BookingStatus status;
    private String pickupZone;
    private String dropZone;
    private Double totalPrice;
    private String currency;
    private PaymentMethod paymentMethod;
    private String trackingToken;
    private String trackingUrl;
}
