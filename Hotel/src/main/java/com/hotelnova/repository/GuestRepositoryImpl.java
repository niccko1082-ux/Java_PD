package com.hotelnova.repository;

import com.hotelnova.model.Guest;
import com.hotelnova.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * JDBC implementation of the GuestRepository interface.
 * Handles direct database operations for Guest entities using PreparedStatements.
 */
public class GuestRepositoryImpl implements GuestRepository {

    /**
     * Persists a new guest into the database.
     */
    @Override
    public void save(Guest guest) throws SQLException {
        String sql = "INSERT INTO guests (first_name, last_name, dni, email, is_active) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, guest.getFirstName());
            pstmt.setString(2, guest.getLastName());
            pstmt.setString(3, guest.getDni());
            pstmt.setString(4, guest.getEmail());
            pstmt.setBoolean(5, guest.isActive());

            pstmt.executeUpdate();
        }
    }

    /**
     * Updates an existing guest's information in the database.
     */
    @Override
    public void update(Guest guest) throws SQLException {
        String sql = "UPDATE guests SET first_name = ?, last_name = ?, dni = ?, email = ?, is_active = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, guest.getFirstName());
            pstmt.setString(2, guest.getLastName());
            pstmt.setString(3, guest.getDni());
            pstmt.setString(4, guest.getEmail());
            pstmt.setBoolean(5, guest.isActive());
            pstmt.setLong(6, guest.getId());

            pstmt.executeUpdate();
        }
    }

    /**
     * Retrieves a guest by their unique identifier.
     */
    @Override
    public Guest findById(Long id) throws SQLException {
        String sql = "SELECT * FROM guests WHERE id = ?";

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
     * Retrieves a guest by their DNI.
     */
    @Override
    public Guest findByDni(String dni) throws SQLException {
        String sql = "SELECT * FROM guests WHERE dni = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, dni);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        }
        return null;
    }

    /**
     * Retrieves all guests from the database.
     */
    @Override
    public List<Guest> findAll() throws SQLException {
        List<Guest> guests = new ArrayList<>();
        String sql = "SELECT * FROM guests";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                guests.add(mapRow(rs));
            }
        }
        return guests;
    }

    /**
     * Checks if a guest is currently active based on their ID.
     */
    @Override
    public boolean isGuestActive(Long id) throws SQLException {
        String sql = "SELECT is_active FROM guests WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getBoolean("is_active");
                }
            }
        }
        return false;
    }

    /**
     * Maps a ResultSet row to a Guest object.
     */
    private Guest mapRow(ResultSet rs) throws SQLException {
        return new Guest(
                rs.getLong("id"),
                rs.getString("first_name"),
                rs.getString("last_name"),
                rs.getString("dni"),
                rs.getString("email"),
                rs.getBoolean("is_active")
        );
    }
}
