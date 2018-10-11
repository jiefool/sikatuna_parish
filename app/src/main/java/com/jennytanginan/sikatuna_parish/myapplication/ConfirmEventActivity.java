package com.jennytanginan.sikatuna_parish.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import cz.msebera.android.httpclient.Header;

public class ConfirmEventActivity extends AppCompatActivity {

    TextView name;
    TextView timeStart;
    TextView timeEnd;
    TextView priest;
    TextView details;
    JSONObject eventObj;
    ApiUtils apiUtils;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_event);

        Intent intent = getIntent();
        String event = intent.getExtras().getString("event");

        name = findViewById(R.id.name);
        timeStart = findViewById(R.id.time_start);
        timeEnd = findViewById(R.id.time_end);
        priest = findViewById(R.id.priest);
        details = findViewById(R.id.details);
        apiUtils = new ApiUtils(this);
        context = this;

        try {
            eventObj = new JSONObject(event);
            name.setText(eventObj.getString("name"));


            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date timeStartObj = formatter.parse(eventObj.getString("time_start"));
            Date timeEndObj = formatter.parse(eventObj.getString("time_start"));

            formatter = new SimpleDateFormat("EEE, d MMM yyyy hh:mm aaa");
            String timeStartz = formatter.format(timeStartObj);
            String timeEndz = formatter.format(timeEndObj);

            timeStart.setText(timeStartz);
            timeEnd.setText(timeEndz);

            priest.setText(eventObj.getJSONObject("user").getString("name"));
            details.setText(eventObj.getString("details"));
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }


    }

    public void cancel(View view) throws JSONException {
        String eventId = eventObj.getString("id");
        JsonHttpResponseHandler jhrh = new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Intent intent = new Intent(context, EventActivity.class);
                intent.putExtra("events", "");
                context.startActivity(intent);
                ((Activity) context).finish();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                EventActivity eventActivity = new EventActivity();
                eventActivity.setErrorText("Unable to delete event.");
            }
        };
        apiUtils.deleteEvent(eventId, jhrh);
    }

    public void confirmEvent(View view) {
        String eventId = null;
        try {

            eventId = eventObj.getString("id");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonHttpResponseHandler jhtrh = new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Intent intent = new Intent(ConfirmEventActivity.this, HomeActivity.class);
                finish();
                startActivity(intent);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {

            }
        };

        apiUtils.confirmEvent(eventId, jhtrh);
    }
}
