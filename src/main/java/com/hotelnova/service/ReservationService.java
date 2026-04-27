package com.hotelnova.service;

import com.hotelnova.config.ConfigLoader;
import com.hotelnova.exception.HotelNovaException;
import com.hotelnova.exception.InactiveGuestException;
import com.hotelnova.exception.ReservationException;
import com.hotelnova.model.Guest;
import com.hotelnova.model.Reservation;
import com.hotelnova.model.Room;
import com.hotelnova.repository.GuestRepository;
import com.hotelnova.repository.ReservationRepository;
import com.hotelnova.repository.RoomRepository;
import com.hotelnova.util.DatabaseConnection;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * Service class handling check-in and check-out business logic for reservations.
 * Manages JDBC transactions to guarantee atomicity across multiple DAO operations.
 */
public class ReservationService {

    private static final Logger logger = Logger.getLogger(ReservationService.class.getName());

    static {
        try {
            FileHandler fileHandler = new FileHandler("app.log", true);
            fileHandler.setFormatter(new SimpleFormatter());
            logger.addHandler(fileHandler);
        } catch (IOException e) {
            System.err.println("Could not setup FileHandler for logger: " + e.getMessage());
        }
    }

    private final ReservationRepository reservationRepository;
    private final RoomRepository roomRepository;
    private final GuestRepository guestRepository;

    /**
     * Constructs a new ReservationService with the required repositories.
     * Uses constructor injection as a best architectural practice.
     */
    public ReservationService(ReservationRepository reservationRepository,
                              RoomRepository roomRepository,
                              GuestRepository guestRepository) {
        this.reservationRepository = reservationRepository;
        this.roomRepository = roomRepository;
        this.guestRepository = guestRepository;
    }

    /**
     * Retrieves all reservations from the database.
     */
    public List<Reservation> listAllReservations() throws SQLException {
        System.out.println("[LOG] GET /api/reservations");
        return reservationRepository.findAll();
    }

    /**
     * Processes a check-in by validating business rules and executing a JDBC transaction.
     * If any step fails, the entire transaction is rolled back.
     */
    public void processCheckIn(Reservation reservation)
            throws SQLException, ReservationException, InactiveGuestException {

        // --- Business rule validations ---
        validateDates(reservation);
        validateRoomAvailability(reservation.getRoomId());
        validateGuestIsActive(reservation.getGuestId());

        // --- Execute transactional check-in ---
        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);

