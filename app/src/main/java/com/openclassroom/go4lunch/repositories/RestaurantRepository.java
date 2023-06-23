package com.openclassroom.go4lunch.repositories;


import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.openclassroom.go4lunch.BuildConfig;
import com.openclassroom.go4lunch.models.Restaurant;
import com.openclassroom.go4lunch.models.entities.PlacesResults;
import com.openclassroom.go4lunch.models.entities.Result;
import com.openclassroom.go4lunch.retrofit.ApiClient;
import com.openclassroom.go4lunch.retrofit.GoogleMapAPI;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RestaurantRepository implements Serializable {

    private static volatile RestaurantRepository instance;
    private MutableLiveData<List<Restaurant>> mRestaurantList = new MutableLiveData<>();
    private List<Restaurant> listOfRestaurant;

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

    public void updateRestaurant() {
        String location = "48.9652044,1.8744598";
        int radius = 1500;
        String type = "restaurant";
        String key = BuildConfig.MAPS_API_KEY;

        listOfRestaurant = new ArrayList<>();

        GoogleMapAPI googleMapAPI = ApiClient.getClient().create(GoogleMapAPI.class);
        googleMapAPI.getAllRestaurant(location, radius, type, key).enqueue(new Callback<PlacesResults>() {
            @Override
            public void onResponse(@NonNull Call<PlacesResults> call, @NonNull Response<PlacesResults> response) {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    List<Result> results = response.body().getResults();
                    Log.d("RestaurantFragment", "list of results: " + results);

                    for (Result result : results) {
                        if (result.getPhotos() != null && result.getRating() != null) {
                            String urlPicture = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=50" +
                                    "&photoreference="
                                    + result.getPhotos().get(0).getPhotoReference() + "&key=" + key;
                            Restaurant restaurant = new Restaurant(result.getPlaceId(), result.getName(),
                                    result.getRating().toString(), result.getRating().floatValue(),
                                    result.getTypes().get(1), urlPicture,
                                    result.getBusinessStatus(), result.getVicinity(), false, result.getGeometry().getLocation());
                            listOfRestaurant.add(restaurant);
                        } else {
                            Restaurant restaurant = new Restaurant(result.getPlaceId(), result.getName(),
                                    "4255266", 4F,
                                    result.getTypes().get(1), "",
                                    result.getBusinessStatus(), result.getVicinity(), false, result.getGeometry().getLocation());
                            listOfRestaurant.add(restaurant);
                        }
                    }
                    mRestaurantList.postValue(listOfRestaurant);
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
