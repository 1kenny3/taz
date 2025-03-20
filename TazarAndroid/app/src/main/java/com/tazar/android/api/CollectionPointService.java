package com.tazar.android.api;

import com.tazar.android.models.CollectionPoint;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import java.util.List;

public interface CollectionPointService {
    @GET("api/collection-points")
    Call<List<CollectionPoint>> getCollectionPoints(@Header("Authorization") String token);
} 