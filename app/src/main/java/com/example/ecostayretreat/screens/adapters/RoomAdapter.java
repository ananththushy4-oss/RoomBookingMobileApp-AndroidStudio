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
import com.example.ecostayretreat.model.RoomModel;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.Locale;

/**
 * File: RoomAdapter.java
 * Description: Adapter for displaying the list of rooms with admin actions.
 */
public class RoomAdapter extends ListAdapter<RoomModel, RoomAdapter.RoomViewHolder> {

    private final Context context;
    private final OnRoomClickListener listener;
    private boolean isAdmin = false;

    public interface OnRoomClickListener {
        void onRoomClick(RoomModel room);
        void onEditClick(RoomModel room);
        void onDeleteClick(String roomId);
    }

    public RoomAdapter(Context context, OnRoomClickListener listener) {
        super(DIFF_CALLBACK);
        this.context = context;
        this.listener = listener;
    }

    public void setAdmin(boolean isAdmin) {
        this.isAdmin = isAdmin;
        notifyDataSetChanged(); // Re-bind all views to show/hide admin controls
    }

    private static final DiffUtil.ItemCallback<RoomModel> DIFF_CALLBACK = new DiffUtil.ItemCallback<RoomModel>() {
        @Override
        public boolean areItemsTheSame(@NonNull RoomModel oldItem, @NonNull RoomModel newItem) {
            return oldItem.getRoomId().equals(newItem.getRoomId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull RoomModel oldItem, @NonNull RoomModel newItem) {
            return oldItem.getName().equals(newItem.getName()) && oldItem.getPrice() == newItem.getPrice();
        }
    };

    @NonNull
    @Override
    public RoomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_room_card, parent, false);
        return new RoomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RoomViewHolder holder, int position) {
        RoomModel currentRoom = getItem(position);
        holder.bind(currentRoom);
    }

    class RoomViewHolder extends RecyclerView.ViewHolder {
        private final ImageView ivRoomImage;
        private final TextView tvRoomName, tvRoomType, tvRoomPrice;
        private final ChipGroup chipGroupFeatures;
        private final LinearLayout adminActionsLayout;
        private final ImageButton btnEdit, btnDelete;

        public RoomViewHolder(@NonNull View itemView) {
            super(itemView);
            ivRoomImage = itemView.findViewById(R.id.ivRoomImage);
            tvRoomName = itemView.findViewById(R.id.tvRoomName);
            tvRoomType = itemView.findViewById(R.id.tvRoomType);
            tvRoomPrice = itemView.findViewById(R.id.tvRoomPrice);
            chipGroupFeatures = itemView.findViewById(R.id.chipGroupFeatures);
            adminActionsLayout = itemView.findViewById(R.id.admin_actions_layout);
            btnEdit = itemView.findViewById(R.id.btnEditRoom);
            btnDelete = itemView.findViewById(R.id.btnDeleteRoom);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    listener.onRoomClick(getItem(position));
                }
            });

            btnEdit.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    listener.onEditClick(getItem(position));
                }
            });

            btnDelete.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    listener.onDeleteClick(getItem(position).getRoomId());
                }
            });
        }

        public void bind(RoomModel room) {
            tvRoomName.setText(room.getName());
            tvRoomType.setText(room.getType());
            tvRoomPrice.setText(String.format(Locale.getDefault(), "$%.0f", room.getPrice()));

            Glide.with(context)
                    .load(room.getImageUrl())
                    .placeholder(R.drawable.ic_home)
                    .into(ivRoomImage);

            chipGroupFeatures.removeAllViews();
            if (room.getFeatures() != null) {
                for (String feature : room.getFeatures()) {
                    Chip chip = new Chip(context);
                    chip.setText(feature);
                    chip.setChipBackgroundColorResource(R.color.colorPrimaryVariant);
                    chip.setTextColor(context.getColor(R.color.colorOnPrimary));
                    chipGroupFeatures.addView(chip);
                }
            }

            adminActionsLayout.setVisibility(isAdmin ? View.VISIBLE : View.GONE);
        }
    }
}