package com.tazar.android.api.services;

import com.tazar.android.models.CollectionPoint;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Сервис для работы с пунктами сбора
 */
public interface CollectionPointService {
    /**
     * Получение списка всех пунктов сбора
     * @return Список пунктов сбора
     */
    @GET("api/collection-points/")
    Call<List<CollectionPoint>> getAllCollectionPoints();
    
    /**
     * Получение списка пунктов сбора по типу отходов
     * @param wasteType Тип отходов
     * @return Список пунктов сбора
     */
    @GET("api/collection-points/")
    Call<List<CollectionPoint>> getCollectionPointsByWasteType(@Query("waste_types") String wasteType);
    
    /**
     * Получение списка пунктов сбора по городу
     * @param city Город
     * @return Список пунктов сбора
     */
    @GET("api/collection-points/")
    Call<List<CollectionPoint>> getCollectionPointsByCity(@Query("city") String city);
    
    /**
     * Получение пункта сбора по ID
     * @param id ID пункта сбора
     * @return Пункт сбора
     */
    @GET("api/collection-points/{id}/")
    Call<CollectionPoint> getCollectionPointById(@Path("id") int id);
    
    /**
     * Создание нового пункта сбора
     * @param collectionPoint Данные пункта сбора
     * @return Созданный пункт сбора
     */
    @POST("api/collection-points/")
    Call<CollectionPoint> createCollectionPoint(@Body CollectionPoint collectionPoint);
    
    /**
     * Обновление пункта сбора
     * @param id ID пункта сбора
     * @param collectionPoint Обновленные данные
     * @return Обновленный пункт сбора
     */
    @PUT("api/collection-points/{id}/")
    Call<CollectionPoint> updateCollectionPoint(@Path("id") int id, @Body CollectionPoint collectionPoint);
    
    /**
     * Удаление пункта сбора
     * @param id ID пункта сбора
     * @return Результат операции
     */
    @DELETE("api/collection-points/{id}/")
    Call<Void> deleteCollectionPoint(@Path("id") int id);
} 