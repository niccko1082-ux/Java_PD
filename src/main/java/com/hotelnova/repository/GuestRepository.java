package com.hotelnova.repository;

import com.hotelnova.model.Guest;
import java.sql.SQLException;
import java.util.List;

public interface GuestRepository {
    void save(Guest guest) throws SQLException;

    void update(Guest guest) throws SQLException;

    Guest findById(Long id) throws SQLException;

    Guest findByDni(String dni) throws SQLException;

    List<Guest> findAll() throws SQLException;

    boolean isGuestActive(Long id) throws SQLException;
}
