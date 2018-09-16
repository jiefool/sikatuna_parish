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
        String event = intent.getExtras().getString("event");
        System.out.println(event);
        i.putExtra("event", event);
        context.startActivity(i);
    }
}
