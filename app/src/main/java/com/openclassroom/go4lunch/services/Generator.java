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
            new Workmates("1", "Scarlett", "https://www.kasandbox.org/programming-images/avatars/leaf-blue.png", "Scarlett@go4lunch.fr",  DUMMY_RESTAURANTS.get(0), false),
            new Workmates("2", "Hugh", "https://www.kasandbox.org/programming-images/avatars/leaf-green.png", "Hugh@go4lunch.fr", DUMMY_RESTAURANTS.get(1), false),
            new Workmates("3,", "Nana", "https://www.kasandbox.org/programming-images/avatars/leaf-grey.png", "Nana@go4lunch.fr", DUMMY_RESTAURANTS.get(2), false),
            new Workmates("4", "Godfrey", "https://www.kasandbox.org/programming-images/avatars/leaf-orange.png", "Godfrey@go4lunch.fr", DUMMY_RESTAURANTS.get(3), false)
    );

    static List<Restaurant> generateRestaurants() { return new ArrayList<>(DUMMY_RESTAURANTS); }

    static List<Workmates> generateWorkmates() { return new ArrayList<>(DUMMY_WORKMATES); }
}
