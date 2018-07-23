package com.example.jan.praca;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v7.app.NotificationCompat;

import static android.content.Context.MODE_PRIVATE;



public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("Alarm", MODE_PRIVATE);
        boolean enable = sharedPreferences.getBoolean("enable", false);
        boolean withSound = sharedPreferences.getBoolean("soundAlarm",true);
        boolean withVibrate = sharedPreferences.getBoolean("withVibrate",false);
        if (enable) {
            Utils.loadLocale(context);
            long when = System.currentTimeMillis();
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            Intent notificationIntent = new Intent(context, Camera.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            NotificationCompat.Builder builder = (NotificationCompat.Builder) new NotificationCompat.Builder(context)
                    .setSmallIcon(R.drawable.ic_add_a_photo_black_24dp)
                    .setContentTitle("Daily Photo")
                    .setContentText(context.getString(R.string.alarm_description))
                    .setAutoCancel(true)
                    .setWhen(when)
                    .setContentIntent(pendingIntent);
            if(withVibrate) builder.setVibrate(new long[]{1000, 1000, 1000, 1000, 1000});
            if(withSound) builder.setSound(alarmSound);
            notificationManager.notify(0, builder.build());
        }
    }
}
