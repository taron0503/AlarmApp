package com.example.alarmapp.models;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.LiveDataReactiveStreams;

import com.example.alarmapp.Alarm;
import com.example.alarmapp.AlarmRepository;

import java.util.List;

public class AlarmActivityViewModel extends AndroidViewModel {

    AlarmRepository alarmRepository;
    private LiveData<List<Alarm>> allAlarms;

    public AlarmActivityViewModel(@NonNull Application application) {
        super(application);

        alarmRepository = AlarmRepository.createInstance(application);
        allAlarms = LiveDataReactiveStreams.fromPublisher(alarmRepository.getAllAlarms());
    }

    public LiveData<List<Alarm>> getAllAlarms(){
        return  allAlarms;
    }

    public void deleteAlarm(Alarm alarm){
        alarmRepository.deleteAlarm(alarm);
    }
}
