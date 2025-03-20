package com.tazar.android.ui.settings;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.tazar.android.R;
import com.tazar.android.TazarApplication;
import com.tazar.android.api.ApiClient;
import com.tazar.android.config.ApiConfig;
import com.tazar.android.utils.ApiUrlUtil;
import com.tazar.android.utils.PreferenceManager;

/**
 * Активность настроек приложения
 */
public class SettingsActivity extends AppCompatActivity {
    
    private EditText apiUrlEditText;
    private Button saveApiUrlButton;
    private Button resetApiUrlButton;
    private TextView currentApiUrlTextView;
    private PreferenceManager preferenceManager;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        
        // Настройка ActionBar
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(R.string.settings);
        }
        
        preferenceManager = TazarApplication.getPreferenceManager();
        
        // Инициализация UI-элементов
        apiUrlEditText = findViewById(R.id.api_url_edit_text);
        saveApiUrlButton = findViewById(R.id.save_api_url_button);
        resetApiUrlButton = findViewById(R.id.reset_api_url_button);
        currentApiUrlTextView = findViewById(R.id.current_api_url_text_view);
        
        // Загрузка текущего URL API
        String currentApiUrl = preferenceManager.getApiUrl();
        currentApiUrlTextView.setText(getString(R.string.current_api_url, currentApiUrl));
        apiUrlEditText.setText(currentApiUrl);
        
        // Установка обработчиков
        saveApiUrlButton.setOnClickListener(view -> saveApiUrl());
        resetApiUrlButton.setOnClickListener(view -> resetApiUrl());
    }
    
    /**
     * Сохранение URL API
     */
    private void saveApiUrl() {
        String apiUrl = apiUrlEditText.getText().toString().trim();
        
        // Проверка, что URL не пустой
        if (apiUrl.isEmpty()) {
            Toast.makeText(this, R.string.api_url_empty, Toast.LENGTH_SHORT).show();
            return;
        }
        
        // Добавление конечного слеша, если его нет
        if (!apiUrl.endsWith("/")) {
            apiUrl += "/";
        }
        
        // Добавление протокола, если его нет
        if (!apiUrl.startsWith("http://") && !apiUrl.startsWith("https://")) {
            apiUrl = "http://" + apiUrl;
        }
        
        // Сохранение URL
        ApiUrlUtil.saveApiUrl(this, apiUrl);
        
        // Обновление UI
        currentApiUrlTextView.setText(getString(R.string.current_api_url, apiUrl));
        
        // Уведомление пользователя
        Toast.makeText(this, R.string.api_url_saved, Toast.LENGTH_SHORT).show();
        
        // Переинициализация API-клиента
        ApiClient.init(this);
    }
    
    /**
     * Сброс URL API до значения по умолчанию
     */
    private void resetApiUrl() {
        // Определение URL по умолчанию в зависимости от устройства
        String defaultApiUrl = ApiUrlUtil.isEmulator() 
                ? "http://10.0.2.2:8000/" 
                : "http://192.168.1.X:8000/"; // Подставьте свой IP-адрес сервера
        
        // Сохранение URL
        ApiUrlUtil.saveApiUrl(this, defaultApiUrl);
        
        // Обновление UI
        apiUrlEditText.setText(defaultApiUrl);
        currentApiUrlTextView.setText(getString(R.string.current_api_url, defaultApiUrl));
        
        // Уведомление пользователя
        Toast.makeText(this, R.string.api_url_reset, Toast.LENGTH_SHORT).show();
        
        // Переинициализация API-клиента
        ApiClient.init(this);
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
} 