package com.example.ecostayretreat.viewmodel;

import androidx.annotation.NonNull; // CORRECTED: Switched to the standard AndroidX annotation.
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class RoomDetailsViewModelFactory implements ViewModelProvider.Factory {
    private final String roomId;

    public RoomDetailsViewModelFactory(String roomId) {
        this.roomId = roomId;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(RoomDetailsViewModel.class)) {
            return (T) new RoomDetailsViewModel(roomId);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}