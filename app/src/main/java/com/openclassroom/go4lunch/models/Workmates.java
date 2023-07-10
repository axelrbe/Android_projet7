package com.openclassroom.go4lunch.models;

import java.io.Serializable;

public class Workmates implements Serializable {
    private String id;
    private String name, urlPicture, email;
    private Restaurant mRestaurant;
    private boolean isNotificationChecked;

    public Workmates() {
    }

    public Workmates(String id, String name, String urlPicture, String email, Restaurant restaurant, boolean isNotificationChecked) {
        this.id = id;
        this.name = name;
        this.urlPicture = urlPicture;
        this.email = email;
        mRestaurant = restaurant;
        this.isNotificationChecked = isNotificationChecked;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrlPicture() {
        return urlPicture;
    }

    public void setUrlPicture(String urlPicture) {
        this.urlPicture = urlPicture;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Restaurant getRestaurant() {
        return mRestaurant;
    }

    public void setRestaurant(Restaurant restaurant) {
        mRestaurant = restaurant;
    }

    public boolean isNotificationChecked() {
        return isNotificationChecked;
    }

    public void setNotificationChecked(boolean notificationChecked) {
        isNotificationChecked = notificationChecked;
    }
}
