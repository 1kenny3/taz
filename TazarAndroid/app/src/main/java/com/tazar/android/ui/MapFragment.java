package com.tazar.android.ui;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.tazar.android.R;
import com.tazar.android.TazarApplication;
import com.tazar.android.adapters.WasteTypeFilterAdapter;
import com.tazar.android.api.ApiConfig;
import com.tazar.android.api.services.RecyclingPointsService;
import com.tazar.android.models.RecyclingPoint;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import okhttp3.OkHttpClient;

public class MapFragment extends Fragment implements OnMapReadyCallback, WasteTypeFilterAdapter.OnFilterSelectedListener {
    private static final String TAG = "MapFragment";
    
    private GoogleMap mMap;
    private ChipGroup wasteTypeFilterGroup;
    private ProgressBar progressBar;
    private RecyclingPointsService recyclingPointsService;
    private List<RecyclingPoint> recyclingPoints = new ArrayList<>();
    private Map<Marker, RecyclingPoint> markerPointMap = new HashMap<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        recyclingPointsService = ApiConfig.getService(RecyclingPointsService.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        
        // Получаем правильную группу фильтров
        wasteTypeFilterGroup = view.findViewById(R.id.waste_type_filter_group);
        progressBar = view.findViewById(R.id.progress_bar);
        
        if (progressBar == null) {
            // Создаем ProgressBar программно, если его нет в макете
            progressBar = new ProgressBar(requireContext());
            progressBar.setId(View.generateViewId());
            ((ViewGroup) view).addView(progressBar);
            progressBar.setVisibility(View.GONE);
        }
        
        // Инициализируем карту
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
        
        // Добавляем слушатель для чипов
        wasteTypeFilterGroup.setOnCheckedChangeListener((group, checkedId) -> {
            Log.d(TAG, "Фильтр изменен: " + checkedId);
            updateMapMarkers();
        });
        
        // Добавляем кнопки масштабирования
        View zoomInButton = view.findViewById(R.id.btn_zoom_in);
        View zoomOutButton = view.findViewById(R.id.btn_zoom_out);
        
        if (zoomInButton == null || zoomOutButton == null) {
            // Если кнопок нет в макете, добавляем их программно
            addCustomZoomControls(view);
        } else {
            // Если кнопки есть в макете, настраиваем их
            zoomInButton.setOnClickListener(v -> {
                if (mMap != null) mMap.animateCamera(CameraUpdateFactory.zoomIn());
            });
            
            zoomOutButton.setOnClickListener(v -> {
                if (mMap != null) mMap.animateCamera(CameraUpdateFactory.zoomOut());
            });
        }
        
        return view;
    }

    private void addCustomZoomControls(View rootView) {
        // Создаем кнопки для масштабирования
        RelativeLayout mapContainer = rootView.findViewById(R.id.map_container);
        
        if (mapContainer == null) {
            // Если контейнера карты нет, используем родительский view
            if (rootView instanceof ViewGroup) {
                mapContainer = new RelativeLayout(requireContext());
                mapContainer.setLayoutParams(new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT));
                
                // Копируем все дочерние элементы в новый контейнер
                ViewGroup parent = (ViewGroup) rootView;
                while (parent.getChildCount() > 0) {
                    View child = parent.getChildAt(0);
                    parent.removeView(child);
                    mapContainer.addView(child);
                }
                
                parent.addView(mapContainer);
            } else {
                Log.e(TAG, "Не удалось найти контейнер карты");
                return;
            }
        }
        
        // Создаем кнопку увеличения масштаба
        FloatingActionButton zoomInButton = new FloatingActionButton(requireContext());
        zoomInButton.setId(View.generateViewId());
        zoomInButton.setImageResource(R.drawable.ic_zoom_in);
        zoomInButton.setSize(FloatingActionButton.SIZE_MINI);
        
