package com.nicolas.android.meteo2.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
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


    private static final double LAT = 40.716709;
    private static final double LNG = -74.005698;

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


        mFloatingButtonFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, FavoriteActivity.class);
                startActivity(intent);
            }
        });

        initViews();


        if (Util.isActiveNetwork(mContext)) {
            updateWeatherDataCoordinates();
        } else {
            updateViewError(R.string.no_connexion);
        }

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

    public void updateWeatherDataCoordinates() {

        String[] params = {String.valueOf(LAT), String.valueOf(LNG)};

        String s = String.format(UtilAPI.OPEN_WEATHER_MAP_API_COORDINATES, (Object[]) params);
        Log.d("TAG", "updateWeatherDataCoordinates: " + s);
        Request request = new Request.Builder().url(s).build();
        mOkHttpClient.newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("TAG", "onFailure: ");
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {

                final String stringJson = response.body().string();
                Log.d("TAG", "onResponse: " + stringJson);
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

    public void updateViewNoConnection() {
        mLinearLayoutMain.setVisibility(View.INVISIBLE);
        mFloatingButtonFavorite.setVisibility(View.INVISIBLE);
        mTextViewNoConnection.setVisibility(View.VISIBLE);
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
}