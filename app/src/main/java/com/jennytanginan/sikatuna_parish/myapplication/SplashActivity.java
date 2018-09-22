package com.jennytanginan.sikatuna_parish.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class SplashActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

        Thread timer = new Thread() {
            public void run() {
                try {
                    sleep(1000);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    Intent openClass = new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(openClass);
                }
            }
        };

        timer.start();

    }
}

