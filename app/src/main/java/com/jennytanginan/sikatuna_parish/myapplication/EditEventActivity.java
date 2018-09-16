package com.jennytanginan.sikatuna_parish.myapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import cz.msebera.android.httpclient.Header;

public class EditEventActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private SimpleDateFormat mFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private Boolean isStart = false;
    private Button setDateStart;
    private Button setDateEnd;

    EditText etName;
    EditText etTimeStart;
    EditText etTimeEnd;
    EditText etAlarm;
    EditText etDetails;
    ApiUtils apiUtils;
    JSONArray priestUsersArr;
    Spinner priestSpinner;
    String userId;
    TextView createEventResponse;
    JSONObject  eventObj;

    private SlideDateTimeListener listener = new SlideDateTimeListener() {

        @Override
        public void onDateTimeSet(Date date)
        {
            Toast.makeText(EditEventActivity.this, mFormatter.format(date), Toast.LENGTH_SHORT).show();
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
            Toast.makeText(EditEventActivity.this,
                    "Canceled", Toast.LENGTH_SHORT).show();
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_event);

        Intent intent = getIntent();
        String event = intent.getStringExtra("event");
        apiUtils = new ApiUtils(this);


        etName = findViewById(R.id.name);
        etTimeStart =findViewById(R.id.start_datetime);
        etTimeEnd = findViewById(R.id.end_datetime);
        etAlarm = findViewById(R.id.alarm);
        etDetails = findViewById(R.id.details);
        priestSpinner   = findViewById(R.id.priestSpinner);
        setDateStart =  findViewById(R.id.button_start_datetime);
        setDateEnd =  findViewById(R.id.button_end_datetime);
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



        try {
            eventObj = new JSONObject(event);
            etName.setText(eventObj.getString("name"));
            etTimeStart.setText(eventObj.getString("time_start"));
            etTimeEnd.setText(eventObj.getString("time_end"));
            etDetails.setText(eventObj.getString("details"));

            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date timeStart = formatter.parse(eventObj.getString("time_start"));
            Date alarm = formatter.parse(eventObj.getString("alarm"));
            long difference = Math.abs(timeStart.getTime() - alarm.getTime());
            long minutesDiff = difference / (60 * 1000);

            etAlarm.setText(Long.toString(minutesDiff));


        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        JsonHttpResponseHandler jhrh = new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                priestUsersArr = response;
                ArrayList<String> items = new ArrayList<>();
                try {
                    for (int i = 0; i < response.length(); i++) {
                        items.add(response.getJSONObject(i).getString("name"));
                    }

                    final ArrayAdapter<String>  adapter = new ArrayAdapter<>(EditEventActivity.this, android.R.layout.simple_spinner_dropdown_item, items);
                    priestSpinner.setOnItemSelectedListener(EditEventActivity.this);
                    priestSpinner.setAdapter(adapter);


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {

            }
        };

        apiUtils.getPriestUsers(jhrh);






    }

    public void clearFields(View view) {
        etName.setText("");
        etTimeStart.setText("");
        etTimeEnd.setText("");
        etAlarm.setText("");
        etDetails.setText("");
    }

    public void updateEvent(View view) {
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

        JsonHttpResponseHandler jhrh = new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Intent intent = new Intent(EditEventActivity.this, EventActivity.class);
                finish();
                startActivity(intent);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject jsonObject){
                createEventResponse.setText("Failed to update event.");
            }

        };

        String eventId = "";
        try {
            eventId = eventObj.getString("id");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        apiUtils.updateEvent(eventId, params, jhrh);

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
}
