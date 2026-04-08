package com.example.ecostayretreat.model;

/**
 * File: NotificationModel.java
 * Description: Model for in-app notifications.
 */
public class NotificationModel {
    private String notificationId;
    private String title;
    private String message;
    private long timestamp;
    private boolean isRead;

    public NotificationModel() {
    }

    // Getters and Setters
    public String getNotificationId() { return notificationId; }
    public void setNotificationId(String notificationId) { this.notificationId = notificationId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
    public boolean isRead() { return isRead; }
    public void setRead(boolean read) { isRead = read; }
}