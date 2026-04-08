package com.example.ecostayretreat.screens;

import android.animation.Animator;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull; // CORRECTED: Switched to the standard AndroidX annotation.
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.example.ecostayretreat.R;
import com.example.ecostayretreat.databinding.DialogChangePasswordBinding;
import com.example.ecostayretreat.databinding.ScreenProfileBinding;
import com.example.ecostayretreat.model.UserModel;
import com.example.ecostayretreat.viewmodel.ProfileViewModel;
import com.google.android.material.snackbar.Snackbar;

public class ProfileScreen extends Fragment {

    private ScreenProfileBinding binding;
    private ProfileViewModel viewModel;
    private UserModel currentUserModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = ScreenProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(ProfileViewModel.class);

        setupObservers();
        setupClickListeners();
    }

    // ... rest of the ProfileScreen.java code remains the same ...
    // The only change was the import statement.

    private void setupObservers() {
        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading ->
                binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE));

        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), error -> {
            if (!TextUtils.isEmpty(error)) {
                Snackbar.make(binding.getRoot(), error, Snackbar.LENGTH_LONG)
                        .setBackgroundTint(getResources().getColor(R.color.colorError, null)).show();
            }
        });

        viewModel.getUserLiveData().observe(getViewLifecycleOwner(), userModel -> {
            if (userModel != null) {
                currentUserModel = userModel;
                populateUserData(userModel);
            }
        });

        viewModel.getUpdateSuccess().observe(getViewLifecycleOwner(), success -> {
            if (success != null && success) {
                playAnimation(R.raw.update_success, () ->
                        Snackbar.make(binding.getRoot(), "Profile updated successfully!", Snackbar.LENGTH_SHORT)
                                .setBackgroundTint(getResources().getColor(R.color.colorPrimary, null)).show());
            }
        });

        viewModel.getPasswordChangeSuccess().observe(getViewLifecycleOwner(), success -> {
            if (success != null && success) {
                playAnimation(R.raw.password_change, () ->
                        Snackbar.make(binding.getRoot(), "Password changed successfully!", Snackbar.LENGTH_SHORT)
                                .setBackgroundTint(getResources().getColor(R.color.colorPrimary, null)).show());
            }
        });
    }

    private void setupClickListeners() {
        binding.btnSaveChanges.setOnClickListener(v -> saveProfileChanges());
        binding.btnChangePassword.setOnClickListener(v -> showChangePasswordDialog());
    }

    private void populateUserData(UserModel user) {
        binding.etFirstName.setText(user.getFirstName());
        binding.etLastName.setText(user.getLastName());
        binding.etEmail.setText(user.getEmail());
        binding.etAddress.setText(user.getAddress());
        binding.etPhone.setText(user.getPhone());
        binding.etNic.setText(user.getNic());
    }

    private void saveProfileChanges() {
        if (currentUserModel == null) return;

        currentUserModel.setFirstName(binding.etFirstName.getText().toString().trim());
        currentUserModel.setLastName(binding.etLastName.getText().toString().trim());
        currentUserModel.setAddress(binding.etAddress.getText().toString().trim());
        currentUserModel.setPhone(binding.etPhone.getText().toString().trim());

        viewModel.updateUserProfile(currentUserModel);
    }

    private void showChangePasswordDialog() {
        DialogChangePasswordBinding dialogBinding = DialogChangePasswordBinding.inflate(getLayoutInflater());
        new AlertDialog.Builder(requireContext(), R.style.Theme_EcoStayRetreat)
                .setTitle("Change Password")
                .setView(dialogBinding.getRoot())
                .setPositiveButton("Change", (dialog, which) -> {
                    String oldPass = dialogBinding.etCurrentPassword.getText().toString();
                    String newPass = dialogBinding.etNewPassword.getText().toString();
                    String confirmPass = dialogBinding.etConfirmNewPassword.getText().toString();

                    if (TextUtils.isEmpty(oldPass) || TextUtils.isEmpty(newPass) || TextUtils.isEmpty(confirmPass)) {
                        Toast.makeText(getContext(), "All fields are required.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (!newPass.equals(confirmPass)) {
                        Toast.makeText(getContext(), "New passwords do not match.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (newPass.length() < 6) {
                        Toast.makeText(getContext(), "Password must be at least 6 characters.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    viewModel.changePassword(oldPass, newPass);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void playAnimation(int animationResId, Runnable onAnimationEnd) {
        binding.animationContainer.setVisibility(View.VISIBLE);
        binding.lottieAnimationView.setAnimation(animationResId);
        binding.lottieAnimationView.playAnimation();
        binding.lottieAnimationView.addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationEnd(Animator animation) {
                binding.animationContainer.setVisibility(View.GONE);
                if (onAnimationEnd != null) {
                    onAnimationEnd.run();
                }
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