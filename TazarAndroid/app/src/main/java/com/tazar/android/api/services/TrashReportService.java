package com.tazar.android.api.services;

import com.tazar.android.models.Comment;
import com.tazar.android.models.TrashReport;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Сервис для работы с отчетами о мусоре
 */
public interface TrashReportService {
    /**
     * Получение списка всех отчетов
     * @return Список отчетов
     */
    @GET("api/trash-reports/")
    Call<List<TrashReport>> getAllTrashReports();
    
    /**
     * Получение списка отчетов пользователя
     * @param userId ID пользователя
     * @return Список отчетов
     */
    @GET("api/trash-reports/")
    Call<List<TrashReport>> getTrashReportsByUser(@Query("user") int userId);
    
    /**
     * Получение списка отчетов по статусу
     * @param status Статус отчета
     * @return Список отчетов
     */
    @GET("api/trash-reports/")
    Call<List<TrashReport>> getTrashReportsByStatus(@Query("status") String status);
    
    /**
     * Получение отчета по ID
     * @param id ID отчета
     * @return Отчет
     */
    @GET("api/trash-reports/{id}/")
    Call<TrashReport> getTrashReportById(@Path("id") int id);
    
    /**
     * Создание нового отчета
     * @param address Адрес
     * @param description Описание
     * @param photo Фото
     * @param latitude Широта
     * @param longitude Долгота
     * @return Созданный отчет
     */
    @Multipart
    @POST("api/report-trash/")
    Call<TrashReport> createTrashReport(
            @Part("address") RequestBody address,
            @Part("description") RequestBody description,
            @Part MultipartBody.Part photo,
            @Part("latitude") RequestBody latitude,
            @Part("longitude") RequestBody longitude
    );
    
    /**
     * Обновление статуса отчета
     * @param id ID отчета
     * @param status Новый статус
     * @return Обновленный отчет
     */
    @PATCH("api/trash-reports/{id}/")
    Call<TrashReport> updateTrashReportStatus(@Path("id") int id, @Body TrashReport status);
    
    /**
     * Удаление отчета
     * @param id ID отчета
     * @return Результат операции
     */
    @DELETE("api/trash-reports/{id}/")
    Call<Void> deleteTrashReport(@Path("id") int id);
    
    /**
     * Получение комментариев к отчету
     * @param reportId ID отчета
     * @return Список комментариев
     */
    @GET("api/trash-reports/{id}/comments/")
    Call<List<Comment>> getTrashReportComments(@Path("id") int reportId);
    
    /**
     * Добавление комментария к отчету
     * @param reportId ID отчета
     * @param comment Комментарий
     * @return Добавленный комментарий
     */
    @POST("api/trash-reports/{id}/comments/")
    Call<Comment> addTrashReportComment(@Path("id") int reportId, @Body Comment comment);

    @GET("api/trash-reports/")
    Call<List<TrashReport>> getTrashReports(@Header("Authorization") String token);
    
    @GET("api/trash-reports/")
    Call<List<TrashReport>> getTrashReportsByStatus(
        @Header("Authorization") String token,
        @Query("status") String status
    );
} 