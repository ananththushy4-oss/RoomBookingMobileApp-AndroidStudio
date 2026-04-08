package com.example.ecostayretreat.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.ecostayretreat.model.NotificationModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * File: NotificationViewModel.java
 * Description: ViewModel for fetching and managing user notifications.
 */
public class NotificationViewModel extends ViewModel {

    private final DatabaseReference notificationsRef;
    private final String currentUserId;

    private final MutableLiveData<List<NotificationModel>> notificationsList = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    public NotificationViewModel() {
        currentUserId = FirebaseAuth.getInstance().getUid();
        notificationsRef = FirebaseDatabase.getInstance().getReference("notifications").child(currentUserId);
        fetchNotifications();
    }

    // LiveData Getters
    public LiveData<List<NotificationModel>> getNotificationsList() { return notificationsList; }
    public LiveData<Boolean> getIsLoading() { return isLoading; }
    public LiveData<String> getErrorMessage() { return errorMessage; }

    private void fetchNotifications() {
        isLoading.setValue(true);
        notificationsRef.orderByChild("timestamp").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<NotificationModel> notifications = new ArrayList<>();
                for (DataSnapshot notificationSnapshot : snapshot.getChildren()) {
                    NotificationModel notification = notificationSnapshot.getValue(NotificationModel.class);
                    if (notification != null) {
                        notification.setNotificationId(notificationSnapshot.getKey());
                        notifications.add(notification);
                    }
                }
                // Reverse the list to show newest notifications first
                Collections.reverse(notifications);
                notificationsList.setValue(notifications);
                isLoading.setValue(false);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                errorMessage.setValue("Failed to load notifications: " + error.getMessage());
                isLoading.setValue(false);
            }
        });
    }

    /**
     * Marks a specific notification as read in the database.
     * @param notificationId The ID of the notification to update.
     */
    public void markAsRead(String notificationId) {
        notificationsRef.child(notificationId).child("read").setValue(true);
    }
}