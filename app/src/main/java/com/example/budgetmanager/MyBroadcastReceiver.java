package com.example.budgetmanager;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;


import androidx.core.app.NotificationCompat;

import java.util.Calendar;

import static android.content.Context.NOTIFICATION_SERVICE;

public class MyBroadcastReceiver extends BroadcastReceiver {
    public static MediaPlayer mp;
    @Override
    public void onReceive(Context context, Intent intent) {
        mp=MediaPlayer.create(context, R.raw.alarm);
        mp.start();

        NotificationManager notificationManager;
        intent=new Intent(context,ReminderActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(context, (int) System.currentTimeMillis(), intent, 0);
        final SharedPreferences sharedPreferences=context.getSharedPreferences("",Context.MODE_PRIVATE);

        String alarmDate=sharedPreferences.getString("alarmDate","");
        String alarmTime=sharedPreferences.getString("alarmTime","");

        Notification n = new NotificationCompat.Builder(context,"channelID")
                .setContentTitle("Reminder for Khums on "+alarmDate+" "+alarmTime)
                .setSmallIcon(R.drawable.shiabudget)
                .setContentIntent(pIntent)
                .setVibrate(new long[]{0,500,500,500,500})
                .setPriority(1)
                .build();

        notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(0, n);
        Log.d("myapp8", Calendar.getInstance().getTimeInMillis()+" "+sharedPreferences.getString("mili","0"));
        Toast.makeText(context, "Reminder for Khums", Toast.LENGTH_LONG).show();
    }
}