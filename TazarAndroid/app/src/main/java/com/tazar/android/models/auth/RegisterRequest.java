package com.tazar.android.models.auth;

import com.google.gson.annotations.SerializedName;

/**
 * Запрос на регистрацию
 */
public class RegisterRequest {
    @SerializedName("username")
    private String username;
    
    @SerializedName("email")
    private String email;
    
    @SerializedName("password")
    private String password;
    
    /**
     * Конструктор
     * @param username Имя пользователя
     * @param email Email
     * @param password Пароль
     */
    public RegisterRequest(String username, String email, String password) {
        this.username = username;
        this.email = email;
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
     * Получение email
     * @return Email
     */
    public String getEmail() {
        return email;
    }
    
    /**
     * Установка email
     * @param email Email
     */
    public void setEmail(String email) {
        this.email = email;
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