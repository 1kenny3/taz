package com.tazar.android.helpers;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashSet;
import java.util.Set;

public class PreferencesManager {
    private static final String PREF_NAME = "TazarPreferences";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_PROCESSED_REPORT_IDS = "processed_report_ids";
    
    private SharedPreferences preferences;
    
    public PreferencesManager(Context context) {
        preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }
    
    public void saveUserId(int userId) {
        preferences.edit().putInt(KEY_USER_ID, userId).apply();
    }
    
    public int getUserId() {
        return preferences.getInt(KEY_USER_ID, -1);
    }
    
    public void saveProcessedReportIds(Set<Integer> reportIds) {
        Set<String> stringIds = new HashSet<>();
        for (Integer id : reportIds) {
            stringIds.add(id.toString());
        }
        preferences.edit().putStringSet(KEY_PROCESSED_REPORT_IDS, stringIds).apply();
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
        preferences.edit().remove(KEY_PROCESSED_REPORT_IDS).apply();
    }
} 