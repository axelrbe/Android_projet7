package com.openclassroom.go4lunch.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.openclassroom.go4lunch.R;
import com.openclassroom.go4lunch.databinding.ActivityHomeBinding;
import com.openclassroom.go4lunch.injection.DI;
import com.openclassroom.go4lunch.models.Restaurant;
import com.openclassroom.go4lunch.services.ApiService;
import com.openclassroom.go4lunch.ui.restaurant.DetailedRestaurantActivity;
import com.openclassroom.go4lunch.ui.restaurant.RestaurantAdapter;
import com.openclassroom.go4lunch.users.UserManager;

import java.util.List;
import java.util.Objects;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    com.openclassroom.go4lunch.databinding.ActivityHomeBinding binding;
    private DrawerLayout mDrawerLayout;
    private final UserManager userManager = UserManager.getInstance();
    private View headerLayout;

    TextView userName, userEmail;
    ImageView userImage;

    private List<Restaurant> mRestaurantList;
    ApiService mApiService;
    Restaurant selectedRestaurant;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        headerLayout = binding.leftNavView.getHeaderView(0);
        setContentView(binding.getRoot());

        mApiService = DI.getApiService();

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_home);
        NavigationUI.setupWithNavController(binding.navView, navController);

        ApiService apiService = DI.getApiService();
        mRestaurantList = apiService.getAllRestaurants();

        // Left nav menu drawer
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mDrawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.left_nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.open_left_nav,
                R.string.close_left_nav);
        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Get user data
        updateUIWithUserData();
    }

    // left nav menu implementation
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.left_nav_your_lunch) {
            goToSelectedRestaurantPage();
        } else if (item.getItemId() == R.id.left_nav_settings) {
            startActivity(new Intent(HomeActivity.this, SettingsActivity.class));
        } else if (item.getItemId() == R.id.left_nav_logout) {
            userManager.signOut(this).addOnSuccessListener(aVoid -> {
                startActivity(new Intent(HomeActivity.this, MainActivity.class));
                finish();
            });
        }

        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    // Get user data implementation
    private void updateUIWithUserData() {
        if (userManager.isCurrentUserLogged()) {
            FirebaseUser user = userManager.getCurrentUser();

            if (user.getPhotoUrl() != null) {
                setProfilePicture(user.getPhotoUrl());
            }
            setTextUserData(user);
        }
    }

    private void setProfilePicture(Uri profilePictureUrl) {
        userImage = headerLayout.findViewById(R.id.user_profile_picture);
        Glide.with(this)
                .load(profilePictureUrl)
                .apply(RequestOptions.circleCropTransform())
                .into(userImage);
    }

    private void setTextUserData(FirebaseUser user) {
        //Get email & username from User
        String email = TextUtils.isEmpty(user.getEmail()) ? getString(R.string.info_no_email_found) : user.getEmail();
        String username = TextUtils.isEmpty(user.getDisplayName()) ? getString(R.string.info_no_username_found) : user.getDisplayName();

        //Update views with data
        userName = headerLayout.findViewById(R.id.user_name);
        userEmail = headerLayout.findViewById(R.id.user_email);
        userName.setText(username);
        userEmail.setText(email);
    }

    private void goToSelectedRestaurantPage() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String currentId = user.getUid();

        DocumentReference docRef = db.collection("workmates").document(currentId);
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    Log.d("Firestore", "DocumentSnapshot data: " + document);
                    if (document.get("idSelectedRestaurant") != null) {
                        for (Restaurant restaurant : mRestaurantList) {
                            if (document.get("idSelectedRestaurant").hashCode() == restaurant.getId()) {
                                selectedRestaurant = restaurant;
                                Intent intent = new Intent(HomeActivity.this, DetailedRestaurantActivity.class);
                                intent.putExtra(RestaurantAdapter.RESTAURANT_INFO, restaurant);
                                startActivity(intent);
                            }
                        }
                    } else {
                        Toast.makeText(this, "Vous n'avez pas encore choisis de restaurant pour aujourd-hui !", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.d("Firestore", "No such document");
                }
            } else {
                Log.d("Firestore", "get failed with ", task.getException());
            }
        });
    }
}