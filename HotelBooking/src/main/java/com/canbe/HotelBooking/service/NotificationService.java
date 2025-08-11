package com.canbe.HotelBooking.service;

import com.canbe.HotelBooking.dto.NotificationDto;

public interface NotificationService {

    void sendEmail(NotificationDto notificationDto);
    void sendSMS();
    void sendWhatsApp();
}
