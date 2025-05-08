package com.tazar.android.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.tazar.android.R;
import com.tazar.android.models.EcoupNews;
import com.tazar.android.models.News;

public class NewsDetailActivity extends AppCompatActivity {

    private static final String EXTRA_NEWS_ID = "extra_news_id";
    private static final String EXTRA_NEWS_TITLE = "extra_news_title";
    private static final String EXTRA_NEWS_CONTENT = "extra_news_content";
    private static final String EXTRA_NEWS_IMAGE = "extra_news_image";
    private static final String EXTRA_NEWS_DATE = "extra_news_date";
    private static final String EXTRA_NEWS_SOURCE = "extra_news_source";

    private ImageView imageView;
    private TextView titleTextView;
    private TextView contentTextView;
    private TextView sourceTextView;
    private TextView dateTextView;
    private FloatingActionButton fabShare;

    public static Intent newIntent(Context context, EcoupNews news) {
        Intent intent = new Intent(context, NewsDetailActivity.class);
        intent.putExtra(EXTRA_NEWS_ID, news.getId());
        intent.putExtra(EXTRA_NEWS_TITLE, news.getTitle());
        intent.putExtra(EXTRA_NEWS_CONTENT, news.getContent());
        intent.putExtra(EXTRA_NEWS_IMAGE, news.getImage());
        intent.putExtra(EXTRA_NEWS_DATE, news.getCreatedAt());
        intent.putExtra(EXTRA_NEWS_SOURCE, "Ecoup");
        return intent;
    }

    public static Intent newIntent(Context context, News news) {
        Intent intent = new Intent(context, NewsDetailActivity.class);
        intent.putExtra(EXTRA_NEWS_TITLE, news.getTitle());
        intent.putExtra(EXTRA_NEWS_CONTENT, news.getDescription());
        intent.putExtra(EXTRA_NEWS_IMAGE, news.getImageUrl());
        intent.putExtra(EXTRA_NEWS_DATE, news.getPublishedAt());
        intent.putExtra(EXTRA_NEWS_SOURCE, news.getSource());
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_detail);

        // Инициализация views
        Toolbar toolbar = findViewById(R.id.toolbar);
        imageView = findViewById(R.id.news_detail_image);
        titleTextView = findViewById(R.id.news_detail_title);
        contentTextView = findViewById(R.id.news_detail_content);
        sourceTextView = findViewById(R.id.news_detail_source);
        dateTextView = findViewById(R.id.news_detail_date);
        fabShare = findViewById(R.id.fab_share);

        // Настройка Toolbar
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("");
        }

        // Получение данных из Intent
        Intent intent = getIntent();
        String title = intent.getStringExtra(EXTRA_NEWS_TITLE);
        String content = intent.getStringExtra(EXTRA_NEWS_CONTENT);
        String imageUrl = intent.getStringExtra(EXTRA_NEWS_IMAGE);
        String date = intent.getStringExtra(EXTRA_NEWS_DATE);
        String source = intent.getStringExtra(EXTRA_NEWS_SOURCE);

        // Отображение данных
        titleTextView.setText(title);
        contentTextView.setText(content);
        sourceTextView.setText(source);
        dateTextView.setText(date);

        // Загрузка изображения
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(this)
                .load(imageUrl)
                .apply(new RequestOptions()
                    .placeholder(R.drawable.placeholder_image)
                    .error(R.drawable.placeholder_image)
                    .centerCrop())
                .into(imageView);
        } else {
            imageView.setImageResource(R.drawable.placeholder_image);
        }

        // Настройка поделиться
        fabShare.setOnClickListener(v -> {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, title);
            shareIntent.putExtra(Intent.EXTRA_TEXT, title + "\n\n" + content);
            startActivity(Intent.createChooser(shareIntent, getString(R.string.share_news)));
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
} 