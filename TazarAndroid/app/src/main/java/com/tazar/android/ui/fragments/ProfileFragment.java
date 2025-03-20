package com.tazar.android.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.tazar.android.R;
import com.tazar.android.TazarApplication;
import com.tazar.android.api.ApiClient;
import com.tazar.android.api.AuthService;
import com.tazar.android.models.User;
import com.tazar.android.ui.auth.LoginActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileFragment extends Fragment {

    private TextView usernameTextView;
    private TextView emailTextView;
    private TextView ratingTextView;
    private TextView achievementsTextView;
    private MaterialButton logoutButton;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Инициализация UI элементов
        usernameTextView = view.findViewById(R.id.username_text_view);
        emailTextView = view.findViewById(R.id.email_text_view);
        ratingTextView = view.findViewById(R.id.rating_text_view);
        achievementsTextView = view.findViewById(R.id.achievements_text_view);
        logoutButton = view.findViewById(R.id.logout_button);

        // Загрузка данных пользователя
        loadUserProfile();

        // Обработчик кнопки выхода
        logoutButton.setOnClickListener(v -> logout());

        return view;
    }

    private void loadUserProfile() {
        AuthService service = ApiClient.getService(AuthService.class);
        String token = TazarApplication.getInstance().getAuthToken();
        Call<User> call = service.getCurrentUser(token);

        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    User user = response.body();
                    updateUI(user);
                } else {
                    Toast.makeText(requireContext(), "Ошибка загрузки профиля", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                Toast.makeText(requireContext(), "Ошибка сети: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUI(User user) {
        usernameTextView.setText(user.getUsername());
        emailTextView.setText(user.getEmail());
        ratingTextView.setText(String.format("Очки: %d", user.getPoints()));
        achievementsTextView.setVisibility(View.GONE);
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