package com.nicolas.android.meteo2.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.nicolas.android.meteo2.R;
import com.nicolas.android.meteo2.activities.FavoriteActivity;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private TextView mTextViewCityName;
    private FloatingActionButton mFloatingActionButtonAdd;
    private LinearLayout mLinearLayoutCity;
    private TextView mTextViewNoConnection;
    private EditText mEditTextMessage;

    private Context mContext;

    private OkHttpClient mOkHttpClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d("TAG", "MainActivity: onCreate()");

        mContext = this;
        mOkHttpClient = new OkHttpClient();





        mTextViewCityName = (TextView) findViewById(R.id.text_view_city_name);
        mTextViewCityName.setText(R.string.city_name);

        mLinearLayoutCity = (LinearLayout) findViewById(R.id.id_linear_current_city);
        mTextViewNoConnection = (TextView) findViewById(R.id.text_view_no_connection);
        mFloatingActionButtonAdd = (FloatingActionButton) findViewById(R.id.floating_button_add);
        mEditTextMessage = (EditText) findViewById(R.id.edit_text_message);

        mFloatingActionButtonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("TAG", "Clic sur le bouton avec un coeur");
                Intent intent = new Intent(mContext, FavoriteActivity.class);
                intent.putExtra("key_message", mEditTextMessage.getText().toString());
                startActivity(intent);
            }
        });


        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(
                mContext.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            Log.d("TAG", "Oui je suis connecté");


            //TEST
            Request request = new Request.Builder().url("https://api.openweathermap.org/data/2.5/weather?lat=47.390026&lon=0.688891&appid=6d5354d77bc2d1bb385e9a0a369b9712").build();
            mOkHttpClient.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.d("TAG", "ComprendPo");
                    e.printStackTrace();

                }
                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        final String stringJson = response.body().string();
                        Log.d("TAG", stringJson);
                    }
                }
            });
            //FIN TEST


        } else {
            Log.d("TAG", "Non j’ai rien du tout");
            updateViewNoConnection();
        }

    }

    public void updateViewNoConnection() {
        mLinearLayoutCity.setVisibility(View.INVISIBLE);
        mFloatingActionButtonAdd.setVisibility(View.INVISIBLE);
        mTextViewNoConnection.setVisibility(View.VISIBLE);
    }


/*    public void onClickFloatingAdd(View view) {
        Log.d("TAG", "Clic sur Bouton");
    }*/

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("TAG", "MainActivity: onDestroy()");
    }



}