            try {
                // Step 1: Save the reservation record
                reservationRepository.save(reservation, conn);

                // Step 2: Mark the room as occupied
                roomRepository.updateStatus(reservation.getRoomId(), "OCCUPIED", conn);

                conn.commit();
                System.out.println("[LOG] POST /api/reservations/check-in - Reservation for guestId="
                        + reservation.getGuestId() + ", roomId=" + reservation.getRoomId() + " - SUCCESS");

            } catch (SQLException e) {
                conn.rollback();
                System.out.println("[LOG] POST /api/reservations/check-in - ROLLBACK due to: " + e.getMessage());
                throw new SQLException("Check-in transaction failed and was rolled back.", e);
            }
        }
    }

    /**
     * Processes a check-out for the given reservation ID.
     * Calculates the total with IVA from config.properties and executes an atomic transaction
     * that marks the reservation as COMPLETED and frees the room.
     */
    public BigDecimal processCheckOut(Long reservationId)
            throws SQLException, ReservationException {

        // --- Load reservation and associated room ---
        Reservation reservation = reservationRepository.findById(reservationId);
        if (reservation == null) {
            throw new ReservationException("Reservation with ID " + reservationId + " not found.");
        }

        // --- Business rule: reservation must not be already completed ---
        if ("COMPLETED".equalsIgnoreCase(reservation.getStatus())) {
            throw new ReservationException("Reservation with ID " + reservationId + " has already been checked out.");
        }

        Room room = roomRepository.findById(reservation.getRoomId());
        if (room == null) {
            throw new ReservationException("Room associated with reservation " + reservationId + " not found.");
        }

        // --- Calculate total price with IVA ---
        long nights = Math.max(1, ChronoUnit.DAYS.between(reservation.getCheckIn(), reservation.getCheckOut()));
        BigDecimal ivaRate = new BigDecimal(ConfigLoader.getProperty("iva.rate"));
        BigDecimal subtotal = room.getPrice().multiply(BigDecimal.valueOf(nights));
        BigDecimal totalWithIva = calculateTotal(subtotal, ivaRate);

        // --- Execute transactional check-out ---
        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);

            try {
                // Step 1: Mark reservation as COMPLETED
                reservationRepository.updateStatus(reservationId, "COMPLETED", conn);

                // Step 2: Mark the room as AVAILABLE again
                roomRepository.updateStatus(reservation.getRoomId(), "AVAILABLE", conn);

                conn.commit();
                System.out.printf("[LOG] PATCH /api/reservations/%d/checkout - Total: %.2f%n",
                        reservationId, totalWithIva);

            } catch (SQLException e) {
                conn.rollback();
                System.out.println("[LOG] PATCH /api/reservations/" + reservationId + "/checkout - ROLLBACK due to: " + e.getMessage());
                throw new SQLException("Check-out transaction failed and was rolled back.", e);
            }
        }

        return totalWithIva;
    }

    /**
     * Exports all ACTIVE reservations to a CSV file.
     * The CSV will contain ID, RoomNumber, GuestDNI, CheckIn, CheckOut, and TotalPrice.
     */
    public void exportActiveReservations(String filePath) throws SQLException, HotelNovaException {
        List<Reservation> activeReservations = reservationRepository.findByStatus("ACTIVE");

        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            // Write CSV header
            writer.println("ID,RoomNumber,GuestDNI,CheckIn,CheckOut,TotalPrice");

            // Write reservation rows
            for (Reservation res : activeReservations) {
                // Fetch associated entities to get RoomNumber and GuestDNI
                Room room = roomRepository.findById(res.getRoomId());
                Guest guest = guestRepository.findById(res.getGuestId());

                String roomNumber = room != null ? room.getNumber() : "N/A";
                String guestDni = guest != null ? guest.getDni() : "N/A";

                writer.printf("%d,%s,%s,%s,%s,%s%n",
                        res.getId(),
                        roomNumber,
                        guestDni,
                        res.getCheckIn(),
                        res.getCheckOut(),
                        res.getTotalPrice() != null ? res.getTotalPrice().toString() : "0.00"
                );
            }
            
            System.out.println("[LOG] GET /api/reservations/export - File created at: " + filePath);
            
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error detallado: ", e);
            throw new HotelNovaException("Could not export report. Please check permissions.");
        }
    }

    /**
     * Validates that check-in date is strictly before check-out date.
     */
    private void validateDates(Reservation reservation) throws ReservationException {
        if (!reservation.getCheckIn().isBefore(reservation.getCheckOut())) {
            throw new ReservationException("Check-in date must be before check-out date.");
        }
    }

    /**
     * Validates that the requested room is currently available.
     */
    private void validateRoomAvailability(Long roomId) throws SQLException, ReservationException {
        var room = roomRepository.findById(roomId);
        if (room == null || !room.isAvailable()) {
            throw new ReservationException("Room with ID " + roomId + " is not available for check-in.");
        }
    }

    /**
     * Validates that the guest is active and allowed to make reservations.
     */
    private void validateGuestIsActive(Long guestId) throws SQLException, InactiveGuestException {
        if (!guestRepository.isGuestActive(guestId)) {
            throw new InactiveGuestException(
                    "Guest with ID " + guestId + " is inactive and cannot make reservations.");
        }
    }

    /**
     * Applies IVA to a subtotal and rounds to 2 decimal places.
     * Extracted as a static method to allow direct unit testing without database access.
     */
    static BigDecimal calculateTotal(BigDecimal subtotal, BigDecimal ivaRate) {
        return subtotal.multiply(BigDecimal.ONE.add(ivaRate)).setScale(2, RoundingMode.HALF_UP);
    }
}
