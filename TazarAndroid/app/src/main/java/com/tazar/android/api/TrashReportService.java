package com.tazar.android.api;

import com.tazar.android.models.TrashReport;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface TrashReportService {
    @Multipart
    @POST("api/trash-reports/")
    Call<Void> createReport(
            @Header("Authorization") String token,
            @Part("latitude") RequestBody latitude,
            @Part("longitude") RequestBody longitude,
            @Part("description") RequestBody description,
            @Part MultipartBody.Part photo,
            @Part("address") RequestBody address,
            @Part("user") RequestBody user
    );
    
    @GET("api/trash-reports/")
    Call<List<TrashReport>> getTrashReports(@Header("Authorization") String token);
} 