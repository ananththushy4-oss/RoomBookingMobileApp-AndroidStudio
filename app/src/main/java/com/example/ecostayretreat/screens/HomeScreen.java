package com.example.ecostayretreat.screens;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.ecostayretreat.R;
import com.example.ecostayretreat.databinding.ScreenHomeBinding;
import com.example.ecostayretreat.screens.adapters.ActivityCarouselAdapter;
import com.example.ecostayretreat.screens.adapters.RoomCarouselAdapter;
import com.example.ecostayretreat.viewmodel.HomeViewModel;

/**
 * File: HomeScreen.java
 * Description: The main fragment displayed after login, showing an overview of the app.
 */
public class HomeScreen extends Fragment {

    private ScreenHomeBinding binding;
    private HomeViewModel homeViewModel;
    private RoomCarouselAdapter roomAdapter;
    private ActivityCarouselAdapter activityAdapter;
    private NavController navController;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = ScreenHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController = Navigation.findNavController(view);
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        setupRecyclerViews();
        setupObservers();
        setupClickListeners();
    }

    private void setupRecyclerViews() {
        // Rooms RecyclerView
        binding.rvTopRooms.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        roomAdapter = new RoomCarouselAdapter(getContext());
        binding.rvTopRooms.setAdapter(roomAdapter);

        // Activities RecyclerView
        binding.rvEcoActivities.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        activityAdapter = new ActivityCarouselAdapter(getContext());
        binding.rvEcoActivities.setAdapter(activityAdapter);
    }

    private void setupObservers() {
        homeViewModel.getTopRoomsLiveData().observe(getViewLifecycleOwner(), rooms -> {
            if (rooms != null) {
                roomAdapter.setRoomList(rooms);
            }
        });

        homeViewModel.getEcoActivitiesLiveData().observe(getViewLifecycleOwner(), activities -> {
            if (activities != null) {
                activityAdapter.setActivityList(activities);
            }
        });

        homeViewModel.getErrorMessage().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupClickListeners() {
        binding.btnBookRoom.setOnClickListener(v -> navController.navigate(R.id.nav_explore_rooms));
        binding.btnReserveActivity.setOnClickListener(v -> navController.navigate(R.id.nav_activities));
        binding.btnViewBookings.setOnClickListener(v -> navController.navigate(R.id.nav_bookings));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // Avoid memory leaks
    }
}