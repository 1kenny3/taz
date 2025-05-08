package com.tazar.android.api;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import com.tazar.android.BuildConfig;
import com.tazar.android.EcoupApplication;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.util.concurrent.TimeUnit;

public class ApiConfig {
    private static final String TAG = "ApiConfig";
    
    // Обновляем URL на правильный эндпоинт Django
    private static final String BASE_URL = "http://10.0.2.2:8000/"; // Для эмулятора
    // private static final String BASE_URL = "http://192.168.1.x:8000/"; // Для физического устройства
    
    private static Retrofit retrofit = null;
    private static OkHttpClient client = null;

    private static OkHttpClient getClient() {
        if (client == null) {
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(message -> 
                Log.d(TAG, message));
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            
            client = new OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .addInterceptor(loggingInterceptor)
                    .build();
        }
        return client;
    }

    public static Retrofit getRetrofit() {
        if (retrofit == null) {
            // Создаем Gson с более терпимым отношением к ошибкам JSON
            Gson gson = new GsonBuilder()
                    .setLenient()
                    .create();
            
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(getClient())
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
            
            Log.d(TAG, "Retrofit создан с BASE_URL: " + BASE_URL);
        }
        return retrofit;
    }

    public static <T> T getService(Class<T> serviceClass) {
        return getRetrofit().create(serviceClass);
    }

    public static String getBaseUrl() {
        // Получаем URL из настроек приложения, если он там есть
        String savedUrl = EcoupApplication.getInstance().getServerUrl();
        return savedUrl != null ? savedUrl : BASE_URL;
    }

    public static void resetClient() {
        retrofit = null;
        client = null;
    }
} 