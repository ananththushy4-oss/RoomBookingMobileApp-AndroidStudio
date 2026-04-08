package com.example.ecostayretreat.screens;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.ecostayretreat.databinding.ScreenAdminControlBinding;

/**
 * File: AdminControlScreen.java
 * Description: A dashboard for admin-specific information and navigation.
 */
public class AdminControlScreen extends Fragment {

    private ScreenAdminControlBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = ScreenAdminControlBinding.inflate(inflater, container, false);
        // This screen is currently informational. Logic for stats or actions would go here.
        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}