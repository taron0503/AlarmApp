package com.example.alarmapp.activities;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TimePicker;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.alarmapp.Alarm;
import com.example.alarmapp.R;
import com.example.alarmapp.databinding.ActivityAddAndEditAlarmBinding;
import com.example.alarmapp.models.AddAndEditAlarmViewModel;

public class AddAndEditAlarmActivity extends AppCompatActivity {
    private int RINGTONE_PICKER_REQUEST_CODE = 5;
    private AddAndEditAlarmViewModel model;
    ActivityAddAndEditAlarmBinding binding;

    private TimePicker alarmTimePicker;
    private Button alarmSaveButton;
    private Button alarmCancelButton;
    private NumberPicker secondsNumberPicker;
    private LinearLayout alarmSoundLayout;


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_and_edit_alarm);
//        AddAndEditAlarmActivityClickHandlers addAndEditAlarmActivityClickHandlers = new AddAndEditAlarmActivityClickHandlers();
//        binding.setClickHandlers(addAndEditAlarmActivityClickHandlers);
        init();
        setClickHandlers();
        setObservers();
        configureSeconds();
    }




    private void init() {
        Intent intent = getIntent();
        int alarmId = intent.getIntExtra("alarmId", 0);
        boolean updateMode = intent.getBooleanExtra("update", false);

        model = new ViewModelProvider
                .AndroidViewModelFactory(getApplication())
                .create(AddAndEditAlarmViewModel.class);
        model.setUpdateModeOn(updateMode);
        model.setAlarm(alarmId);

        alarmSaveButton = findViewById(R.id.alarmSaveButton);
        alarmCancelButton = findViewById(R.id.alarmCancelButton);
        secondsNumberPicker = findViewById(R.id.numberPicker);
        alarmTimePicker = findViewById(R.id.alarmTimePicker);
        alarmSoundLayout = findViewById(R.id.alarmSoundLayout);

        alarmTimePicker.setIs24HourView(true);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RINGTONE_PICKER_REQUEST_CODE && resultCode == RESULT_OK) {
            Uri uri = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
            if (uri != null) {
                model.setSound(uri.toString());
            }
        }
    }

    private void setClickHandlers() {
        alarmSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                model.onSave();
                finish();
            }
        });

        alarmCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });

        alarmSoundLayout.setOnClickListener(view -> {
            Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_ALL);
            startActivityForResult(intent, RINGTONE_PICKER_REQUEST_CODE);
        });
    }

    private void setObservers() {
        model.getMutableAlarm().observe(this, new Observer<Alarm>() {
            @Override
            public void onChanged(Alarm alarm) {
                binding.setViewModel(model);
            }
        });

        model.getShowDatePickerDialog().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean show) {
                if(show){
                    callDatePicker();
                }
            }
        });
    }


    public void callDatePicker(){
        DatePickerDialog.OnDateSetListener d=new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                model.setDate(year,monthOfYear,dayOfMonth);
            }
        };

        Alarm alarm = model.getMutableAlarm().getValue();
        DatePickerDialog datePickerDialog = new DatePickerDialog(AddAndEditAlarmActivity.this,
                //R.style.DatePickerDialogStyle,
                d,
                alarm.getYear(),
                alarm.getMonth(),
                alarm.getDayOfMonth());

        datePickerDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                model.setUpdateModeOn(false);
            }
        });
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
        datePickerDialog.show();
    }


    private void configureSeconds(){
        secondsNumberPicker.setMinValue(0);
        secondsNumberPicker.setMaxValue(59);
        secondsNumberPicker.setFormatter(new NumberPicker.Formatter() {
            @Override
            public String format(int value) {
                return value<10?"0"+value:""+value;
            }
        });
    }

//    public class AddAndEditAlarmActivityClickHandlers{
//        public void onSave(View view){
//            model.onSave();
//            finish();
//        }
//
//        public void onCancel(View view){
//            setResult(RESULT_CANCELED);
//            finish();
//        }
//    }

}