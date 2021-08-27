package com.nicolas.android.meteo2.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.nicolas.android.meteo2.R;
import com.nicolas.android.meteo2.activities.FavoriteActivity;
import com.nicolas.android.meteo2.models.City;
import com.nicolas.android.meteo2.utils.Util;
import com.nicolas.android.meteo2.utils.UtilAPI;

import org.json.JSONException;

import java.io.IOException;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    final private int REQUEST_CODE = 123;

    private static final double LAT = 40.716709;
    private static final double LNG = -74.005698;


    private LocationManager mLocationManager;
    private LocationListener mLocationListener;

    private Location mCurrentLocation;

    private FloatingActionButton mFloatingButtonFavorite;

    private ProgressBar mProgressBarMain;

    private LinearLayout mLinearLayoutMain;
    private TextView mTextViewNoConnection;

    private TextView mTextViewCity;
    private TextView mTextViewDetails;
    private TextView mTextViewCurrentTemperature;
    private ImageView mImageViewWeatherIcon;

    private Handler mHandler;
    private OkHttpClient mOkHttpClient;
    private City mCurrentCity;

    private Context mContext;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = this;
        mHandler = new Handler();
        mOkHttpClient = new OkHttpClient();

        mLinearLayoutMain = (LinearLayout) findViewById(R.id.linear_layout_current_city);
        mTextViewNoConnection = (TextView) findViewById(R.id.text_view_no_connection);
        mFloatingButtonFavorite = (FloatingActionButton) findViewById(R.id.floating_action_button_favorite);

        mProgressBarMain = (ProgressBar) findViewById(R.id.progress_bar_main);
        mFloatingButtonFavorite = (FloatingActionButton) findViewById(R.id.floating_action_button_favorite);

        mTextViewCity = (TextView) findViewById(R.id.text_view_city_name);
        mTextViewDetails = (TextView) findViewById(R.id.text_view_city_desc);
        mTextViewCurrentTemperature = (TextView) findViewById(R.id.text_view_city_temp);
        mImageViewWeatherIcon = (ImageView) findViewById(R.id.image_view_city_weather);

        mFloatingButtonFavorite.setOnClickListener(v -> startActivity(new Intent(mContext, FavoriteActivity.class)));
        //mFloatingButtonFavorite.setOnClickListener(v -> startActivity(new Intent(mContext, MapsActivity.class)));

        initViews();
        initLocationListener();

        if (Util.isActiveNetwork(mContext)) {
            //updateWeatherDataCoordinates();
            updateWeatherDataCoordinatesFromMyLocation();
        } else {
            updateViewError(R.string.no_connexion);
        }

    }

    private void initLocationListener() {
        mLocationListener = new LocationListener() {

            @Override
            public void onLocationChanged(Location location) {

                mCurrentLocation = location;

                Log.d("TAG", "onLocationChanged: " + location);

                // Récupération des données pour les coordonnées gps
                updateWeatherDataCoordinates();

                mLocationManager.removeUpdates(mLocationListener);
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {
            }

            @Override
            public void onProviderEnabled(String s) {
            }

            @Override
            public void onProviderDisabled(String s) {
            }
        };

    }

    public void initViews() {

        mLinearLayoutMain.setVisibility(View.INVISIBLE);
        mTextViewNoConnection.setVisibility(View.INVISIBLE);
        mFloatingButtonFavorite.setVisibility(View.INVISIBLE);
        mProgressBarMain.setVisibility(View.VISIBLE);
    }

    public void updateViewError(int resString) {

        mLinearLayoutMain.setVisibility(View.INVISIBLE);
        mProgressBarMain.setVisibility(View.INVISIBLE);
        mFloatingButtonFavorite.setVisibility(View.INVISIBLE);
        mTextViewNoConnection.setVisibility(View.VISIBLE);
        mTextViewNoConnection.setText(resString);
    }

    public void updateWeatherDataCoordinatesFromMyLocation() {

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION},
                    REQUEST_CODE);
            Log.d("TAG", "updateWeatherDataCoordinatesFromMyLocation: " + ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION));
        } else {
            mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            //NETWORK_PROVIDER   ou GPS_PROVIDER
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mLocationListener);
            Log.d("TAG", "updateWeatherDataCoordinatesFromMyLocation: " + mLocationManager);
        }
    }

    public void updateWeatherDataCoordinates() {

        //String[] params = {String.valueOf(LAT), String.valueOf(LNG)};
        String[] params = {String.valueOf(mCurrentLocation.getLatitude()), String.valueOf(mCurrentLocation.getLongitude())};


        String s = String.format(UtilAPI.OPEN_WEATHER_MAP_API_COORDINATES, (Object[]) params);
        Request request = new Request.Builder().url(s).build();
        mOkHttpClient.newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {

                final String stringJson = response.body().string();

                if (response.isSuccessful() && UtilAPI.isSuccessful(stringJson)) {

                    mHandler.post(new Runnable() {
                        public void run() {
                            renderCurrentWeather(stringJson);
                        }
                    });
                } else {
                    mHandler.post(new Runnable() {
                        public void run() {
                            updateViewError(R.string.place_not_found);
                        }
                    });
                }
            }
        });
    }

    private void renderCurrentWeather(String jsonString) {

        try {
            mCurrentCity = new City(jsonString);

            mTextViewCity.setText(mCurrentCity.mName.toUpperCase(Locale.US));
            mTextViewDetails.setText(mCurrentCity.mDescription);
            mTextViewCurrentTemperature.setText(mCurrentCity.mTemperature);
            mImageViewWeatherIcon.setImageResource(mCurrentCity.mWeatherResIconWhite);

            mLinearLayoutMain.setVisibility(View.VISIBLE);
            mFloatingButtonFavorite.setVisibility(View.VISIBLE);
            mProgressBarMain.setVisibility(View.GONE);

        } catch (JSONException e) {
            updateViewError(R.string.api_error);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
                    updateWeatherDataCoordinatesFromMyLocation();
                    Log.d("TAG", "onRequestPermissionsResult: " + grantResults);
                } else {
                    // Permission Denied
                    Toast.makeText(MainActivity.this, "Location Permission Denied", Toast.LENGTH_SHORT)
                            .show();
                    Log.d("TAG", "onRequestPermissionsResult: " + grantResults);
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}