package com.jennytanginan.sikatuna_parish.myapplication;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        System.out.println("SHOULD ALARM");
        Intent i = new Intent(context, AlarmReceiverActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Integer eventId = intent.getExtras().getInt("event_id");
        System.out.println(eventId);
        i.putExtra("event_id", eventId);
        context.startActivity(i);
    }
}
