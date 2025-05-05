package com.tazar.android.api;

import com.tazar.android.models.Report;
import com.tazar.android.models.User;
import com.tazar.android.models.LoginResponse;
import com.tazar.android.models.CollectionPoint;
import com.tazar.android.models.NewsResponse;
import com.tazar.android.models.TazarNews;
import com.tazar.android.models.TrashReport;
import com.tazar.android.models.auth.LoginRequest;
import com.tazar.android.models.auth.TokenResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Интерфейс для описания API endpoints
 */
public interface ApiService {
    
    @FormUrlEncoded
    @POST("api/token/")
    Call<LoginResponse> login(
        @Field("username") String username,
        @Field("password") String password
    );
    
    @GET("api/users/{id}/")
    Call<User> getUser(@Path("id") int userId);
    
    @POST("api/reports/")
    Call<Report> submitReport(@Body Report report);
    
    @GET("api/trash-reports/")
    Call<List<Report>> getReports();
    
    // Аутентификация
    @POST("api/token/")
    Call<TokenResponse> getToken(@Body LoginRequest loginRequest);
    
    // Отчеты о мусоре
    @GET("api/trash-reports/")
    Call<List<TrashReport>> getTrashReports();
    
    // Пункты приема
    @GET("api/collection-points/")
    Call<List<CollectionPoint>> getCollectionPoints(
        @Query("waste_types") String wasteType
    );
    
    // Новости
    @GET("api/news/")
    Call<List<TazarNews>> getNews();
    
    // Другие API методы...
} 