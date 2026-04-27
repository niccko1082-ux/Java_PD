package com.hotelnova.repository;

import com.hotelnova.model.User;
import java.sql.SQLException;
import java.util.List;

/**
 * Repository interface for managing User entities.
 * Defines authentication-related data access operations.
 */
public interface UserRepository {

    /**
     * Persists a new user into the database.
     */
    void save(User user) throws SQLException;

    /**
     * Retrieves all users from the database.
     */
    List<User> findAll() throws SQLException;

    /**
     * Retrieves a user by their username.
     */
    User findByUsername(String username) throws SQLException;
}
