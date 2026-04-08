package com.example.ecostayretreat.screens;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.ecostayretreat.R;
import com.example.ecostayretreat.databinding.ScreenEcoInfoBinding;

/**
 * File: EcoInfoScreen.java
 * Description: Fragment to display static information about the hotel's eco-friendly practices.
 */
public class EcoInfoScreen extends Fragment {

    private ScreenEcoInfoBinding binding;

    // Sample image URLs from a free source like Pexels or Unsplash
    private static final String URL_SOLAR = "https://images.pexels.com/photos/159397/solar-panel-array-power-sun-electricity-159397.jpeg";
    private static final String URL_WATER = "https://images.pexels.com/photos/416528/pexels-photo-416528.jpeg";
    private static final String URL_FARM = "https://images.pexels.com/photos/265216/pexels-photo-265216.jpeg";
    private static final String URL_FACT = "https://images.pexels.com/photos/327090/pexels-photo-327090.jpeg";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = ScreenEcoInfoBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Load images into their respective ImageViews using Glide
        loadImage(URL_SOLAR, binding.ivEcoSolar);
        loadImage(URL_WATER, binding.ivEcoWater);
        loadImage(URL_FARM, binding.ivEcoFarm);
        loadImage(URL_FACT, binding.ivEcoFact);
    }

    /**
     * Helper method to load an image from a URL into an ImageView.
     * @param url The URL of the image to load.
     * @param imageView The target ImageView to display the image.
     */
    private void loadImage(String url, ImageView imageView) {
        if (getContext() != null) {
            Glide.with(getContext())
                    .load(url)
                    .placeholder(R.drawable.ic_eco) // Placeholder while loading
                    .error(R.drawable.ic_eco)       // Image to show on error
                    .into(imageView);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // Prevent memory leaks
    }
}