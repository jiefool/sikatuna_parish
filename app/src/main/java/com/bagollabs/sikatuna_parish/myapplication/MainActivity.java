package com.bagollabs.sikatuna_parish.myapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import android.widget.TextView;


import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.PersistentCookieStore;
import com.loopj.android.http.RequestParams;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;


public class MainActivity extends AppCompatActivity implements View.OnFocusChangeListener {
    SharedPreferences sharedpreferences;
    public static final String mypreference = "mypref";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedpreferences = getSharedPreferences(mypreference, Context.MODE_PRIVATE);

        sharedpreferences = getSharedPreferences(mypreference,
                Context.MODE_PRIVATE);

        String accessToken;
        if (sharedpreferences.contains("access_token")) {
            accessToken = sharedpreferences.getString("access_token", "");
            if(!accessToken.isEmpty()){
                gotoHomeActivity();
            }
        }
    }


    public void userLogin(View view) {
        EditText userName = findViewById(R.id.userName);
        EditText userPassword = findViewById(R.id.userPassword);

        final String userNameStr = userName.getText().toString();
        final String userPasswordStr = userPassword.getText().toString();



        String url = "oauth/token";

        RequestParams params = new RequestParams();
        params.add("grant_type", "password");
        params.add("client_id", "2");
        params.add("client_secret", "TwEKNoGcwmmAJyCihFhs4nUu0n79q2dmgTyS0mrb");
        params.add("username", userNameStr);
        params.add("password", userPasswordStr);
        params.add("scope", "*");

        HttpUtils.post(url, params,new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                MainActivity mainActivity = MainActivity.this;
                SharedPreferences.Editor editor = sharedpreferences.edit();



                try {
                    String accessToken = (String) response.get("access_token");
                    editor.putString("username", userNameStr);
                    editor.putString("access_token", accessToken);
                    editor.commit();

                    mainActivity.gotoHomeActivity();

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                MainActivity mainActivity = MainActivity.this;
                mainActivity.setErrorText("Invalid credentials.");
            }

        });




    }


    public void setErrorText(String errorText){
        TextView errorTextView = (TextView) findViewById(R.id.errorTextView);
        errorTextView.setText(errorText);
    }

    @Override
    public void onFocusChange(View view, boolean b) {
        setErrorText("");
    }

    public void gotoHomeActivity(){
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        finish();
    }

}
