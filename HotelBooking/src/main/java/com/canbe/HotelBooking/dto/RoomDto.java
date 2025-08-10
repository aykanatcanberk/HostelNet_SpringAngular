package com.canbe.HotelBooking.dto;

import com.canbe.HotelBooking.enums.RoomType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class RoomDto {

    private Long id;

    private Integer roomNumber;

    private RoomType type;

    private BigDecimal pricePerNight;

    private Integer capacity;

    private String description;

    private String imageUrl;


}