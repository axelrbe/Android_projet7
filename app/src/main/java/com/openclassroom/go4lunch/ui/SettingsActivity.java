package com.openclassroom.go4lunch.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.openclassroom.go4lunch.R;

public class SettingsActivity extends AppCompatActivity {

    private FirebaseRemoteConfig firebaseRemoteConfig;
    private SharedPreferences sharedPreferences;
    private SwitchMaterial notificationSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        ImageButton arrowBack = findViewById(R.id.settings_arrow_back);
        arrowBack.setOnClickListener(v -> finish());

        firebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(3600)
                .build();
        firebaseRemoteConfig.setConfigSettingsAsync(configSettings);
        firebaseRemoteConfig.setDefaultsAsync(R.xml.remote_config_defaults);

        notificationSwitch = findViewById(R.id.settingSwitch);
        notificationSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> firebaseRemoteConfig.fetchAndActivate().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                boolean notificationsEnabled = firebaseRemoteConfig.getBoolean("notifications_enabled");
                if (notificationsEnabled) {
                    FirebaseMessaging.getInstance().subscribeToTopic("notifications");
                } else {
                    FirebaseMessaging.getInstance().unsubscribeFromTopic("notifications");
                }
            }
        }));

        Button saveButton = findViewById(R.id.settings_save);
        saveButton.setOnClickListener(view -> {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("notifications_enabled", notificationSwitch.isChecked());
            editor.apply();
            Toast.makeText(this, "Changes saved", Toast.LENGTH_SHORT).show();
        });

        sharedPreferences = getSharedPreferences("my_preferences", MODE_PRIVATE);
        boolean notificationsEnabled = sharedPreferences.getBoolean("notifications_enabled", true);
        notificationSwitch.setChecked(notificationsEnabled);
    }

}