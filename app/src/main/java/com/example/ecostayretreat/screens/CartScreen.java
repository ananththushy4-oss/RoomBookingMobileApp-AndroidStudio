package com.example.ecostayretreat.screens;

import android.animation.Animator;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.ecostayretreat.R;
import com.example.ecostayretreat.databinding.ScreenCartBinding;
import com.example.ecostayretreat.screens.adapters.CartAdapter;
import com.example.ecostayretreat.viewmodel.CartViewModel;
import com.google.android.material.snackbar.Snackbar;

import java.util.Locale;

/**
 * File: CartScreen.java
 * Description: Fragment for the user's shopping cart.
 */
public class CartScreen extends Fragment implements CartAdapter.OnCartItemInteractionListener {

    private ScreenCartBinding binding;
    private CartViewModel viewModel;
    private CartAdapter adapter;
    private NavController navController;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = ScreenCartBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(CartViewModel.class);
        navController = Navigation.findNavController(view);

        setupRecyclerView();
        setupObservers();
        binding.btnPayNow.setOnClickListener(v -> processFakePayment());
    }

    private void setupRecyclerView() {
        binding.rvCartItems.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new CartAdapter(getContext(), this);
        binding.rvCartItems.setAdapter(adapter);
    }

    private void setupObservers() {
        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading ->
                binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE));

        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), error -> {
            if (error != null) Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
        });

        viewModel.getCartItems().observe(getViewLifecycleOwner(), items -> {
            adapter.submitList(items);
            binding.tvEmptyCart.setVisibility(items.isEmpty() ? View.VISIBLE : View.GONE);
            binding.btnPayNow.setEnabled(!items.isEmpty());
        });

        viewModel.getTotalPrice().observe(getViewLifecycleOwner(), total ->
                binding.tvTotalPrice.setText(String.format(Locale.getDefault(), "$%.2f", total)));

        viewModel.getPaymentSuccess().observe(getViewLifecycleOwner(), success -> {
            if (success) {
                playAnimation(() -> {
                    Snackbar.make(binding.getRoot(), "Payment successful! Your booking is confirmed.", Snackbar.LENGTH_LONG)
                            .setBackgroundTint(getResources().getColor(R.color.colorPrimary, null)).show();
                    navController.navigate(R.id.nav_bookings);
                });
            }
        });
    }

    private void processFakePayment() {
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.btnPayNow.setEnabled(false);
        // Simulate a 3-second payment processing delay
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            viewModel.processPayment();
            binding.progressBar.setVisibility(View.GONE);
            binding.btnPayNow.setEnabled(true);
        }, 3000);
    }

    @Override
    public void onRemoveItemClick(String itemId) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Remove Item")
                .setMessage("Are you sure you want to remove this item from your cart?")
                .setPositiveButton("Remove", (dialog, which) -> viewModel.removeItemFromCart(itemId))
                .setNegativeButton("Cancel", null)
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