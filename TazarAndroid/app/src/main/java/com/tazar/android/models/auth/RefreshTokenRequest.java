package com.tazar.android.models.auth;

import com.google.gson.annotations.SerializedName;

/**
 * Запрос на обновление токена
 */
public class RefreshTokenRequest {
    @SerializedName("refresh")
    private String refreshToken;
    
    /**
     * Конструктор
     * @param refreshToken Токен обновления
     */
    public RefreshTokenRequest(String refreshToken) {
        this.refreshToken = refreshToken;
    }
    
    /**
     * Получение токена обновления
     * @return Токен обновления
     */
    public String getRefreshToken() {
        return refreshToken;
    }
    
    /**
     * Установка токена обновления
     * @param refreshToken Токен обновления
     */
    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
} 