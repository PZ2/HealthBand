package com.example.het3crab.healthband;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ToggleButton;

public class Settings extends AppCompatActivity {
    private final String APP = "com.example.het3crab.healthband";
    private final String ALERTS_KEY = "com.example.het3crab.healthband.alerts";
    private final String PULSE_FREQ_KEY = "com.example.het3crab.healthband.pulsefreq";
    private final String STEP_KEY = "com.example.het3crab.healthband.stepcount";

    private boolean isAlertOn = false;
    private boolean isStepCountOn = false;
    private int pulseFreqVal = 60;
    
    private EditText pulseFreqText;
    private Button saveButton;
    private ToggleButton alerts;
    private ToggleButton steps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        pulseFreqText = findViewById(R.id.pulseFreq);
        alerts = (ToggleButton) findViewById(R.id.alerts);
        saveButton = (Button) findViewById(R.id.saveButton);
        steps = findViewById(R.id.steps);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveSettings();
            }
        });

        readSettings();

        //update settings view
        pulseFreqText.setText(String.valueOf(pulseFreqVal));
        alerts.setChecked(isAlertOn);
        steps.setChecked(isStepCountOn);
    }

    public void readSettings(){
        SharedPreferences prefs = this.getSharedPreferences(
                APP , Context.MODE_PRIVATE);

        isAlertOn = prefs.getBoolean(ALERTS_KEY, false);
        pulseFreqVal = prefs.getInt(PULSE_FREQ_KEY, 60);
        isStepCountOn = prefs.getBoolean(STEP_KEY, false);
    }

    private void saveSettings() {
        pulseFreqVal = Integer.parseInt(pulseFreqText.getText().toString());
        isAlertOn = alerts.isChecked();
        isStepCountOn = steps.isChecked();

        SharedPreferences prefs = this.getSharedPreferences(
                APP , Context.MODE_PRIVATE);

        prefs.edit().putBoolean(ALERTS_KEY, isAlertOn).apply();
        prefs.edit().putInt(PULSE_FREQ_KEY, pulseFreqVal).apply();
        prefs.edit().putBoolean(STEP_KEY, isStepCountOn).apply();
    }

    public int getPulseFreqVal(){
        return pulseFreqVal;
    }

    public boolean getIsAlertOn(){
        return isAlertOn;
    }

    public boolean getIsStepCountOn(){
        return isStepCountOn;
    }
}
