package com.openclassroom.go4lunch.repositories;

import com.openclassroom.go4lunch.models.Restaurant;
import com.openclassroom.go4lunch.services.ApiService;

import java.util.List;

public class RestaurantRepository {

    private final ApiService mApiService;


    public RestaurantRepository(ApiService apiService) {mApiService = apiService;}

    public List<Restaurant> getRestaurantsList() {return mApiService.getAllRestaurants();}
}
