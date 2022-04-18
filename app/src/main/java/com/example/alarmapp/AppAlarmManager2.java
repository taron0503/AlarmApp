package com.example.alarmapp;

import static android.content.Context.NOTIFICATION_SERVICE;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.CountDownTimer;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.example.alarmapp.activities.AlarmSignalActivity;
import com.example.alarmapp.receivers.AlarmBroadcastReciver;

import io.reactivex.rxjava3.core.Single;


public class AppAlarmManager2 {
    private static int ALARM_NOTIFICATION_ID = 1234;
    private static String CHANNEL_ID = "5555";
    private static CountDownTimer autoSnoozeTimer;
    private static NotificationManager mNotificationManager;
    private static MediaPlayer mediaPlayer;

    private static AlarmRepository alarmRepository;
    static Context context;

    public static void startAlarm(Context context, Intent intent) {
        AppAlarmManager2.context = context;
        alarmRepository = AlarmRepository.createInstance(context);

        initAutoSnoozeTimer(intent);
        int alarmId = intent.getIntExtra("alarmId", 0);
        mNotificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);

        //dissmissAlarm intent
        Intent dissmisIntent = new Intent(context, AlarmBroadcastReciver.class);
        dissmisIntent.putExtra("alarmId", alarmId);
        dissmisIntent.setAction("dismissAlarm");
        PendingIntent dismissPendingIntent = PendingIntent.getBroadcast(context, 112, dissmisIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        //snoozeAlarm intent
        Intent snoozeIntent = new Intent(context, AlarmBroadcastReciver.class);
        snoozeIntent.putExtra("alarmId", alarmId);
        snoozeIntent.setAction("snoozeAlarm");
        PendingIntent snoozePendingIntent = PendingIntent.getBroadcast(context, 114, snoozeIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        //Signal intent
        Intent signalIntent = new Intent(context, AlarmSignalActivity.class);
        signalIntent.putExtra("alarmId", alarmId);
        PendingIntent signalPendingIntent = PendingIntent.getActivity(context, 113, signalIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        getAlarmById(alarmId).subscribe(alarm -> {
            Uri soundUri = Uri.parse(alarm.getSound());

            createNotificationChanel(soundUri);
            startNotification(alarm,snoozePendingIntent,dismissPendingIntent,signalPendingIntent);
            startPlaySound(soundUri);
            autoSnoozeTimer.start();
        });


    }

    public static void dismissAlarm(Intent intent) {
        autoSnoozeTimer.cancel();
        cancelNotification();
        stopSound();

        int alarmId = intent.getIntExtra("alarmId", 0);

        getAlarmById(alarmId)
                .subscribe(
                        alarm -> {
                            alarmRepository.deleteAlarm(alarm);
                        },
                        throwable -> {
                            Log.d("error", throwable.getMessage());
                        }
                );

    }



    public static void snoozeAlarm(Intent intent) {
        autoSnoozeTimer.cancel();
        finishSignalAlarmActivity();
        cancelNotification();
        stopSound();

        int alarmId = intent.getIntExtra("alarmId", 0);


        getAlarmById(alarmId)
                .subscribe(alarm -> {
                    alarm.setSnoozeCount(alarm.getSnoozeCount() + 1);
                    if (alarm.getSnoozeCount() < alarm.getSnoozeCountMax()) {
                        alarm.setSnoozeOn(true);
                        alarmRepository.updateAlarm(alarm);
                        AlarmsPendingIntentManager.setAlarmPendingIntent(context, alarm);
                    } else  {
                        dismissAlarm(intent);
                    }
                });
    }

    private static void createNotificationChanel(Uri soundUri){
        NotificationChannel mChannel;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mChannel = new NotificationChannel(CHANNEL_ID, "CHANNEL_NAME", NotificationManager.IMPORTANCE_HIGH);
            mChannel.setLightColor(Color.GRAY);
            mChannel.enableLights(true);
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .build();
            mChannel.setSound(null, audioAttributes);

            if (mNotificationManager != null) {
                mNotificationManager.createNotificationChannel(mChannel);
            }
        }
    }

    private static void startNotification(Alarm alarm,
                                          PendingIntent snoozePendingIntent,
                                          PendingIntent dismissPendingIntent,
                                          PendingIntent signalPendingIntent){

        NotificationCompat.Builder status = new NotificationCompat.Builder(context, CHANNEL_ID);
        status
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.ic_baseline_alarm_24)
                .setContentTitle("Alarm")
                .setContentText(alarm.getName().isEmpty()?"Wake Up!":alarm.getName())
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setVibrate(new long[]{0, 500, 1000})
                .setDefaults(Notification.DEFAULT_ALL)
                .addAction(R.drawable.ic_baseline_cancel_24, "Snooze", snoozePendingIntent)
                .addAction(R.drawable.ic_baseline_cancel_24, "Dismiss", dismissPendingIntent)
                .setFullScreenIntent(signalPendingIntent, true);

        mNotificationManager.notify(ALARM_NOTIFICATION_ID, status.build());
    }

    private static void startPlaySound(Uri uri){
        mediaPlayer = MediaPlayer.create(context,uri);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();
    }

    private static void stopSound(){
        mediaPlayer.stop();
    }


    private static Single<Alarm> getAlarmById(int alarmId) {
        return alarmRepository.getAlarmById(alarmId);
    }


    private static void cancelNotification() {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(ALARM_NOTIFICATION_ID);
    }

    private static void finishSignalAlarmActivity(){
        Intent signal_intent = new Intent(AlarmSignalActivity.BROADCAST_ACTION);
        context.sendBroadcast(signal_intent);
    }

    private static void initAutoSnoozeTimer(Intent intent){
        autoSnoozeTimer =  new CountDownTimer(15000,1000) {
            @Override
            public void onTick(long l) { }

            @Override
            public void onFinish() {
                Log.d("autosnooze","autosnooze");
                snoozeAlarm(intent);
            }
        };
    }
}
