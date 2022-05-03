package com.example.alarmapp;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.CountDownTimer;
import android.util.Log;

import com.example.alarmapp.activities.AlarmSignalActivity;


public class AppAlarmManager {

    private static CountDownTimer autoSnoozeTimer;
    private static AlarmRepository alarmRepository;
    static Context context;

    public static void startAlarm(Context context, int alarmId) {
        AppAlarmManager.context = context;
        alarmRepository = AlarmRepository.createInstance(context);
        Intent alarmServiceIntent = new Intent(context,AlarmService.class);

        //int alarmId = alarmId.getIntExtra("alarmId", 0);
        alarmServiceIntent.putExtra("alarmId",alarmId);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(alarmServiceIntent);
        }else {
            context.startService(alarmServiceIntent);
        }

        initAutoSnoozeTimer(alarmId);
        autoSnoozeTimer.start();

    }

    public static void dismissAlarm(int alarmId) {
        autoSnoozeTimer.cancel();
        Intent stopAlarmServiceIntent = new Intent(context,AlarmService.class);
        context.stopService(stopAlarmServiceIntent);

       // int alarmId = intent.getIntExtra("alarmId", 0);

        alarmRepository.getAlarmById(alarmId)
                .subscribe(
                        alarm -> {
                            alarmRepository.deleteAlarm(alarm);
                        },
                        throwable -> {
                            Log.d("error", throwable.getMessage());
                        }
                );

    }



    public static void snoozeAlarm(int alarmId) {
        autoSnoozeTimer.cancel();
        finishSignalAlarmActivity();
        Intent stopAlarmServiceIntent = new Intent(context,AlarmService.class);
        context.stopService(stopAlarmServiceIntent);

        //int alarmId = intent.getIntExtra("alarmId", 0);


        alarmRepository.getAlarmById(alarmId)
                .subscribe(alarm -> {
                    alarm.setSnoozeCount(alarm.getSnoozeCount() + 1);
                    if (alarm.getSnoozeCount() < alarm.getSnoozeCountMax()) {
                        alarm.setSnoozeOn(true);
                        alarmRepository.updateAlarm(alarm);
                        AlarmsPendingIntentManager.setAlarmPendingIntent(context, alarm);
                    } else  {
                        dismissAlarm(alarmId);
                    }
                });
    }


    private static void finishSignalAlarmActivity(){
        Intent signal_intent = new Intent(AlarmSignalActivity.BROADCAST_ACTION);
        context.sendBroadcast(signal_intent);
    }

    private static void initAutoSnoozeTimer(int alarmId){
        autoSnoozeTimer =  new CountDownTimer(10000,1000) {
            @Override
            public void onTick(long l) { }

            @Override
            public void onFinish() {
               // Log.d("autosnooze","autosnooze");
                snoozeAlarm(alarmId);
            }
        };
    }
}
