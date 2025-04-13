package com.tazar.android.models;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.gson.annotations.SerializedName;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import androidx.core.content.ContextCompat;
import com.tazar.android.R;

public class RecyclingPoint {
    @SerializedName("id")
    private long id;
    
    @SerializedName("name")
    private String name;
    
    @SerializedName("address")
    private String address;
    
    @SerializedName("latitude")
    private double latitude;
    
    @SerializedName("longitude")
    private double longitude;
    
    @SerializedName("waste_types")
    private String wasteTypes;
    
    @SerializedName("waste_type_display")
    private String wasteTypeDisplay;
    
    // Поле для хранения массива типов отходов
    private String[] acceptedTypes;

    // Константы для типов отходов
    public static final String TYPE_PLASTIC = "plastic";
    public static final String TYPE_PAPER = "paper";
    public static final String TYPE_METAL = "metal";
    public static final String TYPE_GLASS = "glass";
    public static final String TYPE_MEDICAL = "medical";
    public static final String TYPE_CONSTRUCTION = "construction";
    public static final String TYPE_AGRICULTURAL = "agricultural";

    // Геттеры и сеттеры
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    
    public double getLatitude() { return latitude; }
    public void setLatitude(double latitude) { this.latitude = latitude; }
    
    public double getLongitude() { return longitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }
    
    public String getWasteTypes() { return wasteTypes; }
    public void setWasteTypes(String wasteTypes) { 
        this.wasteTypes = wasteTypes;
        
        // Обновляем массив acceptedTypes при установке строки wasteTypes
        if (wasteTypes != null && !wasteTypes.isEmpty()) {
            this.acceptedTypes = wasteTypes.split(",");
            for (int i = 0; i < this.acceptedTypes.length; i++) {
                this.acceptedTypes[i] = this.acceptedTypes[i].trim();
            }
        } else {
            this.acceptedTypes = new String[0];
        }
    }
    
    public String getWasteTypeDisplay() { return wasteTypeDisplay; }
    public void setWasteTypeDisplay(String wasteTypeDisplay) { this.wasteTypeDisplay = wasteTypeDisplay; }

    // Новые методы для работы с массивом типов отходов
    public String[] getAcceptedTypes() {
        if (acceptedTypes == null) {
            // Если массив еще не инициализирован, но есть строка типов отходов
            if (wasteTypes != null && !wasteTypes.isEmpty()) {
                acceptedTypes = wasteTypes.split(",");
                for (int i = 0; i < acceptedTypes.length; i++) {
                    acceptedTypes[i] = acceptedTypes[i].trim();
                }
            } else {
                acceptedTypes = new String[0];
            }
        }
        return acceptedTypes;
    }
    
    public void setAcceptedTypes(String[] acceptedTypes) {
        this.acceptedTypes = acceptedTypes;
        
        // Обновляем строку wasteTypes
        if (acceptedTypes != null && acceptedTypes.length > 0) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < acceptedTypes.length; i++) {
                if (i > 0) {
                    sb.append(",");
                }
                sb.append(acceptedTypes[i]);
            }
            this.wasteTypes = sb.toString();
        } else {
            this.wasteTypes = "";
        }
    }

    public BitmapDescriptor getMarkerIcon(Context context) {
        int drawableRes;
        switch (wasteTypes) {
            case "plastic":
                drawableRes = R.drawable.ic_marker_plastic;
                break;
            case "paper":
                drawableRes = R.drawable.ic_marker_paper;
                break;
            case "metal":
                drawableRes = R.drawable.ic_marker_metal;
                break;
            case "glass":
                drawableRes = R.drawable.ic_marker_glass;
                break;
            case "medical":
                drawableRes = R.drawable.ic_marker_medical;
                break;
            case "construction":
                drawableRes = R.drawable.ic_marker_construction;
                break;
            case "agricultural":
                drawableRes = R.drawable.ic_marker_agricultural;
                break;
            default:
                drawableRes = R.drawable.ic_marker_default;
                break;
        }
        
        Drawable drawable = ContextCompat.getDrawable(context, drawableRes);
        if (drawable == null) {
            return BitmapDescriptorFactory.defaultMarker();
        }
        
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), 
                drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.draw(canvas);
        
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }
} 