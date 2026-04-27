package com.hotelnova.repository;

import com.hotelnova.model.Room;
import com.hotelnova.model.RoomType;
import com.hotelnova.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RoomRepositoryImp implements RoomRepository {
    @Override
    public void save(Room room) throws SQLException {

        String sql = "INSERT INTO rooms (number, type, price, is_available, is_active) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, room.getNumber());
            pstmt.setString(2, room.getType().name()); // Guardamos el nombre del Enum
            pstmt.setBigDecimal(3, room.getPrice());
            pstmt.setBoolean(4, room.isAvailable());
            pstmt.setBoolean(5, true); // Por defecto una habitación nueva está activa

            pstmt.executeUpdate();
        } catch (SQLException e) {

            throw new SQLException("Error al guardar la habitación: " + e.getMessage());
        }
    }


    @Override
    public void update(Room room) throws SQLException {
        String sql = "UPDATE rooms SET price = ?, type = ?, is_available = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setBigDecimal(1, room.getPrice());
            pstmt.setString(2, room.getType().name());
            pstmt.setBoolean(3, room.isAvailable());
            pstmt.setLong(4, room.getId());
            
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new SQLException("Error al actualizar la habitación: " + e.getMessage());
        }
    }

    @Override
    public void delete(Long id) throws SQLException {

    }

    @Override
    public Room findById(Long id) throws SQLException {
        return null;
    }

    @Override
    public List<Room> findAll() throws SQLException {
        List<Room> rooms = new ArrayList<>();
        String sql = "SELECT * FROM rooms WHERE is_active = true"; // Solo traemos las activas

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Room room = new Room();
                room.setId(rs.getLong("id"));
                room.setNumber(rs.getString("number"));

                // Aquí aplicamos la conversión que mencionamos
                room.setType(RoomType.valueOf(rs.getString("type")));

                room.setPrice(rs.getBigDecimal("price"));
                room.setAvailable(rs.getBoolean("is_available"));

                rooms.add(room);
            }
        }
        return rooms;
    }

    @Override
    public List<Room> findByType(RoomType type) throws SQLException {
        List<Room> rooms = new ArrayList<>();
        String sql = "SELECT * FROM rooms WHERE type = ? AND is_active = true";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
             
            pstmt.setString(1, type.name());
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Room room = new Room();
                    room.setId(rs.getLong("id"));
                    room.setNumber(rs.getString("number"));
                    room.setType(RoomType.valueOf(rs.getString("type")));
                    room.setPrice(rs.getBigDecimal("price"));
                    room.setAvailable(rs.getBoolean("is_available"));
                    rooms.add(room);
                }
            }
        }
        return rooms;
    }

    @Override
    public List<Room> findByPriceRange(java.math.BigDecimal min, java.math.BigDecimal max) throws SQLException {
        List<Room> rooms = new ArrayList<>();
        String sql = "SELECT * FROM rooms WHERE price BETWEEN ? AND ? AND is_active = true";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
             
            pstmt.setBigDecimal(1, min);
            pstmt.setBigDecimal(2, max);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Room room = new Room();
                    room.setId(rs.getLong("id"));
                    room.setNumber(rs.getString("number"));
                    room.setType(RoomType.valueOf(rs.getString("type")));
                    room.setPrice(rs.getBigDecimal("price"));
                    room.setAvailable(rs.getBoolean("is_available"));
                    rooms.add(room);
                }
            }
        }
        return rooms;
    }

    @Override
    public List<Room> findByStatus(String status) throws SQLException {
        return List.of();
    }

    @Override
    public boolean isNumberExists(String number) throws SQLException {
        String sql = "SELECT COUNT(*) FROM rooms WHERE number = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, number);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    @Override
    public void updateStatus(Long roomId, String status, Connection conn) throws SQLException {
        String sql = "UPDATE rooms SET is_available = ? WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setBoolean(1, "AVAILABLE".equalsIgnoreCase(status));
            pstmt.setLong(2, roomId);
            pstmt.executeUpdate();
        }
    }

}
