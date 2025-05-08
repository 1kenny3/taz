package com.tazar.android.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.tazar.android.R;
import com.tazar.android.EcoupApplication;
import com.tazar.android.api.ApiClient;
import com.tazar.android.api.services.AuthService;
import com.tazar.android.models.auth.RegisterRequest;
import com.tazar.android.models.User;
import com.tazar.android.ui.MainActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Активность для регистрации нового пользователя
 */
public class RegisterActivity extends AppCompatActivity {
    
    private static final String TAG = "RegisterActivity";
    
    private EditText usernameEditText;
    private EditText emailEditText;
    private EditText passwordEditText;
    private EditText confirmPasswordEditText;
    private Button registerButton;
    private TextView loginLink;
    private View progressOverlay;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        try {
            setContentView(R.layout.activity_register);
            
            // Инициализация UI элементов
            initializeViews();
            
            // Установка обработчиков
            if (registerButton != null && loginLink != null) {
                registerButton.setOnClickListener(v -> attemptRegister());
                loginLink.setOnClickListener(v -> finish()); // Возврат к экрану входа
            } else {
                Log.e(TAG, "Не удалось инициализировать элементы UI");
                Toast.makeText(this, "Ошибка инициализации UI", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e(TAG, "Ошибка при создании активности: ", e);
            Toast.makeText(this, "Ошибка при запуске экрана регистрации: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            finish(); // Возвращаемся к предыдущему экрану
        }
    }
    
    /**
     * Инициализация UI элементов
     */
    private void initializeViews() {
        try {
            usernameEditText = findViewById(R.id.username_edit_text);
            emailEditText = findViewById(R.id.email_edit_text);
            passwordEditText = findViewById(R.id.password_edit_text);
            confirmPasswordEditText = findViewById(R.id.confirm_password_edit_text);
            registerButton = findViewById(R.id.register_button);
            loginLink = findViewById(R.id.login_link);
            progressOverlay = findViewById(R.id.progress_overlay);
            
            // Если не удается найти элементы, логируем это
            if (usernameEditText == null || emailEditText == null || 
                passwordEditText == null || confirmPasswordEditText == null || 
                registerButton == null || loginLink == null || progressOverlay == null) {
                Log.e(TAG, "Один или несколько элементов UI не найдены");
            }
        } catch (Exception e) {
            Log.e(TAG, "Ошибка инициализации UI: ", e);
            Toast.makeText(this, "Ошибка инициализации UI: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    
    /**
     * Попытка регистрации
     */
    private void attemptRegister() {
        // Проверяем, что все UI элементы существуют
        if (usernameEditText == null || emailEditText == null || 
            passwordEditText == null || confirmPasswordEditText == null) {
            Toast.makeText(this, "Ошибка инициализации UI", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // Получаем данные из полей
        String username = usernameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String confirmPassword = confirmPasswordEditText.getText().toString().trim();
        
        // Валидация полей
        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Пожалуйста, заполните все поля", Toast.LENGTH_SHORT).show();
            return;
        }
        
        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Пароли не совпадают", Toast.LENGTH_SHORT).show();
            return;
        }
        
        if (password.length() < 8) {
            Toast.makeText(this, "Пароль должен содержать минимум 8 символов", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // Показываем индикатор загрузки
        showProgress(true);
        
        // Создаем запрос на регистрацию
        RegisterRequest registerRequest = new RegisterRequest(username, email, password);
        
        try {
            // Получаем сервис авторизации
            AuthService authService = ApiClient.getService(AuthService.class);
            
            // Выполняем запрос на регистрацию
            Call<User> call = authService.register(registerRequest);
            call.enqueue(new Callback<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {
                    // Скрываем индикатор загрузки
                    showProgress(false);
                    
                    if (response.isSuccessful() && response.body() != null) {
                        // Показываем сообщение об успешной регистрации
                        Toast.makeText(RegisterActivity.this, "Регистрация успешна", Toast.LENGTH_SHORT).show();
                        
                        // Переходим на главный экран
                        navigateToMainActivity();
                    } else {
                        // Показываем сообщение об ошибке
                        String errorMessage = "Ошибка регистрации";
                        try {
                            if (response.errorBody() != null) {
                                errorMessage = response.errorBody().string();
                            }
                        } catch (Exception e) {
                            Log.e(TAG, "Ошибка при чтении тела ошибки: ", e);
                        }
                        Toast.makeText(RegisterActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                    }
                }
                
                @Override
                public void onFailure(Call<User> call, Throwable t) {
                    // Скрываем индикатор загрузки
                    showProgress(false);
                    
                    // Показываем сообщение об ошибке
                    Toast.makeText(RegisterActivity.this, "Ошибка сети: " + t.getMessage(), Toast.LENGTH_SHORT).show();
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
     * Переход на главный экран
     */
    private void navigateToMainActivity() {
        try {
            Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        } catch (Exception e) {
            Log.e(TAG, "Ошибка при переходе на главный экран: ", e);
            Toast.makeText(this, "Ошибка перехода на главный экран: " + e.getMessage(), Toast.LENGTH_SHORT).show();
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