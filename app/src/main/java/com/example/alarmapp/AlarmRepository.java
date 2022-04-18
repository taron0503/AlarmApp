package com.example.alarmapp;

import android.content.Context;

import com.example.alarmapp.data.AlarmDao;
import com.example.alarmapp.data.AlarmsDataBase;

import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class AlarmRepository {
    private static AlarmRepository alarmRepository;
    AlarmDao alarmDao;
    Flowable<List<Alarm>> allAlarms;

    private AlarmRepository(Context context) {
        AlarmsDataBase db = AlarmsDataBase.getDatabase(context);;
        this.alarmDao = db.getAlarmDao();
        allAlarms = alarmDao.getAllAlarms();
    }

    public static AlarmRepository createInstance(Context context){
        if(alarmRepository == null){
            alarmRepository = new AlarmRepository(context);
        }
        return alarmRepository;
    }

    public Flowable<List<Alarm>> getAllAlarms(){
        return  allAlarms;
    }

    public Single<Long> addAlarm(Alarm alarm){
        return alarmDao.insertAlarm(alarm)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public void deleteAlarm(Alarm alarm){
        alarmDao.deleteAlarm(alarm)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();
    }
    public void updateAlarm(Alarm alarm){
        alarmDao.updateAlarm(alarm)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();
    }


    public Single<Alarm> getAlarmById(int alarmId){
        return alarmDao.getAlarmById(alarmId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

}
