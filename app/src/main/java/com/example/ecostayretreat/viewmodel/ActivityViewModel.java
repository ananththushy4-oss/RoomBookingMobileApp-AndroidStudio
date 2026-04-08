package com.example.ecostayretreat.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.ecostayretreat.model.ActivityModel;
import com.example.ecostayretreat.model.UserModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

/**
 * File: ActivityViewModel.java
 * Description: ViewModel for managing eco-activities data and admin operations.
 */
public class ActivityViewModel extends ViewModel {

    private final DatabaseReference activitiesRef;
    private final MutableLiveData<Boolean> isAdmin = new MutableLiveData<>(false);
    private final MutableLiveData<List<ActivityModel>> allActivities = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<Boolean> operationSuccess = new MutableLiveData<>();

    public ActivityViewModel() {
        activitiesRef = FirebaseDatabase.getInstance().getReference("activities");
        fetchActivities();
        checkIfAdmin();
    }

    // LiveData Getters
    public LiveData<List<ActivityModel>> getAllActivities() { return allActivities; }
    public LiveData<Boolean> isAdmin() { return isAdmin; }
    public LiveData<Boolean> getIsLoading() { return isLoading; }
    public LiveData<String> getErrorMessage() { return errorMessage; }
    public LiveData<Boolean> getOperationSuccess() { return operationSuccess; }


    private void fetchActivities() {
        isLoading.setValue(true);
        activitiesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<ActivityModel> activityList = new ArrayList<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    ActivityModel activity = dataSnapshot.getValue(ActivityModel.class);
                    if (activity != null) {
                        activity.setActivityId(dataSnapshot.getKey());
                        activityList.add(activity);
                    }
                }
                allActivities.setValue(activityList);
                isLoading.setValue(false);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                errorMessage.setValue("Failed to load activities: " + error.getMessage());
                isLoading.setValue(false);
            }
        });
    }

    private void checkIfAdmin() {
        String uid = FirebaseAuth.getInstance().getUid();
        if (uid == null) return;
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(uid);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserModel user = snapshot.getValue(UserModel.class);
                if (user != null) {
                    isAdmin.setValue(user.isAdmin());
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    public void addActivity(ActivityModel activity) {
        String id = activitiesRef.push().getKey();
        if (id != null) {
            activity.setActivityId(id);
            activitiesRef.child(id).setValue(activity)
                    .addOnSuccessListener(aVoid -> operationSuccess.setValue(true))
                    .addOnFailureListener(e -> errorMessage.setValue(e.getMessage()));
        }
    }

    public void updateActivity(ActivityModel activity) {
        activitiesRef.child(activity.getActivityId()).setValue(activity)
                .addOnSuccessListener(aVoid -> operationSuccess.setValue(true))
                .addOnFailureListener(e -> errorMessage.setValue(e.getMessage()));
    }

    public void deleteActivity(String activityId) {
        activitiesRef.child(activityId).removeValue()
                .addOnSuccessListener(aVoid -> operationSuccess.setValue(true))
                .addOnFailureListener(e -> errorMessage.setValue(e.getMessage()));
    }
}