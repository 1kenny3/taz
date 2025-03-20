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
    private static Retrofit retrofit;
    private static OkHttpClient okHttpClient;
    
    /**
     * Инициализация API клиента
     * @param context Контекст приложения
     */
    public static void init(Context context) {
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
                .baseUrl(ApiConfig.BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
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
} 