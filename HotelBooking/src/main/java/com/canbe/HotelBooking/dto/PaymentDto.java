package com.canbe.HotelBooking.dto;

import com.canbe.HotelBooking.enums.PaymentGateway;
import com.canbe.HotelBooking.enums.PaymentStatus;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class PaymentDto {

    private Long id;

    private BookingDto booking;

    private String transactionId;

    private BigDecimal amount;

    private PaymentGateway paymentMethod;

    private LocalDateTime paymentDate;

    private PaymentStatus status;

    private String bookingReference;

    private String failureReason;

    private String approvalLink;
}
