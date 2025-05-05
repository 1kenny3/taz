package com.tazar.android.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.tazar.android.api.ApiClient;
import com.tazar.android.api.ApiService;
import com.tazar.android.models.News;
import com.tazar.android.models.TazarNews;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeViewModel extends ViewModel {
    private final MutableLiveData<List<News>> news = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private final ApiService apiService;

    public HomeViewModel() {
        apiService = ApiClient.getInstance().create(ApiService.class);
        loadNews();
    }

    public void loadNews() {
        isLoading.setValue(true);

        apiService.getNews().enqueue(new Callback<List<TazarNews>>() {
            @Override
            public void onResponse(Call<List<TazarNews>> call, Response<List<TazarNews>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<News> newsList = new ArrayList<>();
                    for (TazarNews tazarNews : response.body()) {
                        // Конвертируем TazarNews в News для совместимости с адаптером
                        newsList.add(tazarNews.toNews());
                    }
                    news.setValue(newsList);
                } else {
                    error.setValue("Ошибка загрузки новостей: " + response.code());
                }
                isLoading.setValue(false);
            }

            @Override
            public void onFailure(Call<List<TazarNews>> call, Throwable t) {
                error.setValue("Ошибка сети: " + t.getMessage());
                isLoading.setValue(false);
            }
        });
    }

    public LiveData<List<News>> getNews() {
        return news;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<String> getError() {
        return error;
    }
} 