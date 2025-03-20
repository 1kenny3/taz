package com.tazar.android.models;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class Achievement {
    public static final String TYPE_REPORTS = "reports";
    public static final String TYPE_POINTS = "points";
    public static final String TYPE_ACTIVITY = "activity";
    
    @SerializedName("id")
    private int id;
    
    @SerializedName("name")
    private String name;
    
    @SerializedName("description")
    private String description;
    
    @SerializedName("icon")
    private String icon;
    
    @SerializedName("points_required")
    private int pointsRequired;
    
    @SerializedName("type")
    private String type;
    
    @SerializedName("created_at")
    private Date createdAt;
    
    private boolean earned;
    private Date dateEarned;
    
    // Геттеры и сеттеры
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getIcon() {
        return icon;
    }
    
    public void setIcon(String icon) {
        this.icon = icon;
    }
    
    public int getPointsRequired() {
        return pointsRequired;
    }
    
    public void setPointsRequired(int pointsRequired) {
        this.pointsRequired = pointsRequired;
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public Date getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
    
    public boolean isEarned() {
        return earned;
    }
    
    public void setEarned(boolean earned) {
        this.earned = earned;
    }
    
    public Date getDateEarned() {
        return dateEarned;
    }
    
    public void setDateEarned(Date dateEarned) {
        this.dateEarned = dateEarned;
    }
    
    // Получить локализованную строку типа достижения
    public String getTypeLocalized() {
        switch (type) {
            case TYPE_REPORTS:
                return "За отчеты";
            case TYPE_POINTS:
                return "За баллы";
            case TYPE_ACTIVITY:
                return "За активность";
            default:
                return type;
        }
    }
} 