package com.example.alarmapp.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.alarmapp.AppAlarmManager;
import com.example.alarmapp.R;
import com.example.alarmapp.UI.DismissButtonView;
import com.example.alarmapp.models.AlarmSignalActivityViewModel;

public class AlarmSignalActivity extends AppCompatActivity {
    AlarmSignalActivityViewModel model;
    int alarmId;
    BroadcastReceiver broadcastReceiver;
    TextView alarmTimeTextView;
    Button snoozeButton;
    DismissButtonView dismissButtonView;

    public final static String BROADCAST_ACTION = "com.example.clockApp.AlarmSignalActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_signal);
        addFlags();
        init();
        setDatas();

        IntentFilter intentFilter = new IntentFilter(BROADCAST_ACTION);
        intentFilter.addAction(BROADCAST_ACTION);
        registerReceiver(broadcastReceiver,intentFilter);


        snoozeButton.setOnClickListener(view -> {
            snoozeAlarm();
        });


        dismissButtonView.setOnSwipeListener(() -> {
            dismissAlarm();
        });

    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(broadcastReceiver);
    }


    private void init(){
        Intent intent = getIntent();
        alarmId = intent.getIntExtra("alarmId",0);
        snoozeButton = findViewById(R.id.snoozeButton);
        dismissButtonView = findViewById(R.id.dismissButton);
        alarmTimeTextView = findViewById(R.id.alarmTimeTextView);
        model = new AlarmSignalActivityViewModel(getApplication());
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                AlarmSignalActivity.this.finish();
            }
        };
    }

    private void setDatas(){
        model.getAlarmById(alarmId)
                .subscribe(alarm -> {
                    alarmTimeTextView.setText(alarm.getTimeInFormat());
                });
    }

    private void dismissAlarm(){
        AppAlarmManager.dismissAlarm(getIntent());
        finish();
    }

    private void snoozeAlarm(){
        AppAlarmManager.snoozeAlarm(getIntent());
        finish();
    }

    private void addFlags(){
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED| WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON| WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN |
                        WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                        WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                        WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_FULLSCREEN |
                        WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                        WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                        WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
    }

}