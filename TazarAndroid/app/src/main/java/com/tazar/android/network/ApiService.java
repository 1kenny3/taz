package com.tazar.android.network;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.GET;
import retrofit2.http.Header;

public interface ApiService {
    @POST("api/auth/login/")
    Call<LoginResponse> login(@Body LoginRequest loginRequest);
    
    @POST("api/auth/register/")
    Call<RegisterResponse> register(@Body RegisterRequest registerRequest);
    
    @GET("api/auth/user/")
    Call<UserResponse> getUserProfile(@Header("Authorization") String token);
}

class LoginRequest {
    private String email;
    private String password;
    
    public LoginRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }
}

class LoginResponse {
    private String token;
    private String refreshToken;
    private UserResponse user;
    
    public String getToken() { return token; }
    public String getRefreshToken() { return refreshToken; }
    public UserResponse getUser() { return user; }
}

class RegisterRequest {
    private String email;
    private String password;
    private String name;
    
    public RegisterRequest(String email, String password, String name) {
        this.email = email;
        this.password = password;
        this.name = name;
    }
}

class RegisterResponse {
    private String token;
    private UserResponse user;
    
    public String getToken() { return token; }
    public UserResponse getUser() { return user; }
}

class UserResponse {
    private Long id;
    private String email;
    private String name;
    
    public Long getId() { return id; }
    public String getEmail() { return email; }
    public String getName() { return name; }
} 