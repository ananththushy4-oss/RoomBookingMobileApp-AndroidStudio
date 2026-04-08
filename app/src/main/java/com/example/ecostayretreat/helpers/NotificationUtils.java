package com.example.ecostayretreat.helpers;

import com.example.ecostayretreat.model.NotificationModel;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * File: NotificationUtils.java
 * Description: A utility class for creating and sending notifications to the Firebase database.
 */
public class NotificationUtils {

    /**
     * Sends a notification to a specific user.
     * @param userId The UID of the user to notify.
     * @param title The title of the notification.
     * @param message The message body of the notification.
     */
    public static void sendNotification(String userId, String title, String message) {
        if (userId == null) return;

        DatabaseReference notificationRef = FirebaseDatabase.getInstance()
                .getReference("notifications")
                .child(userId);

        String notificationId = notificationRef.push().getKey();
        if (notificationId == null) return;

        NotificationModel notification = new NotificationModel();
        notification.setNotificationId(notificationId);
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setTimestamp(System.currentTimeMillis());
        notification.setRead(false);

        notificationRef.child(notificationId).setValue(notification);
    }
}