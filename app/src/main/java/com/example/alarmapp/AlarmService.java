package com.example.alarmapp;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;

import androidx.core.app.NotificationCompat;

import com.example.alarmapp.activities.AlarmSignalActivity;
import com.example.alarmapp.receivers.AlarmBroadcastReciver;

public class AlarmService extends Service {

    private  int ALARM_NOTIFICATION_ID = 143;
    private  String CHANNEL_ID = "5591";
    private  NotificationManager mNotificationManager;
    private  MediaPlayer mediaPlayer;

    private  AlarmRepository alarmRepository;
    Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        alarmRepository = AlarmRepository.createInstance(context);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

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

        alarmRepository.getAlarmById(alarmId).subscribe(alarm -> {
            Uri soundUri = Uri.parse(alarm.getSound());

            createNotificationChanel(soundUri);
            startNotification(alarm,snoozePendingIntent,dismissPendingIntent,signalPendingIntent);
            startPlaySound(soundUri);
        });

        return super.onStartCommand(intent, flags, startId);
    }


    private void createNotificationChanel(Uri soundUri){
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

    private void startNotification(Alarm alarm,
                                          PendingIntent snoozePendingIntent,
                                          PendingIntent dismissPendingIntent,
                                          PendingIntent signalPendingIntent){

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID);
        builder
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.ic_baseline_alarm_24)
                .setContentTitle("Alarm")
                .setContentText(alarm.getName().isEmpty()?"Wake Up!":alarm.getName())
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setOngoing(true)
                .setCategory(NotificationCompat.CATEGORY_CALL)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setVibrate(new long[]{0, 500, 1000})
                .setDefaults(Notification.DEFAULT_ALL)
                .addAction(R.drawable.ic_baseline_cancel_24, "Snooze", snoozePendingIntent)
                .addAction(R.drawable.ic_baseline_cancel_24, "Dismiss", dismissPendingIntent)
                .setFullScreenIntent(signalPendingIntent, true);

        Notification notification = builder.build();
        startForeground(1,notification);
//        mNotificationManager.notify(ALARM_NOTIFICATION_ID, builder.build());
    }

    private void startPlaySound(Uri uri){
        mediaPlayer = MediaPlayer.create(context,uri);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        mediaPlayer.stop();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
//        throw new UnsupportedOperationException("Not yet implemented");
        return null;
    }
}