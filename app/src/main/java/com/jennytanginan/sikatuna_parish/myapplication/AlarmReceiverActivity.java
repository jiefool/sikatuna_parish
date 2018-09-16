package com.jennytanginan.sikatuna_parish.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AlarmReceiverActivity extends Activity {
    private MediaPlayer mMediaPlayer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.alarm);

        Intent intent = getIntent();


        TextView eventNameTv = findViewById(R.id.event_name);
        TextView timeStartTv = findViewById(R.id.time_start);
        TextView timeEndTv = findViewById(R.id.time_end);
        TextView priestTv = findViewById(R.id.priest);
        TextView detailsTv = findViewById(R.id.details);

        String eventStr = intent.getExtras().getString("event");
        JSONObject event = new JSONObject();
        try {
            event = new JSONObject(eventStr);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        System.out.println(event);
        try {

            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date timeStart = formatter.parse(event.getString("time_start"));
            Date timeEnd = formatter.parse(event.getString("time_start"));

            formatter = new SimpleDateFormat("EEE, d MMM yyyy hh:mm aaa");
            String timeStartz = formatter.format(timeStart);
            String timeEndz = formatter.format(timeEnd);

            eventNameTv.setText(event.getString("name"));
            timeStartTv.setText(timeStartz);
            timeEndTv.setText(timeEndz);
            priestTv.setText(event.getJSONObject("user").getString("name"));
            detailsTv.setText(event.getString("details"));
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Button stopAlarm = findViewById(R.id.stopAlarm);

        stopAlarm.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View arg0, MotionEvent arg1) {
                mMediaPlayer.stop();
                finish();
                return false;
            }
        });

        playSound(this, getAlarmUri());
    }

    private void playSound(Context context, Uri alert) {
        mMediaPlayer = new MediaPlayer();
        try {
            mMediaPlayer.setDataSource(context, alert);
            final AudioManager audioManager = (AudioManager) context
                    .getSystemService(Context.AUDIO_SERVICE);
            if (audioManager.getStreamVolume(AudioManager.STREAM_ALARM) != 0) {
                mMediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
                mMediaPlayer.prepare();
                mMediaPlayer.start();
            }
        } catch (IOException e) {
            System.out.println("OOPS");
        }
    }

    //Get an alarm sound. Try for an alarm. If none set, try notification,
    //Otherwise, ringtone.
    private Uri getAlarmUri() {
        Uri alert = RingtoneManager
                .getDefaultUri(RingtoneManager.TYPE_ALARM);
        if (alert == null) {
            alert = RingtoneManager
                    .getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            if (alert == null) {
                alert = RingtoneManager
                        .getDefaultUri(RingtoneManager.TYPE_RINGTONE);
            }
        }
        return alert;
    }

}
