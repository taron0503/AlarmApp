package com.example.alarmapp.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.alarmapp.Alarm;
import com.example.alarmapp.AlarmRepository;
import com.example.alarmapp.AlarmsPendingIntentManager;

public class BootBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            AlarmRepository alarmRepository = AlarmRepository.createInstance(context);
            alarmRepository.getAllAlarms().subscribe(alarms -> {
                for(Alarm alarm:alarms){
                    AlarmsPendingIntentManager.setAlarmPendingIntent(context, alarm);
                }
            });
        }
    }
}