        RelativeLayout.LayoutParams zoomInParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        zoomInParams.addRule(RelativeLayout.ALIGN_PARENT_END);
        zoomInParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        zoomInParams.setMargins(0, 0, 32, 200); // margins: left, top, right, bottom
        zoomInButton.setLayoutParams(zoomInParams);
        
        zoomInButton.setOnClickListener(v -> {
            if (mMap != null) mMap.animateCamera(CameraUpdateFactory.zoomIn());
        });
        
        // Создаем кнопку уменьшения масштаба
        FloatingActionButton zoomOutButton = new FloatingActionButton(requireContext());
        zoomOutButton.setId(View.generateViewId());
        zoomOutButton.setImageResource(R.drawable.ic_zoom_out);
        zoomOutButton.setSize(FloatingActionButton.SIZE_MINI);
        
        RelativeLayout.LayoutParams zoomOutParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        zoomOutParams.addRule(RelativeLayout.ALIGN_PARENT_END);
        zoomOutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        zoomOutParams.setMargins(0, 0, 32, 120); // margins: left, top, right, bottom
        zoomOutButton.setLayoutParams(zoomOutParams);
        
        zoomOutButton.setOnClickListener(v -> {
            if (mMap != null) mMap.animateCamera(CameraUpdateFactory.zoomOut());
        });
        
        // Добавляем кнопки в контейнер
        mapContainer.addView(zoomInButton);
        mapContainer.addView(zoomOutButton);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        
        if (checkLocationPermission()) {
            mMap.setMyLocationEnabled(true);
        }
        
        // Настройка элементов управления на карте
        mMap.getUiSettings().setZoomControlsEnabled(true);     // Кнопки масштабирования
        mMap.getUiSettings().setCompassEnabled(true);          // Компас для ориентации
        mMap.getUiSettings().setMapToolbarEnabled(true);       // Панель инструментов (навигация и т.д.)
        mMap.getUiSettings().setMyLocationButtonEnabled(true); // Кнопка "Моё местоположение"
        
        mMap.setOnMarkerClickListener(this::onMarkerClick);
        
        // Создаем тестовые данные, если API не работает
        createTestData();
        
