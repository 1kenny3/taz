package com.tazar.android.api;

import com.tazar.android.models.Report;
import com.tazar.android.models.User;
import com.tazar.android.models.LoginResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

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
    
    // Другие API методы...
} 