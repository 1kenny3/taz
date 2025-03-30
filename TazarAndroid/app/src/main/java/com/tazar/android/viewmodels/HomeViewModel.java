package com.tazar.android.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.tazar.android.BuildConfig;
import com.tazar.android.models.Article;
import com.tazar.android.models.News;
import com.tazar.android.models.NewsResponse;
import com.tazar.android.network.NewsApiService;
import com.tazar.android.network.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeViewModel extends ViewModel {
    private final MutableLiveData<List<News>> news = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private final NewsApiService newsApiService;

    public HomeViewModel() {
        newsApiService = RetrofitClient.getNewsApiService();
        loadNews();
    }

    public void loadNews() {
        isLoading.setValue(true);

        newsApiService.getEcoNews(
            "экология OR природа OR переработка мусора",
            "ru",
            "publishedAt",
            BuildConfig.NEWS_API_KEY
        ).enqueue(new Callback<NewsResponse>() {
            @Override
            public void onResponse(Call<NewsResponse> call, Response<NewsResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<News> newsList = new ArrayList<>();
                    for (Article article : response.body().getArticles()) {
                        newsList.add(new News(
                            article.getTitle(),
                            article.getDescription(),
                            article.getUrl(),
                            article.getUrlToImage(),
                            article.getPublishedAt(),
                            article.getSource().getName()
                        ));
                    }
                    news.setValue(newsList);
                } else {
                    error.setValue("Ошибка загрузки новостей: " + response.code());
                }
                isLoading.setValue(false);
            }

            @Override
            public void onFailure(Call<NewsResponse> call, Throwable t) {
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