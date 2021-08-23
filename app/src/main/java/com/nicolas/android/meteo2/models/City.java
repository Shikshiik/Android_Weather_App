package com.nicolas.android.meteo2.models;

public class City {

    public String mName;
    public String mDescription;
    public String mTemperature;
    public int mWeatherIcon;

    public City(String mName, String mDescription, String mTeperature, int mWeatherIcon) {
        this.mName = mName;
        this.mDescription = mDescription;
        this.mTemperature = mTeperature;
        this.mWeatherIcon = mWeatherIcon;
    }
}
