package com.canbe.HotelBooking.dto;

import com.canbe.HotelBooking.enums.UserRole;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Response {

    //generic
    private int status;
    private String message;

    //login
    private String token;
    private UserRole role;
    private Boolean isActive;
    private String expirationTime;

    //user data
    private UserDto user;
    private List<UserDto> users;

    //Booking data
    private BookingDto booking;
    private List<BookingDto> bookings;

    //Room data
    private RoomDto room;
    private List<RoomDto> rooms;

    //Payment data
    private PaymentDto payment;
    private List<PaymentDto> payments;
    private String transactionId;

    //Payment data
    private NotificationDto notification;
    private List<NotificationDto> notifications;

    private final LocalDateTime timestamp = LocalDateTime.now();

}