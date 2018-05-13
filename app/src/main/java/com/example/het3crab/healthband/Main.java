package com.example.het3crab.healthband;

import android.app.NotificationChannel;
import android.content.Context;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Timer;
import java.util.TimerTask;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

public class Main extends AppCompatActivity {
    private int heartRate = 60;
    private Button toSettingButton;
    private TextView heartRateView;
    private Notifications mNotifications;
    private SmsManager smsManager = SmsManager.getDefault();

    public RealmResults<RealmPulseReading> pulses;
    public List<RealmPulseReading> pulsesToAdd = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startService(new Intent(this, TimeService.class));

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



        Timer myTimer = new Timer();
        myTimer.schedule(new TimerTask() {
            @Override
            public void run() {Main.this.runOnUiThread(new Runnable() {
                public void run() {
                    UpdateGUI();
                }
            });}
        }, 0, 15000);
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


    public static Date getDate(long milliSeconds) {
        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return calendar.getTime();
    }

    public static String getDate(double milliSeconds, String dateFormat)
    {
        // Create a DateFormatter object for displaying date in specified format.
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis((long) milliSeconds);
        return formatter.format(calendar.getTime());
    }

    void UpdateGUI(){
        Realm.init(this);
        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder().build();
        Realm realm = Realm.getInstance(realmConfiguration);

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                for(RealmPulseReading pulse : pulsesToAdd){
                    realm.insertOrUpdate(pulse);
                }
            }
        });

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                pulses = realm.where(RealmPulseReading.class).findAll();
                if(pulses.size()>0) {
                    heartRate = pulses.get(pulses.size() - 1).getValue();
                }
            }
        });

        setHeartRate(heartRate);

        DataPoint[] dataPoints = new DataPoint[pulses.size()];
        int x = 0;
        for(RealmPulseReading pulse : pulses){
            DataPoint dataPoint = new DataPoint(getDate(pulse.getDate()), (double)pulse.getValue());
            dataPoints[x] = dataPoint;
            x++;
        }

        GraphView graph = (GraphView) findViewById(R.id.graph);
        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(dataPoints);

        graph.addSeries(series);

        realm.close();
    }

    void createGraph(){
        GraphView graph = (GraphView) findViewById(R.id.graph);

        graph.getViewport().setYAxisBoundsManual(true);
        graph.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter() {
            @Override
            public String formatLabel(double value, boolean isValueX) {
                if (isValueX) {
                    // show date values
                    return getDate(value, "dd/MM HH:mm");
                } else {
                    // show normal y values
                    return super.formatLabel(value, isValueX);
                }
            }
        });

        graph.getGridLabelRenderer().setNumHorizontalLabels(3);
        graph.getViewport().setMinY(0);
        graph.getViewport().setMaxY(220);

        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMinX(150000);
        graph.getViewport().setMaxX(999999999);

        graph.getViewport().setScrollable(true); // enables horizontal scrolling
        graph.getViewport().setScrollableY(true); // enables vertical scrolling
        graph.getViewport().setScalable(true);
    }
}



