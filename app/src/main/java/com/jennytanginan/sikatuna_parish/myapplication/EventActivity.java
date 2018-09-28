package com.jennytanginan.sikatuna_parish.myapplication;

import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Environment;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import cz.msebera.android.httpclient.Header;

import static android.os.Environment.DIRECTORY_DOCUMENTS;
import static android.os.Environment.getExternalStorageDirectory;
import static android.os.Environment.getExternalStoragePublicDirectory;
import static android.os.Environment.getExternalStorageState;
import static android.os.Environment.getRootDirectory;
import static com.loopj.android.http.AsyncHttpClient.LOG_TAG;

public class EventActivity extends AppCompatActivity implements MyAdapter.ItemClickListener {
    MyAdapter adapter;
    ApiUtils apiUtils;
    JSONArray priestUsers;
    String events = "";
    ArrayList<JSONObject> eventObjectList;
    CurrentUser currentUser;
    Button addEvent;


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

        currentUser = new CurrentUser(this);
        String type = currentUser.getType();
        System.out.println("type: "+type);
        addEvent = findViewById(R.id.add_new_event);

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

    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    public void scanMedia(String path, Context context) {
        File file = new File(path);
        Uri uri = Uri.fromFile(file);
        Intent scanFileIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri);
        context.sendBroadcast(scanFileIntent);
    }

    public File getPublicDocumentStorageDir(String dirName) {
        // Get the directory for the user's public pictures directory.
        File file = new File(getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOCUMENTS), dirName);
        if (!file.mkdirs()) {
            Log.e(LOG_TAG, "Directory not created");
        }
        return file;
    }

}