        // Загружаем точки с сервера
        loadRecyclingPointsFromApi();
    }
    
    private void createTestData() {
        // Создаем тестовые данные для демонстрации
        Log.d(TAG, "Создаем тестовые данные");
        
        recyclingPoints.clear();
        
        // Пример точек сбора в Москве
        addTestPoint("Пункт сбора пластика", "Ленинский проспект, 12", 55.7522, 37.6156, "plastic");
        addTestPoint("Пункт сбора бумаги", "Тверская, 5", 55.7558, 37.6173, "paper");
        addTestPoint("Пункт сбора металла", "Арбат, 10", 55.7520, 37.5930, "metal");
        addTestPoint("Пункт сбора стекла", "Садовая-Кудринская, 32", 55.7616, 37.5878, "glass");
        addTestPoint("Медицинские отходы", "Хамовнический Вал, 24", 55.7235, 37.5698, "medical");
        addTestPoint("Строительный мусор", "Большая Якиманка, 35", 55.7345, 37.6153, "construction");
        addTestPoint("Сельхоз отходы", "Кутузовский проспект, 21", 55.7520, 37.5478, "agricultural");
        
        updateMapMarkers();
    }
    
    private void addTestPoint(String name, String address, double lat, double lng, String wasteType) {
        RecyclingPoint point = new RecyclingPoint();
        point.setId(recyclingPoints.size() + 1);
        point.setName(name);
        point.setAddress(address);
        point.setLatitude(lat);
        point.setLongitude(lng);
        point.setWasteTypes(wasteType);
        recyclingPoints.add(point);
    }
    
    private void loadRecyclingPointsFromApi() {
        progressBar.setVisibility(View.VISIBLE);
        
        try {
            // Логируем полный URL запроса
            String fullUrl = ApiConfig.getRetrofit().baseUrl() + "api/collection-points/";
            Log.d(TAG, "Запрос к API: " + fullUrl);
            
            recyclingPointsService.getRecyclingPoints().enqueue(new Callback<List<RecyclingPoint>>() {
                @Override
                public void onResponse(Call<List<RecyclingPoint>> call, Response<List<RecyclingPoint>> response) {
                    progressBar.setVisibility(View.GONE);
                    
                    // Подробное логирование ответа
                    Log.d(TAG, "Код ответа API: " + response.code());
                    Log.d(TAG, "URL запроса: " + call.request().url());
                    
                    if (response.code() == 404) {
                        Log.e(TAG, "Ошибка получения пунктов переработки: Код: 404");
                        Toast.makeText(getContext(), "Эндпоинт не найден: " + call.request().url(), Toast.LENGTH_LONG).show();
                        // Проверяем альтернативный URL
                        tryAlternativeUrl();
                        return;
                    }
                    
                    if (response.errorBody() != null) {
                        try {
                            Log.d(TAG, "Тело ошибки: " + response.errorBody().string());
                        } catch (Exception e) {
                            Log.e(TAG, "Не удалось прочитать тело ошибки");
                        }
                    }
                    
                    if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                        recyclingPoints = response.body();
                        Log.d(TAG, "Получены данные с API: " + recyclingPoints.size() + " точек");
                        updateMapMarkers();
                    } else {
                        Log.d(TAG, "API вернул пустой или некорректный результат, используем тестовые данные");
                        Toast.makeText(getContext(), "Не удалось получить данные с сервера. Используются тестовые данные.", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<List<RecyclingPoint>> call, Throwable t) {
                    progressBar.setVisibility(View.GONE);
                    Log.e(TAG, "Сбой сети при получении пунктов переработки", t);
                    Toast.makeText(getContext(), "Ошибка сети: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    
                    // Убедимся, что у нас есть тестовые данные для отображения
                    if (recyclingPoints.isEmpty()) {
                        createTestData();
                    }
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "Ошибка при вызове API", e);
            progressBar.setVisibility(View.GONE);
            Toast.makeText(getContext(), "Ошибка при вызове API: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            
            // Убедимся, что у нас есть тестовые данные для отображения
            if (recyclingPoints.isEmpty()) {
                createTestData();
            }
        }
    }

    // Метод для попытки использования альтернативного URL, если первый не работает
    private void tryAlternativeUrl() {
        // Создаем новый Retrofit клиент с другим URL
        Retrofit alternativeRetrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:8000/")  // Пробуем другой URL
                .client(new OkHttpClient())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        
        RecyclingPointsService alternativeService = alternativeRetrofit.create(RecyclingPointsService.class);
        
        Log.d(TAG, "Пробуем альтернативный URL: http://10.0.2.2:8000/api/collection-points/");
        
        alternativeService.getRecyclingPoints().enqueue(new Callback<List<RecyclingPoint>>() {
            @Override
            public void onResponse(Call<List<RecyclingPoint>> call, Response<List<RecyclingPoint>> response) {
                Log.d(TAG, "Ответ альтернативного URL: " + response.code());
                if (response.isSuccessful() && response.body() != null) {
                    recyclingPoints = response.body();
                    updateMapMarkers();
                } else {
                    createTestData();
                }
            }

            @Override
            public void onFailure(Call<List<RecyclingPoint>> call, Throwable t) {
                Log.e(TAG, "Ошибка альтернативного URL", t);
                createTestData();
            }
        });
    }

    private void updateMapMarkers() {
        if (mMap == null) return;
        
        Log.d(TAG, "Обновляем маркеры на карте, всего точек: " + recyclingPoints.size());
        
        mMap.clear();
        markerPointMap.clear();
        
        List<String> selectedTypes = getSelectedWasteTypes();
        
        for (RecyclingPoint point : recyclingPoints) {
            String wasteType = point.getWasteTypes();
            
            // Если выбраны все или совпадает с типом точки
            if (selectedTypes.isEmpty() || selectedTypes.contains(wasteType)) {
                Log.d(TAG, "Добавляем маркер типа " + wasteType + ": " + point.getName());
                addMarkerForPoint(point);
            }
        }
        
        if (!markerPointMap.isEmpty()) {
            zoomToFitMarkers();
        } else {
            Log.d(TAG, "Нет подходящих маркеров для отображения");
        }
    }
    
    private List<String> getSelectedWasteTypes() {
        List<String> selectedTypes = new ArrayList<>();
        
        // Маппинг ID чипов на типы отходов
        Map<Integer, String> chipToWasteType = new HashMap<>();
        chipToWasteType.put(R.id.chip_plastic, "plastic");
        chipToWasteType.put(R.id.chip_paper, "paper");
        chipToWasteType.put(R.id.chip_metal, "metal");
        chipToWasteType.put(R.id.chip_glass, "glass");
        chipToWasteType.put(R.id.chip_medical, "medical");
        chipToWasteType.put(R.id.chip_construction, "construction");
        chipToWasteType.put(R.id.chip_agricultural, "agricultural");
        
        // Проверяем каждый чип
        for (int i = 0; i < wasteTypeFilterGroup.getChildCount(); i++) {
            View view = wasteTypeFilterGroup.getChildAt(i);
            if (view instanceof Chip) {
                Chip chip = (Chip) view;
                if (chip.isChecked() && chipToWasteType.containsKey(chip.getId())) {
                    String wasteType = chipToWasteType.get(chip.getId());
                    selectedTypes.add(wasteType);
                    Log.d(TAG, "Выбран фильтр: " + wasteType);
                }
            }
        }
        
        return selectedTypes;
    }

    private void addMarkerForPoint(RecyclingPoint point) {
        try {
            LatLng position = new LatLng(point.getLatitude(), point.getLongitude());
            
            Marker marker = mMap.addMarker(new MarkerOptions()
                    .position(position)
                    .title(point.getName())
                    .snippet(point.getAddress())
                    .icon(point.getMarkerIcon(requireContext())));
            
            if (marker != null) {
                markerPointMap.put(marker, point);
            }
        } catch (Exception e) {
            Log.e(TAG, "Ошибка при добавлении маркера: " + e.getMessage(), e);
        }
    }

    @Override
    public void onFilterSelected(String wasteType) {
        updateMapMarkers();
    }

    private void zoomToFitMarkers() {
        if (mMap == null || markerPointMap.isEmpty()) return;
        
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (Marker marker : markerPointMap.keySet()) {
            builder.include(marker.getPosition());
        }
        
        try {
            LatLngBounds bounds = builder.build();
            int padding = 100; // Отступ от краев в пикселях
            mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, padding));
        } catch (Exception e) {
            Log.e(TAG, "Ошибка при масштабировании карты: " + e.getMessage());
        }
    }

    private boolean onMarkerClick(Marker marker) {
        RecyclingPoint point = markerPointMap.get(marker);
        if (point != null) {
            showPointDetails(point);
            return true;
        }
        return false;
    }

    private void showPointDetails(RecyclingPoint point) {
        String wasteTypeRu = translateWasteType(point.getWasteTypes());
        String message = String.format("%s\n%s\nТип: %s", 
                point.getName(), point.getAddress(), wasteTypeRu);
        
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show();
    }
    
    private String translateWasteType(String wasteType) {
        switch (wasteType) {
            case "plastic": return "Пластик";
            case "paper": return "Бумага";
            case "metal": return "Металл";
            case "glass": return "Стекло";
            case "medical": return "Медицинские отходы";
            case "construction": return "Строительные отходы";
            case "agricultural": return "Сельхоз отходы";
            default: return wasteType;
        }
    }

    private boolean checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            
            ActivityCompat.requestPermissions(requireActivity(),
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return false;
        }
        return true;
    }
} 