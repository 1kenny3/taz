package com.tazar.android.ui.auth;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.tazar.android.R;
import com.tazar.android.TazarApplication;
import com.tazar.android.api.ApiClient;
import com.tazar.android.api.services.AuthService;
import com.tazar.android.models.auth.LoginRequest;
import com.tazar.android.models.auth.TokenResponse;
import com.tazar.android.utils.PreferenceManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Активность для входа в приложение
 */
public class LoginActivity extends AppCompatActivity {
    
    private static final String TAG = "LoginActivity";
    private static final int PERMISSION_REQUEST_CODE = 100;
    
    private EditText usernameEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private TextView registerLink;
    private TextView forgotPasswordLink;
    private View progressOverlay;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        try {
            // Устанавливаем макет
            setContentView(R.layout.activity_login);
            
            // Инициализация UI элементов
            initializeViews();
            
            // Установка обработчиков, если элементы успешно инициализированы
            if (loginButton != null && registerLink != null && forgotPasswordLink != null) {
                loginButton.setOnClickListener(v -> attemptLogin());
                registerLink.setOnClickListener(v -> navigateToRegister());
                forgotPasswordLink.setOnClickListener(v -> navigateToForgotPassword());
            } else {
                Log.e(TAG, "Не удалось инициализировать элементы UI");
                Toast.makeText(this, "Ошибка инициализации UI", Toast.LENGTH_SHORT).show();
            }
            
            // Проверяем и запрашиваем необходимые разрешения
            checkAndRequestPermissions();
        } catch (Exception e) {
            Log.e(TAG, "Ошибка при создании активности: ", e);
            Toast.makeText(this, "Ошибка при запуске экрана входа: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            
            // Если возникла ошибка, показываем временный интерфейс
            showFallbackUI();
        }
    }
    
    /**
     * Проверка и запрос необходимых разрешений
     */
    private void checkAndRequestPermissions() {
        String[] permissions = {
            Manifest.permission.CAMERA,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        };
        
        boolean allPermissionsGranted = true;
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                allPermissionsGranted = false;
                break;
            }
        }
        
        if (!allPermissionsGranted) {
            ActivityCompat.requestPermissions(this, permissions, PERMISSION_REQUEST_CODE);
        }
    }
    
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        
        if (requestCode == PERMISSION_REQUEST_CODE) {
            boolean allGranted = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    allGranted = false;
                    break;
                }
            }
            
            if (!allGranted) {
                Toast.makeText(this, "Для полной функциональности приложения необходимы все разрешения", Toast.LENGTH_LONG).show();
            }
        }
    }
    
    /**
     * Инициализация UI элементов
     */
    private void initializeViews() {
        try {
            usernameEditText = findViewById(R.id.username_edit_text);
            passwordEditText = findViewById(R.id.password_edit_text);
            loginButton = findViewById(R.id.login_button);
            registerLink = findViewById(R.id.register_link);
            forgotPasswordLink = findViewById(R.id.forgot_password_link);
            progressOverlay = findViewById(R.id.progress_overlay);
            
            // Если не удается найти элементы, логируем это
            if (usernameEditText == null || passwordEditText == null || 
                loginButton == null || registerLink == null || forgotPasswordLink == null || progressOverlay == null) {
                Log.e(TAG, "Один или несколько элементов UI не найдены");
            }
        } catch (Exception e) {
            Log.e(TAG, "Ошибка инициализации UI: ", e);
            Toast.makeText(this, "Ошибка инициализации UI: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    
    /**
     * Показывает резервный UI в случае ошибки загрузки основного макета
     */
    private void showFallbackUI() {
        // Создаем простой интерфейс программно
        try {
            // Очищаем текущий контент
            setContentView(new View(this));
            
            // Здесь можно было бы создать программно простой интерфейс,
            // но в этой версии просто выводим сообщение об ошибке
            Toast.makeText(this, "Не удалось загрузить интерфейс приложения.", Toast.LENGTH_LONG).show();
            
            // Закрываем активность через некоторое время
            new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(this::finish, 3000);
        } catch (Exception e) {
            Log.e(TAG, "Не удалось отобразить резервный UI: ", e);
            finish(); // Завершаем активность при критической ошибке
        }
    }
    
    /**
     * Попытка входа
     */
    private void attemptLogin() {
        // Проверяем, что все UI элементы существуют
        if (usernameEditText == null || passwordEditText == null) {
            Toast.makeText(this, "Ошибка инициализации UI", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // Получаем данные из полей
        String username = usernameEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        
        // Проверяем, что поля не пустые
        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Пожалуйста, заполните все поля", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // Показываем индикатор загрузки
        showProgress(true);
        
        // Создаем запрос на вход
        LoginRequest loginRequest = new LoginRequest(username, password);
        
        try {
            // Получаем сервис авторизации
            AuthService authService = ApiClient.getService(AuthService.class);
            
            // Выполняем запрос на вход
            Call<TokenResponse> call = authService.login(loginRequest);
            call.enqueue(new Callback<TokenResponse>() {
                @Override
                public void onResponse(Call<TokenResponse> call, Response<TokenResponse> response) {
                    // Скрываем индикатор загрузки
                    showProgress(false);
                    
                    if (response.isSuccessful() && response.body() != null) {
                        // Получаем токены из ответа
                        TokenResponse tokenResponse = response.body();
                        
                        // Сохраняем токены
                        PreferenceManager preferenceManager = TazarApplication.getPreferenceManager();
                        preferenceManager.saveTokens(
                                tokenResponse.getAccessToken(),
                                tokenResponse.getRefreshToken()
                        );
                        
                        // Показываем сообщение об успешном входе
                        Toast.makeText(LoginActivity.this, "Вход выполнен успешно", Toast.LENGTH_SHORT).show();
                        
                        // TODO: Переходим на главный экран
                        // navigateToMainActivity();
                    } else {
                        // Показываем сообщение об ошибке
                        Toast.makeText(LoginActivity.this, "Ошибка входа. Проверьте логин и пароль.", Toast.LENGTH_SHORT).show();
                    }
                }
                
                @Override
                public void onFailure(Call<TokenResponse> call, Throwable t) {
                    // Скрываем индикатор загрузки
                    showProgress(false);
                    
                    // Показываем сообщение об ошибке
                    Toast.makeText(LoginActivity.this, "Ошибка сети: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            // Скрываем индикатор загрузки
            showProgress(false);
            
            // Логируем ошибку и показываем сообщение
            Log.e(TAG, "Ошибка при выполнении запроса: ", e);
            Toast.makeText(this, "Ошибка: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    
    /**
     * Переход на экран регистрации
     */
    private void navigateToRegister() {
        try {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        } catch (Exception e) {
            Log.e(TAG, "Ошибка при переходе на экран регистрации: ", e);
            Toast.makeText(this, "Экран регистрации недоступен: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    
    /**
     * Переход на экран восстановления пароля
     */
    private void navigateToForgotPassword() {
        try {
            // TODO: Реализовать переход на экран восстановления пароля
            Toast.makeText(this, "Функция восстановления пароля в разработке", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e(TAG, "Ошибка при переходе на экран восстановления пароля: ", e);
            Toast.makeText(this, "Экран восстановления пароля недоступен: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    
    /**
     * Показать/скрыть индикатор загрузки
     * @param show true - показать, false - скрыть
     */
    private void showProgress(boolean show) {
        if (progressOverlay != null) {
            progressOverlay.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }
} 