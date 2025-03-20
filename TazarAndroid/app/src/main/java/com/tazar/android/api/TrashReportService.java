package com.tazar.android.api;

import com.tazar.android.models.TrashReport;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import java.util.List;

public interface TrashReportService {
    @GET("api/trash-reports")
    Call<List<TrashReport>> getTrashReports(@Header("Authorization") String token);
} 