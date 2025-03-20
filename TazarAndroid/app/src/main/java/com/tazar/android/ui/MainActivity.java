package com.tazar.android.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.tazar.android.R;
import com.tazar.android.TazarApplication;
import com.tazar.android.ui.fragments.HomeFragment;
import com.tazar.android.ui.fragments.MapFragment;
import com.tazar.android.ui.fragments.ProfileFragment;
import com.tazar.android.ui.fragments.ReportFragment;
import com.tazar.android.utils.PreferenceManager;

/**
 * Главная активность приложения
 */
public class MainActivity extends AppCompatActivity {
    
    private static final String TAG = "MainActivity";
    private BottomNavigationView bottomNavigationView;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        try {
            setContentView(R.layout.activity_main);
            
            // Инициализация UI элементов
            bottomNavigationView = findViewById(R.id.bottom_navigation);
            
            if (bottomNavigationView == null) {
                Log.e(TAG, "Не удалось найти элемент нижней навигации");
                Toast.makeText(this, "Ошибка инициализации навигации", Toast.LENGTH_SHORT).show();
                return;
            }
            
            // Установка обработчика для нижней навигации
            bottomNavigationView.setOnItemSelectedListener(item -> {
                Fragment fragment = null;
                String tag = null;
                
                int itemId = item.getItemId();
                if (itemId == R.id.nav_home) {
                    fragment = new HomeFragment();
                    tag = "home";
                } else if (itemId == R.id.nav_map) {
                    fragment = new MapFragment();
                    tag = "map";
                } else if (itemId == R.id.nav_report) {
                    fragment = new ReportFragment();
                    tag = "report";
                } else if (itemId == R.id.nav_profile) {
                    fragment = new ProfileFragment();
                    tag = "profile";
                }
                
                if (fragment != null) {
                    getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, fragment, tag)
                        .commit();
                    return true;
                }
                
                return false;
            });
            
            // Устанавливаем главный экран как выбранный по умолчанию
            bottomNavigationView.setSelectedItemId(R.id.nav_home);
        } catch (Exception e) {
            Log.e(TAG, "Ошибка при инициализации главного экрана: ", e);
            Toast.makeText(this, "Ошибка инициализации: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
} 