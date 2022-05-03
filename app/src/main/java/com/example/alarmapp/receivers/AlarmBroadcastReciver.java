package com.example.alarmapp.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.example.alarmapp.AppAlarmManager;

public class AlarmBroadcastReciver extends BroadcastReceiver {


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        int alarmId = intent.getIntExtra("alarmId",0);

        switch (action) {
            case "dismissAlarm":
                AppAlarmManager.dismissAlarm(alarmId);
                break;
            case "snoozeAlarm":
                AppAlarmManager.snoozeAlarm(alarmId);
                break;
            case "startAlarm":
                AppAlarmManager.startAlarm(context,alarmId);
                break;
        }
    }



}
