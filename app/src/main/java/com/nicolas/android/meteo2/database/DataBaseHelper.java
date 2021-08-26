package com.nicolas.android.meteo2.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.nicolas.android.meteo2.models.City;

import org.json.JSONException;

import java.util.ArrayList;

public class DataBaseHelper extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "database_weather";

    // Table Names
    private static final String TABLE_CITY = "city";

    // Column names
    private static final String KEY_ID = "id";
    private static final String KEY_ID_CITY = "id_city";
    private static final String KEY_NAME = "name";
    private static final String KEY_TEMP = "temperature";
    private static final String KEY_DESC = "description";
    private static final String KEY_RES_ICON = "res_icon";
    private static final String KEY_LAT = "lat";
    private static final String KEY_LNG = "lng";

    // Create Table
    private static final String CREATE_TABLE_CITY = "CREATE TABLE "
            + TABLE_CITY
            + "("
            + KEY_ID + " INTEGER PRIMARY KEY,"
            + KEY_ID_CITY + " INTEGER,"
            + KEY_NAME + " TEXT,"
            + KEY_TEMP + " TEXT,"
            + KEY_DESC + " TEXT,"
            + KEY_RES_ICON + " INTEGER,"
            + KEY_LAT + " DECIMAL (3, 10),"
            + KEY_LNG + " DECIMAL (3, 10)"
            + ")";

    // Constructor
    public DataBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_CITY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CITY);
        onCreate(db);
    }

    // Insert a City row
    public int insertCity(City city) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ID_CITY, city.mIdCity);
        values.put(KEY_NAME, city.mName);
        values.put(KEY_TEMP, city.mTemperature);
        values.put(KEY_DESC, city.mDescription);
        values.put(KEY_RES_ICON, city.mWeatherResIconGrey);
        values.put(KEY_LAT, city.mLatitude);
        values.put(KEY_LNG, city.mLongitude);

        // insert row
        int id = (int) db.insert(TABLE_CITY, null, values);

        db.close();

        return id;
    }


    // Delete a City
    public void deleteCity(int idDataBase) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CITY, KEY_ID + " = ?", new String[]{String.valueOf(idDataBase)});
        db.close();
    }

    // Select All Cities
    public ArrayList<City> selectAllCities() {
        ArrayList<City> cities = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_CITY;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                City city = new City();
                city.mIdDataBase = c.getInt(c.getColumnIndex(KEY_ID));
                city.mIdCity = c.getInt(c.getColumnIndex(KEY_ID_CITY));
                city.mName = c.getString(c.getColumnIndex(KEY_NAME));
                city.mTemperature = c.getString(c.getColumnIndex(KEY_TEMP));
                city.mDescription = c.getString(c.getColumnIndex(KEY_DESC));
                city.mWeatherResIconGrey = c.getInt(c.getColumnIndex(KEY_RES_ICON));
                city.mLatitude = c.getDouble(c.getColumnIndex(KEY_LAT));
                city.mLongitude = c.getDouble(c.getColumnIndex(KEY_LNG));

                cities.add(city);
            } while (c.moveToNext());
        }

        db.close();

        return cities;
    }

}
