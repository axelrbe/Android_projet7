package com.openclassroom.go4lunch.repositories;


import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FetchPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.openclassroom.go4lunch.BuildConfig;
import com.openclassroom.go4lunch.models.Restaurant;
import com.openclassroom.go4lunch.models.entities.PlacesResults;
import com.openclassroom.go4lunch.models.entities.Result;
import com.openclassroom.go4lunch.retrofit.ApiClient;
import com.openclassroom.go4lunch.retrofit.GoogleMapAPI;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RestaurantRepository implements Serializable {

    private static volatile RestaurantRepository instance;
    private final MutableLiveData<List<Restaurant>> mRestaurantList = new MutableLiveData<>();
    private List<Restaurant> listOfRestaurant;
    private Place place;
    private String phoneNumber, websiteUrl, userLocation;
    private LatLng mLatLng;
    private boolean isOpenNow;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public RestaurantRepository() {
    }

    public static RestaurantRepository getInstance() {
        if (instance == null) {
            instance = new RestaurantRepository();
        }
        return instance;
    }

    public MutableLiveData<List<Restaurant>> getAllRestaurant() {
        return mRestaurantList;
    }

    public void calculateAverageRatings(List<Restaurant> restaurants) {
        db.collection("workmates")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Map<String, Integer> restaurantsRatings = new HashMap<>();
                        int totalWorkmates = task.getResult().size();

                        for (DocumentSnapshot document : task.getResult()) {
                            Object likedRestaurantsObj = document.get("likedRestaurants");
                            if (likedRestaurantsObj instanceof List<?>) {
                                List<String> likedRestaurants = (List<String>) likedRestaurantsObj;
                                for (String restaurantId : likedRestaurants) {
                                    restaurantsRatings.put(restaurantId, restaurantsRatings.getOrDefault(restaurantId, 0) + 1);
                                }
                            }
                        }
                        getAverageRatings(restaurants, restaurantsRatings, totalWorkmates);
                    }
                }).addOnFailureListener(e -> {

                });
    }

    public float getAverageRatings(List<Restaurant> restaurants, Map<String, Integer> restaurantsRatings, int totalWorkmates) {
        double averageRating = 0;
        for (Restaurant restaurant : restaurants) {
            Integer rating = restaurantsRatings.get(restaurant.getIdR());
            if (rating != null) {
                averageRating = (double) rating / totalWorkmates;
                return (float) averageRating;
            }
        }
        return (float) averageRating;
    }

    public void updateRestaurant(Context context) {
        if (!Places.isInitialized()) {
            Places.initialize(context, BuildConfig.MAPS_API_KEY, Locale.FRANCE);
        }

        listOfRestaurant = new ArrayList<>();

        // Get the current user location
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (location != null) {
            userLocation = location.getLatitude() + "," + location.getLongitude();
        } else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    userLocation = location.getLatitude() + "," + location.getLongitude();
                    locationManager.removeUpdates(this);
                }
                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {}
                @Override
                public void onProviderEnabled(String provider) {}
                @Override
                public void onProviderDisabled(String provider) {}
            });
        }

        PlacesClient mPlacesClient = Places.createClient(context);
        final List<Place.Field> placeFields = Arrays.asList(Place.Field.PHONE_NUMBER, Place.Field.WEBSITE_URI,
                Place.Field.OPENING_HOURS,
                Place.Field.UTC_OFFSET, Place.Field.LAT_LNG);

        GoogleMapAPI googleMapAPI = ApiClient.getClient().create(GoogleMapAPI.class);
        googleMapAPI.getAllRestaurant(userLocation, 1500, "restaurant", BuildConfig.MAPS_API_KEY).enqueue(new Callback<PlacesResults>() {
            @Override
            public void onResponse(@NonNull Call<PlacesResults> call, @NonNull Response<PlacesResults> response) {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    List<Result> results = response.body().getResults();

                    for (Result result : results) {
                        String placeId = result.getPlaceId();
                        FetchPlaceRequest request = FetchPlaceRequest.newInstance(placeId, placeFields);

                        Task<FetchPlaceResponse> task = mPlacesClient.fetchPlace(request);
                        task.addOnSuccessListener(fetchPlaceResponse -> {
                            place = fetchPlaceResponse.getPlace();
                            websiteUrl = String.valueOf(place.getWebsiteUri());
                            phoneNumber = place.getPhoneNumber();
                            mLatLng = place.getLatLng();
                            isOpenNow = Boolean.TRUE.equals(place.isOpen());

                            if (result.getPhotos() != null && websiteUrl != null) {
                                String urlPicture = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=1500" +
                                        "&photoreference="
                                        + result.getPhotos().get(0).getPhotoReference() + "&key=" + BuildConfig.MAPS_API_KEY;
                                Restaurant restaurant = new Restaurant(result.getPlaceId(), result.getName(),
                                        phoneNumber, result.getRating().floatValue(),
                                        result.getTypes().get(1), urlPicture,
                                        websiteUrl, result.getVicinity(), isOpenNow, mLatLng, 0, 0);
                                listOfRestaurant.add(restaurant);
                            } else {
                                Restaurant restaurant = new Restaurant(result.getPlaceId(), result.getName(),
                                        phoneNumber, 0F,
                                        result.getTypes().get(1), "https://images.pexels.com/photos/914388/pexels-photo-914388.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2",
                                        websiteUrl, result.getVicinity(), isOpenNow, mLatLng, 0, 0);
                                listOfRestaurant.add(restaurant);
                            }
                            mRestaurantList.postValue(listOfRestaurant);
                        }).addOnFailureListener(e -> Log.d("RestaurantRepository",
                                "Fail to call place details : " + e.getMessage()));

                    }
                } else {
                    Log.d("RestaurantRepository", "Failed");
                }
            }

            @Override
            public void onFailure(@NonNull Call<PlacesResults> call, @NonNull Throwable t) {
                Log.d("RestaurantRepository", "onFailure: " + t.getMessage());
            }
        });
    }
}
