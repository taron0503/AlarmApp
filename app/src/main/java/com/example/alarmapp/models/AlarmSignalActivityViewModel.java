package com.example.alarmapp.models;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;

import com.example.alarmapp.Alarm;
import com.example.alarmapp.AlarmRepository;

import io.reactivex.rxjava3.core.Single;


public class AlarmSignalActivityViewModel extends AndroidViewModel {
    AlarmRepository alarmRepository;

    public AlarmSignalActivityViewModel(Application application) {
        super(application);
        alarmRepository = AlarmRepository.createInstance(application);
    }

    public Single<Alarm> getAlarmById(Integer alarmId){
        return alarmRepository.getAlarmById(alarmId);
    }
}
