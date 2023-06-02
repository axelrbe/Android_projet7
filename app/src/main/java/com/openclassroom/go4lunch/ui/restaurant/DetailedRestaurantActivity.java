package com.openclassroom.go4lunch.ui.restaurant;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Source;
import com.openclassroom.go4lunch.R;
import com.openclassroom.go4lunch.injection.DI;
import com.openclassroom.go4lunch.models.Restaurant;
import com.openclassroom.go4lunch.services.ApiService;

public class DetailedRestaurantActivity extends AppCompatActivity {

    TextView restaurantName, restaurantType, restaurantAddress;
    RatingBar restaurantRating;
    ImageView restaurantImage, arrowBack;
    Restaurant mRestaurant;
    FloatingActionButton selectRestaurantBtn;
    ApiService mApiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_restaurant);

        mApiService = DI.getApiService();
        mRestaurant = getIntent().getParcelableExtra(RestaurantAdapter.RESTAURANT_INFO);

        restaurantName = findViewById(R.id.detailed_page_name);
        restaurantType = findViewById(R.id.detailed_page_type);
        restaurantAddress = findViewById(R.id.detailed_page_address);
        restaurantRating = findViewById(R.id.detailed_page_rating);
        restaurantImage = findViewById(R.id.detailed_page_image);
        arrowBack = findViewById(R.id.arrow_back);
        selectRestaurantBtn = findViewById(R.id.select_restaurant_btn);

        arrowBack.setOnClickListener(v -> finish());

        setAllRestaurantInfo();
        changeSelectedStatus();
    }

    private void setAllRestaurantInfo() {
        if (mRestaurant != null) {
            restaurantName.setText(mRestaurant.getName());
            restaurantAddress.setText(mRestaurant.getAddress());
            restaurantType.setText(mRestaurant.getType());
            restaurantRating.setRating(mRestaurant.getRating());
        }
    }

    private void changeSelectedStatus() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String currentId = user.getUid();

        DocumentReference docRef = db.collection("workmates").document(currentId);
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    Log.d("Firestore", "DocumentSnapshot data: " + document);
                    if (document.get("idSelectedRestaurant") != null && document.get("idSelectedRestaurant").hashCode() == mRestaurant.getId()) {
                        selectRestaurantBtn.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.baseline_check_circle_24, null));
                    } else {
                        selectRestaurantBtn.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.baseline_add_24, null));
                    }

                    selectRestaurantBtn.setOnClickListener(v -> {
                        if (selectRestaurantBtn.getDrawable().equals(ResourcesCompat.getDrawable(getResources(), R.drawable.baseline_check_circle_24, null))) {
                            selectRestaurantBtn.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.baseline_add_24, null));
                        } else {
                            selectRestaurantBtn.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.baseline_check_circle_24, null));
                        }

                        if (document.get("idSelectedRestaurant") != null) {
                            if (document.get("idSelectedRestaurant").hashCode() == mRestaurant.getId()) {
                                selectRestaurantBtn.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.baseline_add_24, null));
                                db.collection("workmates")
                                        .document(currentId)
                                        .update("idSelectedRestaurant", FieldValue.delete());
                            } else {
                                db.collection("workmates")
                                        .document(currentId)
                                        .update("idSelectedRestaurant", mRestaurant.getId());
                            }
                        } else {
                            db.collection("workmates")
                                    .document(currentId)
                                    .update("idSelectedRestaurant", mRestaurant.getId());
                        }

                    });
                } else {
                    Log.d("Firestore", "No such document");
                }
            } else {
                Log.d("Firestore", "get failed with ", task.getException());
            }
        });
    }
}