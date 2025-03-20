package com.tazar.android.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.tazar.android.R;
import com.tazar.android.TazarApplication;
import com.tazar.android.api.ApiClient;
import com.tazar.android.api.TrashReportService;
import com.tazar.android.models.TrashReport;
import com.tazar.android.ui.adapters.TrashReportAdapter;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReportsFragment extends Fragment {

    private RecyclerView recyclerView;
    private TrashReportAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reports, container, false);

        recyclerView = view.findViewById(R.id.reports_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new TrashReportAdapter(new ArrayList<>());
        recyclerView.setAdapter(adapter);

        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this::loadReports);

        // Загружаем отчеты при создании фрагмента
        loadReports();

        return view;
    }

    private void loadReports() {
        TrashReportService service = ApiClient.getService(TrashReportService.class);
        String token = "Bearer " + TazarApplication.getInstance().getAuthToken();
        Call<List<TrashReport>> call = service.getTrashReports(token);

        call.enqueue(new Callback<List<TrashReport>>() {
            @Override
            public void onResponse(@NonNull Call<List<TrashReport>> call,
                                 @NonNull Response<List<TrashReport>> response) {
                swipeRefreshLayout.setRefreshing(false);

                if (response.isSuccessful() && response.body() != null) {
                    adapter.updateReports(response.body());
                } else {
                    Toast.makeText(requireContext(), "Ошибка загрузки отчетов", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<TrashReport>> call, @NonNull Throwable t) {
                swipeRefreshLayout.setRefreshing(false);
                Toast.makeText(requireContext(), "Ошибка сети: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
} 