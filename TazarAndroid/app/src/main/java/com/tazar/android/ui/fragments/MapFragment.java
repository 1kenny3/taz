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
    private Map<Marker, TrashReport> markerReportMap = new HashMap<>();

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

        setupFilterListeners();

        view.findViewById(R.id.fab_add_report).setOnClickListener(v -> 
            startActivity(new Intent(getActivity(), CreateReportActivity.class))
        );

        return view;
    }

    private void setupFilterListeners() {
        statusFilterGroup.setOnCheckedStateChangeListener((group, checkedIds) -> updateMarkers());
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        if (checkLocationPermission()) {
            mMap.setMyLocationEnabled(true);
        }

        mMap.setOnMarkerClickListener(this::onMarkerClick);

        // Загружаем точки
        loadTrashReports();
    }

    private void loadTrashReports() {
        showLoading(true);

        TrashReportService service = ApiConfig.getService(TrashReportService.class);
        String token = "Bearer " + TazarApplication.getInstance().getAuthToken();

        service.getTrashReports(token).enqueue(new Callback<List<TrashReport>>() {
            @Override
            public void onResponse(@NonNull Call<List<TrashReport>> call, 
                                 @NonNull Response<List<TrashReport>> response) {
                showLoading(false);
                
                if (response.isSuccessful() && response.body() != null) {
                    allReports = response.body();
                    updateMarkers();
                    zoomToFitMarkers();
                } else {
                    Toast.makeText(getContext(), R.string.error_loading, Toast.LENGTH_SHORT).show();
                }
            }
            
            @Override
            public void onFailure(@NonNull Call<List<TrashReport>> call, @NonNull Throwable t) {
                showLoading(false);
                Toast.makeText(getContext(), R.string.error_network, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateMarkers() {
        if (mMap == null) return;
        
        mMap.clear();
        markerReportMap.clear();
        
        List<String> activeFilters = new ArrayList<>();
        if (((Chip) statusFilterGroup.findViewById(R.id.chip_new)).isChecked()) 
            activeFilters.add("new");
        if (((Chip) statusFilterGroup.findViewById(R.id.chip_in_progress)).isChecked()) 
            activeFilters.add("in_progress");
        if (((Chip) statusFilterGroup.findViewById(R.id.chip_completed)).isChecked()) 
            activeFilters.add("completed");
        
        for (TrashReport report : allReports) {
            if (activeFilters.contains(report.getStatus())) {
                LatLng position = new LatLng(report.getLatitude(), report.getLongitude());
                
                BitmapDescriptor icon = getMarkerIcon(report.getStatus());
                
                Marker marker = mMap.addMarker(new MarkerOptions()
                        .position(position)
                        .title(report.getAddress())
                        .snippet(report.getDescription())
                        .icon(icon));
                
                if (marker != null) {
                    markerReportMap.put(marker, report);
                }
            }
        }
    }

    private BitmapDescriptor getMarkerIcon(String status) {
        int drawableRes;
        switch (status) {
            case "new":
                drawableRes = R.drawable.ic_marker_new;
                break;
            case "in_progress":
                drawableRes = R.drawable.ic_marker_in_progress;
                break;
            case "completed":
                drawableRes = R.drawable.ic_marker_completed;
                break;
            default:
                return BitmapDescriptorFactory.defaultMarker();
        }
        
        Drawable drawable = ContextCompat.getDrawable(requireContext(), drawableRes);
        if (drawable == null) return BitmapDescriptorFactory.defaultMarker();
        
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), 
                drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.draw(canvas);
        
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    private void zoomToFitMarkers() {
        if (mMap == null || markerReportMap.isEmpty()) return;
        
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (Marker marker : markerReportMap.keySet()) {
            builder.include(marker.getPosition());
        }
        
        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(
                builder.build(), 100));
    }

    private boolean onMarkerClick(Marker marker) {
        TrashReport report = markerReportMap.get(marker);
        if (report != null) {
            // Показываем детали точки
            showReportDetails(report);
            return true;
        }
        return false;
    }

    private void showReportDetails(TrashReport report) {
        // TODO: Показать диалог с деталями точки
        // Можно использовать BottomSheetDialog
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
} 