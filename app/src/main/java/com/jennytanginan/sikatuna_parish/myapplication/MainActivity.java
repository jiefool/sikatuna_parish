package com.jennytanginan.sikatuna_parish.myapplication;


import android.content.Intent;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import android.widget.TextView;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;



public class MainActivity extends AppCompatActivity implements View.OnFocusChangeListener {
    CurrentUser currentUser;
    ApiUtils apiUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        currentUser = new CurrentUser(this);
        apiUtils = new ApiUtils(this);

        String accessToken = currentUser.getAccessToken();
        if(!accessToken.isEmpty()){
            gotoHomeActivity();
        }

    }


    public void userLogin(View view) {
        EditText userName = findViewById(R.id.userName);
        EditText userPassword = findViewById(R.id.userPassword);

        final String userNameStr = userName.getText().toString();
        final String userPasswordStr = userPassword.getText().toString();


        final MainActivity mainActivity = this;

        JsonHttpResponseHandler jhrh= new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    String accessToken = (String) response.get("access_token");
                    currentUser.setDataToSharedPreferences("email", userNameStr);
                    currentUser.setDataToSharedPreferences("access_token", accessToken);
                    mainActivity.gotoHomeActivity();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                System.out.println(errorResponse);
                mainActivity.setErrorText("Unable to login.");
            }
        };

        apiUtils.loginUser(userNameStr, userPasswordStr, jhrh, "");

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
