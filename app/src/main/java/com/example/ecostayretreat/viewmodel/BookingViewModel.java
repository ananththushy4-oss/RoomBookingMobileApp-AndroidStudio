package com.example.ecostayretreat.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.ecostayretreat.model.BookingModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * File: BookingViewModel.java
 * Description: ViewModel for managing the user's booking history.
 */
public class BookingViewModel extends ViewModel {

    private final DatabaseReference bookingsRef;
    private final String currentUserId;

    private final MutableLiveData<List<BookingModel>> allUserBookings = new MutableLiveData<>();
    private final MutableLiveData<List<BookingModel>> filteredBookings = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<Boolean> cancellationSuccess = new MutableLiveData<>();

    public BookingViewModel() {
        bookingsRef = FirebaseDatabase.getInstance().getReference("bookings");
        currentUserId = FirebaseAuth.getInstance().getUid();
        fetchUserBookings();
    }

    // LiveData Getters
    public LiveData<List<BookingModel>> getFilteredBookings() { return filteredBookings; }
    public LiveData<Boolean> getIsLoading() { return isLoading; }
    public LiveData<String> getErrorMessage() { return errorMessage; }
    public LiveData<Boolean> getCancellationSuccess() { return cancellationSuccess; }

    private void fetchUserBookings() {
        isLoading.setValue(true);
        // Query to fetch only the bookings for the current user
        Query userBookingsQuery = bookingsRef.orderByChild("userId").equalTo(currentUserId);

        userBookingsQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<BookingModel> bookings = new ArrayList<>();
                for (DataSnapshot bookingSnapshot : snapshot.getChildren()) {
                    BookingModel booking = bookingSnapshot.getValue(BookingModel.class);
                    if (booking != null) {
                        bookings.add(booking);
                    }
                }
                allUserBookings.setValue(bookings);
                filteredBookings.setValue(bookings); // Initially, show all
                isLoading.setValue(false);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                errorMessage.setValue("Failed to load bookings: " + error.getMessage());
                isLoading.setValue(false);
            }
        });
    }

    /**
     * Updates a booking's status to "CANCELLED".
     * @param bookingId The ID of the booking to cancel.
     */
    public void cancelBooking(String bookingId) {
        isLoading.setValue(true);
        bookingsRef.child(bookingId).child("status").setValue("CANCELLED")
                .addOnCompleteListener(task -> {
                    isLoading.setValue(false);
                    if (task.isSuccessful()) {
                        cancellationSuccess.setValue(true);
                        cancellationSuccess.postValue(false); // Reset state
                    } else {
                        errorMessage.setValue("Failed to cancel booking.");
                    }
                });
    }

    /**
     * Filters the user's bookings based on status and type.
     * @param statusFilter Filter by "CONFIRMED" or "CANCELLED". "All" shows both.
     * @param typeFilter Filter by "Room" or "Activity". "All" shows both.
     */
    public void applyFilters(String statusFilter, String typeFilter) {
        List<BookingModel> currentBookings = allUserBookings.getValue();
        if (currentBookings == null) return;

        List<BookingModel> filteredList = currentBookings.stream()
                .filter(booking -> {
                    boolean statusMatch = statusFilter.equalsIgnoreCase("All") ||
                            booking.getStatus().equalsIgnoreCase(statusFilter);
                    boolean typeMatch = typeFilter.equalsIgnoreCase("All") ||
                            booking.getItemType().equalsIgnoreCase(typeFilter);
                    return statusMatch && typeMatch;
                })
                .collect(Collectors.toList());

        filteredBookings.setValue(filteredList);
    }
}