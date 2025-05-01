package com.tazar.android.helpers;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashSet;
import java.util.Set;

public class PreferencesManager {
    private static final String PREF_NAME = "TazarPrefs";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_USER_NAME = "user_name";
    private static final String KEY_USER_EMAIL = "user_email";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";
    private static final String KEY_PROCESSED_REPORT_IDS = "processed_report_ids";
    
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    
    public PreferencesManager(Context context) {
        preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = preferences.edit();
    }
    
    public void saveUserId(int userId) {
        editor.putInt(KEY_USER_ID, userId);
        editor.apply();
    }
    
    public int getUserId() {
        return preferences.getInt(KEY_USER_ID, -1);
    }
    
    public void saveUserName(String userName) {
        editor.putString(KEY_USER_NAME, userName);
        editor.apply();
    }
    
    public String getUserName() {
        return preferences.getString(KEY_USER_NAME, "");
    }
    
    public void saveUserEmail(String email) {
        editor.putString(KEY_USER_EMAIL, email);
        editor.apply();
    }
    
    public String getUserEmail() {
        return preferences.getString(KEY_USER_EMAIL, "");
    }
    
    public void setLoggedIn(boolean isLoggedIn) {
        editor.putBoolean(KEY_IS_LOGGED_IN, isLoggedIn);
        editor.apply();
    }
    
    public boolean isLoggedIn() {
        return preferences.getBoolean(KEY_IS_LOGGED_IN, false);
    }
    
    public void clearUserData() {
        editor.remove(KEY_USER_ID);
        editor.remove(KEY_USER_NAME);
        editor.remove(KEY_USER_EMAIL);
        editor.remove(KEY_IS_LOGGED_IN);
        editor.apply();
    }
    
    public void saveProcessedReportIds(Set<Integer> reportIds) {
        Set<String> stringIds = new HashSet<>();
        for (Integer id : reportIds) {
            stringIds.add(id.toString());
        }
        editor.putStringSet(KEY_PROCESSED_REPORT_IDS, stringIds);
        editor.apply();
    }
    
    public Set<Integer> getProcessedReportIds() {
        Set<String> stringIds = preferences.getStringSet(KEY_PROCESSED_REPORT_IDS, new HashSet<>());
        Set<Integer> intIds = new HashSet<>();
        for (String id : stringIds) {
            try {
                intIds.add(Integer.parseInt(id));
            } catch (NumberFormatException e) {
                // Игнорируем неправильный формат
            }
        }
        return intIds;
    }
    
    public void clearProcessedReportIds() {
        editor.remove(KEY_PROCESSED_REPORT_IDS);
        editor.apply();
    }
} 