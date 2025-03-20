package com.tazar.android.models;

import com.google.gson.annotations.SerializedName;

public class User {
    @SerializedName("id")
    private int id;
    
    @SerializedName("username")
    private String username;
    
    @SerializedName("email")
    private String email;
    
    @SerializedName("rating")
    private int rating;
    
    @SerializedName("achievements_count")
    private int achievementsCount;
    
    @SerializedName("points")
    private int points;
    
    @SerializedName("profile_photo")
    private String profilePhotoUrl;
    
    @SerializedName("bio")
    private String bio;
    
    @SerializedName("is_collector")
    private boolean isCollector;
    
    // Геттеры и сеттеры
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public int getRating() {
        return rating;
    }
    
    public int getPoints() {
        return points;
    }
    
    public void setPoints(int points) {
        this.points = points;
    }
    
    public String getProfilePhotoUrl() {
        return profilePhotoUrl;
    }
    
    public void setProfilePhotoUrl(String profilePhotoUrl) {
        this.profilePhotoUrl = profilePhotoUrl;
    }
    
    public String getBio() {
        return bio;
    }
    
    public void setBio(String bio) {
        this.bio = bio;
    }
    
    public boolean isCollector() {
        return isCollector;
    }
    
    public void setCollector(boolean collector) {
        isCollector = collector;
    }

    public int getAchievementsCount() {
        return achievementsCount;
    }
} 