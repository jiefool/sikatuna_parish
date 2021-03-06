package com.jennytanginan.sikatuna_parish.myapplication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.github.jjobes.slidedatetimepicker.SlideDateTimeListener;
import com.github.jjobes.slidedatetimepicker.SlideDateTimePicker;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import cz.msebera.android.httpclient.Header;

@SuppressLint("SimpleDateFormat")
public class AddNewEventActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private SimpleDateFormat mFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private Button setDateStart;
    private Button setDateEnd;
    private Boolean isStart = false;
    JSONArray priestUsersArr;
    String userId;
    ApiUtils apiUtils;
    TextView createEventResponse;

    EditText etName;
    EditText etTimeStart;
    EditText etTimeEnd;
    EditText etAlarm;
    EditText etDetails;

    private SlideDateTimeListener listener = new SlideDateTimeListener() {

        @Override
        public void onDateTimeSet(Date date)
        {
            Toast.makeText(AddNewEventActivity.this, mFormatter.format(date), Toast.LENGTH_SHORT).show();
            if(isStart){
                EditText startDate = findViewById(R.id.start_datetime);
                startDate.setText(mFormatter.format(date));
            }else{
                EditText startDate = findViewById(R.id.end_datetime);
                startDate.setText(mFormatter.format(date));
            }

        }

        // Optional cancel listener
        @Override
        public void onDateTimeCancel()
        {
            Toast.makeText(AddNewEventActivity.this,
                    "Canceled", Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_event);

        etName = findViewById(R.id.name);
        etTimeStart = findViewById(R.id.start_datetime);
        etTimeEnd = findViewById(R.id.end_datetime);
        etAlarm = findViewById(R.id.alarm);
        etDetails = findViewById(R.id.details);

        setDateStart =  findViewById(R.id.button_start_datetime);
        setDateEnd =  findViewById(R.id.button_end_datetime);
        apiUtils = new ApiUtils(this);
        createEventResponse = findViewById(R.id.create_event_response);
        setDateStart.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v)
            {
                isStart = true;
                new SlideDateTimePicker.Builder(getSupportFragmentManager())
                        .setListener(listener)
                        .setInitialDate(new Date())
                        .build()
                        .show();
            }
        });

        setDateEnd.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v)
            {
                isStart = false;
                new SlideDateTimePicker.Builder(getSupportFragmentManager())
                        .setListener(listener)
                        .setInitialDate(new Date())
                        .build()
                        .show();
            }
        });

        Intent intent = this.getIntent();
        String priestUsers = intent.getExtras().getString("priest_users");

        System.out.println(priestUsers);

        ArrayList<String> items = new ArrayList<>();
        try {
            priestUsersArr = new JSONArray(priestUsers);
            for (int i=0; i < priestUsersArr.length(); i++) {
                items.add(priestUsersArr.getJSONObject(i).getString("name"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Spinner priestSpinner = findViewById(R.id.priestSpinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        priestSpinner.setOnItemSelectedListener(this);
        priestSpinner.setAdapter(adapter);


    }

    public void submitEventDetails(View view) {
        String name = etName.getText().toString();
        String timeStart = etTimeStart.getText().toString();
        String timeEnd = etTimeEnd.getText().toString();
        String alarm = etAlarm.getText().toString();
        String details = etDetails.getText().toString();

        RequestParams params = new RequestParams();
        params.add("name", name);
        params.add("time_start", timeStart);
        params.add("time_end", timeEnd);
        params.add("alarm", alarm);
        params.add("details", details);
        params.add("user_id", userId);

        CurrentUser currentUser = new CurrentUser(this);

        if(currentUser.getUserId().equals(userId)){
            params.add("is_confirmed", "true");
        }else{
            params.add("is_confirmed", "false");
        }





        JsonHttpResponseHandler jhrh = new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                createEventResponse.setText("Event created successfully.");
                Intent intent = new Intent(AddNewEventActivity.this, EventActivity.class);
                finish();
                startActivity(intent);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject jsonObject){
                createEventResponse.setText("Failed to create event.");
            }

        };

        apiUtils.createEvent(params, jhrh);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        try {
            userId = priestUsersArr.getJSONObject(i).getString("id");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    public void clearFields(View view) {
        etName.setText("");
        etTimeStart.setText("");
        etTimeEnd.setText("");
        etAlarm.setText("");
        etDetails.setText("");
    }
}
