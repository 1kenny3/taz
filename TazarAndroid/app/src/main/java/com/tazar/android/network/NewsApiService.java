package com.tazar.android.network;

import com.tazar.android.models.NewsResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface NewsApiService {
    @GET("v2/everything")
    Call<NewsResponse> getEcoNews(
        @Query("q") String query,
        @Query("language") String language,
        @Query("sortBy") String sortBy,
        @Query("apiKey") String apiKey
    );
} 