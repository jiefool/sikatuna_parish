package com.jennytanginan.sikatuna_parish.myapplication;

import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import cz.msebera.android.httpclient.Header;

public class EventActivity extends AppCompatActivity implements MyAdapter.ItemClickListener {
    MyAdapter adapter;
    ApiUtils apiUtils;
    JSONArray priestUsers;
    String events = "";
    ArrayList<JSONObject> eventObjectList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        apiUtils = new ApiUtils(this);

        Intent intent = getIntent();
        events = intent.getStringExtra("events");

        if (events == null){
            this.getEvents();
        }else{
            populateEventList();
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

    }


    @Override
    public void onItemClick(View view, int position) {
        Toast.makeText(this, "You clicked " + adapter.getItem(position) + " on row number " + position, Toast.LENGTH_SHORT).show();
    }

    public void gotoAddNewEventActvity(View view) {
        Intent intent = new Intent(EventActivity.this, AddNewEventActivity.class);
        intent.putExtra("priest_users", priestUsers.toString());
        finish();
        startActivity(intent);
    }

    public void setErrorText(String s) {
        TextView responseText = findViewById(R.id.response_text);
        responseText.setText(s);
    }

    public void getEvents(){
        JsonHttpResponseHandler jhtrh = new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                System.out.println(response);
                EventActivity.this.events = response.toString();
                populateEventList();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);

            }
        };
        apiUtils.getEvents(jhtrh);
    }


    public void populateEventList(){
        eventObjectList = new ArrayList<>();
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

    public void saveToDocument(View view) throws JSONException {
        Date currentTime = Calendar.getInstance().getTime();
        String fileName = currentTime.toString() + "_events.txt";

        String data = "";
        for(int i=0;i<eventObjectList.size();i++){
            data+=eventObjectList.get(i).getString("name");
            data+="\n";
            data+=eventObjectList.get(i).getString("time_start");
            data+="\n";
            data+=eventObjectList.get(i).getString("time_end");
            data+="\n";
            data+=eventObjectList.get(i).getJSONObject("user").getString("name");
            data+="\n";
            data+=eventObjectList.get(i).getString("details");
            data+="\n";
            data+="======================================";
            data+="\n";
        }

        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(this.openFileOutput(fileName, this.getApplicationContext().MODE_PRIVATE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
