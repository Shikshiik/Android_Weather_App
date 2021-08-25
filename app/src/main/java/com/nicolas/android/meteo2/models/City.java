package com.nicolas.android.meteo2.models;

import com.nicolas.android.meteo2.utils.Util;

import org.json.JSONException;
import org.json.JSONObject;

public class City {

    public int mIdCity;
    public String mName;
    public String mCountry;
    public int mWeatherResIconWhite;
    public int mWeatherResIconGrey;
    public String mTemperature;
    public String mDescription;
    public double mLatitude;
    public double mLongitude;

    public String mStringJson;

    public City(String stringJson) throws JSONException {

        mStringJson = stringJson;

        JSONObject json = new JSONObject(stringJson);

        JSONObject details = json.getJSONArray("weather").getJSONObject(0);
        JSONObject main = json.getJSONObject("main");
        JSONObject coord = json.getJSONObject("coord");

        mIdCity = json.getInt("id");
        mName = json.getString("name");
        mCountry = json.getJSONObject("sys").getString("country");
        mTemperature = String.format("%.0f", main.getDouble("temp")) + " ℃";
        mDescription = Util.capitalize(details.getString("description"));
        mWeatherResIconWhite = Util.setWeatherIcon(details.getInt("id"), json.getJSONObject("sys").getLong("sunrise") * 1000, json.getJSONObject("sys").getLong("sunset") * 1000);
        mWeatherResIconGrey = Util.setWeatherIcon(details.getInt("id"));
        mLatitude = coord.getDouble("lat");
        mLongitude = coord.getDouble("lon");

    }
}