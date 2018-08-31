package com.bagollabs.sikatuna_parish.myapplication;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.PersistentCookieStore;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.impl.cookie.BasicClientCookie;

public class MainActivity extends AppCompatActivity implements View.OnFocusChangeListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    public void userLogin(View view) {
        EditText userName = (EditText)findViewById(R.id.userName);
        EditText userPassword = (EditText)findViewById(R.id.userPassword);

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
                AsyncHttpClient myClient = new AsyncHttpClient();
                PersistentCookieStore myCookieStore = new PersistentCookieStore(getApplicationContext());
                myClient.setCookieStore(myCookieStore);

                try {
                    String accessToken = (String) response.get("access_token");
                    addCookie("access_token", accessToken, myCookieStore);
                    addCookie("username", userNameStr, myCookieStore);
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


            public void addCookie(String name, String value, PersistentCookieStore cookieStore){
                BasicClientCookie newCookie = new BasicClientCookie(name, value);
                newCookie.setVersion(1);
                newCookie.setDomain("sikatunaparish.com");
                newCookie.setPath("/");
                cookieStore.addCookie(newCookie);
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
    }

}
