package com.example.ecostayretreat.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.ecostayretreat.model.BookingModel;
import com.example.ecostayretreat.model.CartItemModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * File: CartViewModel.java
 * Description: ViewModel for managing the shopping cart and checkout process.
 */
public class CartViewModel extends ViewModel {

    private final DatabaseReference rootRef;
    private final DatabaseReference cartRef;
    private final String currentUserId;

    private final MutableLiveData<List<CartItemModel>> cartItems = new MutableLiveData<>();
    private final MutableLiveData<Double> totalPrice = new MutableLiveData<>(0.0);
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<Boolean> paymentSuccess = new MutableLiveData<>();

    public CartViewModel() {
        currentUserId = FirebaseAuth.getInstance().getUid();
        rootRef = FirebaseDatabase.getInstance().getReference();
        cartRef = rootRef.child("cart").child(currentUserId).child("items");
        fetchCartItems();
    }

    // LiveData Getters
    public LiveData<List<CartItemModel>> getCartItems() { return cartItems; }
    public LiveData<Double> getTotalPrice() { return totalPrice; }
    public LiveData<Boolean> getIsLoading() { return isLoading; }
    public LiveData<String> getErrorMessage() { return errorMessage; }
    public LiveData<Boolean> getPaymentSuccess() { return paymentSuccess; }

    private void fetchCartItems() {
        isLoading.setValue(true);
        cartRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<CartItemModel> items = new ArrayList<>();
                double total = 0.0;
                for (DataSnapshot itemSnapshot : snapshot.getChildren()) {
                    CartItemModel item = itemSnapshot.getValue(CartItemModel.class);
                    if (item != null) {
                        items.add(item);
                        total += item.getPrice();
                    }
                }
                cartItems.setValue(items);
                totalPrice.setValue(total);
                isLoading.setValue(false);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                errorMessage.setValue("Failed to load cart: " + error.getMessage());
                isLoading.setValue(false);
            }
        });
    }

    public void removeItemFromCart(String itemId) {
        cartRef.child(itemId).removeValue();
    }

    /**
     * Processes payment by creating bookings and clearing the cart in an atomic operation.
     */
    public void processPayment() {
        isLoading.setValue(true);
        List<CartItemModel> currentCartItems = cartItems.getValue();
        if (currentCartItems == null || currentCartItems.isEmpty()) {
            errorMessage.setValue("Your cart is empty.");
            isLoading.setValue(false);
            return;
        }

        // Use a multi-path update to ensure atomicity
        Map<String, Object> updates = new HashMap<>();

        for (CartItemModel item : currentCartItems) {
            // Generate a new unique key for the booking
            String bookingId = rootRef.child("bookings").push().getKey();
            if (bookingId == null) {
                errorMessage.setValue("Could not create booking ID.");
                isLoading.setValue(false);
                return;
            }

            BookingModel booking = new BookingModel();
            booking.setBookingId(bookingId);
            booking.setUserId(currentUserId);
            booking.setItemId(item.getRefId());
            booking.setItemName(item.getName());
            booking.setItemType(item.getType());
            booking.setTotalPrice(item.getPrice());
            booking.setStatus("CONFIRMED");

            // Handle date range
            if (item.getDateRange() != null && item.getDateRange().contains(" to ")) {
                String[] dates = item.getDateRange().split(" to ");
                booking.setCheckInDate(dates[0]);
                booking.setCheckOutDate(dates[1]);
            } else {
                booking.setCheckInDate(item.getDateRange()); // For single-day activities
                booking.setCheckOutDate(item.getDateRange());
            }
            updates.put("/bookings/" + bookingId, booking);
        }

        // Add the cart deletion to the atomic update
        updates.put("/cart/" + currentUserId, null);

        // Execute the atomic update
        rootRef.updateChildren(updates).addOnCompleteListener(task -> {
            isLoading.setValue(false);
            if (task.isSuccessful()) {
                paymentSuccess.setValue(true);
                paymentSuccess.postValue(false); // Reset the state
            } else {
                errorMessage.setValue("Payment failed. Please try again.");
            }
        });
    }
}