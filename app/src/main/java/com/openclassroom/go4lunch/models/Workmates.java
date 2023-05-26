package com.openclassroom.go4lunch.models;

public class Workmates {
    private String name;
    private Restaurant mRestaurant;

    public Workmates(String name, Restaurant restaurant) {
        this.name = name;
        mRestaurant = restaurant;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Restaurant getRestaurant() {
        return mRestaurant;
    }

    public void setRestaurant(Restaurant restaurant) {
        mRestaurant = restaurant;
    }
}
