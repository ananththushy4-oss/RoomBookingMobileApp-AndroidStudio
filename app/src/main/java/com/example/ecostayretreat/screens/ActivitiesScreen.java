package com.example.ecostayretreat.screens;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.ecostayretreat.R;
import com.example.ecostayretreat.databinding.DialogAddEditActivityBinding;
import com.example.ecostayretreat.databinding.DialogPersonCountBinding;
import com.example.ecostayretreat.databinding.ScreenActivitiesBinding;
import com.example.ecostayretreat.model.ActivityModel;
import com.example.ecostayretreat.model.CartItemModel;
import com.example.ecostayretreat.screens.adapters.ActivityAdapter;
import com.example.ecostayretreat.viewmodel.ActivityViewModel;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;

public class ActivitiesScreen extends Fragment implements ActivityAdapter.OnActivityInteractionListener {

    private ScreenActivitiesBinding binding;
    private ActivityViewModel viewModel;
    private ActivityAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = ScreenActivitiesBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(ActivityViewModel.class);
        setupRecyclerView();
        setupObservers();
        binding.fabAddActivity.setOnClickListener(v -> showAddEditDialog(null));
    }

    private void setupRecyclerView() {
        binding.rvActivities.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ActivityAdapter(getContext(), this);
        binding.rvActivities.setAdapter(adapter);
    }

    private void setupObservers() {
        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading ->
                binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE));

        viewModel.getAllActivities().observe(getViewLifecycleOwner(), activities -> adapter.submitList(activities));

        viewModel.isAdmin().observe(getViewLifecycleOwner(), isAdmin -> {
            binding.fabAddActivity.setVisibility(isAdmin ? View.VISIBLE : View.GONE);
            adapter.setAdmin(isAdmin);
        });

        viewModel.getOperationSuccess().observe(getViewLifecycleOwner(), success -> {
            if (success != null && success) {
                Snackbar.make(binding.getRoot(), "Admin Operation Successful!", Snackbar.LENGTH_SHORT)
                        .setBackgroundTint(getResources().getColor(R.color.colorPrimary, null)).show();
            }
        });
    }

    /**
     * This method is called when the user clicks "Reserve Now".
     * It now opens a dialog to ask for the number of people.
     * @param activity The activity that was clicked.
     */
    @Override
    public void onReserveClick(ActivityModel activity) {
        showPersonCountDialog(activity);
    }

    /**
     * Creates and shows a dialog for selecting person count and adds the item to the cart.
     * @param activity The activity to be added to the cart.
     */
    private void showPersonCountDialog(ActivityModel activity) {
        DialogPersonCountBinding dialogBinding = DialogPersonCountBinding.inflate(getLayoutInflater());

        // Configure the NumberPicker
        dialogBinding.numberPicker.setMinValue(1);
        dialogBinding.numberPicker.setMaxValue(10); // Set a reasonable maximum
        dialogBinding.numberPicker.setValue(1);

        new AlertDialog.Builder(requireContext(), R.style.Theme_EcoStayRetreat)
                .setTitle(String.format(Locale.US, "Reserve: %s", activity.getName()))
                .setView(dialogBinding.getRoot())
                .setPositiveButton("Add to Cart", (dialog, which) -> {
                    int personCount = dialogBinding.numberPicker.getValue();
                    addToCart(activity, personCount);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    /**
     * Handles the logic of creating a CartItemModel and saving it to Firebase Realtime Database.
     * @param activity The selected activity.
     * @param personCount The number of people selected by the user.
     */
    private void addToCart(ActivityModel activity, int personCount) {
        String currentUserId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        DatabaseReference cartRef = FirebaseDatabase.getInstance()
                .getReference("cart")
                .child(currentUserId)
                .child("items");

        String cartItemId = cartRef.push().getKey();
        if (cartItemId == null) {
            Toast.makeText(getContext(), "Error: Could not add to cart.", Toast.LENGTH_SHORT).show();
            return;
        }

        double totalPrice = activity.getPrice() * personCount;
        String schedule = (activity.getSchedule() != null && !activity.getSchedule().isEmpty())
                ? activity.getSchedule().get(0) : "Any time";

        CartItemModel cartItem = new CartItemModel();
        cartItem.setItemId(cartItemId);
        cartItem.setRefId(activity.getActivityId());
        cartItem.setName(String.format(Locale.US, "%s (%d people)", activity.getName(), personCount));
        cartItem.setType("Activity");
        cartItem.setPrice(totalPrice);
        cartItem.setDateRange(schedule); // Use dateRange to store schedule info

        cartRef.child(cartItemId).setValue(cartItem).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Snackbar.make(binding.getRoot(), "Activity added to cart!", Snackbar.LENGTH_SHORT)
                        .setBackgroundTint(getResources().getColor(R.color.colorPrimary, null)).show();
            } else {
                Toast.makeText(getContext(), "Failed to add to cart. Please try again.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onEditClick(ActivityModel activity) {
        showAddEditDialog(activity);
    }

    @Override
    public void onDeleteClick(String activityId) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Delete Activity")
                .setMessage("Are you sure you want to delete this activity?")
                .setPositiveButton("Delete", (dialog, which) -> viewModel.deleteActivity(activityId))
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showAddEditDialog(@Nullable ActivityModel activity) {
        DialogAddEditActivityBinding dialogBinding = DialogAddEditActivityBinding.inflate(getLayoutInflater());
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext(), R.style.Theme_EcoStayRetreat)
                .setView(dialogBinding.getRoot());

        if (activity != null) {
            builder.setTitle("Edit Activity");
            dialogBinding.etActivityName.setText(activity.getName());
            dialogBinding.etActivityDescription.setText(activity.getDescription());
            dialogBinding.etActivityPrice.setText(String.valueOf(activity.getPrice()));
            dialogBinding.etActivityImageUrl.setText(activity.getImageUrl());
            if (activity.getSchedule() != null) {
                dialogBinding.etActivitySchedule.setText(String.join(", ", activity.getSchedule()));
            }
        } else {
            builder.setTitle("Add New Activity");
        }

        builder.setPositiveButton(activity == null ? "Add" : "Save", (dialog, which) -> {
            ActivityModel newActivity = new ActivityModel();
            newActivity.setName(dialogBinding.etActivityName.getText().toString());
            newActivity.setDescription(dialogBinding.etActivityDescription.getText().toString());
            newActivity.setPrice(Double.parseDouble(dialogBinding.etActivityPrice.getText().toString()));
            newActivity.setImageUrl(dialogBinding.etActivityImageUrl.getText().toString());

            String scheduleStr = dialogBinding.etActivitySchedule.getText().toString();
            if (!TextUtils.isEmpty(scheduleStr)) {
                List<String> scheduleList = Arrays.stream(scheduleStr.split(","))
                        .map(String::trim).collect(Collectors.toList());
                newActivity.setSchedule(scheduleList);
            }

            if (activity != null) {
                newActivity.setActivityId(activity.getActivityId());
                viewModel.updateActivity(newActivity);
            } else {
                viewModel.addActivity(newActivity);
            }
        });
        builder.setNegativeButton("Cancel", null).show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}