package com.example.het3crab.healthband;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.icu.text.SimpleDateFormat;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class TimeService extends Service {
    private final String APP = "com.example.het3crab.healthband";
    private final String PULSE_FREQ_KEY = "com.example.het3crab.healthband.pulsefreq";
    int pulseFreq;

    // run on another Thread to avoid crash
    private Handler mHandler = new Handler();
    // timer handling
    private Timer mTimer = null;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        Intent notificationIntent = new Intent(this, Main.class);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0);

        Notification notification = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("My Awesome App")
                .setContentText("Doing some work...")
                .setContentIntent(pendingIntent).build();

        startForeground(1337, notification);

        pulseFreqUpdate();

        // cancel if already existed
        if (mTimer != null) {
            mTimer.cancel();
        } else {
            // recreate new
            mTimer = new Timer();
        }
        // schedule task
        mTimer.scheduleAtFixedRate(new TimeDisplayTimerTask(), 0, pulseFreq * 1000);
    }

    public void pulseFreqUpdate(){
        SharedPreferences prefs = this.getSharedPreferences(APP , Context.MODE_PRIVATE);
        pulseFreq = prefs.getInt(PULSE_FREQ_KEY, 60);
    }

    public void ifFreqChange(){
        SharedPreferences prefs = this.getSharedPreferences(APP , Context.MODE_PRIVATE);
        if (prefs.getInt(PULSE_FREQ_KEY, 60) != pulseFreq) {
            mTimer.cancel();
            mTimer = new Timer();
            pulseFreq = prefs.getInt(PULSE_FREQ_KEY, 60);
            mTimer.scheduleAtFixedRate(new TimeDisplayTimerTask(), 0, pulseFreq * 1000);
        }

    }

    class TimeDisplayTimerTask extends TimerTask {

        @Override
        public void run() {
            // run on another thread
            mHandler.post(new Runnable() {

                @Override
                public void run() {
                    // display toast
                    ifFreqChange();
                    Toast.makeText(getApplicationContext(), getDateTime(),
                            Toast.LENGTH_SHORT).show();
                }

            });
        }

        private String getDateTime() {
            // get date time in custom format
            SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm");
            return sdf.format(new Date());
        }

    }
}
