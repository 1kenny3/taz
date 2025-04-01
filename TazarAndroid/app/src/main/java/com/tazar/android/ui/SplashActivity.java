package com.tazar.android.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.tazar.android.R;
import com.tazar.android.TazarApplication;
import com.tazar.android.ui.auth.LoginActivity;
import com.tazar.android.utils.PreferenceManager;

/**
 * Активность-заставка при запуске приложения
 */
public class SplashActivity extends AppCompatActivity {
    
    private static final String TAG = "SplashActivity";
    private static final int SPLASH_DELAY = 1500; // 1.5 секунды
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        try {
            // Получаем PreferenceManager через getInstance
            PreferenceManager preferenceManager = ((TazarApplication) getApplication()).getPreferenceManager();
            
            // Задержка для отображения splash screen
            new Handler(Looper.getMainLooper()).postDelayed(this::navigateToNextScreen, SPLASH_DELAY);
        } catch (Exception e) {
            Log.e(TAG, "Ошибка при инициализации: ", e);
            navigateToLoginActivity();
        }
    }
    
    /**
     * Переход к следующему экрану в зависимости от состояния авторизации
     */
    private void navigateToNextScreen() {
        try {
            PreferenceManager preferenceManager = ((TazarApplication) getApplication()).getPreferenceManager();
            
            // Проверяем, авторизован ли пользователь
            if (preferenceManager != null && preferenceManager.isLoggedIn()) {
                // Если авторизован, переходим к главному экрану
                navigateToMainActivity();
            } else {
                // Если не авторизован, переходим к экрану входа
                navigateToLoginActivity();
            }
        } catch (Exception e) {
            Log.e(TAG, "Ошибка при определении следующего экрана: ", e);
            // В случае ошибки переходим на экран входа
            navigateToLoginActivity();
        }
        
        // Завершаем текущую активность
        finish();
    }
    
    /**
     * Переход к главному экрану
     */
    private void navigateToMainActivity() {
        try {
            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
            startActivity(intent);
        } catch (Exception e) {
            Log.e(TAG, "Ошибка при переходе на главный экран: ", e);
            Toast.makeText(this, "Ошибка запуска приложения. Попробуйте еще раз.", Toast.LENGTH_SHORT).show();
            // В случае ошибки переходим на экран входа
            navigateToLoginActivity();
        }
    }
    
    /**
     * Переход к экрану входа
     */
    private void navigateToLoginActivity() {
        try {
            Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
            startActivity(intent);
        } catch (Exception e) {
            Log.e(TAG, "Ошибка при переходе на экран входа: ", e);
            
            try {
                // Если активность LoginActivity не найдена, создаем временный Intent
                Intent intent = new Intent();
                intent.setClassName(getPackageName(), getPackageName() + ".ui.auth.LoginActivity");
                startActivity(intent);
            } catch (Exception ex) {
                Log.e(TAG, "Критическая ошибка при запуске приложения: ", ex);
                Toast.makeText(this, "Критическая ошибка запуска. Приложение будет закрыто.", Toast.LENGTH_LONG).show();
                // Критическая ошибка, закрываем приложение
                finishAffinity();
            }
        }
    }
} 