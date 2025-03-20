package com.tazar.android.api;

import com.tazar.android.models.User;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;

public interface AuthService {
    @GET("api/users/me/")
    Call<User> getCurrentUser(@Header("Authorization") String token);
} 