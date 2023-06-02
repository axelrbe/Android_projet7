package com.openclassroom.go4lunch.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.openclassroom.go4lunch.R;

public class SettingsActivity extends AppCompatActivity {

    ImageButton arrowBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        arrowBack = findViewById(R.id.settings_arrow_back);
        arrowBack.setOnClickListener(v -> {
            finish();
        });
    }

}