package com.openclassroom.go4lunch.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.Objects;

public class Restaurant implements Parcelable {
    private long id;
    private String name, distance, type, address, interestedColleagues, openingHours;

    //private int image; if there's one
    private int rating; // Between 0 and 3 stars
    private boolean notDecided;

    public Restaurant(long id, String name, String distance, String type, String address, String interestedColleagues,
                      String openingHours, int rating) {
        this.id = id;
        this.name = name;
        this.distance = distance;
        this.type = type;
        this.address = address;
        this.interestedColleagues = interestedColleagues;
        this.openingHours = openingHours;
        this.rating = rating;
    }
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    /*public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }*/

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getInterestedColleagues() {
        return interestedColleagues;
    }

    public void setInterestedColleagues(String interestedColleagues) {
        this.interestedColleagues = interestedColleagues;
    }

    public String getOpeningHours() {
        return openingHours;
    }

    public void setOpeningHours(String openingHours) {
        this.openingHours = openingHours;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int numberOfPositiveReview) {
        this.rating = numberOfPositiveReview;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Restaurant restaurant = (Restaurant) o;
        return Objects.equals(id, restaurant.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    // ***************** Implementation of Parcelable ****************
    public static final Parcelable.Creator<Restaurant> CREATOR = new Parcelable.Creator<Restaurant>() {
        public Restaurant createFromParcel(Parcel in) {
            return new Restaurant(in);
        }

        public Restaurant[] newArray(int size) {
            return new Restaurant[size];
        }
    };

    public Restaurant(Parcel in) {
        id = in.readInt();
        name = in.readString();
        distance = in.readString();
        type = in.readString();
        address = in.readString();
        interestedColleagues = in.readString();
        openingHours = in.readString();
        rating = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt((int) id);
        dest.writeString(name);
        dest.writeString(distance);
        dest.writeString(type);
        dest.writeString(address);
        dest.writeString(interestedColleagues);
        dest.writeString(openingHours);
        dest.writeInt(rating);
    }
}
