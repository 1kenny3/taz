package com.tazar.android.utils;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.appcompat.app.AppCompatDelegate;

/**
 * Менеджер настроек приложения
 */
public class PreferenceManager {
    private static final String PREF_NAME = "EcoupPreferences";
    private static final String ACCESS_TOKEN_KEY = "access_token";
    private static final String REFRESH_TOKEN_KEY = "refresh_token";
    private static final String USER_ID_KEY = "user_id";
    private static final String SERVER_URL_KEY = "server_url";
    private static final String KEY_API_URL = "api_url";
    private static final String DEFAULT_API_URL = "http://172.20.10.4:8000/";
    private static final String KEY_THEME_MODE = "theme_mode";
    
    private final SharedPreferences sharedPreferences;
    
    /**
     * Конструктор
     * @param context Контекст приложения
     */
    public PreferenceManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }
    
    /**
     * Сохранение токенов авторизации
     * @param accessToken Токен доступа
     * @param refreshToken Токен обновления
     */
    public void saveTokens(String accessToken, String refreshToken) {
        sharedPreferences.edit()
                .putString(ACCESS_TOKEN_KEY, accessToken)
                .putString(REFRESH_TOKEN_KEY, refreshToken)
                .apply();
    }
    
    /**
     * Получение токена авторизации
     * @return Токен авторизации
     */
    public String getAccessToken() {
        return sharedPreferences.getString(ACCESS_TOKEN_KEY, null);
    }
    
    /**
     * Получение токена обновления
     * @return Токен обновления
     */
    public String getRefreshToken() {
        return sharedPreferences.getString(REFRESH_TOKEN_KEY, null);
    }
    
    /**
     * Очистка сессии пользователя
     */
    public void logout() {
        sharedPreferences.edit()
                .remove(ACCESS_TOKEN_KEY)
                .remove(REFRESH_TOKEN_KEY)
                .remove(USER_ID_KEY)
                .apply();
    }
    
    /**
     * Проверка, авторизован ли пользователь
     * @return true, если пользователь авторизован
     */
    public boolean isLoggedIn() {
        return getAccessToken() != null;
    }
    
    /**
     * Сохранение ID пользователя
     * @param userId ID пользователя
     */
    public void saveUserId(int userId) {
        sharedPreferences.edit().putInt(USER_ID_KEY, userId).apply();
    }
    
    /**
     * Получение ID пользователя
     * @return ID пользователя
     */
    public int getUserId() {
        return sharedPreferences.getInt(USER_ID_KEY, -1);
    }
    
    /**
     * Сохранение адреса сервера
     * @param url Адрес сервера
     */
    public void saveServerUrl(String url) {
        sharedPreferences.edit().putString(SERVER_URL_KEY, url).apply();
    }
    
    /**
     * Получение адреса сервера
     * @param defaultUrl Дефолтный адрес сервера
     * @return Адрес сервера
     */
    public String getServerUrl(String defaultUrl) {
        return sharedPreferences.getString(SERVER_URL_KEY, defaultUrl);
    }

    public String getApiUrl() {
        return sharedPreferences.getString(KEY_API_URL, DEFAULT_API_URL);
    }

    public void setApiUrl(String url) {
        sharedPreferences.edit().putString(KEY_API_URL, url).apply();
    }

    public void resetApiUrl() {
        setApiUrl(DEFAULT_API_URL);
    }

    /**
     * Получение сохраненного режима темы приложения
     * @return режим темы из AppCompatDelegate
     */
    public int getThemeMode() {
        return sharedPreferences.getInt(KEY_THEME_MODE, AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
    }

    /**
     * Сохранение режима темы приложения
     * @param mode режим темы из AppCompatDelegate
     */
    public void saveThemeMode(int mode) {
        sharedPreferences.edit().putInt(KEY_THEME_MODE, mode).apply();
    }

    public String getAuthToken() {
        return getAccessToken();
    }

    /**
     * Очистка всех данных сессии пользователя
     */
    public void clearSession() {
        sharedPreferences.edit()
                .remove(ACCESS_TOKEN_KEY)
                .remove(REFRESH_TOKEN_KEY)
                .remove(USER_ID_KEY)
                .remove(SERVER_URL_KEY)
                .remove(KEY_API_URL)
                .clear()
                .apply();
    }
} 