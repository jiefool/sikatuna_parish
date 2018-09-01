package com.bagollabs.sikatuna_parish.myapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class EventActivity extends AppCompatActivity implements MyAdapter.ItemClickListener {
    MyAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

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
}
