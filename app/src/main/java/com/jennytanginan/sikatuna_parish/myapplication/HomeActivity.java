package com.jennytanginan.sikatuna_parish.myapplication;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.roomorama.caldroid.CaldroidFragment;
import com.roomorama.caldroid.CaldroidListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Formatter;

import cz.msebera.android.httpclient.Header;
import hirondelle.date4j.DateTime;

public class HomeActivity extends AppCompatActivity {
    public static JSONObject response = new JSONObject();
    CaldroidFragment caldroidFragment = new CaldroidFragment();
    ApiUtils apiUtils;
    CurrentUser currentUser;
    Boolean showUnconfirmedEvents = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        apiUtils = new ApiUtils(this);
        currentUser = new CurrentUser(this);

        this.getUserData();


        Bundle args = new Bundle();
        Calendar cal = Calendar.getInstance();
        args.putInt(CaldroidFragment.MONTH, cal.get(Calendar.MONTH) + 1);
        args.putInt(CaldroidFragment.YEAR, cal.get(Calendar.YEAR));
        caldroidFragment.setArguments(args);

        FragmentTransaction t = getSupportFragmentManager().beginTransaction();
        t.replace(R.id.calendarView, caldroidFragment);
        t.commit();

        caldroidFragment.setCaldroidListener(listener);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.home_menu, menu);
        return true;
    }





    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.refresh:
                refreshActivity();
                return true;
            case R.id.settings:
                gotoSettingsActivity();
                return true;
            case R.id.logout:
                logoutUser();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void logoutUser() {
        currentUser.setDataToSharedPreferences("email", "");
        currentUser.setDataToSharedPreferences("access_token", "");

        Intent i = new Intent(HomeActivity.this, MainActivity.class);
        finish();
        startActivity(i);
    }


    private void gotoSettingsActivity() {

    }




    private void refreshActivity(){
        this.recreate();
    }


    public void showAllEvents(View view) {
        if (HomeActivity.response.length() != 0) {
            Intent intent = new Intent(HomeActivity.this, EventActivity.class);
            intent.putExtra("events", HomeActivity.response.toString());
            startActivity(intent);
        }else{
            HomeActivity homeActivity = HomeActivity.this;
            homeActivity.setErrorText("No data to show.");
        }

    }


    public void setErrorText(String errorText){
        TextView errorTextView = findViewById(R.id.home_error_text);
        errorTextView.setText(errorText);
    }

    public void setEventAlarms(JSONObject events){
        JSONArray ea = new JSONArray();

        try {
            ea = events.getJSONArray("events");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
        ArrayList<PendingIntent> intentArray = new ArrayList<PendingIntent>();
        for (int i=0; i < ea.length(); i++) {
            JSONObject thisObj = new JSONObject();
            try {
               thisObj = ea.getJSONObject(i);
            } catch (JSONException e) {
                e.printStackTrace();
            }


            try {
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                try {
                    Date eventAlarmDate = formatter.parse(thisObj.getString("alarm"));
                    Date eventStartDate = formatter.parse(thisObj.getString("time_start"));
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(eventAlarmDate);

                    ColorDrawable primary = new ColorDrawable(getResources().getColor(R.color.colorPrimary));
                    caldroidFragment.setBackgroundDrawableForDate(primary, eventStartDate);
                    caldroidFragment.setTextColorForDate(R.color.colorAccent, eventStartDate);
                    caldroidFragment.refreshView();


                    if (System.currentTimeMillis() < eventAlarmDate.getTime()) {
                        Intent intent = new Intent(this, AlarmReceiver.class);
                        intent.putExtra("event", thisObj.toString());
                        PendingIntent pi = PendingIntent.getBroadcast(this, thisObj.getInt("id"), intent, thisObj.getInt("id"));
                        am.cancel(pi);
                        am.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pi);
                        intentArray.add(pi);
                    }



                } catch (ParseException e) {

                    e.printStackTrace();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    final CaldroidListener listener = new CaldroidListener() {

        @Override
        public void onSelectDate(Date date, View view) {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            String eventDate = formatter.format(date);

            try {
                JSONArray eventArr = HomeActivity.response.getJSONArray("events");
                ArrayList<JSONObject> eventsOnDate = new ArrayList<>();
                for(int i=0;i<eventArr.length();i++){
                    String timeStart = eventArr.getJSONObject(i).getString("time_start");
                    Date timeStartDate = formatter.parse(timeStart);
                    timeStart = formatter.format(timeStartDate);

                    if (eventDate.equals(timeStart)){
                        JSONObject eventObject = eventArr.getJSONObject(i);
                        if (eventObject != null) {
                            eventsOnDate.add(eventObject);
                        }
                    }
                }

                System.out.println(eventsOnDate);

                JSONArray eventJSONArray = new JSONArray(eventsOnDate);
                JSONObject newEventJsonObject = new JSONObject();
                newEventJsonObject.put("events", eventJSONArray);

                Intent intent = new Intent(HomeActivity.this, EventActivity.class);
                intent.putExtra("events", newEventJsonObject.toString());
                startActivity(intent);
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }




        }
    };


    public void viewGroups(View view) {
        Intent intent = new Intent(HomeActivity.this, GroupActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.getEventData();
    }

    public void getEventData(){
        String userId = currentUser.getUserId();
        JsonHttpResponseHandler jhtrh = new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                System.out.println(response);
                HomeActivity.response = response;
                HomeActivity homeActivity = HomeActivity.this;
                homeActivity.setEventAlarms(response);
                try {
                    if (!showUnconfirmedEvents){
                        showUnconfirmedEvents = true;
                        homeActivity.showUnconfirmedEvents(response.getJSONArray("events"));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                HomeActivity homeActivity = HomeActivity.this;
                homeActivity.setErrorText("Unable to fetch data from server.");
            }
        };
        apiUtils.getUserEvents(userId, jhtrh);
    }


    public void getUserData(){
        String email = "";
        email = currentUser.getEmail();
        JsonHttpResponseHandler jhtrh = new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                System.out.println(response);
                try {
                    currentUser.setDataToSharedPreferences("user_id", response.getString("id"));
                    HomeActivity.this.getEventData();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);

            }
        };
        apiUtils.getUserDetails(email, jhtrh);
    }

    public void showUnconfirmedEvents(JSONArray events){
        for (int i=0; i < events.length(); i++) {
            JSONObject thisObj = new JSONObject();
            try {
                thisObj = events.getJSONObject(i);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            try {
                String currentUserId = currentUser.getUserId();
                String eventUserId = thisObj.getJSONObject("user").getString("id");

                if (thisObj.getInt("is_confirmed") == 0 && currentUserId == eventUserId){
                  Intent intent = new Intent(HomeActivity.this, ConfirmEventActivity.class);
                  intent.putExtra("event", thisObj.toString());
                  startActivity(intent);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
