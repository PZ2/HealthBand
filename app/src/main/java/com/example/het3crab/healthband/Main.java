package com.example.het3crab.healthband;

import android.app.NotificationChannel;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class Main extends AppCompatActivity {
    private int heartRate = 120;
    private Button toSettingButton;
    private TextView heartRateView;
    private Notifications mNotifications;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        heartRateView = (TextView) findViewById(R.id.heartRate);
        toSettingButton = (Button) findViewById(R.id.toSettingButton);

        setHeartRate(heartRate);

        toSettingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toSettings();
            }
        });

        mNotifications = new Notifications(this);
        mNotifications.showNotification("MY NEW TITLE", "MY NEW MESSAGE",
                                        "com.example.het3crab.healthband.notification",
                                        MiBandActivity.class);
    }

    public void toSettings() {
        Intent Settings = new Intent(this, Settings.class);
        startActivity(Settings);
    }

    public void setHeartRate(int heartRate) {
        heartRateView.setText(String.valueOf(heartRate));
    }

    public void onClick3(View view) {
        Intent intent = new Intent(this, MiBandActivity.class);
        startActivity(intent);
    }
}

