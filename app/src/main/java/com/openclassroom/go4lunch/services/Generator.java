package com.openclassroom.go4lunch.services;

import com.openclassroom.go4lunch.models.Restaurant;
import com.openclassroom.go4lunch.models.Workmates;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Generator {
    public static List<Restaurant> DUMMY_RESTAURANTS = Arrays.asList(
            new Restaurant("first", "23m", "Korean", "1 rue quelque chose", "3", "19h", 1, false),
            new Restaurant("second", "120m", "Chinese", "2 rue quelque chose", "0", "18h", 1, true),
            new Restaurant("third", "3km", "Portuguese", "3 rue quelque chose", "1", "17h30", 3, false),
            new Restaurant("fourth", "10km", "Italian", "4 rue quelque chose", "7", "20h", 2, false)
    );

    public static List<Workmates> DUMMY_WORKMATES = Arrays.asList(
            new Workmates("Scarlett", DUMMY_RESTAURANTS.get(0)),
            new Workmates("Hugh", DUMMY_RESTAURANTS.get(1)),
            new Workmates("Nana", DUMMY_RESTAURANTS.get(2)),
            new Workmates("Godfrey", DUMMY_RESTAURANTS.get(3))
    );

    static List<Restaurant> generateRestaurants() { return new ArrayList<>(DUMMY_RESTAURANTS); }

    static List<Workmates> generateWorkmates() { return new ArrayList<>(DUMMY_WORKMATES); }
}
