package com.canbe.HotelBooking;

import com.canbe.HotelBooking.dto.NotificationDto;
import com.canbe.HotelBooking.notification.NotificationService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@EnableAsync
public class HotelBookingApplication {
	public static void main(String[] args) {

		SpringApplication.run(HotelBookingApplication.class, args);
	}
}
