package com.canbe.HotelBooking.exception;

public class NameValueRequiredException extends RuntimeException {
    public NameValueRequiredException(String message) {
        super(message);
    }
}