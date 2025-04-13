package com.tazar.android.api.services;

import com.tazar.android.models.RecyclingPoint;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;

public interface RecyclingPointsService {
    @GET("api/v1/recycling-points/")
    Call<List<RecyclingPoint>> getRecyclingPoints(@Header("Authorization") String token);
} 