package com.example.alarmapp.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.example.alarmapp.AppAlarmManager;
import com.example.alarmapp.R;
import com.example.alarmapp.databinding.ActivityAlarmSignalBinding;
import com.example.alarmapp.ui.DismissButtonView;
import com.example.alarmapp.models.AlarmSignalActivityViewModel;

public class AlarmSignalActivity extends AppCompatActivity {
    private AlarmSignalActivityViewModel model;
    private int alarmId;
    private BroadcastReceiver broadcastReceiver;
    private ActivityAlarmSignalBinding binding;

    public final static String BROADCAST_ACTION = "com.example.clockApp.AlarmSignalActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_alarm_signal);
//        setContentView(R.layout.activity_alarm_signal);
        addFlags();
        init();
        setBroadcastReceiver();
        setDatas();


        binding.snoozeButton.setOnClickListener(view -> {
            snoozeAlarm();
        });


        binding.dismissButton.setOnSwipeListener(() -> {
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
        model = new AlarmSignalActivityViewModel(getApplication());
    }

    private void setBroadcastReceiver(){
        IntentFilter intentFilter = new IntentFilter(BROADCAST_ACTION);
        intentFilter.addAction(BROADCAST_ACTION);
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                AlarmSignalActivity.this.finish();
            }
        };
        registerReceiver(broadcastReceiver,intentFilter);
    }

    private void setDatas(){
        model.getAlarmById(alarmId)
                .subscribe(alarm -> {
                    binding.alarmTimeTextView.setText(alarm.getTimeInFormat());
                });
    }

    private void dismissAlarm(){
        AppAlarmManager.dismissAlarm(alarmId);
        finish();
    }

    private void snoozeAlarm(){
        AppAlarmManager.snoozeAlarm(alarmId);
        finish();
    }

    private void addFlags(){
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN |
                        WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                        WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                        WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

    }

}