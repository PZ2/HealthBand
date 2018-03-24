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
    private int nID = 0;
    private Context mainContext;
    private NotificationManager mNotificationManager;

    private static String CHANNEL_ID = "";

    public Notifications(Context context){
        mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mainContext = context;

        if(CHANNEL_ID.equals(""))
            getChannel();
    }

    public void showToast(String msg){
        Toast.makeText(mainContext, msg, Toast.LENGTH_LONG).show();
    }

    public int notify(String title, String msg){
        nID += 1;
        Notification n = new NotificationCompat.Builder(mainContext, CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(msg)
                .build();
        //add .setSmallIcon(R.drawable.notification)

        mNotificationManager.notify(nID, n);

        return nID;
    }

    public void deleteNotification(int id){
        mNotificationManager.cancel(id);
    }

    private void getChannel(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = mainContext.getString(R.string.channel_name);
            String description = mainContext.getString(R.string.channel_description);

            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system
            mNotificationManager.createNotificationChannel(channel);
        }
    }
}
