package com.ocr.firebaseoc.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.ocr.firebaseoc.R;

import java.util.Objects;


public class LoadingActivity extends AppCompatActivity {
    private static int SPLASH_TIME_OUT = 1500; // Temps d'affichage de l'écran de démarrage en ms

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        Objects.requireNonNull(getSupportActionBar()).hide();

        new Handler().postDelayed(() -> {
            Intent homeIntent = new Intent(LoadingActivity.this, MainActivity.class);
            startActivity(homeIntent);
            finish();
        }, SPLASH_TIME_OUT);
    }
}
