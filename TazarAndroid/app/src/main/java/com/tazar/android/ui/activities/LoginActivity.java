package com.tazar.android.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.tazar.android.utils.PreferenceManager;
import com.tazar.android.R;

public class LoginActivity extends AppCompatActivity {
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        preferenceManager = new PreferenceManager(this);
        
        // Проверяем, авторизован ли пользователь
        if (preferenceManager.isLoggedIn()) {
            // Если да, сразу переходим на главный экран
            startMainActivity();
            finish();
            return;
        }
        
        setContentView(R.layout.activity_login);
        // ... остальной код инициализации LoginActivity
    }

    private void startMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    // ... остальные методы LoginActivity
} 