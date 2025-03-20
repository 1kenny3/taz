package com.tazar.android.models;

import com.google.gson.annotations.SerializedName;

public class EcoTip {
    public static final String CATEGORY_WASTE = "waste";
    public static final String CATEGORY_ENERGY = "energy";
    public static final String CATEGORY_WATER = "water";
    public static final String CATEGORY_TRANSPORT = "transport";
    
    public static final String DIFFICULTY_EASY = "easy";
    public static final String DIFFICULTY_MEDIUM = "medium";
    public static final String DIFFICULTY_HARD = "hard";
    
    @SerializedName("id")
    private int id;
    
    @SerializedName("title")
    private String title;
    
    @SerializedName("content")
    private String content;
    
    @SerializedName("image")
    private String imageUrl;
    
    @SerializedName("category")
    private String category;
    
    @SerializedName("difficulty")
    private String difficulty;
    
    // Геттеры и сеттеры
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    
    public String getImageUrl() {
        return imageUrl;
    }
    
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
    
    public String getCategory() {
        return category;
    }
    
    public void setCategory(String category) {
        this.category = category;
    }
    
    public String getDifficulty() {
        return difficulty;
    }
    
    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }
    
    // Получить локализованную строку категории
    public String getCategoryLocalized() {
        switch (category) {
            case CATEGORY_WASTE:
                return "Отходы";
            case CATEGORY_ENERGY:
                return "Энергия";
            case CATEGORY_WATER:
                return "Вода";
            case CATEGORY_TRANSPORT:
                return "Транспорт";
            default:
                return category;
        }
    }
    
    // Получить локализованную строку сложности
    public String getDifficultyLocalized() {
        switch (difficulty) {
            case DIFFICULTY_EASY:
                return "Легко";
            case DIFFICULTY_MEDIUM:
                return "Средне";
            case DIFFICULTY_HARD:
                return "Сложно";
            default:
                return difficulty;
        }
    }
    
    // Получить цвет категории
    public int getCategoryColor() {
        switch (category) {
            case CATEGORY_WASTE:
                return android.graphics.Color.parseColor("#4CAF50"); // Зеленый
            case CATEGORY_ENERGY:
                return android.graphics.Color.parseColor("#FFC107"); // Желтый
            case CATEGORY_WATER:
                return android.graphics.Color.parseColor("#2196F3"); // Синий
            case CATEGORY_TRANSPORT:
                return android.graphics.Color.parseColor("#9C27B0"); // Фиолетовый
            default:
                return android.graphics.Color.GRAY;
        }
    }
} 