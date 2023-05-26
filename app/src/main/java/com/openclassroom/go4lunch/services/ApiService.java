package com.openclassroom.go4lunch.services;

import com.openclassroom.go4lunch.models.Restaurant;
import com.openclassroom.go4lunch.models.Workmates;

import java.util.List;

public interface ApiService {

    List<Restaurant> getAllRestaurants();

    List<Workmates> getAllWorkmates();
}
