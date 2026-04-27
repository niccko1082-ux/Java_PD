package com.hotelnova.repository;

import com.hotelnova.model.Room;
import com.hotelnova.model.RoomType;
import java.sql.SQLException;
import java.util.List;

/**
 * Repository interface for managing Room entities.
 * Defines the standard CRUD operations and custom queries for rooms.
 */
public interface RoomRepository {

    /**
     * Persists a new room into the database.
     */
    void save(Room room) throws SQLException;

    /**
     * Updates an existing room in the database.
     */
    void update(Room room) throws SQLException;

    /**
     * Deletes a room from the database by its unique identifier.
     */
    void delete(Long id) throws SQLException;

    /**
     * Retrieves a room by its unique identifier.
     */
    Room findById(Long id) throws SQLException;

    /**
     * Retrieves all rooms from the database.
     */
    List<Room> findAll() throws SQLException;

    /**
     * Retrieves all rooms of a specific type.
     */
    List<Room> findByType(RoomType type) throws SQLException;

    /**
     * Retrieves all rooms within a specific price range.
     */
    List<Room> findByPriceRange(java.math.BigDecimal min, java.math.BigDecimal max) throws SQLException;

    /**
     * Retrieves all rooms matching a specific status.
     */
    List<Room> findByStatus(String status) throws SQLException;

    /**
     * Checks if a room with the specified number already exists.
     */
    boolean isNumberExists(String number) throws SQLException;

    /**
     * Updates the status of a room using an existing connection.
     * Designed to participate in an external transaction — does not close the connection.
     */
    void updateStatus(Long roomId, String status, java.sql.Connection conn) throws SQLException;
}