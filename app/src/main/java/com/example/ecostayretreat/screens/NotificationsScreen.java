package com.example.ecostayretreat.screens;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull; // CORRECTED: Switched to the standard AndroidX annotation.
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.ecostayretreat.databinding.ScreenNotificationsBinding;
import com.example.ecostayretreat.model.NotificationModel;
import com.example.ecostayretreat.screens.adapters.NotificationAdapter;
import com.example.ecostayretreat.viewmodel.NotificationViewModel;

public class NotificationsScreen extends Fragment implements NotificationAdapter.OnNotificationClickListener {

    private ScreenNotificationsBinding binding;
    private NotificationViewModel viewModel;
    private NotificationAdapter adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = ScreenNotificationsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(NotificationViewModel.class);

        setupRecyclerView();
        setupObservers();
    }

    private void setupRecyclerView() {
        binding.rvNotifications.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new NotificationAdapter(this);
        binding.rvNotifications.setAdapter(adapter);
        binding.rvNotifications.addItemDecoration(new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL));
    }

    private void setupObservers() {
        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading ->
                binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE));

        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), error ->
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show());

        viewModel.getNotificationsList().observe(getViewLifecycleOwner(), notifications -> {
            adapter.submitList(notifications);
            binding.tvNoNotifications.setVisibility(notifications.isEmpty() ? View.VISIBLE : View.GONE);
        });
    }

    @Override
    public void onNotificationClick(NotificationModel notification) {
        if (!notification.isRead()) {
            viewModel.markAsRead(notification.getNotificationId());
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}