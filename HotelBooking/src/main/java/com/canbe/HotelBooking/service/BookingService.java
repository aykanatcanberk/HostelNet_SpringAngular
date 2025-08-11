package com.canbe.HotelBooking.service;

import com.canbe.HotelBooking.dto.BookingDto;
import com.canbe.HotelBooking.dto.Response;

public interface BookingService {

    Response getAllBookings();
    Response createBooking(BookingDto bookingDto);
    Response findBookingByReferenceNo(String  bookingReference);
    Response updateBooking(BookingDto bookingDto);
}