package com.example.het3crab.healthband;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
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
    
    /*
    	context - activity to open after clicking on notification
    	action - TAG for receiver
    */
    public void showNotification(String title, String msg, String actionName, Class<?> activity){
		// Tapping the Notification will open up MainActivity
		Intent i = new Intent(mainContext, activity);
		// an action to use later
		// defined as an app constant:
		// public static final String MESSAGE_CONSTANT = "com.example.myapp.notification"
		//i.setAction(MainActivity.MESSAGE_CONSTANT);
		i.setAction(actionName);
		i.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_SINGLE_TOP);
		
		PendingIntent notificationIntent = PendingIntent.getActivity(mainContext, 999, i,
		PendingIntent.FLAG_UPDATE_CURRENT);
		NotificationCompat.Builder builder = new NotificationCompat.Builder(mainContext.getApplicationContext());
		builder.setContentIntent(notificationIntent);
		builder.setAutoCancel(true);
		builder.setLargeIcon(BitmapFactory.decodeResource(mainContext.getResources(),
															android.R.drawable.ic_menu_view));
		builder.setSmallIcon(android.R.drawable.ic_dialog_map);
		builder.setContentText(msg);
		builder.setTicker(msg);
		builder.setContentTitle(title);
		
		// set high priority for Heads Up Notification
		builder.setPriority(NotificationCompat.PRIORITY_HIGH);
		builder.setVisibility(NotificationCompat.VISIBILITY_PUBLIC);
		
		// It won't show "Heads Up" unless it plays a sound
		if (Build.VERSION.SDK_INT >= 21) builder.setVibrate(new long[0]);
		NotificationManager mNotificationManager =
			(NotificationManager)mainContext.getSystemService(Context.NOTIFICATION_SERVICE);
		mNotificationManager.notify(999, builder.build());
	}
}
