package com.example.het3crab.healthband;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

/**
 * Created by aubuntu on 24.03.18.
 */

public class Notifications {
    private Context mainContext;

    public Notifications(Context context){
        mainContext = context;
    }

    public void showToast(String msg){
        Toast.makeText(mainContext, msg, Toast.LENGTH_LONG).show();
    }
}
