package com.tazar.android.services;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.tazar.android.api.ApiClient;
import com.tazar.android.api.ApiService;
import com.tazar.android.helpers.NotificationHelper;
import com.tazar.android.helpers.PreferencesManager;
import com.tazar.android.models.Report;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PointsCheckingService extends Service {
    private static final String TAG = "PointsCheckingService";
    private static final long CHECK_INTERVAL = 20000; // Проверка каждые 20 секунд
    
    private NotificationHelper notificationHelper;
    private ApiClient apiClient;
    private Handler handler;
    private PreferencesManager preferencesManager;
    private Set<Integer> processedReportsIds = new HashSet<>();
    
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "Сервис проверки баллов создан");
        
        notificationHelper = new NotificationHelper(this);
        apiClient = new ApiClient(this);
        handler = new Handler();
        preferencesManager = new PreferencesManager(this);
        
        // Загружаем сохраненные ID обработанных отчетов
        processedReportsIds = preferencesManager.getProcessedReportIds();
    }
    
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "Сервис проверки баллов запущен");
        
        // Отправляем тестовое уведомление при запуске сервиса
        notificationHelper.showPointsAddedNotification(10);
        Log.d(TAG, "Отправлено тестовое уведомление");
        
        // Запускаем периодическую проверку
        startPeriodicCheck();
        
        return START_STICKY;
    }
    
    private void startPeriodicCheck() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                checkForNewPoints();
                handler.postDelayed(this, CHECK_INTERVAL);
            }
        }, CHECK_INTERVAL);
        
        // Делаем первую проверку сразу
        checkForNewPoints();
    }
    
    private void checkForNewPoints() {
        Log.d(TAG, "Проверка новых начисленных баллов");
        
        ApiService apiService = apiClient.getService(ApiService.class);
        Call<List<Report>> call = apiService.getReports();
        
        call.enqueue(new Callback<List<Report>>() {
            @Override
            public void onResponse(Call<List<Report>> call, Response<List<Report>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    processReports(response.body());
                } else {
                    Log.e(TAG, "Ошибка получения отчетов: " + response.code());
                }
            }
            
            @Override
            public void onFailure(Call<List<Report>> call, Throwable t) {
                Log.e(TAG, "Сбой при получении отчетов: " + t.getMessage());
            }
        });
    }
    
    private void processReports(List<Report> reports) {
        int currentUserId = preferencesManager.getUserId();
        int totalNewPoints = 0;
        int newReportsCount = 0;
        
        Log.d(TAG, "Проверка отчетов. ID пользователя: " + currentUserId + ", количество отчетов: " + reports.size());
        
        for (Report report : reports) {
            Log.d(TAG, "Отчет #" + report.getId() + 
                    ", пользователь: " + report.getUserId() + 
                    ", статус: " + report.getStatus() + 
                    ", баллы: " + report.getPoints() + 
                    ", начислены: " + report.isPointsAwarded());
            
            // Проверяем только отчеты текущего пользователя
            if (report.getUserId() == currentUserId && 
                    report.isPointsAwarded() && 
                    !processedReportsIds.contains(report.getId())) {
                
                // Нашли новый отчет с начисленными баллами
                totalNewPoints += report.getPoints();
                newReportsCount++;
                
                // Добавляем ID отчета в обработанные
                processedReportsIds.add(report.getId());
                Log.d(TAG, "НАЙДЕН НОВЫЙ отчет с баллами: ID=" + report.getId() + ", баллы=" + report.getPoints());
            }
        }
        
        // Сохраняем обновленный список обработанных отчетов
        preferencesManager.saveProcessedReportIds(processedReportsIds);
        
        // Если есть новые начисленные баллы, отправляем уведомление
        if (totalNewPoints > 0) {
            Log.d(TAG, "Найдено " + newReportsCount + " новых отчетов с баллами: " + totalNewPoints);
            notificationHelper.showPointsAddedNotification(totalNewPoints);
        } else {
            Log.d(TAG, "Новых отчетов с начисленными баллами не найдено");
        }
    }
    
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
        Log.d(TAG, "Сервис проверки баллов уничтожен");
    }
} 