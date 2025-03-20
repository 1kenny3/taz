package com.tazar.android.api;

import android.content.Context;

import com.tazar.android.config.ApiConfig;
import com.tazar.android.utils.TokenAuthenticator;
import com.tazar.android.utils.TokenInterceptor;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Клиент для работы с API
 */
public class ApiClient {
    // Базовый URL для API
    // Для эмулятора Android использую адрес 10.0.2.2 (соответствует localhost на компьютере разработчика)
    private static final String BASE_URL = "http://10.0.2.2:8000/";
    private static Retrofit retrofit = null;
    private static OkHttpClient okHttpClient;
    private static boolean isInitialized = false;
    
    /**
     * Проверка инициализации ApiClient
     * @return true, если ApiClient инициализирован
     */
    public static boolean isInitialized() {
        return isInitialized && retrofit != null && okHttpClient != null;
    }
    
    /**
     * Инициализация API клиента
     * @param context Контекст приложения
     */
    public static void init(Context context) {
        if (isInitialized()) {
            return;
        }
        
        try {
            // Создаем логирующий интерцептор для отладки
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            
            // Создаем интерцептор для добавления токена в заголовки
            TokenInterceptor tokenInterceptor = new TokenInterceptor(context);
            
            // Создаем аутентификатор для обновления токена при 401 ошибке
            TokenAuthenticator tokenAuthenticator = new TokenAuthenticator(context);
            
            // Настраиваем HTTP клиент
            okHttpClient = new OkHttpClient.Builder()
                    .connectTimeout(ApiConfig.CONNECTION_TIMEOUT, TimeUnit.SECONDS)
                    .readTimeout(ApiConfig.READ_TIMEOUT, TimeUnit.SECONDS)
                    .writeTimeout(ApiConfig.WRITE_TIMEOUT, TimeUnit.SECONDS)
                    .addInterceptor(tokenInterceptor)
                    .addInterceptor(loggingInterceptor)
                    .authenticator(tokenAuthenticator)
                    .build();
            
            // Создаем Retrofit клиент
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            
            isInitialized = true;
        } catch (Exception e) {
            isInitialized = false;
            throw new RuntimeException("Ошибка инициализации ApiClient: " + e.getMessage(), e);
        }
    }
    
    /**
     * Получение сервиса для работы с API
     * @param serviceClass Класс сервиса
     * @param <T> Тип сервиса
     * @return Сервис
     */
    public static <T> T getService(Class<T> serviceClass) {
        if (retrofit == null) {
            throw new IllegalStateException("ApiClient не инициализирован. Вызовите ApiClient.init(context) перед использованием.");
        }
        return retrofit.create(serviceClass);
    }
    
    /**
     * Получение HTTP клиента
     * @return OkHttpClient
     */
    public static OkHttpClient getOkHttpClient() {
        if (okHttpClient == null) {
            throw new IllegalStateException("ApiClient не инициализирован. Вызовите ApiClient.init(context) перед использованием.");
        }
        return okHttpClient;
    }

    public static Retrofit getClient() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
} 