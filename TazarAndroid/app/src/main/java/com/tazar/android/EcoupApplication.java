package com.tazar.android;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;
import android.util.Log;

import com.tazar.android.api.ApiClient;
import com.tazar.android.utils.PreferenceManager;

import org.json.JSONException;
import org.json.JSONObject;

import androidx.appcompat.app.AppCompatDelegate;

public class EcoupApplication extends Application {
    private static final String TAG = "EcoupApplication";
    private static final String PREF_NAME = "tazar_prefs";
    private static final String AUTH_TOKEN_KEY = "auth_token";
    private static final String USER_ID_KEY = "user_id";
    private static final String SERVER_URL_KEY = "server_url";
    private static final String DEFAULT_SERVER_URL = "http://172.20.10.4:8000/";
    
    private static Context appContext;
    private static EcoupApplication instance;
    private PreferenceManager preferenceManager;
    
    @Override
    public void onCreate() {
        super.onCreate();
        try {
            appContext = getApplicationContext();
            instance = this;
            preferenceManager = new PreferenceManager(this);
            
            // Применяем сохраненный режим темы приложения
            AppCompatDelegate.setDefaultNightMode(preferenceManager.getThemeMode());
            
            // Инициализация ApiClient
            ApiClient.init(appContext);
            
            initializeServerUrl();
        } catch (Exception e) {
            Log.e(TAG, "Ошибка при инициализации приложения: ", e);
            // Не крашим приложение, а просто логируем ошибку
        }
    }
    
    private void initializeServerUrl() {
        SharedPreferences prefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        if (!prefs.contains(SERVER_URL_KEY)) {
            prefs.edit().putString(SERVER_URL_KEY, DEFAULT_SERVER_URL).apply();
        }
    }
    
    public static Context getAppContext() {
        return appContext;
    }
    
    public static EcoupApplication getInstance() {
        return instance;
    }

    public String getAuthToken() {
        return preferenceManager.getAuthToken();
    }

    public boolean isLoggedIn() {
        return preferenceManager.isLoggedIn();
    }

    public void setAuthToken(String authToken) {
        String currentRefreshToken = preferenceManager.getRefreshToken();
        preferenceManager.saveTokens(authToken, currentRefreshToken);
    }

    public void clearAuthToken() {
        preferenceManager.logout();
    }

    public void saveAuthToken(String token) {
        SharedPreferences prefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(AUTH_TOKEN_KEY, token);
        editor.apply();
        
        // Извлекаем ID пользователя из токена и сохраняем
        extractAndSaveUserIdFromToken(token);
    }
    
    // Метод для извлечения ID пользователя из JWT токена
    private void extractAndSaveUserIdFromToken(String token) {
        try {
            // JWT токен состоит из трех частей, разделенных точками: заголовок.данные.подпись
            String[] parts = token.split("\\.");
            if (parts.length >= 2) {
                // Декодируем среднюю часть (данные)
                String payload = new String(Base64.decode(parts[1], Base64.DEFAULT));
                JSONObject json = new JSONObject(payload);
                
                // Извлекаем "user_id" или "id", в зависимости от того, как он назван в токене
                String userId = null;
                if (json.has("user_id")) {
                    userId = json.getString("user_id");
                } else if (json.has("id")) {
                    userId = json.getString("id");
                } else if (json.has("sub")) {
                    userId = json.getString("sub");
                }
                
                if (userId != null) {
                    // Сохраняем ID пользователя в SharedPreferences
                    SharedPreferences prefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString(USER_ID_KEY, userId);
                    editor.apply();
                    Log.d(TAG, "Извлечен ID пользователя из токена: " + userId);
                } else {
                    Log.e(TAG, "ID пользователя не найден в токене");
                }
            } else {
                Log.e(TAG, "Некорректный формат токена");
            }
        } catch (JSONException e) {
            Log.e(TAG, "Ошибка при парсинге JSON из токена", e);
        } catch (Exception e) {
            Log.e(TAG, "Ошибка при извлечении ID пользователя из токена", e);
        }
    }
    
    public String getUserId() {
        SharedPreferences prefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        String userId = prefs.getString(USER_ID_KEY, null);
        
        // Если ID не найден, пробуем извлечь его из токена
        if (userId == null) {
            String token = getAuthToken();
            if (token != null) {
                extractAndSaveUserIdFromToken(token);
                userId = prefs.getString(USER_ID_KEY, null);
            }
        }
        
        if (userId == null) {
            Log.e(TAG, "ID пользователя не найден");
        } else {
            Log.d(TAG, "Получен ID пользователя: " + userId);
        }
        
        return userId;
    }

    public String getServerUrl() {
        return preferenceManager.getServerUrl(DEFAULT_SERVER_URL);
    }

    public void setServerUrl(String url) {
        preferenceManager.saveServerUrl(url);
    }

    public PreferenceManager getPreferenceManager() {
        return preferenceManager;
    }
} 