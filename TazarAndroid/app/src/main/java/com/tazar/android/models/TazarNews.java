package com.tazar.android.models;

import java.util.Objects;

public class TazarNews {
    private int id;
    private String title;
    private String content;
    private String image;
    private String created_at;
    private String updated_at;
    private boolean is_published;

    public TazarNews() {
        // Пустой конструктор для Gson
    }

    public TazarNews(int id, String title, String content, String image, String created_at, String updated_at, boolean is_published) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.image = image;
        this.created_at = created_at;
        this.updated_at = updated_at;
        this.is_published = is_published;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public String getImage() {
        return image;
    }

    public String getCreatedAt() {
        return created_at;
    }

    public String getUpdatedAt() {
        return updated_at;
    }

    public boolean isPublished() {
        return is_published;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TazarNews tazarNews = (TazarNews) o;
        return id == tazarNews.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    // Метод для конвертации TazarNews в News для совместимости с существующим адаптером
    public News toNews() {
        return new News(
            title,
            content,
            "", // URL не используется, так как новости показываются непосредственно в приложении
            image,
            created_at,
            "Tazar" // Источник - наша платформа
        );
    }
} 