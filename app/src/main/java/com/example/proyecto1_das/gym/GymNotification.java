package com.example.proyecto1_das.gym;

import static androidx.core.content.ContextCompat.getSystemService;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.example.proyecto1_das.R;

public class GymNotification extends BroadcastReceiver {

    private static final String CHANNEL_ID = "pock_rout";

    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationManager notificationManager =
                getSystemService(context, NotificationManager.class);
        NotificationChannel notificationChannel = notificationManager
                .getNotificationChannel(CHANNEL_ID);

        String gym;
        if (intent.getExtras() != null) {
            gym = intent.getExtras().getString("gymName");
        } else {
            gym = "null";
        }

        if (notificationChannel != null) {
            NotificationCompat.Builder elBuilder =
                    new NotificationCompat.Builder(context, "pock_rout");
            elBuilder.setSmallIcon(R.drawable.ic_launcher)
                    .setContentTitle(context.getString(R.string.reminder_gym))
                    .setContentText(context.getString(R.string.remember_call_text) + gym)
                    .setVibrate(new long[]{0, 1000, 500, 1000})
                    .setAutoCancel(true);

            notificationManager.notify(2, elBuilder.build());
        }
    }
}
