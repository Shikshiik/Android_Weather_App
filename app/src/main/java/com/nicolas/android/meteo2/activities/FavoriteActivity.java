package com.nicolas.android.meteo2.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.nicolas.android.meteo2.R;
import com.nicolas.android.meteo2.adapters.FavoriteAdapter;
import com.nicolas.android.meteo2.models.City;
import com.nicolas.android.meteo2.utils.UtilAPI;
import com.nicolas.android.meteo2.utils.UtilDB;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class FavoriteActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private ArrayList<City> mCities;
    private Context mContext;

    private City mCityRemoved;
    private int mPositionCityRemoved;

    private City mCityShowMap;
    private int mPositionCityShowed;

    private Handler mHandler;
    private OkHttpClient mOkHttpClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        CollapsingToolbarLayout toolBarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        toolBarLayout.setTitle(getResources().getString(R.string.your_favorites));

        mContext = this;
        mHandler = new Handler();
        mOkHttpClient = new OkHttpClient();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

                View v = LayoutInflater.from(mContext).inflate(R.layout.dialog_add_favorite, null);
                final EditText editTextCity = (EditText) v.findViewById(R.id.edit_text_dialog_city);
                builder.setView(v);
                builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (editTextCity.getText().toString().length() > 0) {
                            updateWeatherDataCityName(editTextCity.getText().toString());
                        }
                    }
                });

                builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });

                builder.create().show();
            }
        });




        mCities = UtilDB.initFavoriteCitiesFromDB(mContext);

        /*
        City city1 = new City("Montréal", "Légères pluies", "22°C", R.drawable.weather_rainy_grey);
        City city2 = new City("New York", "Ensoleillé", "22°C", R.drawable.weather_sunny_grey);
        City city3 = new City("Paris", "Nuageux", "24°C", R.drawable.weather_foggy_grey);
        City city4 = new City("Toulouse", "Pluies modérées", "20°C", R.drawable.weather_rainy_grey);

        mCities.add(city1);
        mCities.add(city2);
        mCities.add(city3);
        mCities.add(city4);
        */
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view_favorite);

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new FavoriteAdapter(mContext, mCities);
        mRecyclerView.setAdapter(mAdapter);






        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

                mPositionCityRemoved = ((FavoriteAdapter.ViewHolder) viewHolder).position;
                mCityRemoved = mCities.remove(mPositionCityRemoved);
                mAdapter.notifyDataSetChanged();

                UtilDB.deleteFavoriteCityToDB(mContext, mCityRemoved.mIdDataBase);

                Snackbar.make(findViewById(R.id.myCoordinatorLayout), mCityRemoved.mName + " est supprimé", Snackbar.LENGTH_LONG)
                        .setAction("Annuler", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mCities.add(mPositionCityRemoved, mCityRemoved);
                                UtilDB.saveFavouriteCitiesToDB(mContext, mCityRemoved);
                                mAdapter.notifyDataSetChanged();
                            }
                        })
                        .show();

            }
        });

        ItemTouchHelper itemTouchHelperMap = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {



                mPositionCityShowed = ((FavoriteAdapter.ViewHolder) viewHolder).position;
                mCityShowMap = mCities.get(mPositionCityShowed);


                //startActivity(new Intent(mContext, MapsActivity.class));
                Log.d("TAG", "onSwiped: " + mCityShowMap.mLatitude + " " + mCityShowMap.mLongitude);
                startActivity(new Intent(mContext, MapsActivity.class)
                        .putExtra("lat",mCityShowMap.mLatitude)
                        .putExtra("long",mCityShowMap.mLongitude)
                        .putExtra("cityName", mCityShowMap.mName)
                );




                //Fonctionne pour reset le swipe mais est déprécié
                //mAdapter.notifyItemChanged(viewHolder.getAdapterPosition());

                //ok
                mAdapter.notifyItemChanged(viewHolder.getBindingAdapterPosition());
            }
        });

        itemTouchHelper.attachToRecyclerView(mRecyclerView);
        itemTouchHelperMap.attachToRecyclerView(mRecyclerView);

    }

    public void updateWeatherDataCityName(final String cityName) {

        String[] params = {cityName};
        String s = String.format(UtilAPI.OPEN_WEATHER_MAP_API_CITY_NAME, params);
        Request request = new Request.Builder().url(s).build();
        mOkHttpClient.newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                final String stringJson = response.body().string();

                if (response.isSuccessful() && UtilAPI.isSuccessful(stringJson)) {
                    mHandler.post(new Runnable() {
                        public void run() {
                            renderFavoriteCityWeather(stringJson);
                        }
                    });
                } else {
                    mHandler.post(new Runnable() {
                        public void run() {
                            Toast.makeText(mContext, getString(R.string.place_not_found), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        });
    }

    private void renderFavoriteCityWeather(String stringJson) {

        try {
            City city = new City(stringJson);
            mCities.add(city);
            mAdapter.notifyDataSetChanged();

            UtilDB.saveFavouriteCitiesToDB(mContext, city);
        } catch (JSONException e) {
            Toast.makeText(mContext, getString(R.string.place_not_found), Toast.LENGTH_LONG).show();
        }
    }
}