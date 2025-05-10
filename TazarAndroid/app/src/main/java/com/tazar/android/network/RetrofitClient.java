package com.tazar.android.network;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import java.util.concurrent.TimeUnit;

public class RetrofitClient {
    private static final String NEWS_API_BASE_URL = "https://newsapi.org/";
    // Используем IP-адрес сервера из лога
    private static final String LOCAL_API_BASE_URL = "http://172.20.10.2:8000/";
    
    private static NewsApiService newsApiService;
    private static ApiService apiService;

    public static NewsApiService getNewsApiService() {
        if (newsApiService == null) {
            newsApiService = createRetrofit(NEWS_API_BASE_URL).create(NewsApiService.class);
        }
        return newsApiService;
    }

    public static ApiService getApiService() {
        if (apiService == null) {
            apiService = createRetrofit(LOCAL_API_BASE_URL).create(ApiService.class);
        }
        return apiService;
    }

    private static Retrofit createRetrofit(String baseUrl) {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build();

        return new Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build();
    }
}