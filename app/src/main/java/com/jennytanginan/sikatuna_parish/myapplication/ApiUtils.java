package com.jennytanginan.sikatuna_parish.myapplication;

import android.app.DownloadManager;
import android.content.Context;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;

public class ApiUtils {
    CurrentUser currentUser;
    String accessToken;
    JSONArray priestUsers;

    public ApiUtils(Context context){
        currentUser = new CurrentUser(context);
        accessToken = currentUser.getAccessToken();
    }

    public void loginUser(final String email, final String password, JsonHttpResponseHandler jsonHttpResponseHandler, String s) {
        String url = "oauth/token";
        RequestParams params = new RequestParams();
        params.add("grant_type", "password");
        params.add("client_id", "2");
        params.add("client_secret", "ZiRgHlb3r0zMaovxii6xvxoVMC1bILVVIDglKVqe");
        params.add("username", email);
        params.add("password", password);
        params.add("scope", "*");

        HttpUtils.post(url, params,jsonHttpResponseHandler, accessToken);
    }

    public void getUserDetails(String email, JsonHttpResponseHandler jhrh){
        String url = "user/"+email+"/data";
        RequestParams params = new RequestParams();
        HttpUtils.get(url, params, jhrh, accessToken);
    }

    public void getPriestUsers(JsonHttpResponseHandler jhrh){
        String url = "users/priest";
        RequestParams params = new RequestParams();
        HttpUtils.get(url, params, jhrh, accessToken);
    }

    public void getEvents(JsonHttpResponseHandler jhrh){
        String url = "events";
        RequestParams params = new RequestParams();
        HttpUtils.get(url, params,jhrh, accessToken);
    }

    public void getUserEvents(String userId, JsonHttpResponseHandler jhrh){
        String url = userId + "/events";
        RequestParams params = new RequestParams();
        HttpUtils.get(url, params,jhrh, accessToken);
    }

    public void createEvent(RequestParams params, JsonHttpResponseHandler jhrh){
        String url = "events/store";
        HttpUtils.post(url, params,jhrh, accessToken);
    }

    public void updateEvent(String eventId, RequestParams params, JsonHttpResponseHandler jhrh){
        String url = "events/"+eventId+"/update";
        HttpUtils.post(url, params,jhrh, accessToken);
    }

    public void confirmEvent(String eventId, JsonHttpResponseHandler jhrh){
        String url = "events/"+eventId+"/confirm";
        RequestParams params = new RequestParams();
        HttpUtils.get(url, params,jhrh, accessToken);
    }

    public void deleteEvent(String eventId, JsonHttpResponseHandler jhrh){
        String url = "events/"+eventId+"/destroy";
        RequestParams params = new RequestParams();
        HttpUtils.post(url, params,jhrh, accessToken);
    }

    public void getEventsFromDate(String eventDate, JsonHttpResponseHandler jhrh){
        String url = "events/"+eventDate;
        RequestParams params = new RequestParams();
        HttpUtils.get(url, params, jhrh, accessToken);
    }

    public void createGroup(RequestParams params, JsonHttpResponseHandler jhrh){
        String url = "groups/store";
        HttpUtils.post(url, params,jhrh, accessToken);
    }

    public void getGroups(JsonHttpResponseHandler jhrh){
        String url = "groups";
        RequestParams params = new RequestParams();
        HttpUtils.get(url, params,jhrh, accessToken);
    }


}
