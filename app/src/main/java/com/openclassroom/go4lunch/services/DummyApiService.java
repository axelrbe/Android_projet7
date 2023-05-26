package com.openclassroom.go4lunch.services;

import com.openclassroom.go4lunch.models.Restaurant;
import com.openclassroom.go4lunch.models.Workmates;

import java.util.List;

public class DummyApiService implements ApiService {

    private final List<Restaurant> allRestaurants = Generator.generateRestaurants();
    private final List<Workmates> allWorkmates = Generator.generateWorkmates();

    @Override
    public List<Restaurant> getAllRestaurants() { return allRestaurants; }

    @Override
    public List<Workmates> getAllWorkmates() { return allWorkmates; }

}
