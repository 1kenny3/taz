package com.tazar.android.api.services;

import com.tazar.android.models.User;
import com.tazar.android.models.Achievement;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.PATCH;
import retrofit2.http.Part;
import retrofit2.http.Path;

/**
 * Сервис для работы с пользователями
 */
public interface UserService {
    /**
     * Получение информации о текущем пользователе
     * @return Пользователь
     */
    @GET("api/users/me/")
    Call<User> getCurrentUser();
    
    /**
     * Получение пользователя по ID
     * @param id ID пользователя
     * @return Пользователь
     */
    @GET("api/users/{id}/")
    Call<User> getUserById(@Path("id") int id);
    
    /**
     * Обновление профиля пользователя
     * @param id ID пользователя
     * @param user Обновленные данные
     * @return Обновленный пользователь
     */
    @PATCH("api/users/{id}/")
    Call<User> updateUser(@Path("id") int id, @Body User user);
    
    /**
     * Обновление фото профиля
     * @param id ID пользователя
     * @param photo Фото
     * @param bio Описание профиля
     * @return Обновленный пользователь
     */
    @Multipart
    @PATCH("api/users/{id}/")
    Call<User> updateUserPhoto(
            @Path("id") int id,
            @Part MultipartBody.Part photo,
            @Part("bio") RequestBody bio
    );
    
    /**
     * Получение достижений пользователя
     * @param id ID пользователя
     * @return Список достижений
     */
    @GET("api/users/{id}/achievements/")
    Call<List<Achievement>> getUserAchievements(@Path("id") int id);
} 