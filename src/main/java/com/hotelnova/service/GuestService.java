package com.hotelnova.service;

import com.hotelnova.model.Guest;
import com.hotelnova.repository.GuestRepository;
import com.hotelnova.exception.InactiveGuestException;
import java.sql.SQLException;
import java.util.List;

/**
 * Service class handling business logic operations for Guest entities.
 */
public class GuestService {
    private final GuestRepository guestRepository;

    /**
     * Constructs a new GuestService with the given GuestRepository.
     */
    public GuestService(GuestRepository guestRepository) {
        this.guestRepository = guestRepository;
    }

    /**
     * Registers a new guest by delegating to the DAO layer.
     */
    public void registerGuest(Guest guest) throws SQLException {
        guestRepository.save(guest);
        System.out.println("[LOG] POST /api/guests - Guest " + guest.getFirstName() + " " + guest.getLastName() + " registered");
    }

    /**
     * Updates an existing guest by delegating to the DAO layer.
     */
    public void updateGuest(Guest guest) throws SQLException {
        guestRepository.update(guest);
        System.out.println("[LOG] PUT /api/guests/" + guest.getId() + " - Guest updated");
    }

    /**
     * Retrieves all registered guests from the database.
     */
    public List<Guest> getAllGuests() throws SQLException {
        return guestRepository.findAll();
    }

    /**
     * Validates if a guest is active.
     * Throws an InactiveGuestException if the guest is inactive and cannot perform
     * operations like making reservations.
     */
    public void validateGuestIsActive(Long guestId) throws SQLException, InactiveGuestException {
        // The service queries the DAO to retrieve the current active status
        if (!guestRepository.isGuestActive(guestId)) {
            // Throw a custom exception if the business rule is not met
            throw new InactiveGuestException(
                    "The guest with ID " + guestId + " is currently inactive and cannot make reservations.");
        }
        // Simulate a console log request as required by the project
        System.out.println("[LOG] GET /api/guests/" + guestId + "/status - Active check passed");
    }
}