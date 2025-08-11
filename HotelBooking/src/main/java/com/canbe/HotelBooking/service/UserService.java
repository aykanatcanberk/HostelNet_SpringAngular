package com.canbe.HotelBooking.service;

import com.canbe.HotelBooking.dto.LoginRequest;
import com.canbe.HotelBooking.dto.RegistrationRequest;
import com.canbe.HotelBooking.dto.Response;
import com.canbe.HotelBooking.dto.UserDto;
import com.canbe.HotelBooking.model.User;

public interface UserService {

    Response loginUser(LoginRequest loginRequest);
    Response registerUser(RegistrationRequest registrationRequest);
    Response getAllUsers();
    Response getOwnAccountDetails();
    User getCurrentLoggedInUser();
    Response updateOwnAccount(UserDto userDto);
    Response deleteOwnAccount();
    Response getMyBookingHistory();
}