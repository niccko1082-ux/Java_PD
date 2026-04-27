package com.hotelnova.repository;

import com.hotelnova.model.Reservation;
import com.hotelnova.util.DatabaseConnection;

import java.sql.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * JDBC implementation of the ReservationRepository interface.
 * Uses an externally managed Connection to support atomic transactions.
 */
public class ReservationRepositoryImpl implements ReservationRepository {

    /**
     * Inserts a new reservation into the database using the provided connection.
     * Does not close the connection — the caller is responsible for commit/rollback.
     */
    @Override
    public void save(Reservation reservation, Connection conn) throws SQLException {
        String sql = """
                INSERT INTO reservations (room_id, guest_id, user_id, check_in, check_out, total_price, status)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, reservation.getRoomId());
            pstmt.setLong(2, reservation.getGuestId());
            pstmt.setLong(3, reservation.getUserId());
            pstmt.setDate(4, Date.valueOf(reservation.getCheckIn()));
            pstmt.setDate(5, Date.valueOf(reservation.getCheckOut()));
            pstmt.setBigDecimal(6, reservation.getTotalPrice());
            pstmt.setString(7, reservation.getStatus());

            pstmt.executeUpdate();
        }
    }

    /**
     * Retrieves a reservation by its unique identifier using its own connection.
     */
    @Override
    public Reservation findById(Long id) throws SQLException {
        String sql = "SELECT * FROM reservations WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        }
        return null;
    }

    /**
     * Retrieves all reservations matching a specific status using its own connection.
     */
    @Override
    public List<Reservation> findByStatus(String status) throws SQLException {
        List<Reservation> reservations = new ArrayList<>();
        String sql = "SELECT * FROM reservations WHERE status = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
             
            pstmt.setString(1, status);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    reservations.add(mapRow(rs));
                }
            }
        }
        return reservations;
    }

    /**
     * Retrieves all reservations.
     */
    @Override
    public List<Reservation> findAll() throws SQLException {
        List<Reservation> reservations = new ArrayList<>();
        String sql = "SELECT * FROM reservations";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
             
            while (rs.next()) {
                reservations.add(mapRow(rs));
            }
        }
        return reservations;
    }

    /**
     * Updates the status of a reservation using the provided connection.
     * Does not close the connection — the caller is responsible for commit/rollback.
     */
    @Override
    public void updateStatus(Long reservationId, String status, Connection conn) throws SQLException {
        String sql = "UPDATE reservations SET status = ? WHERE id = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, status);
            pstmt.setLong(2, reservationId);
            pstmt.executeUpdate();
        }
    }

    /**
     * Maps a ResultSet row to a Reservation object.
     */
    private Reservation mapRow(ResultSet rs) throws SQLException {
        return new Reservation(
                rs.getLong("id"),
                rs.getLong("room_id"),
                rs.getLong("guest_id"),
                rs.getLong("user_id"),
                rs.getDate("check_in").toLocalDate(),
                rs.getDate("check_out").toLocalDate(),
                rs.getBigDecimal("total_price"),
                rs.getString("status")
        );
    }
}
