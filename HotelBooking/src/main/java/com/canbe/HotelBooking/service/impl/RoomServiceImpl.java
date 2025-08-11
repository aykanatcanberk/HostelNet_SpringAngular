package com.canbe.HotelBooking.service.impl;

import com.canbe.HotelBooking.dto.Response;
import com.canbe.HotelBooking.dto.RoomDto;
import com.canbe.HotelBooking.enums.RoomType;
import com.canbe.HotelBooking.exception.InvalidBookingStateAndDateException;
import com.canbe.HotelBooking.exception.NotFoundException;
import com.canbe.HotelBooking.model.Room;
import com.canbe.HotelBooking.repository.RoomRepository;
import com.canbe.HotelBooking.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RoomServiceImpl implements RoomService {

    private final RoomRepository roomRepository;
    private final ModelMapper modelMapper;

    private final String IMAGE_DIRECTORY = System.getProperty("user.dir") + "/product-image";

    @Override
    public Response addRoom(RoomDto roomDto, MultipartFile imageFile) {
        Room roomToSave = modelMapper.map(roomDto, Room.class);

        if (imageFile != null) {
            String imagePath = saveImage(imageFile);
            roomToSave.setImageUrl(imagePath);
        }

        roomRepository.save(roomToSave);

        return Response.builder()
                .status(200)
                .message("Room successfully added")
                .build();
    }

    @Override
    public Response updateRoom(RoomDto roomDto, MultipartFile imageFile) {
        Room existingRoom = roomRepository.findById(roomDto.getId())
                .orElseThrow(()-> new NotFoundException("Room not found"));

        if (imageFile != null && !imageFile.isEmpty()){
            String imagePath = saveImage(imageFile);
            existingRoom.setImageUrl(imagePath);
        }

        if (roomDto.getRoomNumber() != null && roomDto.getRoomNumber() >= 0){
            existingRoom.setRoomNumber(roomDto.getRoomNumber());
        }

        if (roomDto.getPricePerNight() != null && roomDto.getPricePerNight().compareTo(BigDecimal.ZERO) >= 0){
            existingRoom.setPricePerNight(roomDto.getPricePerNight());
        }

        if (roomDto.getCapacity() != null && roomDto.getCapacity() > 0){
            existingRoom.setCapacity(roomDto.getCapacity());
        }
        if (roomDto.getType() != null) existingRoom.setType(roomDto.getType());

        if(roomDto.getDescription() != null) existingRoom.setDescription(roomDto.getDescription());

        roomRepository.save(existingRoom);

        return Response.builder()
                .status(200)
                .message("Room updated successfully")
                .build();
    }

    @Override
    public Response getAllRooms() {
        List<Room> roomList = roomRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));

        List<RoomDto> roomDtos = modelMapper.map(roomList,new TypeToken<List<RoomDto>>() {}.getType());

        return Response.builder()
                .status(200)
                .message("success")
                .rooms(roomDtos)
                .build();
    }

    @Override
    public Response getRoomById(Long id) {

        Room room = roomRepository.findById(id).orElseThrow(()-> new NotFoundException("Room not found"));
        RoomDto roomDto = modelMapper.map(room,RoomDto.class);

        return Response.builder()
                .status(200)
                .message("success")
                .room(roomDto)
                .build();
    }

    @Override
    public Response deleteRoom(Long id) {

        roomRepository.deleteById(id);

        return Response.builder()
                .status(200)
                .message("Room successfully deleted")
                .build();
    }

    @Override
    public Response getAvailableRooms(LocalDate checkInDate, LocalDate checkOutDate, RoomType roomType) {

        if (checkInDate.isBefore(LocalDate.now())){
            throw new InvalidBookingStateAndDateException("Check-in date cannot be in the past. Please select a valid future date.");
        }

        if (checkOutDate.isBefore(checkInDate)){
            throw new InvalidBookingStateAndDateException("Check-out date cannot be earlier than check-in date. Please select valid dates.");
        }

        if (checkInDate.isEqual(checkOutDate)){
            throw new InvalidBookingStateAndDateException("Check-in date cannot be the same as check-out date. Please choose different dates.");
        }

        List<Room> roomList = roomRepository.findAvailableRooms(checkInDate, checkOutDate, roomType);
        List<RoomDto> roomDtos = modelMapper.map(roomList, new TypeToken<List<RoomDto>>() {}.getType());

        return Response.builder()
                .status(200)
                .message("Success. Available rooms have been listed.")
                .rooms(roomDtos)
                .build();
    }


    @Override
    public List<RoomType> getAllRoomTypes() {
        return Arrays.asList(RoomType.values());
    }

    @Override
    public Response searchRoom(String input) {
        List<Room> roomList = roomRepository.searchRooms(input);

        List<RoomDto> roomDtos = modelMapper.map(roomList,new TypeToken<List<RoomDto>>() {}.getType());

        return Response.builder()
                .status(200)
                .message("success")
                .rooms(roomDtos)
                .build();
    }

    private String saveImage(MultipartFile imageFile) {
        if (!imageFile.getContentType().startsWith("image/")) {
            throw new IllegalArgumentException("Only Image files are allowed");
        }

        String projectDirectory = System.getProperty("user.dir");

        File directory = new File(projectDirectory + "/product-image");

        if (!directory.exists()) {
            boolean dirCreated = directory.mkdirs();  // This will create the directory along with any missing parent directories
            if (!dirCreated) {
                throw new IllegalArgumentException("Failed to create image directory.");
            }
        }

        String uniqueFileName = UUID.randomUUID() + "_" + imageFile.getOriginalFilename();
        String imagePath = directory.getAbsolutePath() + "/" + uniqueFileName;

        try {
            File destinationFile = new File(imagePath);
            imageFile.transferTo(destinationFile);
        } catch (Exception ex) {
            throw new IllegalArgumentException("Error saving image: " + ex.getMessage());
        }

        return imagePath;
    }

}
