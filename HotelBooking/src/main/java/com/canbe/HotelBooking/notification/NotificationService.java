package com.canbe.HotelBooking.notification;

import com.canbe.HotelBooking.dto.NotificationDto;

public interface NotificationService {

    void sendEmail(NotificationDto notificationDto);
    void sendSMS();
    void sendWhatsApp();
}
