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
    public void setWasteTypes(String wasteTypes) { this.wasteTypes = wasteTypes; }
    
    public String getWasteTypeDisplay() { return wasteTypeDisplay; }
    public void setWasteTypeDisplay(String wasteTypeDisplay) { this.wasteTypeDisplay = wasteTypeDisplay; }

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