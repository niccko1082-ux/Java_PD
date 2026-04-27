package com.hotelnova.service;

import com.hotelnova.model.Room;
import com.hotelnova.model.RoomType;
import com.hotelnova.repository.RoomRepository;
import com.hotelnova.exception.DuplicateRoomException;
import com.hotelnova.exception.HotelNovaException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

/**
 * Service class handling business logic operations for Room entities.
 */
public class RoomService {
    private final RoomRepository roomRepository;

    /**
     * Constructs a new RoomService with the given RoomRepository.
     * Uses constructor injection as a best architectural practice.
     */
    public RoomService(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    /**
     * Registers a new room after validating business rules.
     */
    public void registerRoom(Room room) throws SQLException, DuplicateRoomException {
        // 1. Business validation: Does the room number already exist?
        if (roomRepository.isNumberExists(room.getNumber())) {
            throw new DuplicateRoomException("Room number " + room.getNumber() + " is already registered.");
        }

        // 2. If validation passes, call the Data Access Object (DAO) to persist
        roomRepository.save(room);

        // 3. Simulate an "HTTP POST" log request as required
        System.out.println("[LOG] POST /api/rooms - Room successfully registered");
    }

    /**
     * Retrieves a list of all registered rooms.
     */
    public List<Room> getAllRooms() throws SQLException {
        System.out.println("[LOG] GET /api/rooms");
        return roomRepository.findAll();
    }

    /**
     * Updates an existing room, validating that price is not negative.
     */
    public void updateRoom(Room room) throws SQLException, HotelNovaException {
        if (room.getPrice() != null && room.getPrice().compareTo(BigDecimal.ZERO) < 0) {
            throw new HotelNovaException("El precio de la habitación no puede ser negativo.");
        }
        roomRepository.update(room);
        System.out.println("[LOG] PUT /api/rooms/" + room.getId() + " - Room updated");
    }

    /**
     * Retrieves a list of all rooms matching a specific type.
     */
    public List<Room> getRoomsByType(RoomType type) throws SQLException {
        System.out.println("[LOG] GET /api/rooms?type=" + type.name());
        return roomRepository.findByType(type);
    }

    /**
     * Retrieves a list of all rooms within a specific price range.
     */
    public List<Room> getRoomsByPriceRange(BigDecimal min, BigDecimal max) throws SQLException, HotelNovaException {
        if (min.compareTo(BigDecimal.ZERO) < 0 || max.compareTo(BigDecimal.ZERO) < 0) {
            throw new HotelNovaException("Los precios mínimo y máximo no pueden ser negativos.");
        }
        if (min.compareTo(max) > 0) {
            throw new HotelNovaException("El precio mínimo no puede ser mayor que el precio máximo.");
        }
        System.out.println("[LOG] GET /api/rooms?minPrice=" + min + "&maxPrice=" + max);
        return roomRepository.findByPriceRange(min, max);
    }
}