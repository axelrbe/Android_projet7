package com.openclassroom.go4lunch.models;

import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.Nullable;

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
    private Location location;


    public Restaurant(String idR, String name, @Nullable String phone, Float rating, @Nullable String type,
                      @Nullable String urlPicture, @Nullable String webSite, @Nullable String address,
                      boolean isOpenNow, Location location) {
        this.idR = idR;
        this.name = name;
        this.phone = phone;
        this.rating = rating;
        this.type = type;
        this.urlPicture = urlPicture;
        this.webSite = webSite;
        this.address = address;
        this.isOpenNow = isOpenNow;
        this.location = location;
    }

    // --- GETTERS ---
    public String getIdR() {
        return idR;
    }

    public String getName() {
        return name;
    }

    @Nullable
    public String getPhone() {
        return phone;
    }

    public Float getRating() {
        return rating;
    }

    @Nullable
    public String getType() {
        return type;
    }

    @Nullable
    public String getUrlPicture() {
        return urlPicture;
    }

    @Nullable
    public String getWebSite() {
        return webSite;
    }

    // --- SETTERS ---
    public void setIdR(String idR) {
        this.idR = idR;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPhone(@Nullable String phone) {
        this.phone = phone;
    }

    public void setRating(Float rating) {
        this.rating = rating;
    }

    public void setType(@Nullable String type) {
        this.type = type;
    }

    public void setUrlPicture(@Nullable String urlPicture) {
        this.urlPicture = urlPicture;
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

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
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