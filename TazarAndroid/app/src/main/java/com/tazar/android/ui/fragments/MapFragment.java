package com.tazar.android.ui.fragments;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.tazar.android.R;
import com.tazar.android.TazarApplication;
import com.tazar.android.config.ApiConfig;
import com.tazar.android.models.CollectionPoint;
import com.tazar.android.api.CollectionPointService;
import com.tazar.android.ui.activities.CreateReportActivity;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap mMap;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private FloatingActionButton fabAddReport;

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container, @NonNull Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        // Инициализация кнопки добавления отчета
        fabAddReport = view.findViewById(R.id.fab_add_report);
        fabAddReport.setOnClickListener(v -> openCreateReportActivity());

        // Получение фрагмента карты и настройка обратного вызова
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        return view;
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        } else {
            ActivityCompat.requestPermissions(requireActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        }

        // Загружаем пункты сбора
        loadCollectionPoints();
    }

    private void loadCollectionPoints() {
        CollectionPointService service = ApiConfig.getService(CollectionPointService.class);
        String token = "Bearer " + TazarApplication.getInstance().getAuthToken();
        Call<List<CollectionPoint>> call = service.getCollectionPoints(token);

        call.enqueue(new Callback<List<CollectionPoint>>() {
            @Override
            public void onResponse(@NonNull Call<List<CollectionPoint>> call,
                                 @NonNull Response<List<CollectionPoint>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    for (CollectionPoint point : response.body()) {
                        LatLng location = new LatLng(point.getLatitude(), point.getLongitude());
                        mMap.addMarker(new MarkerOptions()
                                .position(location)
                                .title(point.getName())
                                .snippet(point.getDescription()));
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<CollectionPoint>> call, @NonNull Throwable t) {
                Toast.makeText(requireContext(), "Ошибка загрузки пунктов сбора", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                         @NonNull int[] grantResults) {
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (mMap != null) {
                    mMap.setMyLocationEnabled(true);
                }
            }
        }
    }

    private void openCreateReportActivity() {
        Intent intent = new Intent(getActivity(), CreateReportActivity.class);
        startActivity(intent);
    }
} 