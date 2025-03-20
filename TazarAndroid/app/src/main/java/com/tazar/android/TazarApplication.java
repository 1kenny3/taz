package com.tazar.android;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.tazar.android.api.ApiClient;
import com.tazar.android.utils.PreferenceManager;

public class TazarApplication extends Application {
    private static final String TAG = "TazarApplication";
    private static Context appContext;
    private static PreferenceManager preferenceManager;
    
    @Override
    public void onCreate() {
        super.onCreate();
        try {
            appContext = getApplicationContext();
            preferenceManager = new PreferenceManager(appContext);
            
            // Инициализация ApiClient
            ApiClient.init(appContext);
        } catch (Exception e) {
            Log.e(TAG, "Ошибка при инициализации приложения: ", e);
            // Не крашим приложение, а просто логируем ошибку
        }
    }
    
    public static Context getAppContext() {
        return appContext;
    }
    
    public static PreferenceManager getPreferenceManager() {
        return preferenceManager;
    }
} 