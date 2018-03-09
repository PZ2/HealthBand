package com.example.het3crab.healthband;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ToggleButton;

public class Settings extends AppCompatActivity {
    private final String APP = "com.example.het3crab.healthband";
    private final String ALERTS_KEY = "com.example.het3crab.healthband.alerts";
    private final String PULSE_FREQ_KEY = "com.example.het3crab.healthband.pulsefreq";

    private boolean isAlertOn = false;
    private int pulseFreq = 60;

    private Button saveButton;
    private ToggleButton alerts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        alerts = (ToggleButton) findViewById(R.id.alerts);
        saveButton = (Button) findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveSettings();
            }
        });
        alerts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alerts();
            }
        });

        readSettings();
    }

    private void readSettings(){
        SharedPreferences prefs = this.getSharedPreferences(
                APP , Context.MODE_PRIVATE);

        isAlertOn = prefs.getBoolean(ALERTS_KEY, false);
        pulseFreq = prefs.getInt(PULSE_FREQ_KEY, 60);
    }

    private void saveSettings() {
        SharedPreferences prefs = this.getSharedPreferences(
                APP , Context.MODE_PRIVATE);

        prefs.edit().putBoolean(APP, isAlertOn).apply();
        prefs.edit().putInt(APP, pulseFreq).apply();

        Intent Main = new Intent(this, Main.class);
        startActivity(Main);
    }

    private void alerts() {

    }


}
