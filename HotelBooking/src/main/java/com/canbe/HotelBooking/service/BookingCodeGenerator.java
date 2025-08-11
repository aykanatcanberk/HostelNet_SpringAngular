package com.canbe.HotelBooking.service;

import com.canbe.HotelBooking.model.BookingReference;
import com.canbe.HotelBooking.repository.BookingReferenceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class BookingCodeGenerator {

    private final BookingReferenceRepository bookingReferenceRepository;

    private static final int CODE_LENGTH = 10;
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ123456789";
    private final SecureRandom secureRandom = new SecureRandom();

    public String generateBookingReference() {
        String bookingReference;

        do {
            bookingReference = generateRandomAlphaNumericCode();
        } while (isBookingReferenceExist(bookingReference));

        saveBookingReferenceToDatabase(bookingReference);

        return bookingReference;
    }

    private String generateRandomAlphaNumericCode() {
        StringBuilder codeBuilder = new StringBuilder(CODE_LENGTH);

        for (int i = 0; i < CODE_LENGTH; i++) {
            int index = secureRandom.nextInt(CHARACTERS.length());
            codeBuilder.append(CHARACTERS.charAt(index));
        }

        return codeBuilder.toString();
    }

    private boolean isBookingReferenceExist(String bookingReference) {
        return bookingReferenceRepository.findByReferenceNo(bookingReference).isPresent();
    }

    private void saveBookingReferenceToDatabase(String bookingReference) {
        BookingReference newBookingReference = BookingReference.builder()
                .referenceNo(bookingReference)
                .build();
        bookingReferenceRepository.save(newBookingReference);
    }
}
