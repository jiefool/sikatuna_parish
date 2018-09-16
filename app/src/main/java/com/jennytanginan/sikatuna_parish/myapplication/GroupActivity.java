package com.jennytanginan.sikatuna_parish.myapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Spinner;
import android.widget.TextView;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.acl.Group;
import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class GroupActivity extends AppCompatActivity implements GroupAdapter.ItemClickListener {
    GroupAdapter adapter;
    RecyclerView groupList;
    ApiUtils apiUtils;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);
        groupList = findViewById(R.id.group_list);
        groupList.setLayoutManager(new LinearLayoutManager(this));

        apiUtils = new ApiUtils(this);

        final ArrayList<JSONObject> groupObjectList = new ArrayList<>();

        JsonHttpResponseHandler jhrh = new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {

                try {
                    for (int i=0; i < response.length(); i++) {
                        groupObjectList.add(response.getJSONObject(i));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                adapter = new GroupAdapter(GroupActivity.this, groupObjectList);
                adapter.setClickListener(GroupActivity.this);
                groupList.setAdapter(adapter);

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                GroupActivity.this.setResponseText("Unable to fetch data from server.");
            }
        };

        apiUtils.getGroups(jhrh);

    }

    private void setResponseText(String text) {
        TextView responseText = findViewById(R.id.response_text);
        responseText.setText(text);
    }

    public void gotoAddNewGroupActvity(View view) {
        Intent intent = new Intent(this,AddGroupActivity.class);
        finish();
        startActivity(intent);
    }

    @Override
    public void onItemClick(View view, int position) {

    }
}
