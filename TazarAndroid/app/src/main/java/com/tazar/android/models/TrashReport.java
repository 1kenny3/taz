package com.tazar.android.models;

import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.List;

public class TrashReport {
    public static final String STATUS_NEW = "new";
    public static final String STATUS_IN_PROGRESS = "in_progress";
    public static final String STATUS_COMPLETED = "completed";
    public static final String STATUS_REJECTED = "rejected";
    
    @SerializedName("id")
    private int id;
    
    @SerializedName("user")
    private int userId;
    
    @SerializedName("user_name")
    private String userName;
    
    @SerializedName("address")
    private String address;
    
    @SerializedName("description")
    private String description;
    
    @SerializedName("photo")
    private String photoUrl;
    
    @SerializedName("latitude")
    private double latitude;
    
    @SerializedName("longitude")
    private double longitude;
    
    @SerializedName("status")
    private String status;
    
    @SerializedName("points_awarded")
    private boolean pointsAwarded;
    
    @SerializedName("created_at")
    private Date createdAt;
    
    @SerializedName("updated_at")
    private Date updatedAt;
    
    @SerializedName("comments")
    private List<Comment> comments;
    
    // Геттеры и сеттеры
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public int getUserId() {
        return userId;
    }
    
    public void setUserId(int userId) {
        this.userId = userId;
    }
    
    public String getUserName() {
        return userName;
    }
    
    public void setUserName(String userName) {
        this.userName = userName;
    }
    
    public String getAddress() {
        return address;
    }
    
    public void setAddress(String address) {
        this.address = address;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getPhotoUrl() {
        return photoUrl;
    }
    
    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
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
    
    public boolean isPointsAwarded() {
        return pointsAwarded;
    }
    
    public void setPointsAwarded(boolean pointsAwarded) {
        this.pointsAwarded = pointsAwarded;
    }
    
    public Date getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
    
    public Date getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public List<Comment> getComments() {
        return comments;
    }
    
    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }
    
    // Получить цвет статуса
    public int getStatusColor() {
        switch (status) {
            case STATUS_NEW:
                return android.graphics.Color.BLUE;
            case STATUS_IN_PROGRESS:
                return android.graphics.Color.rgb(255, 152, 0); // Оранжевый
            case STATUS_COMPLETED:
                return android.graphics.Color.GREEN;
            case STATUS_REJECTED:
                return android.graphics.Color.RED;
            default:
                return android.graphics.Color.GRAY;
        }
    }
    
    // Получить текст статуса на русском
    public String getStatusText() {
        switch (status) {
            case STATUS_NEW:
                return "Новый";
            case STATUS_IN_PROGRESS:
                return "В обработке";
            case STATUS_COMPLETED:
                return "Убрано";
            case STATUS_REJECTED:
                return "Отклонено";
            default:
                return "Неизвестно";
        }
    }
} 