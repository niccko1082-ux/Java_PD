package com.hotelnova.service;

import com.hotelnova.model.Room;
import com.hotelnova.model.RoomType;
import com.hotelnova.exception.DuplicateRoomException;
import com.hotelnova.repository.RoomRepository;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link RoomService} class.
 */
public class RoomServiceTest {

    /**
     * Tests that attempting to register a room with a number that already exists
     * results in a {@link DuplicateRoomException}.
     */
    @Test
    void whenRoomNumberExists_thenThrowException() {

        // Mock the repository to always return true, forcing the exception to be thrown
        RoomRepository mockRepository = new RoomRepository() {
            @Override
            public void save(Room room) {}

            @Override
            public boolean isNumberExists(String number) {
                return true; // Simulate that the room number already exists
            }

            @Override
            public void update(Room room) {}

            @Override
            public void delete(Long id) {}

            @Override
            public Room findById(Long id) {
                return null;
            }

            @Override
            public List<Room> findByType(RoomType type) {
                return null;
            }

            @Override
            public List<Room> findAll() {
                return null;
            }

            @Override
            public List<Room> findByStatus(String status) {
                return null;
            }

            @Override
            public List<Room> findByPriceRange(BigDecimal min, BigDecimal max) {
                return null;
            }

            @Override
            public void updateStatus(Long roomId, String status, java.sql.Connection conn) {}
        };

        RoomService service = new RoomService(mockRepository);
        Room duplicateRoom = new Room(null, "101", RoomType.SINGLE, new BigDecimal("100.00"), true);


        assertThrows(DuplicateRoomException.class, () -> {
            service.registerRoom(duplicateRoom);
        });
    }
}