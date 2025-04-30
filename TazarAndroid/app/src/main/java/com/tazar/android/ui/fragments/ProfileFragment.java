package com.tazar.android.ui.fragments;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
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
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.signature.ObjectKey;
import com.google.android.material.button.MaterialButton;
import com.tazar.android.R;
import com.tazar.android.TazarApplication;
import com.tazar.android.api.ApiClient;
import com.tazar.android.api.services.UserService;
import com.tazar.android.helpers.PreferencesManager;
import com.tazar.android.models.User;
import com.tazar.android.ui.auth.LoginActivity;
import com.tazar.android.utils.UrlUtil;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import okhttp3.OkHttpClient;
import java.util.concurrent.TimeUnit;

public class ProfileFragment extends Fragment {
    private static final String TAG = "ProfileFragment";

    private ImageView profilePhotoImageView;
    private ImageView avatarImageView;
    private TextView usernameTextView;
    private TextView emailTextView;
    private TextView ratingTextView;
    private TextView achievementsTextView;
    private TextView bioTextView;
    private MaterialButton logoutButton;
    private View loadingView;

    private PreferencesManager preferencesManager;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Инициализация UI элементов
        profilePhotoImageView = view.findViewById(R.id.profile_photo);
        avatarImageView = view.findViewById(R.id.avatarImageView);
        usernameTextView = view.findViewById(R.id.username_text_view);
        emailTextView = view.findViewById(R.id.email_text_view);
        ratingTextView = view.findViewById(R.id.rating_text_view);
        achievementsTextView = view.findViewById(R.id.achievements_text_view);
        bioTextView = view.findViewById(R.id.bio_text_view);
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

    private void loadUserProfile() {
        showLoading(true);
        
        UserService userService = ApiClient.getService(UserService.class);
        Call<User> call = userService.getCurrentUser();
        
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                showLoading(false);
                if (response.isSuccessful() && response.body() != null) {
                    displayUserProfile(response.body());
                } else {
                    showError("Ошибка загрузки профиля: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                showLoading(false);
                showError("Ошибка сети: " + t.getMessage());
            }
        });
    }

    private void displayUserProfile(User user) {
        usernameTextView.setText(user.getUsername());
        emailTextView.setText(user.getEmail());
        ratingTextView.setText(String.format(getString(R.string.rating), user.getRating()));
        achievementsTextView.setText(String.format(getString(R.string.achievements_count), user.getAchievementsCount()));
        
        if (user.getBio() != null && !user.getBio().isEmpty()) {
            bioTextView.setVisibility(View.VISIBLE);
            bioTextView.setText(user.getBio());
        } else {
            bioTextView.setVisibility(View.GONE);
        }

        loadProfilePhoto(user.getProfilePhotoUrl());
        loadAvatar(user.getAvatarUrl());
    }

    private void loadProfilePhoto(String photoUrl) {
        final String finalPhotoUrl = UrlUtil.processUrl(photoUrl);
        
        if (finalPhotoUrl != null && !finalPhotoUrl.isEmpty() && !finalPhotoUrl.equals("null")) {
            try {
                // Создаем OkHttpClient с увеличенными таймаутами
                OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(60, TimeUnit.SECONDS)
                    .readTimeout(60, TimeUnit.SECONDS)
                    .writeTimeout(60, TimeUnit.SECONDS)
                    .build();

                // Создаем GlideUrl с кастомными заголовками
                GlideUrl glideUrl = new GlideUrl(finalPhotoUrl, new LazyHeaders.Builder()
                    .addHeader("User-Agent", "Tazar-Android")
                    .build());

                RequestOptions requestOptions = new RequestOptions()
                    .diskCacheStrategy(DiskCacheStrategy.NONE) // Отключаем кэширование на диск
                    .skipMemoryCache(true) // Отключаем кэширование в памяти
                    .format(DecodeFormat.PREFER_RGB_565)
                    .override(Target.SIZE_ORIGINAL) // Используем оригинальный размер
                    .signature(new ObjectKey(System.currentTimeMillis())) // Добавляем подпись для обхода кэша
                    .placeholder(R.drawable.profile_photo_placeholder)
                    .error(R.drawable.profile_photo_placeholder);

                Glide.with(requireContext())
                    .load(glideUrl)
                    .apply(requestOptions)
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model,
                                                  Target<Drawable> target, boolean isFirstResource) {
                            Log.e(TAG, "Ошибка загрузки фото профиля: " + finalPhotoUrl, e);
                            if (e != null) {
                                for (Throwable t : e.getRootCauses()) {
                                    Log.e(TAG, "Причина: " + t.getMessage());
                                }
                            }
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model,
                                                     Target<Drawable> target, DataSource dataSource,
                                                     boolean isFirstResource) {
                            Log.d(TAG, "Фото профиля успешно загружено");
                            return false;
                        }
                    })
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(profilePhotoImageView);

            } catch (Exception e) {
                Log.e(TAG, "Ошибка при загрузке фото профиля", e);
                profilePhotoImageView.setImageResource(R.drawable.profile_photo_placeholder);
            }
        } else {
            profilePhotoImageView.setImageResource(R.drawable.profile_photo_placeholder);
        }
    }

    private void loadAvatar(String avatarUrl) {
        final String finalAvatarUrl = UrlUtil.processUrl(avatarUrl);
        
        if (finalAvatarUrl != null && !finalAvatarUrl.isEmpty() && !finalAvatarUrl.equals("null")) {
            try {
                // Создаем OkHttpClient с увеличенными таймаутами
                OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(60, TimeUnit.SECONDS)
                    .readTimeout(60, TimeUnit.SECONDS)
                    .writeTimeout(60, TimeUnit.SECONDS)
                    .build();

                // Создаем GlideUrl с кастомными заголовками
                GlideUrl glideUrl = new GlideUrl(finalAvatarUrl, new LazyHeaders.Builder()
                    .addHeader("User-Agent", "Tazar-Android")
                    .build());

                RequestOptions requestOptions = new RequestOptions()
                    .diskCacheStrategy(DiskCacheStrategy.NONE) // Отключаем кэширование на диск
                    .skipMemoryCache(true) // Отключаем кэширование в памяти
                    .format(DecodeFormat.PREFER_RGB_565)
                    .override(300, 300) // Фиксированный размер для аватара
                    .signature(new ObjectKey(System.currentTimeMillis())) // Добавляем подпись для обхода кэша
                    .placeholder(R.drawable.ic_default_avatar)
                    .error(R.drawable.ic_error_avatar)
                    .circleCrop();

                Glide.with(requireContext())
                    .load(glideUrl)
                    .apply(requestOptions)
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model,
                                                  Target<Drawable> target, boolean isFirstResource) {
                            Log.e(TAG, "Ошибка загрузки аватара: " + finalAvatarUrl, e);
                            if (e != null) {
                                for (Throwable t : e.getRootCauses()) {
                                    Log.e(TAG, "Причина: " + t.getMessage());
                                }
                            }
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model,
                                                     Target<Drawable> target, DataSource dataSource,
                                                     boolean isFirstResource) {
                            Log.d(TAG, "Аватар успешно загружен");
                            return false;
                        }
                    })
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(avatarImageView);

            } catch (Exception e) {
                Log.e(TAG, "Ошибка при загрузке аватара", e);
                avatarImageView.setImageResource(R.drawable.ic_default_avatar);
            }
        } else {
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
        }
    }

    private void logout() {
        TazarApplication.getInstance().clearAuthToken();
        Intent intent = new Intent(requireContext(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
} 