package com.example.alarmapp.activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.alarmapp.Alarm;
import com.example.alarmapp.AlarmAdapter;
import com.example.alarmapp.AlarmsPendingIntentManager;
import com.example.alarmapp.R;
import com.example.alarmapp.models.AlarmActivityViewModel;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private AlarmActivityViewModel model;
    private RecyclerView alarmRecyclerView;
    private AlarmAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    private Button addAlarmButton;
    private ArrayList<Alarm> alarmArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        addAlarmButton = findViewById(R.id.addAlarmButton);

        alarmRecyclerView = findViewById(R.id.alarmRecyclerView);
        alarmRecyclerView.setHasFixedSize(true);

//         model = new ViewModelProvider(this).get(AlarmActivityViewModel.class);
        model = new ViewModelProvider
                .AndroidViewModelFactory(getApplication())
                .create(AlarmActivityViewModel.class);
        alarmArrayList = (ArrayList<Alarm>) model.getAllAlarms().getValue();
        if(alarmArrayList == null){
            alarmArrayList = new ArrayList<>();
        }

//        adapter = new NoteAdapter();
        adapter = new AlarmAdapter(this, new AlarmAdapter.DeleteEditAlarmInterface() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void deleteAlarm(Alarm alarm) {
                MainActivity.this.deleteAlarm(alarm);
            }

            @Override
            public void editAlarm(Alarm alarm) {
                Intent intent = new Intent(getApplicationContext(), AddAndEditAlarmActivity.class);
                intent.putExtra("update",true);
                intent.putExtra("alarmId",alarm.getId());
                startActivity(intent);
            }
        });

        model.getAllAlarms().observe(this, new Observer<List<Alarm>>() {
            @Override
            public void onChanged(List<Alarm> alarms) {
                alarmArrayList = (ArrayList<Alarm>) alarms;
//                if(alarms.size()>0)
                    adapter.submitList(alarms);
            }
        });


        layoutManager = new LinearLayoutManager(this);

        alarmRecyclerView.setAdapter(adapter);
        alarmRecyclerView.setLayoutManager(layoutManager);

        addAlarmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), AddAndEditAlarmActivity.class);
                startActivity(intent);
            }
        });

    }

    private void deleteAlarm(Alarm alarm){
        AlarmsPendingIntentManager.deleteAlarm(this,alarm);
        model.deleteAlarm(alarm);
    }
}