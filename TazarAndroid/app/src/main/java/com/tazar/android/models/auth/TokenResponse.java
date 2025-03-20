package com.tazar.android.models.auth;

import com.google.gson.annotations.SerializedName;

/**
 * Ответ с токенами авторизации
 */
public class TokenResponse {
    @SerializedName("access")
    private String accessToken;
    
    @SerializedName("refresh")
    private String refreshToken;
    
    /**
     * Получение токена доступа
     * @return Токен доступа
     */
    public String getAccessToken() {
        return accessToken;
    }
    
    /**
     * Установка токена доступа
     * @param accessToken Токен доступа
     */
    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
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