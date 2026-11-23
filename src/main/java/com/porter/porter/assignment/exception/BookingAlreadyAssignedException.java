package com.porter.porter.assignment.exception;

public class BookingAlreadyAssignedException extends RuntimeException {
    public BookingAlreadyAssignedException(String message) {
        super(message);
    }
}
