package com.nicolas.android.meteo2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {

    private TextView mTextViewCityName;
    private FloatingActionButton mFloatingActionButtonAdd;
    private LinearLayout mLinearLayoutCity;
    private TextView mTextViewNoConnection;
    private EditText mEditTextMessage;

    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d("TAG", "MainActivity: onCreate()");

        mContext = this;

        mTextViewCityName = (TextView) findViewById(R.id.text_view_city_name);
        mTextViewCityName.setText(R.string.city_name);

        mLinearLayoutCity = (LinearLayout) findViewById(R.id.id_linear_current_city);
        mTextViewNoConnection = (TextView) findViewById(R.id.text_view_no_connection);
        mFloatingActionButtonAdd = (FloatingActionButton) findViewById(R.id.floating_button_add);
        mEditTextMessage = (EditText) findViewById(R.id.edit_text_message);

        mFloatingActionButtonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("TAG", "Clic sur Bouton");
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