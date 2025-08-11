package com.canbe.HotelBooking.service;

import com.canbe.HotelBooking.dto.Response;
import com.canbe.HotelBooking.dto.RoomDto;
import com.canbe.HotelBooking.enums.RoomType;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

public interface RoomService {

    Response addRoom(RoomDto roomDto, MultipartFile imageFile);
    Response updateRoom(RoomDto roomDto, MultipartFile imageFile);
    Response getAllRooms();
    Response getRoomById(Long id);
    Response deleteRoom(Long id);
    Response getAvailableRooms(LocalDate checkInDate, LocalDate checkOutDate, RoomType roomType);
    List<RoomType> getAllRoomTypes();
    Response searchRoom(String input);
}
