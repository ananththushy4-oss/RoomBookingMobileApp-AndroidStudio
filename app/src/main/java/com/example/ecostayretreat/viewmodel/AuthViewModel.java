package com.example.ecostayretreat.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.ecostayretreat.model.UserModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.Objects;

/**
 * File: AuthViewModel.java
 * Description: ViewModel for handling all authentication-related logic (Login, Register).
 */
public class AuthViewModel extends AndroidViewModel {

    private final FirebaseAuth firebaseAuth;
    private final DatabaseReference databaseReference;

    // LiveData to observe authentication state changes
    private final MutableLiveData<FirebaseUser> userLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private final MutableLiveData<Boolean> registrationSuccess = new MutableLiveData<>();

    public AuthViewModel(@NonNull Application application) {
        super(application);
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        // Check if a user is already logged in when the ViewModel is created
        if (firebaseAuth.getCurrentUser() != null) {
            userLiveData.postValue(firebaseAuth.getCurrentUser());
        }
    }

    // LiveData getters for observers in UI
    public LiveData<FirebaseUser> getUserLiveData() {
        return userLiveData;
    }
    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }
    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }
    public LiveData<Boolean> getRegistrationSuccess() {
        return registrationSuccess;
    }

    /**
     * Handles user login with email and password.
     * @param email User's email.
     * @param password User's password.
     */
    public void login(String email, String password) {
        isLoading.setValue(true);
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    isLoading.setValue(false);
                    if (task.isSuccessful()) {
                        userLiveData.postValue(firebaseAuth.getCurrentUser());
                    } else {
                        errorMessage.postValue(Objects.requireNonNull(task.getException()).getMessage());
                    }
                });
    }

    /**
     * Handles new user registration.
     * @param firstName User's first name.
     * @param lastName User's last name.
     * @param address User's address.
     * @param nic User's NIC.
     * @param phone User's phone number.
     * @param email User's email.
     * @param password User's password.
     */
    public void register(String firstName, String lastName, String address, String nic, String phone, String email, String password) {
        isLoading.setValue(true);
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                        if (firebaseUser != null) {
                            String uid = firebaseUser.getUid();
                            // Create a user model and save to Realtime Database
                            UserModel userModel = new UserModel(uid, firstName, lastName, email, address, nic, phone);
                            databaseReference.child("users").child(uid).setValue(userModel)
                                    .addOnCompleteListener(dbTask -> {
                                        isLoading.setValue(false);
                                        if (dbTask.isSuccessful()) {
                                            registrationSuccess.postValue(true);
                                        } else {
                                            errorMessage.postValue(Objects.requireNonNull(dbTask.getException()).getMessage());
                                        }
                                    });
                        }
                    } else {
                        isLoading.setValue(false);
                        errorMessage.postValue(Objects.requireNonNull(task.getException()).getMessage());
                    }
                });
    }

    /**
     * Logs out the current user.
     */
    public void logout() {
        firebaseAuth.signOut();
        userLiveData.postValue(null);
    }
}