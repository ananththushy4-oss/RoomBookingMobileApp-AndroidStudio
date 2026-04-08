package com.example.ecostayretreat.screens.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.ecostayretreat.R;
import com.example.ecostayretreat.model.RoomModel;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * File: RoomCarouselAdapter.java
 * Description: Adapter for the top rooms carousel on the Home Screen.
 */
public class RoomCarouselAdapter extends RecyclerView.Adapter<RoomCarouselAdapter.RoomViewHolder> {

    private final Context context;
    private List<RoomModel> roomList = new ArrayList<>();

    public RoomCarouselAdapter(Context context) {
        this.context = context;
    }

    public void setRoomList(List<RoomModel> roomList) {
        this.roomList = roomList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RoomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_carousel_card, parent, false);
        return new RoomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RoomViewHolder holder, int position) {
        RoomModel room = roomList.get(position);
        holder.tvTitle.setText(room.getName());
        holder.tvSubtitle.setText(String.format(Locale.getDefault(), "$%.2f / night", room.getPrice()));
        Glide.with(context)
                .load(room.getImageUrl())
                .placeholder(R.drawable.ic_home)
                .error(R.drawable.ic_home)
                .into(holder.ivImage);
    }

    @Override
    public int getItemCount() {
        return roomList.size();
    }

    static class RoomViewHolder extends RecyclerView.ViewHolder {
        ImageView ivImage;
        TextView tvTitle, tvSubtitle;

        public RoomViewHolder(@NonNull View itemView) {
            super(itemView);
            ivImage = itemView.findViewById(R.id.ivCarouselImage);
            tvTitle = itemView.findViewById(R.id.tvCarouselTitle);
            tvSubtitle = itemView.findViewById(R.id.tvCarouselSubtitle);
        }
    }
}