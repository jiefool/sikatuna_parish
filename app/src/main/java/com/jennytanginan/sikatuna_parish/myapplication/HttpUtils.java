package com.jennytanginan.sikatuna_parish.myapplication;
import com.loopj.android.http.*;

public class HttpUtils {
    private static final String BASE_URL = "http://192.168.254.103:8000/";

    private static AsyncHttpClient client = new AsyncHttpClient();

    public static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler, String accessToken) {
        client.addHeader("Authorization", "Bearer "+accessToken);
        client.addHeader("Accept", "application/json");
        client.get(getAbsoluteUrl(url), params, responseHandler);
    }

    public static void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler, String accessToken) {
        client.addHeader("Authorization", "Bearer "+accessToken);
        client.addHeader("Accept", "application/json");
        client.post(getAbsoluteUrl(url), params, responseHandler);
    }

    public static void getByUrl(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.get(url, params, responseHandler);
    }

    public static void postByUrl(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.post(url, params, responseHandler);
    }

    private static String getAbsoluteUrl(String relativeUrl) {
        return BASE_URL + relativeUrl;
    }
}