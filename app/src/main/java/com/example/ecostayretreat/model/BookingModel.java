package com.example.ecostayretreat.model;

/**
 * File: BookingModel.java
 * Description: Model class representing a booking.
 */
public class BookingModel {
    private String bookingId;
    private String userId;
    private String itemId; // Can be roomId or activityId
    private String itemName;
    private String itemType; // "Room" or "Activity"
    private String checkInDate;
    private String checkOutDate;
    private double totalPrice;
    private String status; // e.g., "CONFIRMED", "CANCELLED"

    public BookingModel() {
    }

    // Getters and Setters
    public String getBookingId() { return bookingId; }
    public void setBookingId(String bookingId) { this.bookingId = bookingId; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getItemId() { return itemId; }
    public void setItemId(String itemId) { this.itemId = itemId; }
    public String getItemName() { return itemName; }
    public void setItemName(String itemName) { this.itemName = itemName; }
    public String getItemType() { return itemType; }
    public void setItemType(String itemType) { this.itemType = itemType; }
    public String getCheckInDate() { return checkInDate; }
    public void setCheckInDate(String checkInDate) { this.checkInDate = checkInDate; }
    public String getCheckOutDate() { return checkOutDate; }
    public void setCheckOutDate(String checkOutDate) { this.checkOutDate = checkOutDate; }
    public double getTotalPrice() { return totalPrice; }
    public void setTotalPrice(double totalPrice) { this.totalPrice = totalPrice; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}