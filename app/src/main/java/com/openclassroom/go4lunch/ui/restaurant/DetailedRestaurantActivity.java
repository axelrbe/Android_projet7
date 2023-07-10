package com.openclassroom.go4lunch.ui.restaurant;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.openclassroom.go4lunch.R;
import com.openclassroom.go4lunch.models.Restaurant;
import com.openclassroom.go4lunch.models.Workmates;
import com.openclassroom.go4lunch.repositories.RestaurantRepository;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DetailedRestaurantActivity extends AppCompatActivity implements Serializable {

    private TextView restaurantName, restaurantAddress, likeBtn;
    private RatingBar restaurantRating;
    private ImageView restaurantImage;
    private Restaurant mRestaurant;
    private FloatingActionButton selectRestaurantBtn;
    private FirebaseUser currentUser;
    private String userId;
    private List<Workmates> mDetailedWorkmatesList;
    private RecyclerView mDetailedWorkmatesRecyclerView;
    private DetailedWorkmatesAdapter mDetailedWorkmatesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_restaurant);

        mRestaurant = getIntent().getParcelableExtra(RestaurantAdapter.RESTAURANT_INFO);

        restaurantName = findViewById(R.id.detailed_page_name);
        restaurantAddress = findViewById(R.id.detailed_page_address);
        restaurantRating = findViewById(R.id.detailed_page_rating);
        restaurantImage = findViewById(R.id.detailed_page_image);
        ImageView arrowBack = findViewById(R.id.arrow_back);
        selectRestaurantBtn = findViewById(R.id.select_restaurant_btn);
        TextView linkToPhoneCallBtn = findViewById(R.id.link_to_phone_call);
        TextView linkToWebsiteBtn = findViewById(R.id.link_to_website);
        likeBtn = findViewById(R.id.like_btn);

        arrowBack.setOnClickListener(v -> finish());

        linkToWebsiteBtn.setOnClickListener(v -> {
            String url = mRestaurant.getWebSite();
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            startActivity(intent);
        });

        linkToPhoneCallBtn.setOnClickListener(v -> callRestaurantListener());
        setUpLikeManagement(mRestaurant);
        setAllRestaurantInfo();
        changeSelectedStatus();
        putWorkmatesInRecyclerView();
    }

    private void putWorkmatesInRecyclerView() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("workmates")
                .get()
                .addOnCompleteListener(task -> {
                    currentUser = FirebaseAuth.getInstance().getCurrentUser();
                    assert currentUser != null;
                    userId = currentUser.getUid();
                    if (task.isSuccessful()) {
                        mDetailedWorkmatesList = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            if (!userId.equals(document.getId())) {
                                Log.d("DetailedRestaurantActivity", "Are they equals ? "
                                        + Objects.equals(document.getString("idSelectedRestaurant"),
                                        mRestaurant.getIdR()));
                                Log.d("DetailedRestaurantActivity", "Detailed restaurant id = " + mRestaurant.getIdR());
                                Log.d("DetailedRestaurantActivity",
                                        "documents restaurant ids = " + document.getString("idSelectedRestaurant"));
                                if (Objects.equals(document.getString("idSelectedRestaurant"), mRestaurant.getIdR())) {
                                    Workmates workmate = new Workmates(document.getId(),
                                            document.getString("name"),
                                            document.getString("profilePicture"),
                                            document.getString("email"),
                                            mRestaurant,
                                            false);
                                    mDetailedWorkmatesList.add(workmate);
                                }

                                mDetailedWorkmatesRecyclerView = findViewById(R.id.interested_colleagues_recycler_view);
                                mDetailedWorkmatesAdapter = new DetailedWorkmatesAdapter(this, mDetailedWorkmatesList);
                                mDetailedWorkmatesRecyclerView.setAdapter(mDetailedWorkmatesAdapter);
                                mDetailedWorkmatesRecyclerView.setLayoutManager(new LinearLayoutManager(this,
                                        RecyclerView.VERTICAL, false));
                                mDetailedWorkmatesRecyclerView.setHasFixedSize(true);
                                mDetailedWorkmatesRecyclerView.setNestedScrollingEnabled(false);
                            }
                        }
                    } else {
                        Log.d("DetailedRestaurantActivity", "Error getting documents: ", task.getException());
                    }
                });
    }

    private void setAllRestaurantInfo() {
        if (mRestaurant != null) {
            restaurantName.setText(mRestaurant.getName());
            restaurantAddress.setText(mRestaurant.getAddress());

            if (mRestaurant.getRating() != 0F) {
                restaurantRating.setRating(mRestaurant.getRating());
            } else {
                restaurantRating.setVisibility(View.GONE);
            }

            Glide.with(this)
                    .load(mRestaurant.getUrlPicture())
                    .centerCrop()
                    .into(restaurantImage);
        }
    }

    private void changeSelectedStatus() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        assert user != null;
        String currentId = user.getUid();

        DocumentReference docRef = db.collection("workmates").document(currentId);
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    Log.d("DetailedRestaurantActivity", "DocumentSnapshot data: " + document);
                    if (document.get("idSelectedRestaurant") != null && Objects.equals(document.getString(
                            "idSelectedRestaurant"),
                            mRestaurant.getIdR())) {
                        selectRestaurantBtn.setImageDrawable(ResourcesCompat.getDrawable(getResources(),
                                R.drawable.baseline_check_circle_24, null));
                    } else {
                        selectRestaurantBtn.setImageDrawable(ResourcesCompat.getDrawable(getResources(),
                                R.drawable.baseline_add_24, null));
                    }

                    selectRestaurantBtn.setOnClickListener(v -> {
                        if (selectRestaurantBtn.getDrawable().equals(ResourcesCompat.getDrawable(getResources(),
                                R.drawable.baseline_check_circle_24, null))) {
                            selectRestaurantBtn.setImageDrawable(ResourcesCompat.getDrawable(getResources(),
                                    R.drawable.baseline_add_24, null));
                        } else {
                            selectRestaurantBtn.setImageDrawable(ResourcesCompat.getDrawable(getResources(),
                                    R.drawable.baseline_check_circle_24, null));
                        }

                        if (document.get("idSelectedRestaurant") != null) {
                            if (Objects.equals(document.getString("idSelectedRestaurant"), mRestaurant.getIdR())) {
                                selectRestaurantBtn.setImageDrawable(ResourcesCompat.getDrawable(getResources(),
                                        R.drawable.baseline_add_24, null));
                                db.collection("workmates")
                                        .document(currentId)
                                        .update("idSelectedRestaurant", FieldValue.delete());
                            } else {
                                db.collection("workmates")
                                        .document(currentId)
                                        .update("idSelectedRestaurant", mRestaurant.getIdR());
                            }
                        } else {
                            db.collection("workmates")
                                    .document(currentId)
                                    .update("idSelectedRestaurant", mRestaurant.getIdR());
                        }

                    });
                } else {
                    Log.d("DetailedRestaurantActivity", "No such document");
                }
            } else {
                Log.d("DetailedRestaurantActivity", "get failed with ", task.getException());
            }
        });
    }

    private void callRestaurantListener() {
        AlertDialog.Builder builder = new AlertDialog.Builder(DetailedRestaurantActivity.this);
        builder.setMessage(R.string.call_restaurant_question)
                .setTitle(mRestaurant.getName() + "\n" + mRestaurant.getPhone())
                .setIcon(R.drawable.baseline_local_phone_24)
                .setPositiveButton(R.string.oui, (dialog, which) -> {
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:" + mRestaurant.getPhone()));
                    startActivity(intent);
                }).setNegativeButton(R.string.non, (dialog, which) -> finish()).create().show();
    }

    @SuppressWarnings("unchecked")
    private void setUpLikeManagement(Restaurant restaurant) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String userId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        String restaurantId = restaurant.getIdR();
        CollectionReference usersRef = db.collection("workmates");
        DocumentReference userRef = usersRef.document(userId);

        userRef.get()
                .addOnSuccessListener(documentSnapshot -> {
                    List<String> likedRestaurants = (List<String>) documentSnapshot.get("likedRestaurants");
                    if (likedRestaurants != null && likedRestaurants.contains(restaurantId)) {
                        likeBtn.setTextColor(ContextCompat.getColor(this, R.color.green));
                        Drawable drawable = ContextCompat.getDrawable(this, R.drawable.baseline_star_24_green);
                        likeBtn.setCompoundDrawablesWithIntrinsicBounds(null, drawable, null, null);
                        likeBtn.setText(R.string.like);

                        likeBtn.setOnClickListener(v -> {
                            likeBtn.setTextColor(ContextCompat.getColor(this, R.color.orange));
                            Drawable newDrawable = ContextCompat.getDrawable(this, R.drawable.baseline_star_24);
                            likeBtn.setCompoundDrawablesWithIntrinsicBounds(null, newDrawable, null, null);
                            likeBtn.setText(R.string.liked);

                            userRef.update("likedRestaurants", FieldValue.arrayRemove(restaurantId))
                                    .addOnSuccessListener(i -> Log.w("DetailedRestaurantActivity", "Successfully added restaurant id to liked restaurants" + i))
                                    .addOnFailureListener(e -> Log.w("DetailedRestaurantActivity", "Error adding restaurant to liked restaurants", e));
                        });
                    } else {
                        likeBtn.setTextColor(ContextCompat.getColor(this, R.color.orange));
                        Drawable drawable = ContextCompat.getDrawable(this, R.drawable.baseline_star_24);
                        likeBtn.setCompoundDrawablesWithIntrinsicBounds(null, drawable, null, null);
                        likeBtn.setText(R.string.liked);

                        likeBtn.setOnClickListener(v -> {
                            likeBtn.setTextColor(ContextCompat.getColor(this, R.color.green));
                            Drawable newDrawable = ContextCompat.getDrawable(this, R.drawable.baseline_star_24_green);
                            likeBtn.setCompoundDrawablesWithIntrinsicBounds(null, newDrawable, null, null);
                            likeBtn.setText(R.string.like);

                            userRef.update("likedRestaurants", FieldValue.arrayRemove(restaurantId))
                                    .addOnSuccessListener(i -> Log.w("DetailedRestaurantActivity", "Successfully added restaurant id to liked restaurants" + i))
                                    .addOnFailureListener(e -> Log.w("DetailedRestaurantActivity", "Error adding restaurant to liked restaurants", e));
                        });
                    }
                })
                .addOnFailureListener(e -> Log.w("DetailedRestaurantActivity", "Error getting user document", e));
    }
}