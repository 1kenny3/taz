package com.tazar.android.models.auth;

import com.google.gson.annotations.SerializedName;

/**
 * Запрос на авторизацию
 */
public class LoginRequest {
    @SerializedName("username")
    private String username;
    
    @SerializedName("password")
    private String password;
    
    /**
     * Конструктор
     * @param username Имя пользователя
     * @param password Пароль
     */
    public LoginRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }
    
    /**
     * Получение имени пользователя
     * @return Имя пользователя
     */
    public String getUsername() {
        return username;
    }
    
    /**
     * Установка имени пользователя
     * @param username Имя пользователя
     */
    public void setUsername(String username) {
        this.username = username;
    }
    
    /**
     * Получение пароля
     * @return Пароль
     */
    public String getPassword() {
        return password;
    }
    
    /**
     * Установка пароля
     * @param password Пароль
     */
    public void setPassword(String password) {
        this.password = password;
    }
} 