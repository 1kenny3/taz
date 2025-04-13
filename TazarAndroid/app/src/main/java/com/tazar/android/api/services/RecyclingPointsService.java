package com.tazar.android.api.services;

import com.tazar.android.models.RecyclingPoint;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

public interface RecyclingPointsService {
    // Точный путь к API пунктов переработки в Django
    @GET("api/collection-points/")  // Изменено с recycling-points на collection-points
    Call<List<RecyclingPoint>> getRecyclingPoints(
        @Header("Authorization") String token,
        @Query("waste_type") String wasteType
    );
    
    // Вариант с токеном авторизации без фильтрации
    @GET("api/collection-points/")  // Изменено с recycling-points на collection-points
    Call<List<RecyclingPoint>> getRecyclingPoints(
        @Header("Authorization") String token
    );
    
    // Простой вариант без токена
    @GET("api/collection-points/")  // Изменено с recycling-points на collection-points
    Call<List<RecyclingPoint>> getRecyclingPoints();
} 