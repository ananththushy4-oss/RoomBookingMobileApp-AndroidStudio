package com.example.ecostayretreat.model;

import java.util.List;

/**
 * File: ActivityModel.java
 * Description: Model class representing an eco-activity.
 */
public class ActivityModel {
    private String activityId;
    private String name;
    private String description;
    private double price;
    private String imageUrl;
    private List<String> schedule; // e.g., ["MON 10:00", "WED 14:00"]

    public ActivityModel() {
    }

    // Getters and Setters
    public String getActivityId() { return activityId; }
    public void setActivityId(String activityId) { this.activityId = activityId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public List<String> getSchedule() { return schedule; }
    public void setSchedule(List<String> schedule) { this.schedule = schedule; }
}