package com.example.alarmapp.data;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.alarmapp.Alarm;

@Database(entities = {Alarm.class},version = 1)
public abstract class AlarmsDataBase extends RoomDatabase {
    private static AlarmsDataBase INSTANCE;

    public static synchronized  AlarmsDataBase getDatabase(Context context) {
        if (INSTANCE == null) {
            INSTANCE =
                    Room.databaseBuilder(context.getApplicationContext(), AlarmsDataBase.class, "AlarmsDb")
                            .allowMainThreadQueries()
                            .build();
        }
        return INSTANCE;
    }
    public abstract AlarmDao getAlarmDao();
}
