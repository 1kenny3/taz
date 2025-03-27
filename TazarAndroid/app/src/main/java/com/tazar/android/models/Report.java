package com.tazar.android.models;

import com.google.gson.annotations.SerializedName;

/**
 * Модель для отчетов об отходах
 */
public class Report {
    
    @SerializedName("id")
    private int id;
    
    @SerializedName("title")
    private String title;
    
    @SerializedName("description")
    private String description;
    
    @SerializedName("location")
    private String location;
    
    @SerializedName("latitude")
    private double latitude;
    
    @SerializedName("longitude")
    private double longitude;
    
    @SerializedName("status")
    private String status;
    
    @SerializedName("created_at")
    private String createdAt;
    
    @SerializedName("updated_at")
    private String updatedAt;
    
    @SerializedName("points")
    private int points;
    
    @SerializedName("image_url")
    private String imageUrl;
    
    @SerializedName("user_id")
    private int userId;
    
    @SerializedName("points_awarded")
    private boolean pointsAwarded;

    // Конструктор по умолчанию
    public Report() {
    }

    // Конструктор с параметрами
    public Report(String title, String description, String location, double latitude, double longitude, String imageUrl) {
        this.title = title;
        this.description = description;
        this.location = location;
        this.latitude = latitude;
        this.longitude = longitude;
        this.imageUrl = imageUrl;
    }

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public boolean isPointsAwarded() {
        return pointsAwarded;
    }

    public void setPointsAwarded(boolean pointsAwarded) {
        this.pointsAwarded = pointsAwarded;
    }

    @Override
    public String toString() {
        return "Report{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", location='" + location + '\'' +
                ", status='" + status + '\'' +
                ", points=" + points +
                '}';
    }
} 