package com.example.het3crab.healthband;

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

        Notifications not = new Notifications(Main.this);
        not.notify("TEST TITLE", "MSG TEXT");
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

