package com.hotelnova.repository;

import com.hotelnova.model.Reservation;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * Repository interface for managing Reservation entities.
 * Methods accept an external Connection to support transactional operations.
 */
public interface ReservationRepository {

    /**
     * Persists a new reservation using the provided connection.
     * Does not close the connection — designed to participate in an external transaction.
     */
    void save(Reservation reservation, Connection conn) throws SQLException;

    /**
     * Retrieves a reservation by its unique identifier.
     */
    Reservation findById(Long id) throws SQLException;

    /**
     * Retrieves all reservations matching a specific status.
     */
    List<Reservation> findByStatus(String status) throws SQLException;

    /**
     * Retrieves all reservations.
     */
    List<Reservation> findAll() throws SQLException;

    /**
     * Updates the status of a reservation using an existing connection.
     * Does not close the connection — designed to participate in an external transaction.
     */
    void updateStatus(Long reservationId, String status, Connection conn) throws SQLException;
}
