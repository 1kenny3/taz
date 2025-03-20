package com.tazar.android.config;

/**
 * Конфигурация API
 * 
 * Содержит константы для работы с API-сервером
 */
public class ApiConfig {
    
    /**
     * Базовый URL API сервера
     * 
     * Для локальной разработки используйте:
     * - http://10.0.2.2:8000/ для эмулятора Android (соответствует localhost компьютера)
     * - http://ваш_локальный_ip:8000/ для физического устройства в той же сети
     */
    public static final String BASE_URL = "http://10.0.2.2:8000/";
    
    /**
     * URL для авторизации и получения токена
     */
    public static final String AUTH_TOKEN_URL = "api/token/";
    
    /**
     * URL для обновления токена
     */
    public static final String REFRESH_TOKEN_URL = "api/token/refresh/";
    
    /**
     * URL для регистрации
     */
    public static final String REGISTER_URL = "api/register/";
    
    /**
     * URL API
     */
    public static final String API_URL = "api/";
    
    /**
     * Базовый URL для медиа-файлов
     */
    public static final String MEDIA_URL = BASE_URL + "media/";
    
    /**
     * Тайм-аут соединения (в секундах)
     */
    public static final int CONNECTION_TIMEOUT = 30;
    
    /**
     * Тайм-аут чтения (в секундах)
     */
    public static final int READ_TIMEOUT = 30;
    
    /**
     * Тайм-аут записи (в секундах)
     */
    public static final int WRITE_TIMEOUT = 30;
} 