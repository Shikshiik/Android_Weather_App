package com.nicolas.android.meteo2.utils;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class UtilAPI {

    public static final String OPEN_WEATHER_MAP_API_COORDINATES = "https://api.openweathermap.org/data/2.5/weather?lat=%s&lon=%s&units=metric&lang=fr&appid=6d5354d77bc2d1bb385e9a0a369b9712";
    public static final String OPEN_WEATHER_MAP_API_CITY_NAME = "https://api.openweathermap.org/data/2.5/weather?q=%s&units=metric&lang=fr&appid=6d5354d77bc2d1bb385e9a0a369b9712";
    public static final String OPEN_WEATHER_MAP_API_FAVOURITE = "https://api.openweathermap.org/data/2.5/group?id=%s&units=metric&lang=fr&appid=6d5354d77bc2d1bb385e9a0a369b9712";
    public static final String OPEN_WEATHER_MAP_API_FORECAST = "https://api.openweathermap.org/data/2.5/forecast/daily?id=%s&units=metric&cnt=5&lang=fr&appid=6d5354d77bc2d1bb385e9a0a369b9712";

    public static boolean isSuccessful(String stringJson) {

        try {
            JSONObject json = new JSONObject(stringJson);

            int cod = json.getInt("cod");
            if (cod != 200)
                return false;

        } catch (Exception e) {
            return false;
        }

        return true;
    }

    public static boolean isSuccessForecast(String stringJson) {

        try {
            JSONObject json = new JSONObject(stringJson);

            int cod = json.getInt("cnt");
            if (cod == 0)
                return false;

        } catch (Exception e) {
            return false;
        }

        return true;
    }
}