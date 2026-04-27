package com.hotelnova.model;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Represents a reservation entity within the hotel system.
 * Links a guest to a room for a specific date range and tracks its status.
 */
public class Reservation {
    private Long id;
    private Long roomId;
    private Long guestId;
    private Long userId;
    private LocalDate checkIn;
    private LocalDate checkOut;
    private BigDecimal totalPrice;
    private String status;

    /**
     * Default constructor.
     */
    public Reservation() {}

    /**
     * Constructs a new Reservation with the specified details.
     */
    public Reservation(Long id, Long roomId, Long guestId, Long userId,
                       LocalDate checkIn, LocalDate checkOut,
                       BigDecimal totalPrice, String status) {
        this.id = id;
        this.roomId = roomId;
        this.guestId = guestId;
        this.userId = userId;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.totalPrice = totalPrice;
        this.status = status;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getRoomId() { return roomId; }
    public void setRoomId(Long roomId) { this.roomId = roomId; }

    public Long getGuestId() { return guestId; }
    public void setGuestId(Long guestId) { this.guestId = guestId; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public LocalDate getCheckIn() { return checkIn; }
    public void setCheckIn(LocalDate checkIn) { this.checkIn = checkIn; }

    public LocalDate getCheckOut() { return checkOut; }
    public void setCheckOut(LocalDate checkOut) { this.checkOut = checkOut; }

    public BigDecimal getTotalPrice() { return totalPrice; }
    public void setTotalPrice(BigDecimal totalPrice) { this.totalPrice = totalPrice; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    @Override
    public String toString() {
        return "Reservation{" +
                "id=" + id +
                ", roomId=" + roomId +
                ", guestId=" + guestId +
                ", checkIn=" + checkIn +
                ", checkOut=" + checkOut +
                ", totalPrice=" + totalPrice +
                ", status='" + status + '\'' +
                '}';
    }
}
