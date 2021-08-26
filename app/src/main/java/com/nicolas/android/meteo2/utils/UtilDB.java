package com.nicolas.android.meteo2.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.nicolas.android.meteo2.database.DataBaseHelper;
import com.nicolas.android.meteo2.models.City;

import org.json.JSONArray;

import java.util.ArrayList;

public class UtilDB {

    public static ArrayList<City> initFavoriteCitiesFromDB(Context context) {
        DataBaseHelper db = new DataBaseHelper(context);
        return db.selectAllCities();
    }

    public static void saveFavouriteCitiesToDB(Context context, City city) {
        DataBaseHelper db = new DataBaseHelper(context);
        int id = db.insertCity(city);
        city.mIdDataBase = id;
    }

    public static void deleteFavoriteCityToDB(Context context, int cityId) {
        DataBaseHelper db = new DataBaseHelper(context);
        db.deleteCity(cityId);
    }




}
