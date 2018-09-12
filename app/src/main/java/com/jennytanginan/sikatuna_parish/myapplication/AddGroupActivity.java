package com.jennytanginan.sikatuna_parish.myapplication;

import android.app.DownloadManager;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class AddGroupActivity extends AppCompatActivity {
    EditText groupName;
    EditText groupLeaderName;
    EditText groupContactNumber;
    ApiUtils apiUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_group);

        groupName = findViewById(R.id.group_name);
        groupLeaderName = findViewById(R.id.group_leader_name);
        groupContactNumber = findViewById(R.id.group_contact_number);
        apiUtils = new ApiUtils(this);
    }

    public void createGroup(View view) {
        String groupNameStr = groupName.getText().toString();
        String groupLeaderNameStr = groupLeaderName.getText().toString();
        String groupContactNumberStr = groupContactNumber.getText().toString();

        RequestParams params = new RequestParams();
        params.add("name", groupNameStr);
        params.add("leader", groupLeaderNameStr);
        params.add("contact_number", groupContactNumberStr);

        JsonHttpResponseHandler jhrh = new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                System.out.println(response);
                AddGroupActivity.this.setResponseText("Group successfully created.");
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                AddGroupActivity.this.setResponseText("Failed to create new group.");
            }
        };

        apiUtils.createGroup(params, jhrh);
    }

    public void setResponseText(String text){
        TextView responseTv = findViewById(R.id.response_text);
        responseTv.setText(text);
    }

    public void clearFields(View view) {
        groupName.setText("");
        groupLeaderName.setText("");
        groupContactNumber.setText("");
    }
}
