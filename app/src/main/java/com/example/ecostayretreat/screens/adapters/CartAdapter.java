package com.example.ecostayretreat.screens.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import com.example.ecostayretreat.R;
import com.example.ecostayretreat.model.CartItemModel;
import java.util.Locale;

/**
 * File: CartAdapter.java
 * Description: Adapter for displaying items in the shopping cart.
 */
public class CartAdapter extends ListAdapter<CartItemModel, CartAdapter.CartViewHolder> {

    private final Context context;
    private final OnCartItemInteractionListener listener;

    public interface OnCartItemInteractionListener {
        void onRemoveItemClick(String itemId);
    }

    public CartAdapter(Context context, OnCartItemInteractionListener listener) {
        super(DIFF_CALLBACK);
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    class CartViewHolder extends RecyclerView.ViewHolder {
        private final ImageView icon;
        private final TextView name, dates, price;
        private final ImageButton removeButton;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.ivCartItemIcon);
            name = itemView.findViewById(R.id.tvCartItemName);
            dates = itemView.findViewById(R.id.tvCartItemDates);
            price = itemView.findViewById(R.id.tvCartItemPrice);
            removeButton = itemView.findViewById(R.id.btnRemoveItem);

            removeButton.setOnClickListener(v -> {
                int pos = getAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) {
                    listener.onRemoveItemClick(getItem(pos).getItemId());
                }
            });
        }

        public void bind(CartItemModel item) {
            name.setText(item.getName());
            dates.setText(item.getDateRange());
            price.setText(String.format(Locale.getDefault(), "$%.2f", item.getPrice()));
            if ("Room".equalsIgnoreCase(item.getType())) {
                icon.setImageResource(R.drawable.ic_home);
            } else {
                icon.setImageResource(R.drawable.ic_activities);
            }
        }
    }

    private static final DiffUtil.ItemCallback<CartItemModel> DIFF_CALLBACK = new DiffUtil.ItemCallback<CartItemModel>() {
        @Override
        public boolean areItemsTheSame(@NonNull CartItemModel oldItem, @NonNull CartItemModel newItem) {
            return oldItem.getItemId().equals(newItem.getItemId());
        }
        @Override
        public boolean areContentsTheSame(@NonNull CartItemModel oldItem, @NonNull CartItemModel newItem) {
            return oldItem.getPrice() == newItem.getPrice();
        }
    };
}