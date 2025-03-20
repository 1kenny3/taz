package com.tazar.android.api.services;

import com.tazar.android.models.auth.LoginRequest;
import com.tazar.android.models.auth.RefreshTokenRequest;
import com.tazar.android.models.auth.RegisterRequest;
import com.tazar.android.models.auth.TokenResponse;
import com.tazar.android.models.User;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

/**
 * Сервис для работы с аутентификацией
 */
public interface AuthService {
    /**
     * Авторизация пользователя
     * @param loginRequest Запрос на авторизацию
     * @return Ответ с токенами
     */
    @POST("api/token/")
    Call<TokenResponse> login(@Body LoginRequest loginRequest);
    
    /**
     * Регистрация пользователя
     * @param registerRequest Запрос на регистрацию
     * @return Зарегистрированный пользователь
     */
    @POST("api/register/")
    Call<User> register(@Body RegisterRequest registerRequest);
    
    /**
     * Обновление токена
     * @param refreshTokenRequest Запрос на обновление токена
     * @return Ответ с новыми токенами
     */
    @POST("api/token/refresh/")
    Call<TokenResponse> refreshToken(@Body RefreshTokenRequest refreshTokenRequest);

    /**
     * Получение текущего пользователя
     * @return Текущий пользователь
     */
    @GET("api/users/me/")
    Call<User> getCurrentUser();
} 