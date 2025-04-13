package com.tazar.android.ui.activities;

import android.app.Activity;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.tazar.android.R;
import com.tazar.android.TazarApplication;
import com.tazar.android.api.ApiClient;
import com.tazar.android.api.TrashReportService;
import com.tazar.android.utils.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateReportActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final String TAG = "CreateReportActivity";
    
    // Координаты центра Киргизии (Бишкек)
    private static final LatLng KYRGYZSTAN_CENTER = new LatLng(42.87, 74.59);
    private static final float DEFAULT_ZOOM = 10.0f; // Уровень зума для отображения региона
    
    private EditText etDescription;
    private Button btnSelectPhoto;
    private Button btnSubmit;
    private ImageView ivPhoto;
    private TextView tvLocation;
    private ProgressBar progressBar;
    private GoogleMap mMap;
    
    private LatLng selectedLocation;
    private String selectedAddress;
    private File photoFile;

    private FusedLocationProviderClient fusedLocationClient;
    
    // Лаунчер для выбора фото
    private final ActivityResultLauncher<Intent> pickPhotoLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    Uri selectedPhotoUri = result.getData().getData();
                    if (selectedPhotoUri != null) {
                        try {
                            // Создаем временный файл для фото и копируем в него содержимое
                            photoFile = FileUtils.createImageFile(this);
                            boolean success = FileUtils.copyUriToFile(this, selectedPhotoUri, photoFile);
                            
                            if (success) {
                                // Отображаем фото в ImageView
                                ivPhoto.setImageURI(Uri.fromFile(photoFile));
                                ivPhoto.setVisibility(View.VISIBLE);
                            } else {
                                Toast.makeText(this, "Не удалось скопировать файл", Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            Log.e(TAG, "Ошибка при обработке фото", e);
                            Toast.makeText(this, "Ошибка при выборе фото", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
    );

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_report);
        
        // Включаем кнопку "Назад" в тулбаре
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.create_report);
        }
        
        // Получаем координаты из Intent
        double latitude = getIntent().getDoubleExtra("latitude", 0);
        double longitude = getIntent().getDoubleExtra("longitude", 0);
        
        Log.d(TAG, "Создание отчета для координат: " + latitude + ", " + longitude);
        
        // Инициализация UI компонентов
        initializeViews();
        setupListeners();
        
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        
        // Инициализация карты
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
        
        // По умолчанию используем центр Киргизии
        selectedLocation = KYRGYZSTAN_CENTER;
        updateLocationDisplay(selectedLocation);
        
        // Попробуем получить текущее местоположение пользователя
        tryGetCurrentLocation();
    }
    
    private void initializeViews() {
        etDescription = findViewById(R.id.et_description);
        btnSelectPhoto = findViewById(R.id.btn_select_photo);
        btnSubmit = findViewById(R.id.btn_submit);
        ivPhoto = findViewById(R.id.iv_photo);
        tvLocation = findViewById(R.id.tv_location);
        progressBar = findViewById(R.id.progress_bar);
    }
    
    private void setupListeners() {
        btnSelectPhoto.setOnClickListener(v -> selectPhoto());
        btnSubmit.setOnClickListener(v -> submitReport());
    }
    
    private void selectPhoto() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        pickPhotoLauncher.launch(intent);
    }
    
    private void tryGetCurrentLocation() {
        try {
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, location -> {
                        if (location != null) {
                            // Получили местоположение пользователя
                            LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
                            
                            // Проверяем, находится ли пользователь в Киргизии или рядом
                            // Координаты границ Киргизии (приблизительно)
                            double minLat = 39.0;
                            double maxLat = 43.5;
                            double minLng = 69.0;
                            double maxLng = 80.0;
                            
                            if (location.getLatitude() >= minLat && location.getLatitude() <= maxLat &&
                                location.getLongitude() >= minLng && location.getLongitude() <= maxLng) {
                                // Пользователь в пределах Киргизии, используем его местоположение
                                selectedLocation = userLocation;
                                updateLocationDisplay(selectedLocation);
                                
                                if (mMap != null) {
                                    mMap.clear();
                                    mMap.addMarker(new MarkerOptions().position(selectedLocation).title("Ваше местоположение"));
                                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(selectedLocation, DEFAULT_ZOOM));
                                }
                            } else {
                                // Пользователь за пределами Киргизии, используем центр страны
                                if (mMap != null) {
                                    mMap.clear();
                                    mMap.addMarker(new MarkerOptions().position(KYRGYZSTAN_CENTER).title("Центр Киргизии"));
                                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(KYRGYZSTAN_CENTER, DEFAULT_ZOOM));
                                }
                            }
                        } else {
                            // Не удалось получить местоположение, используем центр Киргизии
                            if (mMap != null) {
                                mMap.clear();
                                mMap.addMarker(new MarkerOptions().position(KYRGYZSTAN_CENTER).title("Центр Киргизии"));
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(KYRGYZSTAN_CENTER, DEFAULT_ZOOM));
                            }
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Ошибка при получении местоположения", e);
                        // В случае ошибки используем центр Киргизии
                        if (mMap != null) {
                            mMap.clear();
                            mMap.addMarker(new MarkerOptions().position(KYRGYZSTAN_CENTER).title("Центр Киргизии"));
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(KYRGYZSTAN_CENTER, DEFAULT_ZOOM));
                        }
                    });
        } catch (SecurityException e) {
            Log.e(TAG, "Нет разрешения на получение местоположения", e);
            Toast.makeText(this, "Для более точного определения местоположения необходимо разрешение", Toast.LENGTH_SHORT).show();
            
            // Используем центр Киргизии
            if (mMap != null) {
                mMap.clear();
                mMap.addMarker(new MarkerOptions().position(KYRGYZSTAN_CENTER).title("Центр Киргизии"));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(KYRGYZSTAN_CENTER, DEFAULT_ZOOM));
            }
        }
    }
    
    private void updateLocationDisplay(LatLng location) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                selectedAddress = address.getAddressLine(0);
                tvLocation.setText(selectedAddress);
            } else {
                // Если не удалось получить адрес, показываем координаты
                tvLocation.setText(String.format(Locale.getDefault(), 
                        "Широта: %.6f, Долгота: %.6f", location.latitude, location.longitude));
                selectedAddress = "Адрес не определен";
            }
        } catch (IOException e) {
            Log.e(TAG, "Ошибка при получении адреса", e);
            tvLocation.setText(String.format(Locale.getDefault(), 
                    "Широта: %.6f, Долгота: %.6f", location.latitude, location.longitude));
            selectedAddress = "Адрес не определен";
        }
    }
    
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        
        // Устанавливаем начальное положение - центр Киргизии
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(KYRGYZSTAN_CENTER, DEFAULT_ZOOM));
        mMap.addMarker(new MarkerOptions().position(KYRGYZSTAN_CENTER).title("Центр Киргизии"));
        
        // Попробуем получить реальное местоположение пользователя
        tryGetCurrentLocation();
        
        // Обработка клика по карте для выбора местоположения
        mMap.setOnMapClickListener(latLng -> {
            selectedLocation = latLng;
            mMap.clear();
            mMap.addMarker(new MarkerOptions().position(latLng).title("Выбранное местоположение"));
            updateLocationDisplay(latLng);
        });
    }
    
    private void submitReport() {
        // Проверяем наличие всех необходимых данных
        if (selectedLocation == null) {
            Toast.makeText(this, "Выберите местоположение на карте", Toast.LENGTH_SHORT).show();
            return;
        }
        
        if (photoFile == null || !photoFile.exists()) {
            Toast.makeText(this, "Выберите фотографию", Toast.LENGTH_SHORT).show();
            return;
        }
        
        String description = etDescription.getText().toString().trim();
        if (description.isEmpty()) {
            Toast.makeText(this, "Введите описание", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // Показываем прогресс-бар
        progressBar.setVisibility(View.VISIBLE);
        btnSubmit.setEnabled(false);
        
        // Получаем токен авторизации
        String token = TazarApplication.getInstance().getAuthToken();
        if (token == null || token.isEmpty()) {
            Toast.makeText(this, "Ошибка авторизации. Войдите снова", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
            btnSubmit.setEnabled(true);
            return;
        }
        
        // Получаем ID пользователя
        String userId = TazarApplication.getInstance().getUserId();
        if (userId == null || userId.isEmpty()) {
            Toast.makeText(this, "Ошибка: не удалось получить айди пользователя", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
            btnSubmit.setEnabled(true);
            return;
        }
        
        // Подготавливаем данные для отправки
        RequestBody latitudeBody = RequestBody.create(MediaType.parse("text/plain"), 
                String.valueOf(selectedLocation.latitude));
        RequestBody longitudeBody = RequestBody.create(MediaType.parse("text/plain"), 
                String.valueOf(selectedLocation.longitude));
        RequestBody descriptionBody = RequestBody.create(MediaType.parse("text/plain"), description);
        RequestBody addressBody = RequestBody.create(MediaType.parse("text/plain"), selectedAddress);
        RequestBody userBody = RequestBody.create(MediaType.parse("text/plain"), userId);
        
        // Создаем MultipartBody.Part из файла изображения
        RequestBody photoRequestBody = RequestBody.create(MediaType.parse("image/*"), photoFile);
        MultipartBody.Part photoPart = MultipartBody.Part.createFormData("photo", 
                photoFile.getName(), photoRequestBody);
        
        // Получаем сервис для отправки отчетов
        TrashReportService service = ApiClient.getInstance().create(TrashReportService.class);
        
        // Отправляем запрос
        Call<Void> call = service.createReport(
                "Bearer " + token,
                latitudeBody, 
                longitudeBody, 
                descriptionBody, 
                photoPart,
                addressBody,
                userBody);
        
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                progressBar.setVisibility(View.GONE);
                btnSubmit.setEnabled(true);
                
                if (response.isSuccessful()) {
                    Toast.makeText(CreateReportActivity.this, 
                            "Отчет успешно отправлен", Toast.LENGTH_SHORT).show();
                    finish(); // Закрываем экран после успешной отправки
                } else {
                    try {
                        String errorBody = response.errorBody() != null ? 
                                response.errorBody().string() : "Неизвестная ошибка";
                        Log.e(TAG, "Ошибка отправки отчета: " + errorBody);
                        Toast.makeText(CreateReportActivity.this, 
                                "Ошибка отправки отчета: " + response.code(), Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        Log.e(TAG, "Ошибка при чтении ответа сервера", e);
                    }
                }
            }
            
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                btnSubmit.setEnabled(true);
                Log.e(TAG, "Ошибка сети при отправке отчета", t);
                Toast.makeText(CreateReportActivity.this, 
                        "Ошибка сети: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
} 