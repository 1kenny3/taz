package com.tazar.android.config;

import com.tazar.android.api.ApiClient;

/**
 * Класс с конфигурацией для работы с API
 */
public class ApiConfig {
    // Базовый URL для API
    // Для эмулятора Android использую адрес 10.0.2.2 (соответствует localhost на компьютере разработчика)
    public static final String BASE_URL = "http://10.0.2.2:8000/";
    public static final int CONNECTION_TIMEOUT = 30; // Таймаут подключения в секундах
    public static final int READ_TIMEOUT = 30; // Таймаут чтения в секундах
    public static final int WRITE_TIMEOUT = 30; // Таймаут записи в секундах

    /**
     * Получение сервиса для работы с API
     * @param serviceClass Класс сервиса
     * @param <T> Тип сервиса
     * @return Сервис
     */
    public static <T> T getService(Class<T> serviceClass) {
        return ApiClient.getService(serviceClass);
    }
} 