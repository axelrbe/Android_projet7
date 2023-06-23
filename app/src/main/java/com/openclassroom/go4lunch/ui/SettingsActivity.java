package com.openclassroom.go4lunch.ui;

import android.os.Bundle;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.openclassroom.go4lunch.R;

public class SettingsActivity extends AppCompatActivity {

    ImageButton arrowBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        arrowBack = findViewById(R.id.settings_arrow_back);
        arrowBack.setOnClickListener(v -> finish());
    }

}