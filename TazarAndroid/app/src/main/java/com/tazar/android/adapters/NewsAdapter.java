package com.tazar.android.adapters;

import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.card.MaterialCardView;
import com.tazar.android.R;
import com.tazar.android.models.News;
import com.tazar.android.ui.activities.NewsDetailActivity;

public class NewsAdapter extends ListAdapter<News, NewsAdapter.NewsViewHolder> {

    public NewsAdapter() {
        super(new NewsDiffCallback());
    }

    @NonNull
    @Override
    public NewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_news, parent, false);
        return new NewsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NewsViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    static class NewsViewHolder extends RecyclerView.ViewHolder {
        private final ImageView newsImage;
        private final TextView titleText;
        private final TextView descriptionText;
        private final TextView sourceText;
        private final TextView dateText;
        private final View itemContainer;

        public NewsViewHolder(@NonNull View itemView) {
            super(itemView);
            itemContainer = itemView.findViewById(R.id.item_container);
            newsImage = itemView.findViewById(R.id.news_image);
            titleText = itemView.findViewById(R.id.title_text);
            descriptionText = itemView.findViewById(R.id.description_text);
            sourceText = itemView.findViewById(R.id.source_text);
            dateText = itemView.findViewById(R.id.date_text);
        }

        public void bind(News news) {
            titleText.setText(news.getTitle());
            descriptionText.setText(news.getDescription());
            sourceText.setText(news.getSource());
            dateText.setText(news.getPublishedAt());
            
            MaterialCardView cardView = (MaterialCardView) itemView;

            if (news.getImageUrl() != null && !news.getImageUrl().isEmpty()) {
                // Если есть URL изображения - показываем и загружаем его
                newsImage.setVisibility(View.VISIBLE);
                
                // Сбрасываем стили карточки
                cardView.setStrokeWidth(1);
                cardView.setCardBackgroundColor(itemView.getContext().getResources().getColor(android.R.color.white, null));
                
                Glide.with(itemView.getContext())
                    .load(news.getImageUrl())
                    .apply(new RequestOptions()
                        .placeholder(R.drawable.placeholder_image)
                        .error(R.drawable.placeholder_image)
                        .centerCrop())
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(newsImage);
                    
                // Сбрасываем стиль заголовка
                titleText.setTextColor(itemView.getContext().getResources().getColor(android.R.color.black, null));
            } else {
                // Если изображения нет - скрываем ImageView и меняем стиль
                newsImage.setVisibility(View.GONE);
                
                // Особый стиль для карточки без изображения
                cardView.setStrokeWidth(0);
                cardView.setCardBackgroundColor(itemView.getContext().getResources().getColor(R.color.colorPrimaryContainer, null));
                
                // Добавляем левую цветную полоску
                cardView.setStrokeColor(itemView.getContext().getResources().getColor(R.color.colorPrimary, null)); 
                cardView.setStrokeWidth(8);
                
                // Меняем цвет заголовка
                titleText.setTextColor(itemView.getContext().getResources().getColor(R.color.colorPrimary, null));
            }

            itemView.setOnClickListener(v -> {
                Intent intent = NewsDetailActivity.newIntent(v.getContext(), news);
                v.getContext().startActivity(intent);
            });

            itemView.setClickable(true);
            itemView.setFocusable(true);
        }
    }

    static class NewsDiffCallback extends DiffUtil.ItemCallback<News> {
        @Override
        public boolean areItemsTheSame(@NonNull News oldItem, @NonNull News newItem) {
            return oldItem.getUrl().equals(newItem.getUrl());
        }

        @Override
        public boolean areContentsTheSame(@NonNull News oldItem, @NonNull News newItem) {
            return oldItem.equals(newItem);
        }
    }
} 