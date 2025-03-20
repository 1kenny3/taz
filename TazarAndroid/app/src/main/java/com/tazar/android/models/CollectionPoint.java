package com.tazar.android.models;

import com.google.gson.annotations.SerializedName;

public class CollectionPoint {
    @SerializedName("id")
    private int id;
    
    @SerializedName("name")
    private String name;
    
    @SerializedName("description")
    private String description;
    
    @SerializedName("latitude")
    private double latitude;
    
    @SerializedName("longitude")
    private double longitude;
    
    @SerializedName("accepted_types")
    private String[] acceptedTypes;
    
    // Геттеры и сеттеры
    public int getId() {
        return id;
    }
    
    public String getName() {
        return name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public double getLatitude() {
        return latitude;
    }
    
    public double getLongitude() {
        return longitude;
    }
    
    public String[] getAcceptedTypes() {
        return acceptedTypes;
    }
    
    // Получить цвет маркера в зависимости от типа отходов
    public int getMarkerColor() {
        switch (acceptedTypes[0]) {
            case "plastic":
                return android.graphics.Color.BLUE;
            case "paper":
                return android.graphics.Color.YELLOW;
            case "metal":
                return android.graphics.Color.GRAY;
            case "glass":
                return android.graphics.Color.CYAN;
            default:
                return android.graphics.Color.GREEN;
        }
    }
} 