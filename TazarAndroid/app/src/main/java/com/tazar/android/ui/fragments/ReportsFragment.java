package com.tazar.android.ui.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReportsFragment extends Fragment {
    private static final String TAG = "ReportsFragment";

    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView emptyView;
    private TrashReportAdapter adapter;
    private List<TrashReport> reports = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reports, container, false);
        
        recyclerView = view.findViewById(R.id.reports_recycler_view);
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);
        emptyView = view.findViewById(R.id.empty_view);
        
        setupRecyclerView();
        setupSwipeRefresh();
        
        // Загружаем отчеты при создании фрагмента
        loadReports();
        
        return view;
    }
    
    private void setupRecyclerView() {
        adapter = new TrashReportAdapter(reports);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
    }
    
    private void setupSwipeRefresh() {
        swipeRefreshLayout.setOnRefreshListener(this::loadReports);
        swipeRefreshLayout.setColorSchemeResources(
                android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light
        );
    }
    
    private void loadReports() {
        String token = TazarApplication.getInstance().getAuthToken();
        if (token == null || token.isEmpty()) {
            showError("Необходима авторизация");
            swipeRefreshLayout.setRefreshing(false);
            return;
        }
        
        // Получаем ID текущего пользователя
        String userId = TazarApplication.getInstance().getUserId();
        if (userId == null || userId.isEmpty()) {
            showError("Не удалось получить ID пользователя");
            swipeRefreshLayout.setRefreshing(false);
            return;
        }
        
        TrashReportService service = ApiClient.getInstance().create(TrashReportService.class);
        Call<List<TrashReport>> call = service.getTrashReports("Bearer " + token);
        
        call.enqueue(new Callback<List<TrashReport>>() {
            @Override
            public void onResponse(Call<List<TrashReport>> call, Response<List<TrashReport>> response) {
                swipeRefreshLayout.setRefreshing(false);
                
                if (response.isSuccessful() && response.body() != null) {
                    List<TrashReport> allReports = response.body();
                    
                    // Фильтруем отчеты, оставляя только те, которые принадлежат текущему пользователю
                    int currentUserId = Integer.parseInt(userId);
                    List<TrashReport> userReports = allReports.stream()
                            .filter(report -> report.getUserId() == currentUserId)
                            .collect(Collectors.toList());
                    
                    updateUI(userReports);
                } else {
                    Log.e(TAG, "Ошибка получения отчетов: " + response.code());
                    showError("Ошибка получения отчетов: " + response.code());
                }
            }
            
            @Override
            public void onFailure(Call<List<TrashReport>> call, Throwable t) {
                swipeRefreshLayout.setRefreshing(false);
                Log.e(TAG, "Ошибка сети при получении отчетов", t);
                showError("Ошибка сети: " + t.getMessage());
            }
        });
    }
    
    private void updateUI(List<TrashReport> reports) {
        this.reports.clear();
        this.reports.addAll(reports);
        adapter.notifyDataSetChanged();
        
        if (reports.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
        }
    }
    
    private void showError(String message) {
        if (getContext() != null) {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }
} 