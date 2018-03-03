package com.example.het3crab.healthband;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ToggleButton;

public class Settings extends AppCompatActivity {
    private Button toMainButton;
    private ToggleButton alerts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        alerts = (ToggleButton) findViewById(R.id.alerts);
        toMainButton = (Button) findViewById(R.id.toMainButton);
        toMainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toMain();
            }
        });
        alerts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alerts();
            }
        });
    }

    public void toMain() {
        Intent Main = new Intent(this, Main.class);
        startActivity(Main);
    }

    public void alerts() {

    }


}
