package com.example.ecostayretreat.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.ecostayretreat.model.UserModel;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.Objects;

/**
 * File: ProfileViewModel.java
 * Description: ViewModel for managing the user's profile data and actions.
 */
public class ProfileViewModel extends ViewModel {

    private final FirebaseAuth firebaseAuth;
    private final DatabaseReference userRef;
    private final FirebaseUser currentUser;

    private final MutableLiveData<UserModel> userLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<Boolean> updateSuccess = new MutableLiveData<>();
    private final MutableLiveData<Boolean> passwordChangeSuccess = new MutableLiveData<>();

    public ProfileViewModel() {
        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();
        userRef = FirebaseDatabase.getInstance().getReference("users").child(currentUser.getUid());
        fetchUserData();
    }

    // LiveData Getters
    public LiveData<UserModel> getUserLiveData() { return userLiveData; }
    public LiveData<Boolean> getIsLoading() { return isLoading; }
    public LiveData<String> getErrorMessage() { return errorMessage; }
    public LiveData<Boolean> getUpdateSuccess() { return updateSuccess; }
    public LiveData<Boolean> getPasswordChangeSuccess() { return passwordChangeSuccess; }


    private void fetchUserData() {
        isLoading.setValue(true);
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserModel user = snapshot.getValue(UserModel.class);
                userLiveData.setValue(user);
                isLoading.setValue(false);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                errorMessage.setValue("Failed to load profile: " + error.getMessage());
                isLoading.setValue(false);
            }
        });
    }

    /**
     * Updates the user's profile information in the Realtime Database.
     * @param userModel The UserModel object with updated information.
     */
    public void updateUserProfile(UserModel userModel) {
        isLoading.setValue(true);
        userRef.setValue(userModel).addOnCompleteListener(task -> {
            isLoading.setValue(false);
            if (task.isSuccessful()) {
                updateSuccess.setValue(true);
                // Reset success state to prevent re-triggering
                updateSuccess.postValue(false);
            } else {
                errorMessage.setValue(Objects.requireNonNull(task.getException()).getMessage());
            }
        });
    }

    /**
     * Changes the user's password after re-authenticating them.
     * @param oldPassword The user's current password.
     * @param newPassword The new password to set.
     */
    public void changePassword(String oldPassword, String newPassword) {
        isLoading.setValue(true);
        if (currentUser == null || currentUser.getEmail() == null) {
            errorMessage.setValue("User not found.");
            isLoading.setValue(false);
            return;
        }

        // Re-authenticate user first for security
        AuthCredential credential = EmailAuthProvider.getCredential(currentUser.getEmail(), oldPassword);
        currentUser.reauthenticate(credential).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // If re-authentication is successful, update the password
                currentUser.updatePassword(newPassword).addOnCompleteListener(passwordTask -> {
                    isLoading.setValue(false);
                    if (passwordTask.isSuccessful()) {
                        passwordChangeSuccess.setValue(true);
                        passwordChangeSuccess.postValue(false);
                    } else {
                        errorMessage.setValue(Objects.requireNonNull(passwordTask.getException()).getMessage());
                    }
                });
            } else {
                isLoading.setValue(false);
                errorMessage.setValue("Re-authentication failed. Please check your current password.");
            }
        });
    }
}