package com.example.ecostayretreat.screens.adapters;

import android.content.Context;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import com.example.ecostayretreat.R;
import com.example.ecostayretreat.model.NotificationModel;

/**
 * File: NotificationAdapter.java
 * Description: Adapter for displaying a list of notifications.
 */
public class NotificationAdapter extends ListAdapter<NotificationModel, NotificationAdapter.NotificationViewHolder> {

    private final OnNotificationClickListener listener;

    public interface OnNotificationClickListener {
        void onNotificationClick(NotificationModel notification);
    }

    public NotificationAdapter(OnNotificationClickListener listener) {
        super(DIFF_CALLBACK);
        this.listener = listener;
    }

    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notification, parent, false);
        return new NotificationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    class NotificationViewHolder extends RecyclerView.ViewHolder {
        private final LinearLayout rootLayout;
        private final View unreadIndicator;
        private final TextView title, message, timestamp;

        public NotificationViewHolder(@NonNull View itemView) {
            super(itemView);
            rootLayout = itemView.findViewById(R.id.notification_item_root);
            unreadIndicator = itemView.findViewById(R.id.unread_indicator);
            title = itemView.findViewById(R.id.tvNotificationTitle);
            message = itemView.findViewById(R.id.tvNotificationMessage);
            timestamp = itemView.findViewById(R.id.tvNotificationTimestamp);

            itemView.setOnClickListener(v -> {
                int pos = getAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) {
                    listener.onNotificationClick(getItem(pos));
                }
            });
        }

        public void bind(NotificationModel notification) {
            title.setText(notification.getTitle());
            message.setText(notification.getMessage());

            // Format timestamp to "5m ago", "2h ago", etc.
            long now = System.currentTimeMillis();
            CharSequence relativeTime = DateUtils.getRelativeTimeSpanString(notification.getTimestamp(), now, DateUtils.MINUTE_IN_MILLIS);
            timestamp.setText(relativeTime);

            if (notification.isRead()) {
                unreadIndicator.setVisibility(View.INVISIBLE);
                rootLayout.setAlpha(0.6f);
            } else {
                unreadIndicator.setVisibility(View.VISIBLE);
                rootLayout.setAlpha(1.0f);
            }
        }
    }

    private static final DiffUtil.ItemCallback<NotificationModel> DIFF_CALLBACK = new DiffUtil.ItemCallback<NotificationModel>() {
        @Override
        public boolean areItemsTheSame(@NonNull NotificationModel oldItem, @NonNull NotificationModel newItem) {
            return oldItem.getNotificationId().equals(newItem.getNotificationId());
        }
        @Override
        public boolean areContentsTheSame(@NonNull NotificationModel oldItem, @NonNull NotificationModel newItem) {
            return oldItem.isRead() == newItem.isRead();
        }
    };
}