package com.canbe.HotelBooking.service.impl;

import com.canbe.HotelBooking.dto.NotificationDto;
import com.canbe.HotelBooking.enums.NotificationType;
import com.canbe.HotelBooking.model.Notification;
import com.canbe.HotelBooking.service.NotificationService;
import com.canbe.HotelBooking.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl implements NotificationService {

    private final JavaMailSender javaMailSender;
    private final NotificationRepository notificationRepository;

    @Override
    @Async
    public void sendEmail(NotificationDto notificationDto) {

        log.info("Sending email notification");

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(notificationDto.getRecipient());
        message.setSubject(notificationDto.getSubject());
        message.setText(notificationDto.getBody());

        javaMailSender.send(message);

        Notification notification = Notification.builder()
                .recipient(notificationDto.getRecipient())
                .subject(notificationDto.getSubject())
                .body(notificationDto.getBody())
                .bookingReference(notificationDto.getBookingReference())
                .type(NotificationType.EMAIL)
                .build();

        notificationRepository.save(notification);
    }

    @Override
    public void sendSMS() {

    }

    @Override
    public void sendWhatsApp() {

    }
}
