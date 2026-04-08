package com.example.ecostayretreat.viewmodel;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.ecostayretreat.model.RoomModel;
import com.example.ecostayretreat.model.UserModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class RoomViewModel extends ViewModel {

    private final DatabaseReference roomsRef;
    private final DatabaseReference usersRef;
    private final String currentUserId;

    private final MutableLiveData<List<RoomModel>> allRooms = new MutableLiveData<>();
    private final MutableLiveData<List<RoomModel>> filteredRooms = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isAdmin = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> operationSuccess = new MutableLiveData<>();


    public RoomViewModel() {
        roomsRef = FirebaseDatabase.getInstance().getReference("rooms");
        usersRef = FirebaseDatabase.getInstance().getReference("users");
        currentUserId = FirebaseAuth.getInstance().getUid();
        fetchRooms();
        checkIfAdmin();
    }

    public LiveData<List<RoomModel>> getFilteredRooms() { return filteredRooms; }
    public LiveData<Boolean> getIsLoading() { return isLoading; }
    public LiveData<String> getErrorMessage() { return errorMessage; }
    public LiveData<Boolean> isAdmin() { return isAdmin; }
    public LiveData<Boolean> getOperationSuccess() { return operationSuccess; }


    private void fetchRooms() {
        isLoading.setValue(true);
        roomsRef.addValueEventListener(new ValueEventListener() {
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
                allRooms.setValue(roomList);
                filteredRooms.setValue(roomList);
                isLoading.setValue(false);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                errorMessage.setValue("Failed to load rooms: " + error.getMessage());
                isLoading.setValue(false);
            }
        });
    }

    public void applyFilters(String query, float minPrice, float maxPrice, @Nullable Long startDate, @Nullable Long endDate, List<String> featureFilters) {
        List<RoomModel> currentRooms = allRooms.getValue();
        if (currentRooms == null) return;

        List<RoomModel> filteredList = currentRooms.stream()
                .filter(room -> {
                    // Search query filter
                    boolean matchesQuery = query.isEmpty() ||
                            room.getName().toLowerCase().contains(query.toLowerCase());

                    // Price filter
                    boolean matchesPrice = room.getPrice() >= minPrice && room.getPrice() <= maxPrice;

                    // Features filter
                    boolean matchesFeatures = featureFilters.isEmpty() ||
                            (room.getFeatures() != null && room.getFeatures().containsAll(featureFilters));

                    // Availability filter
                    boolean matchesAvailability = isDateRangeAvailable(room, startDate, endDate);

                    return matchesQuery && matchesPrice && matchesFeatures && matchesAvailability;
                })
                .collect(Collectors.toList());

        filteredRooms.setValue(filteredList);
    }

    private boolean isDateRangeAvailable(RoomModel room, @Nullable Long startDate, @Nullable Long endDate) {
        if (startDate == null || endDate == null) {
            return true; // No date filter applied
        }
        if (room.getAvailability() == null) {
            return true; // If no availability is specified, assume it's available
        }

        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(startDate);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

        while (cal.getTimeInMillis() <= endDate) {
            String dateKey = sdf.format(cal.getTime());
            Map<String, Boolean> availabilityMap = room.getAvailability();
            // If the date is in the map and it's marked as unavailable (false), the room is not available.
            if (availabilityMap.containsKey(dateKey) && !availabilityMap.get(dateKey)) {
                return false;
            }
            cal.add(Calendar.DAY_OF_YEAR, 1);
        }
        return true; // All dates in the range are available
    }

    private void checkIfAdmin() {
        if (currentUserId == null) return;
        usersRef.child(currentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserModel user = snapshot.getValue(UserModel.class);
                if (user != null) {
                    isAdmin.setValue(user.isAdmin());
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                errorMessage.setValue("Failed to verify admin status.");
            }
        });
    }

    // ... addRoom, updateRoom, deleteRoom methods remain the same ...
    public void addRoom(RoomModel room) {
        String roomId = roomsRef.push().getKey();
        if (roomId != null) {
            room.setRoomId(roomId);
            roomsRef.child(roomId).setValue(room).addOnCompleteListener(task -> {
                operationSuccess.setValue(task.isSuccessful());
                if (!task.isSuccessful()) errorMessage.setValue(Objects.requireNonNull(task.getException()).getMessage());
            });
        }
    }
    public void updateRoom(RoomModel room) {
        roomsRef.child(room.getRoomId()).setValue(room).addOnCompleteListener(task -> {
            operationSuccess.setValue(task.isSuccessful());
            if (!task.isSuccessful()) errorMessage.setValue(Objects.requireNonNull(task.getException()).getMessage());
        });
    }
    public void deleteRoom(String roomId) {
        roomsRef.child(roomId).removeValue().addOnCompleteListener(task -> {
            operationSuccess.setValue(task.isSuccessful());
            if (!task.isSuccessful()) errorMessage.setValue(Objects.requireNonNull(task.getException()).getMessage());
        });
    }
}