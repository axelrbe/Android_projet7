package com.openclassroom.go4lunch.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class Restaurant implements Parcelable {
    private String name;
    private String distance;

    //private int image; if there's one
    private String type;
    private String address;
    private String interestedColleagues;
    private String openingHours;
    private int rating; // Between 0 and 3 stars

    public Restaurant(String name, String distance, String type, String address, String interestedColleagues, String openingHours, int rating) {
        this.name = name;
        this.distance = distance;
        this.type = type;
        this.address = address;
        this.interestedColleagues = interestedColleagues;
        this.openingHours = openingHours;
        this.rating = rating;
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

    // Implementation of Parcelable

    public static final Parcelable.Creator<Restaurant> CREATOR = new Parcelable.Creator<Restaurant>() {
        public Restaurant createFromParcel(Parcel in) {
            return new Restaurant(in);
        }

        public Restaurant[] newArray(int size) {
            return new Restaurant[size];
        }
    };

    public Restaurant(Parcel in) {
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
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(distance);
        dest.writeString(type);
        dest.writeString(address);
        dest.writeString(interestedColleagues);
        dest.writeString(openingHours);
        dest.writeInt(rating);
    }
}
