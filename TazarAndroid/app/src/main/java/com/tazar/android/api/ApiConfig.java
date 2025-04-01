package com.tazar.android.api;

import com.tazar.android.BuildConfig;
import com.tazar.android.TazarApplication;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiConfig {
    private static final String BASE_URL = "http://10.0.2.2:8000/"; // URL для эмулятора
    private static Retrofit retrofit = null;

    public static Retrofit getClient() {
        if (retrofit == null) {
            OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

            // Добавляем логирование для отладки
            if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
                logging.setLevel(HttpLoggingInterceptor.Level.BODY);
                httpClient.addInterceptor(logging);
            }

            retrofit = new Retrofit.Builder()
                    .baseUrl(getBaseUrl())
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(httpClient.build())
                    .build();
        }
        return retrofit;
    }

    public static <T> T getService(Class<T> serviceClass) {
        return getClient().create(serviceClass);
    }

    public static String getBaseUrl() {
        // Получаем URL из настроек приложения, если он там есть
        String savedUrl = TazarApplication.getInstance().getServerUrl();
        return savedUrl != null ? savedUrl : BASE_URL;
    }

    public static void resetClient() {
        retrofit = null;
    }
} 