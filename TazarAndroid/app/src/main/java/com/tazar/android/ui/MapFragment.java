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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
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

public class MapFragment extends Fragment implements OnMapReadyCallback, WasteTypeFilterAdapter.OnFilterSelectedListener {
    private GoogleMap mMap;
    private ChipGroup statusFilterGroup;
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
        
        statusFilterGroup = view.findViewById(R.id.status_filter_group);
        progressBar = view.findViewById(R.id.progress_bar);
        
        // Инициализируем все чипы и устанавливаем им слушатели
        initChips(view);
        
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
        
        return view;
    }
    
    private void initChips(View view) {
        // Установим все чипы как выбранные по умолчанию
        Chip chipPlastic = view.findViewById(R.id.chip_new);
        Chip chipPaper = view.findViewById(R.id.chip_in_progress);
        Chip chipMetal = view.findViewById(R.id.chip_completed);
        Chip chipGlass = view.findViewById(R.id.chip_glass);
        Chip chipMedical = view.findViewById(R.id.chip_medical);
        Chip chipConstruction = view.findViewById(R.id.chip_construction);
        Chip chipAgricultural = view.findViewById(R.id.chip_agricultural);
        
        if (chipPlastic != null) chipPlastic.setChecked(true);
        if (chipPaper != null) chipPaper.setChecked(true);
        if (chipMetal != null) chipMetal.setChecked(true);
        if (chipGlass != null) chipGlass.setChecked(true);
        if (chipMedical != null) chipMedical.setChecked(true);
        if (chipConstruction != null) chipConstruction.setChecked(true);
        if (chipAgricultural != null) chipAgricultural.setChecked(true);
        
        // Добавим логирование для отладки
        statusFilterGroup.setOnCheckedChangeListener((group, checkedId) -> {
            Log.d("MapFragment", "Chip checked changed: " + checkedId);
            updateMapMarkers();
        });
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        
        if (checkLocationPermission()) {
            mMap.setMyLocationEnabled(true);
        }
        
        mMap.setOnMarkerClickListener(this::onMarkerClick);
        
        // Загружаем точки после инициализации карты
        loadRecyclingPoints();
    }
    
    private void loadRecyclingPoints() {
        progressBar.setVisibility(View.VISIBLE);
        
        String token = "Bearer " + TazarApplication.getInstance().getAuthToken();
        recyclingPointsService.getRecyclingPoints(token).enqueue(new Callback<List<RecyclingPoint>>() {
            @Override
            public void onResponse(Call<List<RecyclingPoint>> call, Response<List<RecyclingPoint>> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    recyclingPoints = response.body();
                    updateMapMarkers();
                } else {
                    Toast.makeText(getContext(), 
                        R.string.error_loading_recycling_points, 
                        Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<RecyclingPoint>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getContext(), 
                    R.string.error_network, 
                    Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateMapMarkers() {
        if (mMap == null) return;
        
        Log.d("MapFragment", "Updating map markers, points count: " + recyclingPoints.size());
        
        mMap.clear();
        markerPointMap.clear();
        
        List<String> selectedTypes = new ArrayList<>();
        
        try {
            // Используем более надежный способ проверки выбранных чипов
            for (int i = 0; i < statusFilterGroup.getChildCount(); i++) {
                View view = statusFilterGroup.getChildAt(i);
                if (view instanceof Chip) {
                    Chip chip = (Chip) view;
                    if (chip.isChecked()) {
                        String wasteType = "";
                        int id = chip.getId();
                        
                        if (id == R.id.chip_new) wasteType = "plastic";
                        else if (id == R.id.chip_in_progress) wasteType = "paper";
                        else if (id == R.id.chip_completed) wasteType = "metal";
                        else if (id == R.id.chip_glass) wasteType = "glass";
                        else if (id == R.id.chip_medical) wasteType = "medical";
                        else if (id == R.id.chip_construction) wasteType = "construction";
                        else if (id == R.id.chip_agricultural) wasteType = "agricultural";
                        
                        if (!wasteType.isEmpty()) {
                            selectedTypes.add(wasteType);
                            Log.d("MapFragment", "Added selected type: " + wasteType);
                        }
                    }
                }
            }
        } catch (Exception e) {
            Log.e("MapFragment", "Error getting selected types: " + e.getMessage(), e);
        }
        
        // Если ни один фильтр не выбран, покажем все
        if (selectedTypes.isEmpty()) {
            Log.d("MapFragment", "No filters selected, showing all points");
            for (RecyclingPoint point : recyclingPoints) {
                addMarkerForPoint(point);
            }
        } else {
            // Иначе фильтруем
            for (RecyclingPoint point : recyclingPoints) {
                String wasteType = point.getWasteTypes();
                Log.d("MapFragment", "Checking point type: " + wasteType);
                
                // Если у точки нет типа, присвоим ей случайный тип для тестирования
                if (wasteType == null || wasteType.isEmpty()) {
                    String[] types = {"plastic", "paper", "metal", "glass", "medical", "construction", "agricultural"};
                    int index = (int)(Math.random() * types.length);
                    wasteType = types[index];
                    point.setWasteTypes(wasteType);
                    Log.d("MapFragment", "Assigned random type to point: " + wasteType);
                }
                
                if (selectedTypes.contains(wasteType)) {
                    Log.d("MapFragment", "Adding marker for type: " + wasteType);
                    addMarkerForPoint(point);
                }
            }
        }
        
        if (!markerPointMap.isEmpty()) {
            zoomToFitMarkers();
        } else {
            Log.d("MapFragment", "No markers to show!");
        }
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
            Log.e("MapFragment", "Error adding marker: " + e.getMessage(), e);
        }
    }

    @Override
    public void onFilterSelected(String wasteType) {
        // Этот метод будет вызываться, когда выбран фильтр
        updateMapMarkers();
    }

    private void zoomToFitMarkers() {
        if (mMap == null || markerPointMap.isEmpty()) return;
        
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (Marker marker : markerPointMap.keySet()) {
            builder.include(marker.getPosition());
        }
        
        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(
                builder.build(), 100));
    }

    private boolean onMarkerClick(Marker marker) {
        RecyclingPoint point = markerPointMap.get(marker);
        if (point != null) {
            // Показываем детали точки переработки
            showPointDetails(point);
            return true;
        }
        return false;
    }

    private void showPointDetails(RecyclingPoint point) {
        // Показываем информацию о точке переработки
        Toast.makeText(requireContext(), 
            point.getName() + "\n" + point.getAddress(), 
            Toast.LENGTH_LONG).show();
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