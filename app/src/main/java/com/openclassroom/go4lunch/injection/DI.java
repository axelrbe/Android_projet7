package com.openclassroom.go4lunch.injection;

import com.openclassroom.go4lunch.repositories.RestaurantRepository;
import com.openclassroom.go4lunch.services.DummyApiService;
import com.openclassroom.go4lunch.services.ApiService;

public class DI {
    private static final ApiService API_SERVICE = new DummyApiService();

    public static ApiService getApiService() {return API_SERVICE;}

    public static RestaurantRepository createRestaurantRepository() {
        return new RestaurantRepository(new DummyApiService());
    }
}
