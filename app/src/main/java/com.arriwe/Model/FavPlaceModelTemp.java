package com.arriwe.Model;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Shankar on 6/29/2017.
 */
public class FavPlaceModelTemp {
    public String placeName;
    public  String userSavedName;
    public  String address;
    public LatLng latLng;


    public FavPlaceModelTemp(String placeName, LatLng latLong,String address,String userSavedName){
        this.placeName = placeName;
        this.address = address;
        this.latLng = latLong;
        this.userSavedName = userSavedName;
    }

    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    public String getUserSavedName() {
        return userSavedName;
    }

    public void setUserSavedName(String userSavedName) {
        this.userSavedName = userSavedName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public  LatLng getLatLng() {
        return latLng;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

}
