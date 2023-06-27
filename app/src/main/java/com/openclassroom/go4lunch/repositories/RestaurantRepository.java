package com.openclassroom.go4lunch.repositories;


import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.LocalTime;
import com.google.android.libraries.places.api.model.Period;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FetchPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.openclassroom.go4lunch.BuildConfig;
import com.openclassroom.go4lunch.models.Restaurant;
import
        com.google.android.libraries.places.api.model.OpeningHours;
import com.openclassroom.go4lunch.models.entities.PlacesResults;
import com.openclassroom.go4lunch.models.entities.Result;
import com.openclassroom.go4lunch.retrofit.ApiClient;
import com.openclassroom.go4lunch.retrofit.GoogleMapAPI;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RestaurantRepository implements Serializable {

    private static volatile RestaurantRepository instance;
    private final MutableLiveData<List<Restaurant>> mRestaurantList = new MutableLiveData<>();
    private List<Restaurant> listOfRestaurant;
    private Place place;
    private String phoneNumber, websiteUrl;
    private LatLng mLatLng;
    private boolean isOpenNow;

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

    public void updateRestaurant(Context context) {
        if (!Places.isInitialized()) {
            Places.initialize(context, BuildConfig.MAPS_API_KEY, Locale.FRANCE);
        }

        listOfRestaurant = new ArrayList<>();

        PlacesClient mPlacesClient = Places.createClient(context);
        final List<Place.Field> placeFields = Arrays.asList(Place.Field.PHONE_NUMBER, Place.Field.WEBSITE_URI,
                Place.Field.OPENING_HOURS,
                Place.Field.UTC_OFFSET, Place.Field.LAT_LNG);

        GoogleMapAPI googleMapAPI = ApiClient.getClient().create(GoogleMapAPI.class);
        googleMapAPI.getAllRestaurant("48.9652044,1.8744598", 1500, "restaurant", BuildConfig.MAPS_API_KEY).enqueue(new Callback<PlacesResults>() {
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

                            if (result.getPhotos() != null && result.getRating() != null && websiteUrl != null) {
                                String urlPicture = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=2000" +
                                        "&photoreference="
                                        + result.getPhotos().get(0).getPhotoReference() + "&key=" + BuildConfig.MAPS_API_KEY;
                                Restaurant restaurant = new Restaurant(result.getPlaceId(), result.getName(),
                                        phoneNumber, result.getRating().floatValue(),
                                        result.getTypes().get(1), urlPicture,
                                        websiteUrl, result.getVicinity(), isOpenNow, mLatLng);
                                listOfRestaurant.add(restaurant);
                            } else {
                                Restaurant restaurant = new Restaurant(result.getPlaceId(), result.getName(),
                                        phoneNumber, 0F,
                                        result.getTypes().get(1), "https://images.pexels.com/photos/914388/pexels-photo-914388.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2",
                                        websiteUrl, result.getVicinity(), isOpenNow, mLatLng);
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

    private String getDayOfWeekString(int dayOfWeek) {
        switch (dayOfWeek) {
            case 0:
                return "Sunday";
            case 1:
                return "Monday";
            case 2:
                return "Tuesday";
            case 3:
                return "Wednesday";
            case 4:
                return "Thursday";
            case 5:
                return "Friday";
            case 6:
                return "Saturday";
            default:
                return "";
        }
    }
}
