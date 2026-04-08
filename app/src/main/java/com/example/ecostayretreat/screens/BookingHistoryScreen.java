package com.example.ecostayretreat.screens;

import android.animation.Animator;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.ecostayretreat.R;
import com.example.ecostayretreat.databinding.ScreenBookingHistoryBinding;
import com.example.ecostayretreat.model.BookingModel;
import com.example.ecostayretreat.screens.adapters.BookingHistoryAdapter;
import com.example.ecostayretreat.viewmodel.BookingViewModel;
import com.google.android.material.snackbar.Snackbar;

import java.util.Locale;

/**
 * File: BookingHistoryScreen.java
 * Description: Fragment for displaying user's booking history and allowing cancellations.
 */
public class BookingHistoryScreen extends Fragment implements BookingHistoryAdapter.OnBookingClickListener {

    private ScreenBookingHistoryBinding binding;
    private BookingViewModel viewModel;
    private BookingHistoryAdapter adapter;
    private final String[] statusFilters = {"All", "CONFIRMED", "CANCELLED"};
    private final String[] typeFilters = {"All", "Room", "Activity"};

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = ScreenBookingHistoryBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(BookingViewModel.class);

        setupRecyclerView();
        setupFilterControls();
        setupObservers();
    }

    private void setupRecyclerView() {
        binding.rvBookings.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new BookingHistoryAdapter(getContext(), this);
        binding.rvBookings.setAdapter(adapter);
    }

    private void setupObservers() {
        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading ->
                binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE));

        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), error ->
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show());

        viewModel.getFilteredBookings().observe(getViewLifecycleOwner(), bookings -> {
            adapter.submitList(bookings);
            binding.tvNoBookings.setVisibility(bookings.isEmpty() ? View.VISIBLE : View.GONE);
        });

        viewModel.getCancellationSuccess().observe(getViewLifecycleOwner(), success -> {
            if (success) {
                playAnimation(() ->
                        Snackbar.make(binding.getRoot(), "Booking cancelled successfully!", Snackbar.LENGTH_SHORT)
                                .setBackgroundTint(getResources().getColor(R.color.colorPrimary, null)).show());
            }
        });
    }

    private void setupFilterControls() {
        // Status Spinner
        ArrayAdapter<String> statusAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, statusFilters);
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerFilterStatus.setAdapter(statusAdapter);

        // Type Spinner
        ArrayAdapter<String> typeAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, typeFilters);
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerFilterType.setAdapter(typeAdapter);

        AdapterView.OnItemSelectedListener listener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                applyFilters();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        };
        binding.spinnerFilterStatus.setOnItemSelectedListener(listener);
        binding.spinnerFilterType.setOnItemSelectedListener(listener);
    }

    private void applyFilters() {
        String status = binding.spinnerFilterStatus.getSelectedItem().toString();
        String type = binding.spinnerFilterType.getSelectedItem().toString();
        viewModel.applyFilters(status, type);
    }

    @Override
    public void onBookingClick(BookingModel booking) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext(), R.style.Theme_EcoStayRetreat);
        builder.setTitle("Booking Details: " + booking.getItemName());

        String message = String.format(Locale.getDefault(),
                "Type: %s\nDates: %s - %s\nTotal Price: $%.2f\nStatus: %s",
                booking.getItemType(), booking.getCheckInDate(), booking.getCheckOutDate(),
                booking.getTotalPrice(), booking.getStatus());
        builder.setMessage(message);

        builder.setPositiveButton("Close", (dialog, which) -> dialog.dismiss());

        // Only show cancel button if the booking is confirmed
        if ("CONFIRMED".equalsIgnoreCase(booking.getStatus())) {
            builder.setNeutralButton("Cancel Booking", (dialog, which) -> showCancellationConfirmation(booking.getBookingId()));
        }

        builder.create().show();
    }

    private void showCancellationConfirmation(String bookingId) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Confirm Cancellation")
                .setMessage("Are you sure you want to cancel this booking?")
                .setPositiveButton("Yes, Cancel", (dialog, which) -> viewModel.cancelBooking(bookingId))
                .setNegativeButton("No", null)
                .show();
    }

    private void playAnimation(Runnable onAnimationEnd) {
        binding.animationContainer.setVisibility(View.VISIBLE);
        binding.lottieAnimationView.playAnimation();
        binding.lottieAnimationView.addAnimatorListener(new Animator.AnimatorListener() {
            @Override public void onAnimationEnd(Animator animation) {
                binding.animationContainer.setVisibility(View.GONE);
                if (onAnimationEnd != null) onAnimationEnd.run();
            }
            @Override public void onAnimationStart(Animator animation) {}
            @Override public void onAnimationCancel(Animator animation) {}
            @Override public void onAnimationRepeat(Animator animation) {}
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}