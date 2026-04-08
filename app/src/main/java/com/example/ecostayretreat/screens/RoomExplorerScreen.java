package com.example.ecostayretreat.screens;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.ecostayretreat.R;
import com.example.ecostayretreat.databinding.DialogAddEditRoomBinding;
import com.example.ecostayretreat.databinding.DialogRoomFiltersBinding;
import com.example.ecostayretreat.databinding.ScreenRoomExplorerBinding;
import com.example.ecostayretreat.model.RoomModel;
import com.example.ecostayretreat.screens.adapters.RoomAdapter;
import com.example.ecostayretreat.viewmodel.RoomViewModel;
import com.google.android.material.chip.Chip;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.snackbar.Snackbar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * File: RoomExplorerScreen.java
 * Description: Fragment for browsing, filtering, and managing rooms.
 * It allows regular users to view rooms and admins to perform CRUD operations.
 * This version includes advanced filtering for price, availability, and features,
 * and a fix for the SearchView focus issue.
 */
public class RoomExplorerScreen extends Fragment implements RoomAdapter.OnRoomClickListener {

    private ScreenRoomExplorerBinding binding;
    private RoomViewModel roomViewModel;
    private RoomAdapter adapter;

    // --- Filter State Variables ---
    private float currentMinPrice = 0f;
    private float currentMaxPrice = 1000f;
    private Long currentStartDate = null;
    private Long currentEndDate = null;
    private final List<String> currentFeatureFilters = new ArrayList<>();
    // ---

