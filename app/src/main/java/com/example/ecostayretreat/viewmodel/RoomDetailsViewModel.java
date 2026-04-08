package com.example.ecostayretreat.viewmodel;

import androidx.annotation.NonNull; // CORRECTED: Switched to the standard AndroidX annotation.
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.ecostayretreat.model.CartItemModel;
import com.example.ecostayretreat.model.RoomModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class RoomDetailsViewModel extends ViewModel {

    private final DatabaseReference roomRef;
    private final DatabaseReference cartRef;
    private final String currentUserId;

    private final MutableLiveData<RoomModel> roomDetails = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<Boolean> addToCartSuccess = new MutableLiveData<>();

    public RoomDetailsViewModel(String roomId) {
        currentUserId = FirebaseAuth.getInstance().getUid();
        roomRef = FirebaseDatabase.getInstance().getReference("rooms").child(roomId);
        cartRef = FirebaseDatabase.getInstance().getReference("cart").child(currentUserId).child("items");
        fetchRoomDetails();
    }

    public LiveData<RoomModel> getRoomDetails() { return roomDetails; }
    public LiveData<Boolean> getIsLoading() { return isLoading; }
    public LiveData<String> getErrorMessage() { return errorMessage; }
    public LiveData<Boolean> getAddToCartSuccess() { return addToCartSuccess; }

    private void fetchRoomDetails() {
        isLoading.setValue(true);
        roomRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                RoomModel room = snapshot.getValue(RoomModel.class);
                roomDetails.setValue(room);
                isLoading.setValue(false);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                errorMessage.setValue("Failed to load room details.");
                isLoading.setValue(false);
            }
        });
    }

    public void addToCart(RoomModel room, long startDate, long endDate) {
        String cartItemId = cartRef.push().getKey();
        if (cartItemId == null) {
            errorMessage.setValue("Could not add to cart.");
            return;
        }

        // Calculate number of nights
        long diffInMillis = Math.abs(endDate - startDate);
        long nights = TimeUnit.DAYS.convert(diffInMillis, TimeUnit.MILLISECONDS);
        if (nights == 0) nights = 1; // Minimum 1 night charge

        double total = room.getPrice() * nights;

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        String checkIn = sdf.format(new Date(startDate));
        String checkOut = sdf.format(new Date(endDate));

        CartItemModel cartItem = new CartItemModel();
        cartItem.setItemId(cartItemId);
        cartItem.setRefId(room.getRoomId());
        cartItem.setName(room.getName());
        cartItem.setType("Room");
        cartItem.setPrice(total);
        cartItem.setDateRange(checkIn + " to " + checkOut);

        cartRef.child(cartItemId).setValue(cartItem).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                addToCartSuccess.setValue(true);
                addToCartSuccess.postValue(false);
            } else {
                errorMessage.setValue("Failed to add to cart.");
            }
        });
    }
}