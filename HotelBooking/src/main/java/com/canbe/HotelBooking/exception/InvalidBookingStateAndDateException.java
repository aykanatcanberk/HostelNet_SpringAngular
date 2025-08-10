package com.canbe.HotelBooking.exception;

public class InvalidBookingStateAndDateException extends RuntimeException {
    public InvalidBookingStateAndDateException(String message) {
        super(message);
    }
}