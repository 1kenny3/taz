package com.tazar.android.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.tazar.android.R;
import com.tazar.android.EcoupApplication;
import com.tazar.android.api.ApiClient;
import com.tazar.android.api.TrashReportService;
import com.tazar.android.models.TrashReport;
import com.tazar.android.ui.MainActivity;
import com.tazar.android.ui.adapters.TrashReportAdapter;
import com.tazar.android.ui.interfaces.OnReportClickListener;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReportsFragment extends Fragment implements OnReportClickListener {
    private static final String TAG = "ReportsFragment";

    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ConstraintLayout emptyStateContainer;
    private TextView emptyView;
    private TextView reportsCounter;
    private TrashReportAdapter adapter;
    private List<TrashReport> allReports = new ArrayList<>();
    private List<TrashReport> filteredReports = new ArrayList<>();
    private ChipGroup filterChipGroup;
    private Chip filterAll, filterPending, filterInProgress, filterCompleted;
    private MaterialButton createReportButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reports, container, false);
        
        // Инициализация UI-компонентов
        recyclerView = view.findViewById(R.id.reports_recycler_view);
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);
        emptyStateContainer = view.findViewById(R.id.empty_state_container);
        emptyView = view.findViewById(R.id.empty_view);
        reportsCounter = view.findViewById(R.id.reports_counter);
        filterChipGroup = view.findViewById(R.id.filter_chip_group);
        filterAll = view.findViewById(R.id.filter_all);
        filterPending = view.findViewById(R.id.filter_pending);
        filterInProgress = view.findViewById(R.id.filter_in_progress);
        filterCompleted = view.findViewById(R.id.filter_completed);
        createReportButton = view.findViewById(R.id.create_report_button);
        
        setupRecyclerView();
        setupSwipeRefresh();
        setupFilterChips();
        setupCreateReportButton();
        
        // Загружаем отчеты при создании фрагмента
        loadReports();
        
        return view;
    }
    
    private void setupRecyclerView() {
        adapter = new TrashReportAdapter(filteredReports, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
    }
    
    private void setupSwipeRefresh() {
        swipeRefreshLayout.setOnRefreshListener(this::loadReports);
        swipeRefreshLayout.setColorSchemeResources(
                R.color.primary,
                R.color.accent,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light
        );
    }
    
    private void setupFilterChips() {
        filterChipGroup.setOnCheckedStateChangeListener((group, checkedIds) -> {
            applyFilters();
        });
    }
    
    private void setupCreateReportButton() {
        createReportButton.setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).showAddReportDialog();
            }
        });
    }
    
    private void loadReports() {
        String token = EcoupApplication.getInstance().getAuthToken();
        if (token == null || token.isEmpty()) {
            showError("Необходима авторизация");
            swipeRefreshLayout.setRefreshing(false);
            updateEmptyState(true);
            return;
        }
        
        // Получаем ID текущего пользователя
        String userId = EcoupApplication.getInstance().getUserId();
        if (userId == null || userId.isEmpty()) {
            showError("Не удалось получить ID пользователя");
            swipeRefreshLayout.setRefreshing(false);
            updateEmptyState(true);
            return;
        }
        
        TrashReportService service = ApiClient.getInstance().create(TrashReportService.class);
        Call<List<TrashReport>> call = service.getTrashReports("Bearer " + token);
        
        call.enqueue(new Callback<List<TrashReport>>() {
            @Override
            public void onResponse(Call<List<TrashReport>> call, Response<List<TrashReport>> response) {
                swipeRefreshLayout.setRefreshing(false);
                
                if (response.isSuccessful() && response.body() != null) {
                    List<TrashReport> allReceivedReports = response.body();
                    
                    // Фильтруем отчеты, оставляя только те, которые принадлежат текущему пользователю
                    int currentUserId = Integer.parseInt(userId);
                    allReports = allReceivedReports.stream()
                            .filter(report -> report.getUserId() == currentUserId)
                            .collect(Collectors.toList());
                    
                    // Применяем фильтры к загруженным отчетам
                    applyFilters();
                } else {
                    Log.e(TAG, "Ошибка получения отчетов: " + response.code());
                    showError("Ошибка получения отчетов: " + response.code());
                    updateEmptyState(true);
                }
            }
            
            @Override
            public void onFailure(Call<List<TrashReport>> call, Throwable t) {
                swipeRefreshLayout.setRefreshing(false);
                Log.e(TAG, "Ошибка сети при получении отчетов", t);
                showError("Ошибка сети: " + t.getMessage());
                updateEmptyState(true);
            }
        });
    }
    
    private void applyFilters() {
        filteredReports.clear();
        
        if (filterAll.isChecked()) {
            filteredReports.addAll(allReports);
        } else {
            if (filterPending.isChecked()) {
                filteredReports.addAll(
                        allReports.stream()
                                .filter(report -> "new".equals(report.getStatus()))
                                .collect(Collectors.toList())
                );
            }
            
            if (filterInProgress.isChecked()) {
                filteredReports.addAll(
                        allReports.stream()
                                .filter(report -> "in_progress".equals(report.getStatus()))
                                .collect(Collectors.toList())
                );
            }
            
            if (filterCompleted.isChecked()) {
                filteredReports.addAll(
                        allReports.stream()
                                .filter(report -> "completed".equals(report.getStatus()))
                                .collect(Collectors.toList())
                );
            }
        }
        
        adapter.notifyDataSetChanged();
        updateReportsCounter();
        updateEmptyState(filteredReports.isEmpty());
    }
    
    private void updateReportsCounter() {
        String counterText = String.format("Всего отчетов: %d", filteredReports.size());
        reportsCounter.setText(counterText);
    }
    
    private void updateEmptyState(boolean isEmpty) {
        if (isEmpty) {
            recyclerView.setVisibility(View.GONE);
            emptyStateContainer.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyStateContainer.setVisibility(View.GONE);
        }
    }
    
    private void showError(String message) {
        if (getContext() != null) {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onReportClick(TrashReport report) {
        // Обработка нажатия на отчет - можно открыть деталь отчета
        showReportDetails(report);
    }
    
    private void showReportDetails(TrashReport report) {
        // Здесь можно добавить код для открытия детального просмотра отчета
        Toast.makeText(getContext(), "Детали отчета #" + report.getId(), Toast.LENGTH_SHORT).show();
        
        // Пример перехода на экран с детальной информацией
        // Intent intent = new Intent(getActivity(), ReportDetailActivity.class);
        // intent.putExtra("REPORT_ID", report.getId());
        // startActivity(intent);
    }
} 