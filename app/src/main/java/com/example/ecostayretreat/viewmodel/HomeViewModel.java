package com.example.ecostayretreat.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.ecostayretreat.model.ActivityModel;
import com.example.ecostayretreat.model.RoomModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * File: HomeViewModel.java
 * Description: ViewModel for the Home Screen. Fetches data for carousels.
 */
public class HomeViewModel extends ViewModel {

    private final DatabaseReference roomsRef;
    private final DatabaseReference activitiesRef;

    private final MutableLiveData<List<RoomModel>> topRoomsLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<ActivityModel>> ecoActivitiesLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    public HomeViewModel() {
        roomsRef = FirebaseDatabase.getInstance().getReference("rooms");
        activitiesRef = FirebaseDatabase.getInstance().getReference("activities");
        fetchTopRooms();
        fetchEcoActivities();
    }

    public LiveData<List<RoomModel>> getTopRoomsLiveData() {
        return topRoomsLiveData;
    }

    public LiveData<List<ActivityModel>> getEcoActivitiesLiveData() {
        return ecoActivitiesLiveData;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    /**
     * Fetches a limited number of rooms to display in the home screen carousel.
     */
    private void fetchTopRooms() {
        roomsRef.limitToFirst(5).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<RoomModel> roomList = new ArrayList<>();
                for (DataSnapshot roomSnapshot : snapshot.getChildren()) {
                    RoomModel room = roomSnapshot.getValue(RoomModel.class);
                    if (room != null) {
                        room.setRoomId(roomSnapshot.getKey());
                        roomList.add(room);
                    }
                }
                topRoomsLiveData.setValue(roomList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                errorMessage.setValue("Failed to load top rooms: " + error.getMessage());
            }
        });
    }

    /**
     * Fetches a limited number of activities for the home screen carousel.
     */
    private void fetchEcoActivities() {
        activitiesRef.limitToFirst(5).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<ActivityModel> activityList = new ArrayList<>();
                for (DataSnapshot activitySnapshot : snapshot.getChildren()) {
                    ActivityModel activity = activitySnapshot.getValue(ActivityModel.class);
                    if (activity != null) {
                        activity.setActivityId(activitySnapshot.getKey());
                        activityList.add(activity);
                    }
                }
                ecoActivitiesLiveData.setValue(activityList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                errorMessage.setValue("Failed to load activities: " + error.getMessage());
            }
        });
    }
}