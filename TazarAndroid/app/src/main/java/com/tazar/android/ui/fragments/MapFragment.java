package com.tazar.android.ui.fragments;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
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

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.tazar.android.R;
import com.tazar.android.TazarApplication;
import com.tazar.android.api.ApiConfig;
import com.tazar.android.models.TrashReport;
import com.tazar.android.api.services.TrashReportService;
import com.tazar.android.ui.activities.CreateReportActivity;
import com.tazar.android.models.RecyclingPoint;
import com.tazar.android.api.services.RecyclingPointsService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ProgressBar progressBar;
    private ChipGroup statusFilterGroup;
    private List<TrashReport> allReports = new ArrayList<>();
    private Map<Marker, RecyclingPoint> markerPointMap = new HashMap<>();
    private List<RecyclingPoint> recyclingPoints = new ArrayList<>();
    private RecyclingPointsService recyclingPointsService;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        recyclingPointsService = ApiConfig.getService(RecyclingPointsService.class);
    }

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container, @NonNull Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        progressBar = view.findViewById(R.id.progress_bar);
        statusFilterGroup = view.findViewById(R.id.status_filter_group);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        setupFilters();

        view.findViewById(R.id.fab_add_report).setOnClickListener(v -> {
            showAddReportDialog();
        });

        return view;
    }

    private void setupFilters() {
        statusFilterGroup.setOnCheckedChangeListener((group, checkedId) -> {
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
        String token = "Bearer " + TazarApplication.getInstance().getAuthToken();
        recyclingPointsService.getRecyclingPoints(token).enqueue(new Callback<List<RecyclingPoint>>() {
            @Override
            public void onResponse(Call<List<RecyclingPoint>> call, Response<List<RecyclingPoint>> response) {
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
                Toast.makeText(getContext(), 
                    R.string.error_network, 
                    Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateMapMarkers() {
        if (mMap == null) return;
        
        mMap.clear();
        markerPointMap.clear();
        
        List<String> selectedTypes = new ArrayList<>();
        
        if (((Chip) statusFilterGroup.findViewById(R.id.chip_new)).isChecked()) {
            selectedTypes.add("plastic");
        }
        if (((Chip) statusFilterGroup.findViewById(R.id.chip_in_progress)).isChecked()) {
            selectedTypes.add("paper");
        }
        if (((Chip) statusFilterGroup.findViewById(R.id.chip_completed)).isChecked()) {
            selectedTypes.add("metal");
        }
        if (((Chip) statusFilterGroup.findViewById(R.id.chip_glass)).isChecked()) {
            selectedTypes.add("glass");
        }
        if (((Chip) statusFilterGroup.findViewById(R.id.chip_medical)).isChecked()) {
            selectedTypes.add("medical");
        }
        if (((Chip) statusFilterGroup.findViewById(R.id.chip_construction)).isChecked()) {
            selectedTypes.add("construction");
        }
        if (((Chip) statusFilterGroup.findViewById(R.id.chip_agricultural)).isChecked()) {
            selectedTypes.add("agricultural");
        }
        
        for (RecyclingPoint point : recyclingPoints) {
            if (selectedTypes.isEmpty() || selectedTypes.contains(point.getWasteTypes())) {
                LatLng position = new LatLng(point.getLatitude(), point.getLongitude());
                
                Marker marker = mMap.addMarker(new MarkerOptions()
                        .position(position)
                        .title(point.getName())
                        .snippet(point.getAddress())
                        .icon(point.getMarkerIcon(requireContext())));
                
                if (marker != null) {
                    markerPointMap.put(marker, point);
                }
            }
        }
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
        // TODO: Показать диалог с деталями точки переработки
        // Можно использовать BottomSheetDialog
        Toast.makeText(requireContext(), 
            point.getName() + "\n" + point.getAddress(), 
            Toast.LENGTH_LONG).show();
    }

    private void showLoading(boolean show) {
        if (progressBar != null) {
            progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }

    private boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), 
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    1);
            return false;
        }
        return true;
    }

    private void showAddReportDialog() {
        // TODO: Реализация диалога добавления нового отчета
    }
} 