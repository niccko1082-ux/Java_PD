package com.hotelnova.model;

import java.math.BigDecimal;

/**
 * Represents a room entity within the hotel system.
 * Contains core information such as the room number, type, price, and availability status.
 */
public class Room {

    private Long id;
    private String number;
    private RoomType type;
    private BigDecimal price;
    private boolean isAvailable;

    /**
     * Default constructor.
     */
    public Room() {}

    /**
     * Constructs a new Room with the specified details.
     */
    public Room(Long id, String number, RoomType type, BigDecimal price, boolean isAvailable) {
        this.id = id;
        this.number = number;
        this.type = type;
        this.price = price;
        this.isAvailable = isAvailable;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNumber() { return number; }
    public void setNumber(String number) { this.number = number; }

    public RoomType getType() { return type; }
    public void setType(RoomType type) { this.type = type; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public boolean isAvailable() { return isAvailable; }
    public void setAvailable(boolean available) { isAvailable = available; }
}
