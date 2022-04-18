package com.example.alarmapp.models;

import android.app.Application;
import android.media.RingtoneManager;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.alarmapp.Alarm;
import com.example.alarmapp.AlarmRepository;
import com.example.alarmapp.AlarmsPendingIntentManager;

import io.reactivex.rxjava3.core.Single;


public class AddAndEditAlarmViewModel extends AndroidViewModel {
    private AlarmRepository alarmRepository;
    private MutableLiveData<Alarm> mutableAlarm = new MutableLiveData<>();
    private MutableLiveData<Boolean> showDatePickerDialog = new MutableLiveData<>(false);
    boolean updateModeOn;

    public AddAndEditAlarmViewModel(@NonNull Application application) {
        super(application);
        alarmRepository = AlarmRepository.createInstance(application);
    }

    public void onSave(){
        if(updateModeOn){
            updateAlarm();
        }else{
            addAlarm();
        }
    }

    public void addAlarm(){
        //Alarm aa = mutableAlarm.getValue();

        alarmRepository.addAlarm(mutableAlarm.getValue())
                .flatMap(aLong -> alarmRepository.getAlarmById(aLong.intValue()))
                .subscribe(addedAlarm -> AlarmsPendingIntentManager.setAlarmPendingIntent(getApplication(), addedAlarm));
    }

    public void updateAlarm(){
        Alarm alarm = mutableAlarm.getValue();
        alarm.cancelSnooze();
        //AlarmsPendingIntentManager.setAlarmPendingIntent(getApplication(), alarm);
        alarmRepository.updateAlarm(alarm);
    }




    public void setAlarm(int alarmId){
        if(isUpdateModeOn()){
            Single<Alarm> singleAlarm = alarmRepository.getAlarmById(alarmId);
            singleAlarm.subscribe(alarm -> {
                mutableAlarm.setValue(alarm);
            });
        }else{
            Alarm newAlarm = new Alarm(System.currentTimeMillis());
            Uri uri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            newAlarm.setSound(uri.toString());
            mutableAlarm.setValue(newAlarm);
        }

    }



    public void setDate(int year, int monthOfYear, int dayOfMonth) {
        Alarm alarm = mutableAlarm.getValue();
        alarm.setDayOfMonth(dayOfMonth);
        alarm.setMonth(monthOfYear);
        alarm.setYear(year);
        mutableAlarm.setValue(alarm);
    }

    public void setSound(String sound){
        Alarm alarm = mutableAlarm.getValue();
        alarm.setSound(sound);
        mutableAlarm.setValue(alarm);
    }


    public MutableLiveData<Alarm> getMutableAlarm() {
        return mutableAlarm;
    }



    public boolean isUpdateModeOn() {
        return updateModeOn;
    }

    public void setUpdateModeOn(boolean updateModeOn) {
        this.updateModeOn = updateModeOn;
    }

    public MutableLiveData<Boolean> getShowDatePickerDialog() {
        return showDatePickerDialog;
    }

    public void setShowDatePickerDialog(boolean show) {
        this.showDatePickerDialog.setValue(show);
    }
}
