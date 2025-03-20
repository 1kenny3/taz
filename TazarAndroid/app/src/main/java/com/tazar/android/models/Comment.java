package com.tazar.android.models;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class Comment {
    @SerializedName("id")
    private int id;
    
    @SerializedName("user")
    private int userId;
    
    @SerializedName("user_name")
    private String userName;
    
    @SerializedName("user_photo")
    private String userPhotoUrl;
    
    @SerializedName("trash_report")
    private int trashReportId;
    
    @SerializedName("content")
    private String content;
    
    @SerializedName("created_at")
    private Date createdAt;
    
    @SerializedName("parent")
    private Integer parentId;
    
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
    
    public String getUserPhotoUrl() {
        return userPhotoUrl;
    }
    
    public void setUserPhotoUrl(String userPhotoUrl) {
        this.userPhotoUrl = userPhotoUrl;
    }
    
    public int getTrashReportId() {
        return trashReportId;
    }
    
    public void setTrashReportId(int trashReportId) {
        this.trashReportId = trashReportId;
    }
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    
    public Date getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
    
    public Integer getParentId() {
        return parentId;
    }
    
    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }
    
    public boolean isReply() {
        return parentId != null;
    }
} 