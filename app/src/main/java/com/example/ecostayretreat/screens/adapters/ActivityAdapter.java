package com.example.ecostayretreat.screens.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.ecostayretreat.R;
import com.example.ecostayretreat.model.ActivityModel;
import com.google.android.material.button.MaterialButton;
import java.util.Locale;
import java.util.stream.Collectors;

public class ActivityAdapter extends ListAdapter<ActivityModel, ActivityAdapter.ActivityViewHolder> {

    private final Context context;
    private final OnActivityInteractionListener listener;
    private boolean isAdmin = false;

    public interface OnActivityInteractionListener {
        void onReserveClick(ActivityModel activity);
        void onEditClick(ActivityModel activity);
        void onDeleteClick(String activityId);
    }

    public ActivityAdapter(Context context, OnActivityInteractionListener listener) {
        super(DIFF_CALLBACK);
        this.context = context;
        this.listener = listener;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ActivityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_activity_card, parent, false);
        return new ActivityViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ActivityViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    class ActivityViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView name, schedule, price;
        MaterialButton reserveButton;
        LinearLayout adminActions;
        ImageButton editButton, deleteButton;

        public ActivityViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.ivActivityImage);
            name = itemView.findViewById(R.id.tvActivityName);
            schedule = itemView.findViewById(R.id.tvActivitySchedule);
            price = itemView.findViewById(R.id.tvActivityPrice);
            reserveButton = itemView.findViewById(R.id.btnReserveActivity);
            adminActions = itemView.findViewById(R.id.admin_actions_layout);
            editButton = itemView.findViewById(R.id.btnEditActivity);
            deleteButton = itemView.findViewById(R.id.btnDeleteActivity);
        }

        void bind(ActivityModel activity) {
            name.setText(activity.getName());
            price.setText(String.format(Locale.getDefault(), "$%.0f", activity.getPrice()));
            if (activity.getSchedule() != null) {
                schedule.setText(activity.getSchedule().stream().collect(Collectors.joining(", ")));
            }
            Glide.with(context).load(activity.getImageUrl()).placeholder(R.drawable.ic_activities).into(image);

            reserveButton.setVisibility(isAdmin ? View.GONE : View.VISIBLE);
            adminActions.setVisibility(isAdmin ? View.VISIBLE : View.GONE);

            reserveButton.setOnClickListener(v -> listener.onReserveClick(getItem(getAdapterPosition())));
            editButton.setOnClickListener(v -> listener.onEditClick(getItem(getAdapterPosition())));
            deleteButton.setOnClickListener(v -> listener.onDeleteClick(getItem(getAdapterPosition()).getActivityId()));
        }
    }

    private static final DiffUtil.ItemCallback<ActivityModel> DIFF_CALLBACK = new DiffUtil.ItemCallback<ActivityModel>() {
        @Override
        public boolean areItemsTheSame(@NonNull ActivityModel oldItem, @NonNull ActivityModel newItem) {
            return oldItem.getActivityId().equals(newItem.getActivityId());
        }
        @Override
        public boolean areContentsTheSame(@NonNull ActivityModel oldItem, @NonNull ActivityModel newItem) {
            return oldItem.getName().equals(newItem.getName()) && oldItem.getPrice() == newItem.getPrice();
        }
    };
}