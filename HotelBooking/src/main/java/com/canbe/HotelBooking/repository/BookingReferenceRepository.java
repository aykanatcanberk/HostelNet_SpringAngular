package com.canbe.HotelBooking.repository;

import com.canbe.HotelBooking.model.BookingReference;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BookingReferenceRepository extends JpaRepository<BookingReference, Long> {

    Optional<BookingReference> findByReferenceNo(String referenceNo);
}