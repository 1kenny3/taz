package com.tazar.android.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import androidx.annotation.Nullable;
import com.tazar.android.helpers.NotificationHelper;

public class NotificationService extends Service {
    private NotificationHelper notificationHelper;

    @Override
    public void onCreate() {
        super.onCreate();
        notificationHelper = new NotificationHelper(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && intent.hasExtra("points")) {
            int points = intent.getIntExtra("points", 0);
            if (points > 0) {
                notificationHelper.showPointsAddedNotification(points);
            }
        }
        
        // Сервис останавливается после выполнения задачи
        stopSelf();
        return START_NOT_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
} 