package com.bagollabs.sikatuna_parish.myapplication;

import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class EventActivity extends AppCompatActivity implements MyAdapter.ItemClickListener {
    MyAdapter adapter;
    ApiUtils apiUtils;
    JSONArray priestUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        apiUtils = new ApiUtils(this);

        Intent intent = getIntent();
        String events = intent.getStringExtra("events");
        ArrayList<JSONObject> eventObjectList = new ArrayList<>();

        JSONObject eventObjects = null;
        try {
            eventObjects = new JSONObject(events);

            JSONArray eventList = eventObjects.getJSONArray("events");

            for (int i=0; i < eventList.length(); i++) {
                eventObjectList.add( eventList.getJSONObject(i));
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonHttpResponseHandler jhrh = new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                System.out.println(response);
                EventActivity eventActivity = EventActivity.this;
                eventActivity.priestUsers = response;
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);

            }
        };
        apiUtils.getPriestUsers(jhrh);


        RecyclerView recyclerView = findViewById(R.id.event_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MyAdapter(this, eventObjectList);
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);
    }


    @Override
    public void onItemClick(View view, int position) {
        Toast.makeText(this, "You clicked " + adapter.getItem(position) + " on row number " + position, Toast.LENGTH_SHORT).show();
    }

    public void gotoAddNewEventActvity(View view) {
        Intent intent = new Intent(EventActivity.this, AddNewEventActivity.class);
        intent.putExtra("priest_users", priestUsers.toString());
        startActivity(intent);
    }
}
