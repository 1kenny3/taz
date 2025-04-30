package com.tazar.android.utils;

import android.content.Context;
import android.util.Log;

/**
 * Утилиты для работы с URL
 */
public class UrlUtil {
    private static final String TAG = "UrlUtil";
    
    /**
     * Преобразует URL для работы с эмулятором и реальным устройством
     * 
     * @param url исходный URL
     * @return обработанный URL
     */
    public static String processUrl(String url) {
        if (url == null || url.isEmpty() || url.equals("null")) {
            return null;
        }
        
        try {
            // Преобразуем URL для правильной работы с эмулятором
            // Эмулятор использует 10.0.2.2 для доступа к localhost
            if (url.contains("localhost")) {
                url = url.replace("localhost", "10.0.2.2");
            } else if (url.contains("127.0.0.1")) {
                url = url.replace("127.0.0.1", "10.0.2.2");
            }
            
            Log.d(TAG, "Преобразованный URL: " + url);
            return url;
        } catch (Exception e) {
            Log.e(TAG, "Ошибка обработки URL: " + e.getMessage());
            return url;
        }
    }
} 