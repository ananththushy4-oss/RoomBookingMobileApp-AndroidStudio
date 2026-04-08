package com.example.ecostayretreat.model;

/**
 * File: CartItemModel.java
 * Description: Model class for items in the user's cart.
 */
public class CartItemModel {
    private String itemId; // Unique ID for the cart item itself
    private String refId; // Reference to Room ID or Activity ID
    private String name;
    private String type; // "Room" or "Activity"
    private double price;
    private String dateRange; // e.g., "2025-12-20 to 2025-12-23"

    public CartItemModel() {
    }

    // Getters and Setters
    public String getItemId() { return itemId; }
    public void setItemId(String itemId) { this.itemId = itemId; }
    public String getRefId() { return refId; }
    public void setRefId(String refId) { this.refId = refId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    public String getDateRange() { return dateRange; }
    public void setDateRange(String dateRange) { this.dateRange = dateRange; }
}