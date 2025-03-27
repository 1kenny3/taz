package com.tazar.android.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Менеджер настроек приложения
 */
public class PreferenceManager {
    private static final String PREF_NAME = "tazar_preferences";
    private static final String KEY_ACCESS_TOKEN = "access_token";
    private static final String KEY_REFRESH_TOKEN = "refresh_token";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";
    private static final String KEY_API_URL = "api_url";
    
    private final SharedPreferences preferences;
    
    /**
     * Конструктор
     * @param context Контекст приложения
     */
    public PreferenceManager(Context context) {
        preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }
    
    /**
     * Сохранение токенов авторизации
     * @param accessToken Токен доступа
     * @param refreshToken Токен обновления
     */
    public void saveTokens(String accessToken, String refreshToken) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(KEY_ACCESS_TOKEN, accessToken);
        editor.putString(KEY_REFRESH_TOKEN, refreshToken);
        editor.apply();
    }
    
    /**
     * Получение токена доступа
     * @return Токен доступа
     */
    public String getAccessToken() {
        return preferences.getString(KEY_ACCESS_TOKEN, null);
    }
    
    /**
     * Получение токена обновления
     * @return Токен обновления
     */
    public String getRefreshToken() {
        return preferences.getString(KEY_REFRESH_TOKEN, null);
    }
    
    /**
     * Сохранение информации о пользователе
     * @param id ID пользователя
     * @param username Имя пользователя
     * @param email Email пользователя
     */
    public void saveUserInfo(int id, String username, String email) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(KEY_USER_ID, id);
        editor.putString(KEY_USERNAME, username);
        editor.putString(KEY_EMAIL, email);
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.apply();
    }
    
    /**
     * Получение ID пользователя
     * @return ID пользователя
     */
    public int getUserId() {
        return preferences.getInt(KEY_USER_ID, -1);
    }
    
    /**
     * Получение имени пользователя
     * @return Имя пользователя
     */
    public String getUsername() {
        return preferences.getString(KEY_USERNAME, "");
    }
    
    /**
     * Получение email пользователя
     * @return Email пользователя
     */
    public String getEmail() {
        return preferences.getString(KEY_EMAIL, "");
    }
    
    /**
     * Проверка, авторизован ли пользователь
     * @return true, если пользователь авторизован
     */
    public boolean isLoggedIn() {
        return preferences.getBoolean(KEY_IS_LOGGED_IN, false);
    }
    
    /**
     * Выход из системы (удаление всех данных авторизации)
     */
    public void logout() {
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove(KEY_ACCESS_TOKEN);
        editor.remove(KEY_REFRESH_TOKEN);
        editor.remove(KEY_USER_ID);
        editor.remove(KEY_USERNAME);
        editor.remove(KEY_EMAIL);
        editor.putBoolean(KEY_IS_LOGGED_IN, false);
        editor.apply();
    }
    
    /**
     * Установка адреса API сервера
     * @param apiUrl Адрес API сервера
     */
    public void setApiUrl(String apiUrl) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(KEY_API_URL, apiUrl);
        editor.apply();
    }
    
    /**
     * Получение адреса API сервера
     * @return Адрес API сервера
     */
    public String getApiUrl() {
        return preferences.getString(KEY_API_URL, "http://10.0.2.2:8000/");
    }
    
    /**
     * Сохраняет ID пользователя
     * @param userId ID пользователя
     */
    public void saveUserId(int userId) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("userId", userId);
        editor.apply();
    }
    
    /**
     * Получает ID пользователя
     * @return ID пользователя или -1, если не найден
     */
    public int getUserIdFromPreferences() {
        return preferences.getInt("userId", -1);
    }
} 