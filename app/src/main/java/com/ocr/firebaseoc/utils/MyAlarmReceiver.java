package com.ocr.firebaseoc.utils;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.widget.Button;
import android.widget.Toast;

import com.ocr.firebaseoc.R;

public class MyAlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "Le toast s'affiche toutes les 10 secondes", Toast.LENGTH_SHORT).show();
    }
}