    private final String[] allFeatures = {"WiFi", "Pool", "AC", "Kitchen", "Ocean View", "Garden View"};
    private final String[] roomTypes = {"Villa", "Cabin", "Suite", "Standard"};

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = ScreenRoomExplorerBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        roomViewModel = new ViewModelProvider(this).get(RoomViewModel.class);
        setupRecyclerView();
        setupFilterControls();
        setupObservers();
        binding.fabAddRoom.setOnClickListener(v -> showAddEditRoomDialog(null));
    }

    private void setupRecyclerView() {
        binding.rvRooms.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new RoomAdapter(getContext(), this);
        binding.rvRooms.setAdapter(adapter);
    }

    private void setupObservers() {
        roomViewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading ->
                binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE));
        roomViewModel.getErrorMessage().observe(getViewLifecycleOwner(), error -> {
            if (error != null && !error.isEmpty()) Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
        });
        roomViewModel.getFilteredRooms().observe(getViewLifecycleOwner(), rooms -> adapter.submitList(rooms));
        roomViewModel.isAdmin().observe(getViewLifecycleOwner(), isAdmin -> {
            binding.fabAddRoom.setVisibility(isAdmin ? View.VISIBLE : View.GONE);
            adapter.setAdmin(isAdmin);
        });
        roomViewModel.getOperationSuccess().observe(getViewLifecycleOwner(), success -> {
            if (success != null && success) {
                Snackbar.make(binding.getRoot(), "Operation Successful!", Snackbar.LENGTH_SHORT)
                        .setBackgroundTint(getResources().getColor(R.color.colorPrimary, null)).show();
            }
        });
    }

    private void setupFilterControls() {
        binding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                applyFilters();
                binding.searchView.clearFocus(); // Hide keyboard on submit
                return true;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                applyFilters();
                return true;
            }
        });

        // This listener ensures that tapping anywhere on the SearchView makes it active.
        binding.searchView.setOnClickListener(v -> binding.searchView.onActionViewExpanded());

        binding.btnOpenFilterDialog.setOnClickListener(v -> showFilterDialog());
    }

    private void showFilterDialog() {
        DialogRoomFiltersBinding dialogBinding = DialogRoomFiltersBinding.inflate(getLayoutInflater());
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext(), R.style.Theme_EcoStayRetreat)
                .setTitle("Filter Rooms")
                .setView(dialogBinding.getRoot());

        // Price Slider
        dialogBinding.rangeSliderPrice.setValues(currentMinPrice, currentMaxPrice);
        dialogBinding.tvPriceRangeLabel.setText(String.format(Locale.US, "$%.0f - $%.0f", currentMinPrice, currentMaxPrice));
        dialogBinding.rangeSliderPrice.addOnChangeListener((slider, value, fromUser) -> {
            List<Float> values = slider.getValues();
            dialogBinding.tvPriceRangeLabel.setText(String.format(Locale.US, "$%.0f - $%.0f", values.get(0), values.get(1)));
        });

        // Date Picker
        updateSelectedDatesLabel(dialogBinding);
        dialogBinding.btnSelectDates.setOnClickListener(v -> showDatePicker(dialogBinding));

        // Feature Chips
        for (String feature : allFeatures) {
            Chip chip = new Chip(getContext());
            chip.setText(feature);
            chip.setCheckable(true);
            if (currentFeatureFilters.contains(feature)) {
                chip.setChecked(true);
            }
            dialogBinding.chipGroupFeaturesFilter.addView(chip);
        }

        builder.setPositiveButton("Apply", (dialog, which) -> {
            List<Float> priceValues = dialogBinding.rangeSliderPrice.getValues();
            currentMinPrice = priceValues.get(0);
            currentMaxPrice = priceValues.get(1);

            currentFeatureFilters.clear();
            for (int i = 0; i < dialogBinding.chipGroupFeaturesFilter.getChildCount(); i++) {
                Chip chip = (Chip) dialogBinding.chipGroupFeaturesFilter.getChildAt(i);
                if (chip.isChecked()) {
                    currentFeatureFilters.add(chip.getText().toString());
                }
            }
            applyFilters();
            updateFilterIconState();
        });

        builder.setNegativeButton("Clear", (dialog, which) -> {
            currentMinPrice = 0f;
            currentMaxPrice = 1000f;
            currentStartDate = null;
            currentEndDate = null;
            currentFeatureFilters.clear();
            binding.searchView.setQuery("", false);
            applyFilters();
            updateFilterIconState();
        });
        builder.create().show();
    }

    private void showDatePicker(DialogRoomFiltersBinding dialogBinding) {
        MaterialDatePicker<Pair<Long, Long>> datePicker = MaterialDatePicker.Builder.dateRangePicker()
                .setTitleText("Select Availability Dates").build();
        datePicker.show(getParentFragmentManager(), "DATE_PICKER");
        datePicker.addOnPositiveButtonClickListener(selection -> {
            currentStartDate = selection.first;
            currentEndDate = selection.second;
            updateSelectedDatesLabel(dialogBinding);
        });
    }

    private void updateSelectedDatesLabel(DialogRoomFiltersBinding dialogBinding) {
        if (currentStartDate != null && currentEndDate != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd", Locale.US);
            String dateStr = sdf.format(new Date(currentStartDate)) + " to " + sdf.format(new Date(currentEndDate));
            dialogBinding.tvSelectedDatesLabel.setText(dateStr);
            dialogBinding.tvSelectedDatesLabel.setVisibility(View.VISIBLE);
        } else {
            dialogBinding.tvSelectedDatesLabel.setVisibility(View.GONE);
        }
    }

    private void applyFilters() {
        String query = binding.searchView.getQuery().toString();
        roomViewModel.applyFilters(query, currentMinPrice, currentMaxPrice, currentStartDate, currentEndDate, currentFeatureFilters);
    }

    private void updateFilterIconState() {
        boolean filtersActive = currentMinPrice > 0f || currentMaxPrice < 1000f || currentStartDate != null || !currentFeatureFilters.isEmpty();
        if (filtersActive) {
            binding.btnOpenFilterDialog.setColorFilter(ContextCompat.getColor(requireContext(), R.color.colorError));
        } else {
            binding.btnOpenFilterDialog.setColorFilter(ContextCompat.getColor(requireContext(), R.color.colorPrimary));
        }
    }

    @Override
    public void onRoomClick(RoomModel room) {
        RoomExplorerScreenDirections.ActionNavExploreRoomsToRoomDetailsScreen action =
                RoomExplorerScreenDirections.actionNavExploreRoomsToRoomDetailsScreen(room.getRoomId());
        Navigation.findNavController(requireView()).navigate(action);
    }

    @Override
    public void onEditClick(RoomModel room) {
        showAddEditRoomDialog(room);
    }

    @Override
    public void onDeleteClick(String roomId) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Delete Room")
                .setMessage("Are you sure you want to delete this room?")
                .setPositiveButton("Delete", (dialog, which) -> roomViewModel.deleteRoom(roomId))
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showAddEditRoomDialog(@Nullable RoomModel room) {
        DialogAddEditRoomBinding dialogBinding = DialogAddEditRoomBinding.inflate(getLayoutInflater());
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext(), R.style.Theme_EcoStayRetreat);
        builder.setView(dialogBinding.getRoot());
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_item, roomTypes);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dialogBinding.spinnerRoomType.setAdapter(spinnerAdapter);

        for (String feature : allFeatures) {
            Chip chip = new Chip(getContext());
            chip.setText(feature);
            chip.setCheckable(true);
            if (room != null && room.getFeatures() != null && room.getFeatures().contains(feature)) {
                chip.setChecked(true);
            }
            dialogBinding.chipGroupFeaturesDialog.addView(chip);
        }

        if (room != null) {
            builder.setTitle("Edit Room");
            dialogBinding.etRoomName.setText(room.getName());
            dialogBinding.etRoomDescription.setText(room.getDescription());
            dialogBinding.etRoomPrice.setText(String.valueOf(room.getPrice()));
            dialogBinding.etRoomImageUrl.setText(room.getImageUrl());
            int spinnerPosition = Arrays.asList(roomTypes).indexOf(room.getType());
            if (spinnerPosition >= 0) {
                dialogBinding.spinnerRoomType.setSelection(spinnerPosition);
            }
        } else {
            builder.setTitle("Add New Room");
        }
        builder.setPositiveButton(room == null ? "Add" : "Save", (dialog, which) -> {
            String name = dialogBinding.etRoomName.getText().toString().trim();
            String description = dialogBinding.etRoomDescription.getText().toString().trim();
            String priceStr = dialogBinding.etRoomPrice.getText().toString().trim();
            String imageUrl = dialogBinding.etRoomImageUrl.getText().toString().trim();
            String type = dialogBinding.spinnerRoomType.getSelectedItem().toString();
            if (TextUtils.isEmpty(name) || TextUtils.isEmpty(description) || TextUtils.isEmpty(priceStr)) {
                Toast.makeText(getContext(), "Please fill all required fields", Toast.LENGTH_SHORT).show();
                return;
            }
            double price = Double.parseDouble(priceStr);
            List<String> selectedFeatures = new ArrayList<>();
            for (int i = 0; i < dialogBinding.chipGroupFeaturesDialog.getChildCount(); i++) {
                Chip chip = (Chip) dialogBinding.chipGroupFeaturesDialog.getChildAt(i);
                if (chip.isChecked()) {
                    selectedFeatures.add(chip.getText().toString());
                }
            }
            RoomModel newRoom = new RoomModel();
            newRoom.setName(name);
            newRoom.setDescription(description);
            newRoom.setPrice(price);
            newRoom.setImageUrl(imageUrl);
            newRoom.setType(type);
            newRoom.setFeatures(selectedFeatures);
            if (room != null) {
                newRoom.setRoomId(room.getRoomId());
                roomViewModel.updateRoom(newRoom);
            } else {
                roomViewModel.addRoom(newRoom);
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        builder.create().show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}