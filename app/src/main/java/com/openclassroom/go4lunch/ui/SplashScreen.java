package com.openclassroom.go4lunch.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

@SuppressLint("CustomSplashScreen")
public class SplashScreen extends AppCompatActivity {
    @Override
    protected  void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        new Handler().postDelayed(() -> startActivity(new Intent(SplashScreen.this, MainActivity.class)), 2000);
    }
}
