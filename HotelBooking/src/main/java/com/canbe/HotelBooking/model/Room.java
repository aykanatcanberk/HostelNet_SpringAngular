package com.canbe.HotelBooking.model;

import com.canbe.HotelBooking.enums.RoomType;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name="rooms")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    @Min(value = 1 , message = "Room number must be greater than 0")
    private Integer roomNumber;

    @Enumerated(EnumType.STRING)
    private RoomType type;

    @DecimalMin(value = "0.1" , message = "Price for per night is required")
    private BigDecimal pricePerNight;

    @Min(value = 1 , message = "capacity must be equal 1 or greater.")
    private Integer capacity;

    private String description;

    private String imageUrl;
}
