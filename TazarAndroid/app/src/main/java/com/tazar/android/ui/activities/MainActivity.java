package com.tazar.android.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.tazar.android.utils.PreferenceManager;
import com.tazar.android.R;

public class MainActivity extends AppCompatActivity {
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferenceManager = new PreferenceManager(this);
        
        // Проверяем авторизацию
        if (!preferenceManager.isLoggedIn()) {
            startLoginActivity();
            finish();
            return;
        }
        
        setContentView(R.layout.activity_main);
        // ... остальной код инициализации MainActivity
    }

    private void logout() {
        preferenceManager.logout();
        // Переход на экран входа
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void startLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    // ... остальные методы MainActivity
} 