package com.example.ecostayretreat.model;

import java.util.List;
import java.util.Map;

/**
 * File: RoomModel.java
 * Description: Model class representing a room.
 */
public class RoomModel {
    private String roomId;
    private String name;
    private String type;
    private String description;
    private double price;
    private String imageUrl;
    private List<String> features;
    private Map<String, Boolean> availability; // Key: "YYYY-MM-DD", Value: true/false

    public RoomModel() {
    }

    // Getters and Setters
    public String getRoomId() { return roomId; }
    public void setRoomId(String roomId) { this.roomId = roomId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public List<String> getFeatures() { return features; }
    public void setFeatures(List<String> features) { this.features = features; }
    public Map<String, Boolean> getAvailability() { return availability; }
    public void setAvailability(Map<String, Boolean> availability) { this.availability = availability; }
}