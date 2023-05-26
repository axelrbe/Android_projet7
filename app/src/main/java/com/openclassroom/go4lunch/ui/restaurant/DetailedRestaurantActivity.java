package com.openclassroom.go4lunch.ui.restaurant;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.openclassroom.go4lunch.R;
import com.openclassroom.go4lunch.models.Restaurant;

public class DetailedRestaurantActivity extends AppCompatActivity {

    TextView restaurantName, restaurantType, restaurantAddress;
    RatingBar restaurantRating;
    ImageView restaurantImage, arrowBack;
    Restaurant mRestaurant;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_restaurant);

        mRestaurant = getIntent().getParcelableExtra(RestaurantAdapter.RESTAURANT_INFO);

        restaurantName = findViewById(R.id.detailed_page_name);
        restaurantType = findViewById(R.id.detailed_page_type);
        restaurantAddress = findViewById(R.id.detailed_page_address);
        restaurantRating = findViewById(R.id.detailed_page_rating);
        restaurantImage = findViewById(R.id.detailed_page_image);
        arrowBack = findViewById(R.id.arrow_back);

        arrowBack.setOnClickListener(v -> {
            finish();
        });

        setAllRestaurantInfo();
    }

    private void setAllRestaurantInfo() {
        if (mRestaurant != null) {
            restaurantName.setText(mRestaurant.getName());
            restaurantAddress.setText(mRestaurant.getAddress());
            restaurantType.setText(mRestaurant.getType());
            restaurantRating.setRating(mRestaurant.getRating());
        }
    }
}