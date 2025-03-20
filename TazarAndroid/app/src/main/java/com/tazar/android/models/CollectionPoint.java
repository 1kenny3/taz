package com.tazar.android.models;

import com.google.gson.annotations.SerializedName;

public class CollectionPoint {
    @SerializedName("id")
    private int id;
    
    @SerializedName("waste_types")
    private String wasteTypes;
    
    @SerializedName("city")
    private String city;
    
    @SerializedName("address")
    private String address;
    
    @SerializedName("latitude")
    private double latitude;
    
    @SerializedName("longitude")
    private double longitude;
    
    // Геттеры и сеттеры
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getWasteTypes() {
        return wasteTypes;
    }
    
    public void setWasteTypes(String wasteTypes) {
        this.wasteTypes = wasteTypes;
    }
    
    public String getCity() {
        return city;
    }
    
    public void setCity(String city) {
        this.city = city;
    }
    
    public String getAddress() {
        return address;
    }
    
    public void setAddress(String address) {
        this.address = address;
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
    
    // Получить цвет маркера в зависимости от типа отходов
    public int getMarkerColor() {
        switch (wasteTypes) {
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