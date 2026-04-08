package com.example.ecostayretreat.screens;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.bumptech.glide.Glide;
import com.example.ecostayretreat.R;
import com.example.ecostayretreat.databinding.ScreenRoomDetailsBinding;
import com.example.ecostayretreat.model.RoomModel;
import com.example.ecostayretreat.viewmodel.RoomDetailsViewModel;
import com.example.ecostayretreat.viewmodel.RoomDetailsViewModelFactory;
import com.google.android.material.chip.Chip;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.snackbar.Snackbar;

import java.util.Locale;

public class RoomDetailsScreen extends Fragment {

    private ScreenRoomDetailsBinding binding;
    private RoomDetailsViewModel viewModel;
    private RoomModel currentRoom;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = ScreenRoomDetailsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        String roomId = RoomDetailsScreenArgs.fromBundle(getArguments()).getRoomId();
        RoomDetailsViewModelFactory factory = new RoomDetailsViewModelFactory(roomId);
        viewModel = new ViewModelProvider(this, factory).get(RoomDetailsViewModel.class);

        setupToolbar();
        setupObservers();
        binding.btnAddToCart.setOnClickListener(v -> showDatePicker());
    }

    private void setupToolbar() {
        ((AppCompatActivity) requireActivity()).setSupportActionBar(binding.toolbarDetails);
        ((AppCompatActivity) requireActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        binding.toolbarDetails.setNavigationOnClickListener(v -> requireActivity().onBackPressed());
    }

    private void setupObservers() {
        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading ->
                binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE));

        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), error ->
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show());

        viewModel.getRoomDetails().observe(getViewLifecycleOwner(), this::updateUi);

        viewModel.getAddToCartSuccess().observe(getViewLifecycleOwner(), success -> {
            if (success) {
                Snackbar.make(binding.getRoot(), "Room added to cart!", Snackbar.LENGTH_SHORT)
                        .setBackgroundTint(getResources().getColor(R.color.colorPrimary, null)).show();
            }
        });
    }

    private void updateUi(RoomModel room) {
        currentRoom = room;
        binding.collapsingToolbar.setTitle(room.getName());
        binding.tvRoomDetailDescription.setText(room.getDescription());
        binding.tvRoomDetailPrice.setText(String.format(Locale.getDefault(), "$%.0f / night", room.getPrice()));

        Glide.with(requireContext()).load(room.getImageUrl()).into(binding.ivRoomDetailImage);

        binding.chipGroupDetailFeatures.removeAllViews();
        if (room.getFeatures() != null) {
            for (String feature : room.getFeatures()) {
                Chip chip = new Chip(getContext());
                chip.setText(feature);
                binding.chipGroupDetailFeatures.addView(chip);
            }
        }
    }

    private void showDatePicker() {
        MaterialDatePicker<Pair<Long, Long>> datePicker = MaterialDatePicker.Builder.dateRangePicker()
                .setTitleText("Select Booking Dates")
                .build();
        datePicker.show(getParentFragmentManager(), "DATE_PICKER");

        datePicker.addOnPositiveButtonClickListener(selection -> {
            if (currentRoom != null) {
                viewModel.addToCart(currentRoom, selection.first, selection.second);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}