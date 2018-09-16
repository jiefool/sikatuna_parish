package com.jennytanginan.sikatuna_parish.myapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class SettingsActivity extends AppCompatActivity {
    TextView responseText;
    String userIdRes = "";
    String responseRes = "";
    CurrentUser currentUser;
    ApiUtils apiUtils;
    EditText name;
    EditText username;
    EditText email;
    EditText currentPassword;
    EditText newPassword;
    EditText confirmPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        currentUser = new CurrentUser(this);
        apiUtils = new ApiUtils(this);
        responseText = findViewById(R.id.response_text);
        name = findViewById(R.id.name_input);
        username = findViewById(R.id.username_input);
        email = findViewById(R.id.email_input);
        currentPassword = findViewById(R.id.current_password_input);
        newPassword = findViewById(R.id.new_password_input);
        confirmPassword = findViewById(R.id.confirm_password_input);

        name.setText(currentUser.getName());
        username.setText(currentUser.getUsername());
        email.setText(currentUser.getEmail());

    }

    public void updateUser(View view) {
        String nameStr = name.getText().toString();
        String usernameStr = username.getText().toString();
        String emailStr = email.getText().toString();
        String currentPasswordStr = currentPassword.getText().toString();
        String newPasswordStr = newPassword.getText().toString();
        String confirmPasswordStr = confirmPassword.getText().toString();

        String userId =  currentUser.getUserId();
        RequestParams params = new RequestParams();
        params.add("name", nameStr);
        params.add("username", usernameStr);
        params.add("email", emailStr);
        params.add("current_password", currentPasswordStr);
        params.add("password", newPasswordStr);
        params.add("password_confirmation", confirmPasswordStr);

        JsonHttpResponseHandler jhrh = new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                System.out.println(response);

                try {
                    userIdRes = response.getString("id");
                } catch (JSONException e) {
                    try {
                        responseRes = response.getString("error");
                    } catch (JSONException e1) {
                        e1.printStackTrace();
                    }
                }

                if(!userIdRes.isEmpty()){
                    responseText.setText("Update successful.");
                }

                if (!responseRes.isEmpty()){
                    responseText.setText(responseRes);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                System.out.println(errorResponse);
            }
        };

        apiUtils.updateUser(userId, params, jhrh);

    }
}
