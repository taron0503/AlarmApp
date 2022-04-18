package com.example.alarmapp;


import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.text.SimpleDateFormat;
import java.util.Calendar;

@Entity(tableName = "alarms")
public class Alarm extends BaseObservable{
    @PrimaryKey(autoGenerate = true)
    private int id;
    private long time;
    private String name = "";
    @Ignore
    Calendar calendar = Calendar.getInstance();
    //    private int hour;
//    private int minutes;
//    private int seconds;
    private int pendingIntentRequestCode;
    private int snoozeTime = 20;
    private int snoozeCount = 0;
    private int snoozeCountMax = 3;
    private boolean snoozeOn = false;
    private String timePattern = "HH:mm:ss";

    private String sound;


    public Alarm(long time) {
        this.time = time;
        this.pendingIntentRequestCode = (int) (time/1000);
//        Uri uri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//        this.setSound(uri.toString());
    }

    public String getTimeInFormat(){
        SimpleDateFormat sdf = new SimpleDateFormat(timePattern);
        String ftime = sdf.format(time);
        return ftime;
    }

    public String getTimeInFormat(String pattern){
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        String ftime = sdf.format(time);
        return ftime;
    }

    public Long getTimeAfterSnoozing(){
        return this.time + this.snoozeCount * this.snoozeTime * 1000;
    }

    public String getTimeInFormtAfterSnoozing(){
        SimpleDateFormat sdf = new SimpleDateFormat(timePattern);
        String sTime = sdf.format(getTimeAfterSnoozing());
        return sTime;
    }

    public void cancelSnooze(){
        this.snoozeOn = false;
        this.snoozeCount = 0;
    }


    @Bindable
    public int getSeconds() {
        return getTime(Calendar.SECOND);
    }

    public void setSeconds(int seconds) {
        setTime(Calendar.SECOND,seconds);
    }

    @Bindable
    public int getMinute() {
        return getTime(Calendar.MINUTE);
    }

    public void setMinute(int minute) {
        setTime(Calendar.MINUTE,minute);
    }


    @Bindable
    public int getHour() {
        return getTime(Calendar.HOUR_OF_DAY);
    }

    public void setHour(int hour) {
        setTime(Calendar.HOUR_OF_DAY,hour);
    }

    @Bindable
    public int getDayOfMonth() {
        return getTime(Calendar.DAY_OF_MONTH);
    }

    public void setDayOfMonth(int dayOfMonth) {
        setTime(Calendar.DAY_OF_MONTH,dayOfMonth);
    }

    @Bindable
    public int getMonth() {
        return getTime(Calendar.MONTH);
    }

    public void setMonth(int month) {
        setTime(Calendar.MONTH,month);
    }

    @Bindable
    public int getYear() {
        return getTime(Calendar.YEAR);
    }

    public void setYear(int year) {
        setTime(Calendar.YEAR,year);
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Bindable
    public long getTime() {
        return time;
    }

    private int getTime(int type){
        calendar.setTimeInMillis(this.time);
        return calendar.get(type);
    }

    public void setTime(long time) {
        this.time = time;
        notifyPropertyChanged(BR.time);
    }

    private void setTime(int type, int time){
        calendar.setTimeInMillis(this.time);
        calendar.set(type,time);
        this.time = calendar.getTimeInMillis();
        notifyPropertyChanged(BR.time);
    }

    @Bindable
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        notifyPropertyChanged(BR.name);
    }

    public int getPendingIntentRequestCode() {
        return pendingIntentRequestCode;
    }

    public void setPendingIntentRequestCode(int pendingIntentRequestCode) {
        this.pendingIntentRequestCode = pendingIntentRequestCode;
    }

    public String getTimePattern() {
        return timePattern;
    }

    public void setTimePattern(String timePattern) {
        this.timePattern = timePattern;
    }

    public int getSnoozeTime() {
        return snoozeTime;
    }

    public void setSnoozeTime(int snoozeTime) {
        this.snoozeTime = snoozeTime;
    }

    public int getSnoozeCount() {
        return snoozeCount;
    }

    public void setSnoozeCount(int snoozeCount) {
        this.snoozeCount = snoozeCount;
    }

    public boolean isSnoozeOn() {
        return snoozeOn;
    }

    public void setSnoozeOn(boolean snoozeOn) {
        this.snoozeOn = snoozeOn;
    }

    public int getSnoozeCountMax() {
        return snoozeCountMax;
    }

    public void setSnoozeCountMax(int snoozeCountMax) {
        this.snoozeCountMax = snoozeCountMax;
    }

    public String getSound() {
        return sound;
    }

    public void setSound(String sound) {
        this.sound = sound;
    }
}
