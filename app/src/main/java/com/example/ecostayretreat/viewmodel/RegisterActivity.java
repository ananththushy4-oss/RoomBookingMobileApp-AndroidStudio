package com.example.ecostayretreat;

import android.animation.Animator;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.ecostayretreat.databinding.ActivityRegisterBinding;
import com.example.ecostayretreat.viewmodel.AuthViewModel;

/**
 * File: RegisterActivity.java
 * Description: Handles new user registration.
 */
public class RegisterActivity extends AppCompatActivity {

    private ActivityRegisterBinding binding;
    private AuthViewModel authViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        setupObservers();
        setupClickListeners();
    }

    private void setupObservers() {
        authViewModel.getIsLoading().observe(this, isLoading -> {
            binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            binding.btnRegister.setEnabled(!isLoading);
        });

        authViewModel.getErrorMessage().observe(this, error -> {
            if (error != null) {
                playAnimation(R.raw.error, () -> Toast.makeText(this, error, Toast.LENGTH_LONG).show());
            }
        });

        authViewModel.getRegistrationSuccess().observe(this, isSuccess -> {
            if (isSuccess) {
                playAnimation(R.raw.register_success, () -> {
                    Toast.makeText(this, "Registration successful!", Toast.LENGTH_SHORT).show();
                    finish(); // Go back to login screen
                });
            }
        });
    }

    private void setupClickListeners() {
        binding.btnRegister.setOnClickListener(v -> handleRegister());
        binding.tvLoginLink.setOnClickListener(v -> finish());
    }

    private void handleRegister() {
        String firstName = binding.etFirstName.getText().toString().trim();
        String lastName = binding.etLastName.getText().toString().trim();
        String address = binding.etAddress.getText().toString().trim();
        String nic = binding.etNic.getText().toString().trim();
        String phone = binding.etPhone.getText().toString().trim();
        String email = binding.etEmail.getText().toString().trim();
        String password = binding.etPassword.getText().toString().trim();
        String confirmPassword = binding.etConfirmPassword.getText().toString().trim();

        if (!validateInputs(firstName, lastName, address, nic, phone, email, password, confirmPassword)) {
            return;
        }

        authViewModel.register(firstName, lastName, address, nic, phone, email, password);
    }

    private boolean validateInputs(String firstName, String lastName, String address, String nic, String phone, String email, String password, String confirmPassword) {
        if (TextUtils.isEmpty(firstName)) {
            binding.tilFirstName.setError("First name is required");
            return false;
        }
        if (TextUtils.isEmpty(lastName)) {
            binding.tilLastName.setError("Last name is required");
            return false;
        }
        if (TextUtils.isEmpty(address)) {
            binding.tilAddress.setError("Address is required");
            return false;
        }
        if (TextUtils.isEmpty(nic)) {
            binding.tilNic.setError("NIC is required");
            return false;
        }
        if (TextUtils.isEmpty(phone)) {
            binding.tilPhone.setError("Phone number is required");
            return false;
        }
        if (TextUtils.isEmpty(email) || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.tilEmail.setError("Valid email is required");
            return false;
        }
        if (TextUtils.isEmpty(password) || password.length() < 6) {
            binding.tilPassword.setError("Password must be at least 6 characters");
            return false;
        }
        if (!password.equals(confirmPassword)) {
            binding.tilConfirmPassword.setError("Passwords do not match");
            return false;
        }

        // Clear errors if all valid
        binding.tilFirstName.setError(null);
        binding.tilLastName.setError(null);
        binding.tilAddress.setError(null);
        binding.tilNic.setError(null);
        binding.tilPhone.setError(null);
        binding.tilEmail.setError(null);
        binding.tilPassword.setError(null);
        binding.tilConfirmPassword.setError(null);

        return true;
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
            @Override
            public void onAnimationStart(Animator animation) {}
            @Override
            public void onAnimationCancel(Animator animation) {}
            @Override
            public void onAnimationRepeat(Animator animation) {}
        });
    }
}