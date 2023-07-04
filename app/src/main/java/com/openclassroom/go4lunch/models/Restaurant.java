package com.openclassroom.go4lunch.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.Nullable;

import com.google.android.gms.maps.model.LatLng;


public class Restaurant implements Parcelable {
    private String idR;
    private String name;
    @Nullable
    private String phone;
    private Float rating;
    @Nullable
    private String type;
    @Nullable
    private String urlPicture;
    @Nullable
    private String webSite;
    @Nullable
    private String address;
    private boolean isOpenNow;
    private LatLng latLng;
    private int distanceToUser;
    private int numOfWorkmates;


    public Restaurant(String idR, String name, @Nullable String phone, Float rating, @Nullable String type,
                      @Nullable String urlPicture, @Nullable String webSite, @Nullable String address,
                      boolean isOpenNow, LatLng latLng, int distanceToUser, int numOfWorkmates) {
        this.idR = idR;
        this.name = name;
        this.phone = phone;
        this.rating = rating;
        this.type = type;
        this.urlPicture = urlPicture;
        this.webSite = webSite;
        this.address = address;
        this.isOpenNow = isOpenNow;
        this.latLng = latLng;
        this.distanceToUser = distanceToUser;
        this.numOfWorkmates = numOfWorkmates;
    }

    public String getIdR() {
        return idR;
    }

    public void setIdR(String idR) {
        this.idR = idR;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Nullable
    public String getPhone() {
        return phone;
    }

    public void setPhone(@Nullable String phone) {
        this.phone = phone;
    }

    public Float getRating() {
        return rating;
    }

    public void setRating(Float rating) {
        this.rating = rating;
    }

    @Nullable
    public String getType() {
        return type;
    }

    public void setType(@Nullable String type) {
        this.type = type;
    }

    @Nullable
    public String getUrlPicture() {
        return urlPicture;
    }

    public void setUrlPicture(@Nullable String urlPicture) {
        this.urlPicture = urlPicture;
    }

    @Nullable
    public String getWebSite() {
        return webSite;
    }

    public void setWebSite(@Nullable String webSite) {
        this.webSite = webSite;
    }

    @Nullable
    public String getAddress() {
        return address;
    }

    public void setAddress(@Nullable String address) {
        this.address = address;
    }

    public boolean isOpenNow() {
        return isOpenNow;
    }

    public void setOpenNow(boolean openNow) {
        isOpenNow = openNow;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public void setLatLng(LatLng latLng) {
        latLng = latLng;
    }

    public int getDistanceToUser() {
        return distanceToUser;
    }

    public void setDistanceToUser(int distanceToUser) {
        this.distanceToUser = distanceToUser;
    }

    public int getNumOfWorkmates() {
        return numOfWorkmates;
    }

    public void setNumOfWorkmates(int numOfWorkmates) {
        this.numOfWorkmates = numOfWorkmates;
    }

    /* Implémentation de parcelizable */
    public static final Parcelable.Creator<Restaurant> CREATOR = new Parcelable.Creator<Restaurant>() {
        public Restaurant createFromParcel(Parcel in) {
            return new Restaurant(in);
        }

        public Restaurant[] newArray(int size) {
            return new Restaurant[size];
        }
    };

    private Restaurant(Parcel in) {
        idR = in.readString();
        name = in.readString();
        phone = in.readString();
        rating = in.readFloat();
        type = in.readString();
        urlPicture = in.readString();
        webSite = in.readString();
        address = in.readString();
        isOpenNow = in.readByte() != 0;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(idR);
        dest.writeString(name);
        dest.writeString(phone);
        dest.writeFloat(rating);
        dest.writeString(type);
        dest.writeString(urlPicture);
        dest.writeString(webSite);
        dest.writeString(address);
        dest.writeByte((byte) (isOpenNow ? 1 : 0));
    }
}