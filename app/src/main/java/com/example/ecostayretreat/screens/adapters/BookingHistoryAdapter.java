package com.example.ecostayretreat.screens.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import com.example.ecostayretreat.R;
import com.example.ecostayretreat.model.BookingModel;
import java.util.Locale;

/**
 * File: BookingHistoryAdapter.java
 * Description: Adapter for displaying the list of user bookings.
 */
public class BookingHistoryAdapter extends ListAdapter<BookingModel, BookingHistoryAdapter.BookingViewHolder> {

    private final Context context;
    private final OnBookingClickListener listener;

    public interface OnBookingClickListener {
        void onBookingClick(BookingModel booking);
    }

    public BookingHistoryAdapter(Context context, OnBookingClickListener listener) {
        super(DIFF_CALLBACK);
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public BookingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_booking, parent, false);
        return new BookingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookingViewHolder holder, int position) {
        BookingModel currentBooking = getItem(position);
        holder.bind(currentBooking);
    }

    class BookingViewHolder extends RecyclerView.ViewHolder {
        private final ImageView ivItemIcon;
        private final TextView tvItemName, tvBookingDates, tvBookingTotal, tvBookingStatus;

        public BookingViewHolder(@NonNull View itemView) {
            super(itemView);
            ivItemIcon = itemView.findViewById(R.id.ivItemIcon);
            tvItemName = itemView.findViewById(R.id.tvItemName);
            tvBookingDates = itemView.findViewById(R.id.tvBookingDates);
            tvBookingTotal = itemView.findViewById(R.id.tvBookingTotal);
            tvBookingStatus = itemView.findViewById(R.id.tvBookingStatus);

            itemView.setOnClickListener(v -> {
                int pos = getAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) {
                    listener.onBookingClick(getItem(pos));
                }
            });
        }

        public void bind(BookingModel booking) {
            tvItemName.setText(booking.getItemName());
            tvBookingTotal.setText(String.format(Locale.getDefault(), "Total: $%.2f", booking.getTotalPrice()));
            tvBookingStatus.setText(booking.getStatus());

            if ("Room".equalsIgnoreCase(booking.getItemType())) {
                ivItemIcon.setImageResource(R.drawable.ic_home);
                tvBookingDates.setText(String.format("%s - %s", booking.getCheckInDate(), booking.getCheckOutDate()));
            } else {
                ivItemIcon.setImageResource(R.drawable.ic_activities);
                tvBookingDates.setText(String.format("On: %s", booking.getCheckInDate()));
            }

            if ("CONFIRMED".equalsIgnoreCase(booking.getStatus())) {
                tvBookingStatus.setBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimary));
                tvBookingStatus.setTextColor(ContextCompat.getColor(context, R.color.colorOnPrimary));
            } else {
                tvBookingStatus.setBackgroundColor(ContextCompat.getColor(context, R.color.colorError));
                tvBookingStatus.setTextColor(ContextCompat.getColor(context, R.color.colorOnError));
            }
        }
    }

    private static final DiffUtil.ItemCallback<BookingModel> DIFF_CALLBACK = new DiffUtil.ItemCallback<BookingModel>() {
        @Override
        public boolean areItemsTheSame(@NonNull BookingModel oldItem, @NonNull BookingModel newItem) {
            return oldItem.getBookingId().equals(newItem.getBookingId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull BookingModel oldItem, @NonNull BookingModel newItem) {
            return oldItem.getStatus().equals(newItem.getStatus());
        }
    };
}