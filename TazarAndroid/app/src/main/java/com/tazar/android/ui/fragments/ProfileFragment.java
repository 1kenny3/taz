package com.tazar.android.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.button.MaterialButton;
import com.tazar.android.R;
import com.tazar.android.TazarApplication;
import com.tazar.android.api.ApiClient;
import com.tazar.android.api.AuthService;
import com.tazar.android.api.services.UserService;
import com.tazar.android.helpers.PreferencesManager;
import com.tazar.android.models.User;
import com.tazar.android.ui.auth.LoginActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileFragment extends Fragment {

    private static final String TAG = "ProfileFragment";

    private ImageView avatarImageView;
    private TextView usernameTextView;
    private TextView emailTextView;
    private TextView ratingTextView;
    private TextView achievementsTextView;
    private MaterialButton logoutButton;
    private View loadingView;

    private PreferencesManager preferencesManager;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Инициализация UI элементов
        avatarImageView = view.findViewById(R.id.avatarImageView);
        usernameTextView = view.findViewById(R.id.username_text_view);
        emailTextView = view.findViewById(R.id.email_text_view);
        ratingTextView = view.findViewById(R.id.rating_text_view);
        achievementsTextView = view.findViewById(R.id.achievements_text_view);
        logoutButton = view.findViewById(R.id.logout_button);
        loadingView = view.findViewById(R.id.loading_view);

        // Инициализация менеджера настроек
        preferencesManager = new PreferencesManager(requireContext());

        // Загрузка данных пользователя
        loadUserProfile();

        // Обработчик кнопки выхода
        logoutButton.setOnClickListener(v -> logout());

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private void loadUserProfile() {
        // Показываем загрузку
        showLoading(true);
        
        // Удаляем тестовый код и используем фактические данные с API
        UserService userService = ApiClient.getService(UserService.class);
        Call<User> call = userService.getCurrentUser(); // Используем метод getCurrentUser(), который уже существует
        
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                showLoading(false);
                Log.d(TAG, "Получен ответ от сервера: " + response.code());
                
                if (response.isSuccessful() && response.body() != null) {
                    User user = response.body();
                    // Логируем все поля пользователя для отладки
                    Log.d(TAG, "Пользователь:");
                    Log.d(TAG, "  ID: " + user.getId());
                    Log.d(TAG, "  Имя: " + user.getUsername());
                    Log.d(TAG, "  Email: " + user.getEmail());
                    Log.d(TAG, "  Аватар URL: " + user.getAvatarUrl());
                    Log.d(TAG, "  Фото профиля URL: " + user.getProfilePhotoUrl());
                    
                    displayUserProfile(user);
                } else {
                    String errorMsg = "Ошибка загрузки профиля: " + response.code();
                    if (response.errorBody() != null) {
                        try {
                            String errorBody = response.errorBody().string();
                            Log.e(TAG, "Тело ошибки: " + errorBody);
                        } catch (Exception e) {
                            Log.e(TAG, "Не удалось прочитать тело ошибки");
                        }
                    }
                    showError(errorMsg);
                }
            }
            
            @Override
            public void onFailure(Call<User> call, Throwable t) {
                showLoading(false);
                showError("Ошибка сети: " + t.getMessage());
                Log.e(TAG, "Ошибка при загрузке профиля", t);
            }
        });
    }

    private void displayUserProfile(User user) {
        // Устанавливаем имя пользователя и email
        usernameTextView.setText(user.getUsername());
        emailTextView.setText(user.getEmail());
        ratingTextView.setText(String.format("Очки: %d", user.getPoints()));
        achievementsTextView.setVisibility(View.GONE);

        // Загружаем аватар
        loadAvatar(user.getAvatarUrl());
    }

    private void loadAvatar(String avatarUrl) {
        Log.d(TAG, "Загрузка аватара с URL: " + avatarUrl);
        
        if (avatarUrl != null && !avatarUrl.isEmpty() && !avatarUrl.equals("null")) {
            // Проверяем, содержит ли URL протокол
            if (!avatarUrl.startsWith("http")) {
                // Если URL относительный, добавляем базовый URL
                avatarUrl = "http://10.0.2.2:8000" + avatarUrl;
                Log.d(TAG, "Преобразованный URL аватара: " + avatarUrl);
            }
            
            // Используем Glide для загрузки аватара
            Glide.with(this)
                .load(avatarUrl)
                .apply(RequestOptions.circleCropTransform()) // Делаем аватар круглым
                .placeholder(R.drawable.ic_default_avatar) // Изображение при загрузке
                .error(R.drawable.ic_error_avatar) // Изображение при ошибке
                .into(avatarImageView);
        } else {
            // Если URL аватара отсутствует, устанавливаем изображение по умолчанию
            Log.d(TAG, "URL аватара пустой, устанавливаем иконку по умолчанию");
            avatarImageView.setImageResource(R.drawable.ic_default_avatar);
        }
    }

    private void showLoading(boolean isLoading) {
        if (loadingView != null) {
            loadingView.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        }
    }

    private void showError(String message) {
        if (getContext() != null) {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
            Log.e(TAG, message);
        }
    }

    private void logout() {
        // Очищаем токен авторизации
        TazarApplication.getInstance().clearAuthToken();

        // Переходим на экран входа
        Intent intent = new Intent(requireContext(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
} 