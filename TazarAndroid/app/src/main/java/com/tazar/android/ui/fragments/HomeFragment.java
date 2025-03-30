package com.tazar.android.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.tazar.android.R;
import com.tazar.android.adapters.NewsAdapter;
import com.tazar.android.viewmodels.HomeViewModel;

public class HomeFragment extends Fragment {
    private HomeViewModel viewModel;
    private NewsAdapter newsAdapter;
    private SwipeRefreshLayout swipeRefresh;
    private RecyclerView newsRecyclerView;
    private TextView welcomeText;
    private TextView subtitleText;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        // Инициализация views
        welcomeText = view.findViewById(R.id.welcome_text);
        subtitleText = view.findViewById(R.id.subtitle_text);
        swipeRefresh = view.findViewById(R.id.swipe_refresh);
        newsRecyclerView = view.findViewById(R.id.news_recycler_view);

        // Настройка RecyclerView
        newsAdapter = new NewsAdapter();
        newsRecyclerView.setAdapter(newsAdapter);
        newsRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        // Инициализация ViewModel
        viewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        // Настройка SwipeRefreshLayout
        swipeRefresh.setOnRefreshListener(() -> viewModel.loadNews());

        // Наблюдение за данными
        observeViewModel();
    }

    private void observeViewModel() {
        viewModel.getNews().observe(getViewLifecycleOwner(), news -> {
            newsAdapter.submitList(news);
        });

        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            swipeRefresh.setRefreshing(isLoading);
        });

        viewModel.getError().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                Toast.makeText(requireContext(), error, Toast.LENGTH_LONG).show();
            }
        });
    }
} 