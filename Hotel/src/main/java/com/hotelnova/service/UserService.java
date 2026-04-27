package com.hotelnova.service;

import com.hotelnova.model.User;
import com.hotelnova.model.UserRole;
import com.hotelnova.repository.UserRepository;
import com.hotelnova.exception.AuthenticationException;
import org.mindrot.jbcrypt.BCrypt;
import java.sql.SQLException;
import java.util.List;

/**
 * Service class handling authentication and user management for the hotel system.
 */
public class UserService {
    private final UserRepository userRepository;

    /**
     * Constructs a new UserService with the given UserRepository.
     * Uses constructor injection as a best architectural practice.
     */
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Registers a new user by hashing their password with BCrypt before persisting.
     */
    public void registerUser(String username, String plainPassword, UserRole role) throws SQLException {
        // Hash the plain-text password before storing it
        String hashedPassword = BCrypt.hashpw(plainPassword, BCrypt.gensalt());
        User user = new User(null, username, hashedPassword, role);
        userRepository.save(user);
        System.out.println("[LOG] POST /api/users - User " + username + " registered with role " + role.name());
    }

    /**
     * Authenticates a user by validating the provided password against the stored BCrypt hash.
     * Throws AuthenticationException if credentials are invalid.
     */
    public User authenticate(String username, String plainPassword) throws SQLException, AuthenticationException {
        User user = userRepository.findByUsername(username);

        // Validate that the user exists and the password matches the stored hash
        if (user == null || !BCrypt.checkpw(plainPassword, user.getPassword())) {
            System.out.println("[LOG] POST /api/auth/login - User: " + username + " - FAILED");
            throw new AuthenticationException("Invalid username or password.");
        }

        System.out.println("[LOG] POST /api/auth/login - User: " + username + " - SUCCESS");
        return user;
    }

    /**
     * Retrieves all registered users from the database.
     */
    public List<User> getAllUsers() throws SQLException {
        return userRepository.findAll();
    }
}
