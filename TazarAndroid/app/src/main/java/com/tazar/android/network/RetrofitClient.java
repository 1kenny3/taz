package com.tazar.android.network;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static final String NEWS_API_BASE_URL = "https://newsapi.org/";
    private static NewsApiService newsApiService;

    public static NewsApiService getNewsApiService() {
        if (newsApiService == null) {
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .build();

            Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(NEWS_API_BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

            newsApiService = retrofit.create(NewsApiService.class);
        }
        return newsApiService;
    }
} 