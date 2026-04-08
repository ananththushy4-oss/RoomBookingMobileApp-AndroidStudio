package com.example.ecostayretreat;

import android.animation.Animator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.ecostayretreat.databinding.ActivityLoginBinding;
import com.example.ecostayretreat.viewmodel.AuthViewModel;

/**
 * File: LoginActivity.java
 * Description: Handles user login and serves as the app's entry point.
 */
public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private AuthViewModel authViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        // Check if user is already logged in
        if (authViewModel.getUserLiveData().getValue() != null) {
            navigateToMain();
        }

        setupObservers();
        setupClickListeners();
    }

    private void setupObservers() {
        // Observe loading state
        authViewModel.getIsLoading().observe(this, isLoading -> {
            binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            binding.btnLogin.setEnabled(!isLoading);
        });

        // Observe error messages
        authViewModel.getErrorMessage().observe(this, error -> {
            if (error != null) {
                playAnimation(R.raw.error, () -> Toast.makeText(this, error, Toast.LENGTH_SHORT).show());
            }
        });

        // Observe successful login
        authViewModel.getUserLiveData().observe(this, firebaseUser -> {
            if (firebaseUser != null) {
                // Play success animation then navigate
                playAnimation(R.raw.login_success, this::navigateToMain);
            }
        });
    }

    private void setupClickListeners() {
        binding.btnLogin.setOnClickListener(v -> handleLogin());
        binding.tvRegisterLink.setOnClickListener(v -> startActivity(new Intent(LoginActivity.this, RegisterActivity.class)));
        // TODO: Implement Forgot Password functionality
        binding.tvForgotPassword.setOnClickListener(v -> Toast.makeText(this, "Forgot Password feature coming soon!", Toast.LENGTH_SHORT).show());
    }

    private void handleLogin() {
        String email = binding.etEmail.getText().toString().trim();
        String password = binding.etPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Email and password cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        authViewModel.login(email, password);
    }

    private void navigateToMain() {
        Intent intent = new Intent(LoginActivity.this, MainController.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    /**
     * Plays a Lottie animation once.
     * @param animationResId The raw resource ID of the animation.
     * @param onAnimationEnd A runnable to execute when the animation finishes.
     */
    private void playAnimation(int animationResId, Runnable onAnimationEnd) {
        binding.lottieAnimationView.setAnimation(animationResId);
        binding.lottieAnimationView.setVisibility(View.VISIBLE);
        binding.lottieAnimationView.playAnimation();
        binding.lottieAnimationView.addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationEnd(Animator animation) {
                binding.lottieAnimationView.setVisibility(View.GONE);
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