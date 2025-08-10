package com.canbe.HotelBooking.repository;

import com.canbe.HotelBooking.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
}