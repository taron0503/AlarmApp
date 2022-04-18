package com.example.alarmapp.data;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.alarmapp.Alarm;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;

//import io.reactivex.Completable;
//import io.reactivex.Flowable;
//import io.reactivex.Single;

@Dao
public interface AlarmDao {

    @Insert
    Single<Long> insertAlarm(Alarm alarm);

    @Update
    Completable updateAlarm(Alarm alarm);

    @Delete
    Completable deleteAlarm(Alarm alarmt);

    @Query("select * from alarms order by time")
    Flowable<List<Alarm>> getAllAlarms();

    @Query("SELECT * FROM alarms WHERE id = :id")
    Single<Alarm> getAlarmById(int id);

}
