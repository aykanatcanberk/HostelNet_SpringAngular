package com.canbe.HotelBooking.payment;

import com.canbe.HotelBooking.dto.NotificationDto;
import com.canbe.HotelBooking.dto.Response;
import com.canbe.HotelBooking.enums.NotificationType;
import com.canbe.HotelBooking.enums.PaymentGateway;
import com.canbe.HotelBooking.enums.PaymentStatus;
import com.canbe.HotelBooking.exception.NotFoundException;
import com.canbe.HotelBooking.model.Booking;
import com.canbe.HotelBooking.model.Payment;
import com.canbe.HotelBooking.repository.BookingRepository;
import com.canbe.HotelBooking.repository.PaymentRepository;
import com.canbe.HotelBooking.service.NotificationService;
import com.stripe.Stripe;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final BookingRepository bookingRepository;
    private final PaymentRepository paymentRepository;
    private final NotificationService notificationService;

    @Value("${stripe.api.secret_key}")
    private String secretKey;

    public Response createPayment(PaymentRequest paymentRequest) {

        Stripe.apiKey = secretKey;

        Booking booking = bookingRepository
                .findByBookingReference(paymentRequest.getBookingReference())
                .orElseThrow(() -> new NotFoundException("Booking not found"));

        if (booking.getPaymentStatus() == PaymentStatus.COMPLETED) {
            throw new NotFoundException("Booking is already completed");
        }

        if (!booking.getTotalPrice().equals(paymentRequest.getAmount())) {
            throw new NotFoundException("The payment amount does not match. Please contact customer support.");
        }

        try {
            PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                    .setAmount(paymentRequest.getAmount().multiply(BigDecimal.valueOf(100)).longValue())
                    .setCurrency("usd")
                    .putMetadata("bookingReference", paymentRequest.getBookingReference())
                    .build();

            PaymentIntent intent = PaymentIntent.create(params);
            String transactionId = intent.getClientSecret();

            return Response.builder()
                    .status(200)
                    .message("Payment created successfully")
                    .transactionId(transactionId)
                    .build();

        } catch (Exception e) {
            throw new RuntimeException("Error creating payment", e);
        }

    }

    public void updatePayment(PaymentRequest paymentRequest) {

        Booking booking = bookingRepository.findByBookingReference(paymentRequest.getBookingReference())
                .orElseThrow(() -> new NotFoundException("Booing Not Found"));


        Payment payment = new Payment();
        payment.setPaymentGateway(PaymentGateway.STRIPE);
        payment.setAmount(paymentRequest.getAmount());
        payment.setTransactionId(paymentRequest.getTransactionId());
        payment.setPaymentStatus(paymentRequest.isSuccess() ? PaymentStatus.COMPLETED : PaymentStatus.FAILED);
        payment.setPaymentDate(LocalDateTime.now());
        payment.setBookingReference(paymentRequest.getBookingReference());
        payment.setUser(booking.getUser());

        if (!paymentRequest.isSuccess()) {
            payment.setFailureReason(paymentRequest.getFailureReason());
        }

        paymentRepository.save(payment);

        NotificationDto notificationDTO = NotificationDto.builder()
                .recipient(booking.getUser().getEmail())
                .type(NotificationType.EMAIL)
                .bookingReference(paymentRequest.getBookingReference())
                .build();

        if (paymentRequest.isSuccess()) {
            booking.setPaymentStatus(PaymentStatus.COMPLETED);
            bookingRepository.save(booking);

            notificationDTO.setSubject("Booking Payment Confirmation");
            notificationDTO.setBody("Dear Customer,\n\nWe are pleased to inform you that your payment for booking with reference: "
                    + paymentRequest.getBookingReference() + " has been successfully processed.\n\nThank you for choosing our service.\n\nBest regards,\n[Your Company Name]");
            notificationService.sendEmail(notificationDTO);

        } else {
            booking.setPaymentStatus(PaymentStatus.FAILED);
            bookingRepository.save(booking);

            notificationDTO.setSubject("Booking Payment Failure Notification");
            notificationDTO.setBody("Dear Customer,\n\nUnfortunately, your payment for booking with reference: "
                    + paymentRequest.getBookingReference() + " has failed due to the following reason: "
                    + paymentRequest.getFailureReason() + ".\n\nPlease contact our support team for further assistance.\n\nBest regards,\n[Your Company Name]");
            notificationService.sendEmail(notificationDTO);
        }

    }
}
