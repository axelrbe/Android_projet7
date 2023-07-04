package com.openclassroom.go4lunch.ui;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.Observer;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.PlaceTypes;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.maps.android.SphericalUtil;
import com.openclassroom.go4lunch.BuildConfig;
import com.openclassroom.go4lunch.R;
import com.openclassroom.go4lunch.databinding.ActivityHomeBinding;
import com.openclassroom.go4lunch.models.Restaurant;
import com.openclassroom.go4lunch.repositories.RestaurantRepository;
import com.openclassroom.go4lunch.ui.restaurant.DetailedRestaurantActivity;
import com.openclassroom.go4lunch.ui.restaurant.RestaurantAdapter;
import com.openclassroom.go4lunch.users.UserManager;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
        Serializable {
    com.openclassroom.go4lunch.databinding.ActivityHomeBinding binding;
    private DrawerLayout mDrawerLayout;
    private final UserManager userManager = UserManager.getInstance();
    private View headerLayout;
    private String placeName, placeId;
    private LatLng userLatLng, placeLatLng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        headerLayout = binding.leftNavView.getHeaderView(0);
        setContentView(binding.getRoot());

        RestaurantRepository.getInstance().updateRestaurant(this);

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupWithNavController(binding.navView, navController);

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

        // Get the user location
        getCurrentLocation();

        // Autocomplete implementation
        ImageButton searchView = findViewById(R.id.autocomplete_search_view);
        CardView autocompleteContainer = findViewById(R.id.autocomplete_container);
        searchView.setOnClickListener(v -> {
            if (autocompleteContainer.getVisibility() == View.VISIBLE) {
                autocompleteContainer.setVisibility(View.GONE);
            } else {
                autocompleteContainer.setVisibility(View.VISIBLE);
            }
        });
        autocompleteImplementation();
    }

    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (location != null) {
            userLatLng = new LatLng(location.getLatitude(), location.getLongitude());
        } else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    userLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                    locationManager.removeUpdates(this);
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {
                }

                @Override
                public void onProviderEnabled(String provider) {
                }

                @Override
                public void onProviderDisabled(String provider) {
                }
            });
        }
    }

    private void autocompleteImplementation() {
        if (!Places.isInitialized()) {
            Places.initialize(HomeActivity.this, BuildConfig.MAPS_API_KEY, Locale.FRANCE);
        }
        AutocompleteSupportFragment autocompleteSupportFragment =
                (AutocompleteSupportFragment) getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);
        assert autocompleteSupportFragment != null;
        autocompleteSupportFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME,
                Place.Field.LAT_LNG));
        autocompleteSupportFragment.setTypesFilter(Collections.singletonList(PlaceTypes.RESTAURANT));

        LatLng userLocation = userLatLng;
        Log.d("HomeActivity", "onPlaceSelected: Name = " + userLocation.latitude + " - " + userLocation.longitude);
        double radiusMeters = 1500;
        LatLngBounds bounds = LatLngBounds.builder()
                .include(SphericalUtil.computeOffset(userLocation, radiusMeters, 0))
                .include(SphericalUtil.computeOffset(userLocation, radiusMeters, 90))
                .include(SphericalUtil.computeOffset(userLocation, radiusMeters, 180))
                .include(SphericalUtil.computeOffset(userLocation, radiusMeters, 270))
                .build();
        RectangularBounds locationBias = RectangularBounds.newInstance(bounds);
        autocompleteSupportFragment.setLocationRestriction(locationBias);
        autocompleteSupportFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onError(@NonNull Status status) {
                Log.d("HomeActivity", "onPlaceSelected: Name = " + status.getStatusMessage());
            }

            @Override
            public void onPlaceSelected(@NonNull Place place) {
                placeName = place.getName();
                placeLatLng = place.getLatLng();
                placeId = place.getId();
                Log.d("HomeActivity", "onPlaceSelected: Name = " + placeName);
                Log.d("HomeActivity", "onPlaceSelected: Id = " + placeId);
                Log.d("HomeActivity",
                        "onPlaceSelected: LatLng = " + placeLatLng.latitude + " - " + placeLatLng.longitude);

                RestaurantRepository.getInstance().getAllRestaurant().observe(HomeActivity.this, restaurants -> {
                    for (Restaurant restaurant : restaurants) {
                        if (Objects.equals(placeId, restaurant.getIdR())) {
                            Intent intent = new Intent(HomeActivity.this, DetailedRestaurantActivity.class);
                            intent.putExtra(RestaurantAdapter.RESTAURANT_INFO, restaurant);
                            startActivity(intent);
                        }
                    }
                });

            }
        });
    }

    // left nav menu implementation
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.left_nav_your_lunch) {
            RestaurantRepository.getInstance().getAllRestaurant().observe(this,
                    (Observer<List<Restaurant>>) restaurants -> {
                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        assert user != null;
                        String currentId = user.getUid();

                        DocumentReference docRef = db.collection("workmates").document(currentId);
                        docRef.get().addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    Log.d("homeActivity", "goToSelectedRestaurantPage: " + restaurants);
                                    for (Restaurant mRestaurant : restaurants) {
                                        if (Objects.equals(document.getString("idSelectedRestaurant"),
                                                mRestaurant.getIdR())) {
                                            Intent intent = new Intent(HomeActivity.this,
                                                    DetailedRestaurantActivity.class);
                                            intent.putExtra(RestaurantAdapter.RESTAURANT_INFO, mRestaurant);
                                            startActivity(intent);
                                        }
                                    }
                                } else {
                                    Log.d("homeActivity", "No such document");
                                }
                            } else {
                                Log.d("homeActivity", "get failed with ", task.getException());
                            }
                        });
                    });
        } else if (item.getItemId() == R.id.left_nav_settings) {
            startActivity(new Intent(HomeActivity.this, SettingsActivity.class));
        } else if (item.getItemId() == R.id.left_nav_logout) {
            showDialogForLogout();
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
        ImageView userImage = headerLayout.findViewById(R.id.user_profile_picture);
        Glide.with(this)
                .load(profilePictureUrl)
                .apply(RequestOptions.circleCropTransform())
                .into(userImage);
    }

    private void setTextUserData(FirebaseUser user) {
        //Get email & username from User
        String email = TextUtils.isEmpty(user.getEmail()) ? getString(R.string.info_no_email_found) : user.getEmail();
        String username = TextUtils.isEmpty(user.getDisplayName()) ? getString(R.string.info_no_username_found) :
                user.getDisplayName();

        //Update views with data
        TextView userName = headerLayout.findViewById(R.id.user_name);
        TextView userEmail = headerLayout.findViewById(R.id.user_email);
        userName.setText(username);
        userEmail.setText(email);
    }

    private void showDialogForLogout() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.logout_confirmation)
                .setPositiveButton(R.string.oui,
                        (dialog, id) -> userManager.signOut(this).addOnSuccessListener(aVoid -> {
                            startActivity(new Intent(HomeActivity.this, MainActivity.class));
                            finish();
                        }))
                .setNegativeButton(R.string.non, (dialog, id) -> finish())
                .create()
                .show();
    }
}
