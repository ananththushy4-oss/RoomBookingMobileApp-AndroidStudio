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
import com.example.ecostayretreat.model.ActivityModel;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * File: ActivityCarouselAdapter.java
 * Description: Adapter for the eco activities carousel on the Home Screen.
 */
public class ActivityCarouselAdapter extends RecyclerView.Adapter<ActivityCarouselAdapter.ActivityViewHolder> {

    private final Context context;
    private List<ActivityModel> activityList = new ArrayList<>();

    public ActivityCarouselAdapter(Context context) {
        this.context = context;
    }

    public void setActivityList(List<ActivityModel> activityList) {
        this.activityList = activityList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ActivityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_carousel_card, parent, false);
        return new ActivityViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ActivityViewHolder holder, int position) {
        ActivityModel activity = activityList.get(position);
        holder.tvTitle.setText(activity.getName());
        holder.tvSubtitle.setText(String.format(Locale.getDefault(), "$%.2f per person", activity.getPrice()));
        Glide.with(context)
                .load(activity.getImageUrl())
                .placeholder(R.drawable.ic_activities)
                .error(R.drawable.ic_activities)
                .into(holder.ivImage);
    }

    @Override
    public int getItemCount() {
        return activityList.size();
    }

    static class ActivityViewHolder extends RecyclerView.ViewHolder {
        ImageView ivImage;
        TextView tvTitle, tvSubtitle;

        public ActivityViewHolder(@NonNull View itemView) {
            super(itemView);
            ivImage = itemView.findViewById(R.id.ivCarouselImage);
            tvTitle = itemView.findViewById(R.id.tvCarouselTitle);
            tvSubtitle = itemView.findViewById(R.id.tvCarouselSubtitle);
        }
    }
}