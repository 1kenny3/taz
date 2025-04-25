package com.tazar.android.ui;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;
import com.tazar.android.R;
import com.tazar.android.helpers.NotificationHelper;
import com.tazar.android.services.NotificationService;
import com.tazar.android.services.PointsCheckingService;
import com.tazar.android.ui.fragments.HomeFragment;
import com.tazar.android.ui.fragments.MapFragment;
import com.tazar.android.ui.fragments.ReportsFragment;
import com.tazar.android.ui.fragments.ProfileFragment;
import com.tazar.android.ui.settings.SettingsActivity;
import com.tazar.android.helpers.PreferencesManager;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.view.GravityCompat;
import androidx.appcompat.widget.Toolbar;

/**
 * Главная активность приложения
 */
public class MainActivity extends AppCompatActivity implements NavigationBarView.OnItemSelectedListener, NavigationView.OnNavigationItemSelectedListener {
    private BottomNavigationView bottomNavigationView;
    private NotificationHelper notificationHelper;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ActionBarDrawerToggle drawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(this);

        // Устанавливаем домашний фрагмент по умолчанию
        if (savedInstanceState == null) {
            bottomNavigationView.setSelectedItemId(R.id.navigation_home);
        }

        // Инициализация NotificationHelper
        notificationHelper = new NotificationHelper(this);

        // Запрос разрешения на отправку уведомлений для Android 13+
        if (Build.VERSION.SDK_INT >= 33) {
            requestNotificationPermission();
        }

        // Запускаем сервис проверки баллов
        Intent serviceIntent = new Intent(this, PointsCheckingService.class);
        startService(serviceIntent);

        PreferencesManager preferencesManager = new PreferencesManager(this);
        int userId = preferencesManager.getUserId();
        Log.d("MainActivity", "ID пользователя: " + userId);

        // Если ID не найден или неверный, вы можете установить его явно для тестирования
        if (userId == -1) {
            // Установите корректный ID пользователя
            preferencesManager.saveUserId(5);  // Замените на фактический ID
        }
    }

    @RequiresApi(api = 33)
    private void requestNotificationPermission() {
        if (ContextCompat.checkSelfPermission(
                this, 
                android.Manifest.permission.POST_NOTIFICATIONS) != 
                PackageManager.PERMISSION_GRANTED) {
            
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{android.Manifest.permission.POST_NOTIFICATIONS},
                    100);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100) {
            // Обработка результата запроса разрешения
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Разрешение получено
                Toast.makeText(this, "Уведомления разрешены", Toast.LENGTH_SHORT).show();
            } else {
                // Разрешение не получено
                Toast.makeText(this, "Уведомления не будут показываться", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Этот метод вызывается, когда пользователь получает баллы
    public void onPointsAdded(int points) {
        // Запускаем сервис для отправки уведомления
        Intent serviceIntent = new Intent(this, NotificationService.class);
        serviceIntent.putExtra("points", points);
        startService(serviceIntent);
        
        // Также можно показать Toast для немедленной обратной связи
        Toast.makeText(this, "Начислено " + points + " баллов", Toast.LENGTH_SHORT).show();
    }

    // Обработка нажатий на пункты меню (правильное имя метода)
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment fragment = null;
        int itemId = item.getItemId();
        if (itemId == R.id.navigation_home) {
            fragment = new HomeFragment();
        } else if (itemId == R.id.navigation_map) {
            fragment = new MapFragment();
        } else if (itemId == R.id.navigation_reports) {
            fragment = new ReportsFragment();
        } else if (itemId == R.id.nav_profile_drawer) {
            fragment = new ProfileFragment();
        } else if (itemId == R.id.nav_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        }
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        }
        return false;
    }

    // Пример метода для начисления баллов
    private void addPoints(int points) {
        // ... ваш код для добавления баллов ...
        
        // Отправка уведомления о начислении баллов
        notificationHelper.showPointsAddedNotification(points);
        
        // Для отладки
        Toast.makeText(this, "Начислено " + points + " баллов", Toast.LENGTH_SHORT).show();
    }

    // Обработка клика по иконке гамбургера
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Закрытие бокового меню при нажатии кнопки Назад
    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
} 