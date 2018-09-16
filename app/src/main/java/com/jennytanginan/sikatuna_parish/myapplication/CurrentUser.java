package com.jennytanginan.sikatuna_parish.myapplication;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;

public class CurrentUser{
    SharedPreferences sharedpreferences;
    public static final String mypreference = "mypref";
    SharedPreferences.Editor editor;


    public CurrentUser(Context context){
        sharedpreferences = context.getSharedPreferences(mypreference, context.MODE_PRIVATE);
        editor = sharedpreferences.edit();
    }

    public String getEmail(){
        return getDataFromSharedPreferences("email");
    }

    public String getUserId(){
        return getDataFromSharedPreferences("user_id");
    }

    public String getAccessToken(){
        return getDataFromSharedPreferences("access_token");
    }

    private String getDataFromSharedPreferences(String dataField){
        if (sharedpreferences.contains(dataField)) {
            return sharedpreferences.getString(dataField, "");
        }else{
            return "";
        }
    }

    public void setDataToSharedPreferences(String dataField, String value){
        editor.putString(dataField, value);
        editor.commit();
    }

}
