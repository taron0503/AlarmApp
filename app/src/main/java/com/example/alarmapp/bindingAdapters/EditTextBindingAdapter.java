package com.example.alarmapp.bindingAdapters;

import android.net.Uri;
import android.widget.TextView;

import androidx.databinding.BindingAdapter;

public class EditTextBindingAdapter {
    @BindingAdapter({"app:soundTitle"})
    public static void soundTitle(TextView textView, String soundPath) {
        if(soundPath != null) {
            Uri soundUri = Uri.parse(soundPath);
            String title = soundUri.getQueryParameter("title");
            textView.setText(title != null ? title : "Default Sound");
        }

    }
}
