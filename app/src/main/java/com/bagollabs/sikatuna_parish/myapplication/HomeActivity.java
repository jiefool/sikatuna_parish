package com.bagollabs.sikatuna_parish.myapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.PersistentCookieStore;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class HomeActivity extends AppCompatActivity {
    SharedPreferences sharedpreferences;
    public static final String mypreference = "mypref";
    public static JSONObject response = new JSONObject();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        sharedpreferences = getSharedPreferences(mypreference, Context.MODE_PRIVATE);
        String url = "events";
        String accessToken = "";
        sharedpreferences = getSharedPreferences(mypreference, Context.MODE_PRIVATE);
        if (sharedpreferences.contains("access_token")) {
            accessToken = sharedpreferences.getString("access_token", "");
        }

        RequestParams params = new RequestParams();


        HttpUtils.get(url, params,new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                System.out.println(response);
                HomeActivity.response = response;
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                HomeActivity homeActivity = HomeActivity.this;
                homeActivity.setErrorText("Unable to fetch data from server.");
            }

        }, accessToken);
    }

    public void showAllEvents(View view) {
        if (HomeActivity.response.length() != 0) {
            Intent intent = new Intent(HomeActivity.this, EventActivity.class);
            intent.putExtra("events", HomeActivity.response.toString());
            startActivity(intent);
        }else{
            HomeActivity homeActivity = HomeActivity.this;
            homeActivity.setErrorText("No data to show.");
        }

    }

    public void setErrorText(String errorText){
        TextView errorTextView = findViewById(R.id.home_error_text);
        errorTextView.setText(errorText);
    }
}
