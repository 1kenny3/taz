package com.tazar.android.ui;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.tazar.android.R;
import com.tazar.android.ui.fragments.HomeFragment;
import com.tazar.android.ui.fragments.MapFragment;
import com.tazar.android.ui.fragments.ProfileFragment;
import com.tazar.android.ui.fragments.ReportsFragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

/**
 * Главная активность приложения
 */
public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private FragmentManager fragmentManager;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        try {
            // Устанавливаем макет
            setContentView(R.layout.activity_main);
            
            // Инициализация менеджера фрагментов
            fragmentManager = getSupportFragmentManager();
            
            // Настройка нижней навигации
            setupBottomNavigation();
            
            // Загружаем начальный фрагмент
            loadFragment(new HomeFragment());
        } catch (Exception e) {
            Log.e(TAG, "Ошибка при создании активности: ", e);
        }
    }
    
    /**
     * Настройка нижней навигации
     */
    private void setupBottomNavigation() {
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        if (bottomNav != null) {
            bottomNav.setOnItemSelectedListener(item -> {
                Fragment fragment = null;
                int itemId = item.getItemId();
                
                if (itemId == R.id.nav_home) {
                    fragment = new HomeFragment();
                } else if (itemId == R.id.nav_map) {
                    fragment = new MapFragment();
                } else if (itemId == R.id.nav_reports) {
                    fragment = new ReportsFragment();
                } else if (itemId == R.id.nav_profile) {
                    fragment = new ProfileFragment();
                }
                
                if (fragment != null) {
                    loadFragment(fragment);
                    return true;
                }
                return false;
            });
        }
    }
    
    /**
     * Загрузка фрагмента
     * @param fragment Фрагмент для загрузки
     */
    private void loadFragment(Fragment fragment) {
        try {
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.fragment_container, fragment);
            transaction.commit();
        } catch (Exception e) {
            Log.e(TAG, "Ошибка при загрузке фрагмента: ", e);
        }
    }
} 