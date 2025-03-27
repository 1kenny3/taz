package com.tazar.android.models.auth;

import com.google.gson.annotations.SerializedName;

/**
 * Модель ответа с токенами авторизации
 */
public class TokenResponse {
    @SerializedName("access")
    private String accessToken;
    
    @SerializedName("refresh")
    private String refreshToken;
    
    @SerializedName("user_id")
    private int userId;
    
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
    
    public int getUserId() {
        return userId;
    }
    
    public void setUserId(int userId) {
        this.userId = userId;
    }
    
    @Override
    public String toString() {
        return "TokenResponse{" +
                "accessToken='" + accessToken + '\'' +
                ", refreshToken='" + refreshToken + '\'' +
                ", userId=" + userId +
                '}';
    }
} 