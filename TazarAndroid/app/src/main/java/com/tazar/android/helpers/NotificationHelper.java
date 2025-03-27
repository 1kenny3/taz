package com.tazar.android.helpers;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.tazar.android.R;
import com.tazar.android.ui.MainActivity;

public class NotificationHelper {

    private static final String POINTS_CHANNEL_ID = "points_channel";
    private static final int NOTIFICATION_ID = 100;
    private final Context context;

    public NotificationHelper(Context context) {
        this.context = context;
        createNotificationChannel();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Log.d("NotificationHelper", "Создаем канал уведомлений");
            
            CharSequence name = context.getString(R.string.points_channel_name);
            String description = context.getString(R.string.points_channel_description);
            int importance = NotificationManager.IMPORTANCE_HIGH;
            
            NotificationChannel channel = new NotificationChannel(POINTS_CHANNEL_ID, name, importance);
            channel.setDescription(description);
            channel.enableLights(true);
            channel.setLightColor(Color.GREEN);
            channel.enableVibration(true);
            
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
                Log.d("NotificationHelper", "Канал уведомлений создан успешно");
            } else {
                Log.e("NotificationHelper", "NotificationManager == null");
            }
        }
    }

    public void showPointsAddedNotification(int points) {
        Log.d("NotificationHelper", "Показываем уведомление о начислении " + points + " баллов");
        
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context, 
                0, 
                intent, 
                PendingIntent.FLAG_IMMUTABLE
        );

        String notificationText = context.getString(R.string.points_added_notification_text, points);
        
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, POINTS_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(context.getString(R.string.points_added_notification_title))
                .setContentText(notificationText)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setVibrate(new long[] { 0, 500, 250, 500 })
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        
        try {
            if (Build.VERSION.SDK_INT >= 33) {
                if (notificationManager.areNotificationsEnabled()) {
                    notificationManager.notify(NOTIFICATION_ID, builder.build());
                    Log.d("NotificationHelper", "Уведомление отправлено");
                } else {
                    Log.d("NotificationHelper", "Нет разрешения на отправку уведомлений");
                }
            } else {
                notificationManager.notify(NOTIFICATION_ID, builder.build());
                Log.d("NotificationHelper", "Уведомление отправлено");
            }
        } catch (SecurityException e) {
            Log.e("NotificationHelper", "Ошибка безопасности при отправке уведомления: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            Log.e("NotificationHelper", "Ошибка при отправке уведомления: " + e.getMessage());
            e.printStackTrace();
        }
    }
} 