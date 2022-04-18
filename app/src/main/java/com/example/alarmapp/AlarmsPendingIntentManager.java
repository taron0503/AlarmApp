package com.example.alarmapp;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.example.alarmapp.receivers.AlarmBroadcastReciver;

public class AlarmsPendingIntentManager {
    public static void setAlarmPendingIntent(Context context, Alarm alarm) {
        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        PendingIntent pendingIntent = createPendingIntent(context, alarm);
        Long time = alarm.getTime();
        if(alarm.isSnoozeOn()){
            time = alarm.getTimeAfterSnoozing();
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            manager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, time, pendingIntent);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
            manager.setExact(AlarmManager.RTC_WAKEUP, time, pendingIntent);
        else
            manager.set(AlarmManager.RTC_WAKEUP, time, pendingIntent);
    }

    public static void deleteAlarm(Context context, Alarm alarm) {
        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        PendingIntent pendingIntent = createPendingIntent(context, alarm);
        manager.cancel(pendingIntent);
    }

    private static PendingIntent createPendingIntent(Context context, Alarm alarm) {
        Intent intent = new Intent(context, AlarmBroadcastReciver.class);
        intent.setAction("startAlarm");
        intent.putExtra("alarmId", alarm.getId());
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, alarm.getPendingIntentRequestCode(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
        return pendingIntent;
    }
}
