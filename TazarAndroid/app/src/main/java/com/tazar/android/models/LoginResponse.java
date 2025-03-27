package com.tazar.android.models;

import com.google.gson.annotations.SerializedName;

/**
 * Модель для ответа при входе в систему
 */
public class LoginResponse {
    
    @SerializedName("access")
    private String accessToken;
    
    @SerializedName("refresh")
    private String refreshToken;
    
    @SerializedName("user")
    private User user;
    
    // Геттеры и сеттеры
    public String getAccessToken() {
        return accessToken;
    }
    
    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
    
    public String getRefreshToken() {
        return refreshToken;
    }
    
    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
    
    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
    }
    
    @Override
    public String toString() {
        return "LoginResponse{" +
                "accessToken='" + accessToken + '\'' +
                ", refreshToken='" + refreshToken + '\'' +
                ", user=" + user +
                '}';
    }
} 