package com.canbe.HotelBooking.service.impl;

import com.canbe.HotelBooking.dto.BookingDto;
import com.canbe.HotelBooking.dto.NotificationDto;
import com.canbe.HotelBooking.dto.Response;
import com.canbe.HotelBooking.enums.BookingStatus;
import com.canbe.HotelBooking.enums.PaymentStatus;
import com.canbe.HotelBooking.exception.InvalidBookingStateAndDateException;
import com.canbe.HotelBooking.exception.NotFoundException;
import com.canbe.HotelBooking.model.Booking;
import com.canbe.HotelBooking.model.Room;
import com.canbe.HotelBooking.model.User;
import com.canbe.HotelBooking.repository.BookingRepository;
import com.canbe.HotelBooking.repository.RoomRepository;
import com.canbe.HotelBooking.service.BookingCodeGenerator;
import com.canbe.HotelBooking.service.BookingService;
import com.canbe.HotelBooking.service.NotificationService;
import com.canbe.HotelBooking.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final RoomRepository roomRepository;
    private final UserService userService;
    private final NotificationService notificationService;
    private final BookingCodeGenerator bookingCodeGenerator;
    private final ModelMapper modelMapper;

    @Override
    public Response getAllBookings() {

        List<Booking> bookingList =bookingRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
        List<BookingDto> bookingDtos = modelMapper.map(bookingList, new TypeToken<List<BookingDto>>() {}.getType());

        for(BookingDto bookingDTO: bookingDtos){
            bookingDTO.setUser(null);
            bookingDTO.setRoom(null);
        }

        return Response.builder()
                .status(200)
                .message("success")
                .bookings(bookingDtos)
                .build();
    }

    @Override
    public Response createBooking(BookingDto bookingDto) {

        User currentUser = userService.getCurrentLoggedInUser();
        Room room = roomRepository.findById(bookingDto.getRoomId()).orElseThrow(()-> new NotFoundException("Room Not Found"));

        if (bookingDto.getCheckInDate().isBefore(LocalDate.now())){
            throw new InvalidBookingStateAndDateException("Check-in date cannot be in the past. Please select a valid future date.");
        }

        if (bookingDto.getCheckInDate().isBefore(bookingDto.getCheckInDate())){
            throw new InvalidBookingStateAndDateException("Check-out date cannot be earlier than check-in date. Please select valid dates.");
        }

        if (bookingDto.getCheckInDate().isEqual(bookingDto.getCheckOutDate())){
            throw new InvalidBookingStateAndDateException("Check-in date cannot be the same as check-out date. Please choose different dates.");
        }

        boolean isAvailable = bookingRepository.isRoomAvailable(room.getId(), bookingDto.getCheckInDate(), bookingDto.getCheckOutDate());
        if (!isAvailable) {
            throw new InvalidBookingStateAndDateException("Room is not available for the selected date ranges");
        }

        BigDecimal totalPrice = calculateTotalPrice(room, bookingDto);
        String bookingReference = bookingCodeGenerator.generateBookingReference();

        Booking booking = new Booking();
        booking.setUser(currentUser);
        booking.setRoom(room);
        booking.setCheckInDate(bookingDto.getCheckInDate());
        booking.setCheckOutDate(bookingDto.getCheckOutDate());
        booking.setTotalPrice(totalPrice);
        booking.setBookingReference(bookingReference);
        booking.setBookingStatus(BookingStatus.BOOKED);
        booking.setPaymentStatus(PaymentStatus.PENDING);
        booking.setCreatedDate(LocalDate.now());

        bookingRepository.save(booking);

        String paymentUrl = "http://localhost:4200/payment/"+bookingReference+"/"+totalPrice;

        String emailBody = String.format(
                "Dear %s,\n\n" +
                        "Your booking with reference number %s has been successfully created.\n" +
                        "Here are the details of your booking:\n\n" +
                        "Room: %s\n" +
                        "Check-in Date: %s\n" +
                        "Check-out Date: %s\n" +
                        "Total Price: %.2f\n\n" +
                        "To complete your booking, please proceed with the payment by clicking the link below:\n\n" +
                        "%s\n\n" +
                        "If you have any questions or need further assistance, feel free to contact us.\n\n" +
                        "Best regards,\n" +
                        "Hotel Booking Team",
                currentUser.getFirstName(),
                bookingReference,
                room.getRoomNumber(),
                bookingDto.getCheckInDate(),
                bookingDto.getCheckOutDate(),
                totalPrice,
                paymentUrl
        );

        // Creating the notification object
        NotificationDto notificationDto = NotificationDto.builder()
                .recipient(currentUser.getEmail())
                .subject("Booking Confirmation and Payment Link")
                .body(emailBody)
                .bookingReference(bookingReference)
                .createdAt(LocalDateTime.now())
                .build();
        notificationService.sendEmail(notificationDto);

        return Response.builder()
                .status(200)
                .message("Booking is successfully")
                .booking(bookingDto)
                .build();
    }

    private BigDecimal calculateTotalPrice(Room room, BookingDto bookingDto){
        BigDecimal pricePerNight = room.getPricePerNight();
        long days = ChronoUnit.DAYS.between(bookingDto.getCheckInDate(), bookingDto.getCheckOutDate());
        return pricePerNight.multiply(BigDecimal.valueOf(days));
    }

    @Override
    public Response findBookingByReferenceNo(String bookingReference) {

        Booking booking = bookingRepository.findByBookingReference(bookingReference)
                .orElseThrow(()-> new NotFoundException("Booking reference not found : " + bookingReference ));

        BookingDto bookingDTO = modelMapper.map(booking, BookingDto.class);
        return  Response.builder()
                .status(200)
                .message("success")
                .booking(bookingDTO)
                .build();
    }

    @Override
    public Response updateBooking(BookingDto bookingDto) {

        if (bookingDto.getId() == null) throw new NotFoundException("Booking id is required");

        Booking existingBooking = bookingRepository.findById(bookingDto.getId())
                .orElseThrow(()-> new NotFoundException("Booking Not Found"));

        if (bookingDto.getBookingStatus() != null) {
            existingBooking.setBookingStatus(bookingDto.getBookingStatus());
        }

        if (bookingDto.getPaymentStatus() != null) {
            existingBooking.setPaymentStatus(bookingDto.getPaymentStatus());
        }

        bookingRepository.save(existingBooking);

        return Response.builder()
                .status(200)
                .message("Booking Updated Successfully")
                .build();
    }
}
