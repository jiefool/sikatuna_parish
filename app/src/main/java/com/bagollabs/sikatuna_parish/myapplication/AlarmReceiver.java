package com.bagollabs.sikatuna_parish.myapplication;


import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        System.out.println("SHOULD ALARM");
        Intent i = new Intent(context, AlarmReceiverActivity.class);
        Integer eventId = intent.getExtras().getInt("event_id");
        System.out.println(eventId);
        i.putExtra("event_id", eventId);
        context.startActivity(i);
    }
}
