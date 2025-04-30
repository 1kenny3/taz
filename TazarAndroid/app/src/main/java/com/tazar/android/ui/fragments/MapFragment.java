package com.tazar.android.ui.fragments;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private static final String TAG = "MapFragment";
    
    private GoogleMap mMap;
    private ProgressBar progressBar;
    private ChipGroup wasteTypeFilterGroup;
    private ImageButton btnToggleFilters;
    private boolean isFiltersExpanded = true;
    private List<TrashReport> trashReports = new ArrayList<>();
    private List<RecyclingPoint> recyclingPoints = new ArrayList<>();
    private Map<Marker, Object> markerMap = new HashMap<>();
    private RecyclingPointsService recyclingPointsService;
    private boolean dataLoaded = false; // Флаг для отслеживания загрузки данных
    private List<RecyclingPoint> allRecyclingPoints = new ArrayList<>();

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
        wasteTypeFilterGroup = view.findViewById(R.id.waste_type_filter_group);
        btnToggleFilters = view.findViewById(R.id.btn_toggle_filters);

        // Добавляем слушатели для кнопок масштабирования
        view.findViewById(R.id.btn_zoom_in).setOnClickListener(v -> {
            if (mMap != null) {
                mMap.animateCamera(CameraUpdateFactory.zoomIn());
                Log.d(TAG, "Нажата кнопка приближения");
            }
        });

        view.findViewById(R.id.btn_zoom_out).setOnClickListener(v -> {
            if (mMap != null) {
                mMap.animateCamera(CameraUpdateFactory.zoomOut());
                Log.d(TAG, "Нажата кнопка отдаления");
            }
        });

        // Добавляем слушатель для кнопки добавления отчета
        view.findViewById(R.id.fab_add_report).setOnClickListener(v -> {
            Log.d(TAG, "Нажата кнопка добавления отчета");
            showAddReportDialog();
        });

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        setupFilterPanel();

        // Добавим тестовые данные для отладки
        addTestData();

        return view;
    }

    private void setupFilterPanel() {
        // Восстанавливаем состояние панели, если оно было сохранено
        if (getContext() != null) {
            isFiltersExpanded = getContext()
                .getSharedPreferences("map_preferences", Context.MODE_PRIVATE)
                .getBoolean("filters_expanded", true);
            updateFilterPanelState();
        }

        btnToggleFilters.setOnClickListener(v -> toggleFilters());
    }

    private void toggleFilters() {
        isFiltersExpanded = !isFiltersExpanded;
        updateFilterPanelState();
        
        // Сохраняем состояние
        if (getContext() != null) {
            getContext()
                .getSharedPreferences("map_preferences", Context.MODE_PRIVATE)
                .edit()
                .putBoolean("filters_expanded", isFiltersExpanded)
                .apply();
        }
    }

    private void updateFilterPanelState() {
        if (isFiltersExpanded) {
            wasteTypeFilterGroup.setVisibility(View.VISIBLE);
            btnToggleFilters.setImageResource(R.drawable.ic_expand_less);
        } else {
            wasteTypeFilterGroup.setVisibility(View.GONE);
            btnToggleFilters.setImageResource(R.drawable.ic_expand_more);
        }
    }

    private void setupFilterListeners() {
        // Добавляем слушатель для группы чипов
        wasteTypeFilterGroup.setOnCheckedChangeListener((group, checkedId) -> {
            Log.d(TAG, "Изменен выбор фильтра: " + checkedId);
            updateMapMarkers();
        });

        // Добавляем слушатели для каждого чипа
        for (int i = 0; i < wasteTypeFilterGroup.getChildCount(); i++) {
            View view = wasteTypeFilterGroup.getChildAt(i);
            if (view instanceof Chip) {
                Chip chip = (Chip) view;
                chip.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    Log.d(TAG, "Чип " + chip.getText() + " " + (isChecked ? "выбран" : "снят"));
                    updateMapMarkers();
                });
            }
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        
        // Настройка элементов управления на карте
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setMapToolbarEnabled(true);
        
        // Добавляем слушатель для маркеров
        mMap.setOnMarkerClickListener(this);
        
        // Загружаем данные
        loadRecyclingPoints();
        
        // Устанавливаем все чипы выбранными по умолчанию
        setAllChipsChecked(true);
    }

    private void loadTrashReports() {
        // Код загрузки реальных данных из API
        // Для простоты тут можно использовать тестовые данные из addTestData()
    }

    private void loadRecyclingPoints() {
        showLoading(true);

        String token = "Bearer " + TazarApplication.getInstance().getAuthToken();
        
        recyclingPointsService.getRecyclingPoints(token).enqueue(new Callback<List<RecyclingPoint>>() {
            @Override
            public void onResponse(@NonNull Call<List<RecyclingPoint>> call, 
                                 @NonNull Response<List<RecyclingPoint>> response) {
                showLoading(false);
                
                if (response.isSuccessful() && response.body() != null) {
                    List<RecyclingPoint> points = response.body();
                    Log.d("MapFragment", "Получено пунктов переработки: " + points.size());
                    
                    // Добавляем тестовые данные, если с сервера ничего не пришло
                    if (points.isEmpty()) {
                        addTestRecyclingPoints();
                    } else {
                        allRecyclingPoints.clear();
                        allRecyclingPoints.addAll(points);
                    }
                    
                    updateMapMarkers();
                } else {
                    Log.e("MapFragment", "Ошибка получения пунктов переработки: " + 
                        (response.errorBody() != null ? "Код: " + response.code() : "Нет тела ответа"));
                    
                    // Добавляем тестовые данные, если произошла ошибка
                    addTestRecyclingPoints();
                    updateMapMarkers();
                    
                    Toast.makeText(getContext(), "Используются демо-данные для пунктов переработки", 
                        Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<RecyclingPoint>> call, @NonNull Throwable t) {
                showLoading(false);
                Log.e("MapFragment", "Сбой сети при получении пунктов переработки", t);
                
                // Добавляем тестовые данные при сбое сети
                addTestRecyclingPoints();
                updateMapMarkers();
                
                Toast.makeText(getContext(), "Используются демо-данные для пунктов переработки", 
                    Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addTestData() {
        // Добавляем тестовые точки мусора разных типов
        TrashReport plasticReport = new TrashReport();
        plasticReport.setId(1);
        plasticReport.setLatitude(55.751244);
        plasticReport.setLongitude(37.618423);
        plasticReport.setAddress("Москва, Кремль");
        plasticReport.setDescription("Пластиковый мусор");
        plasticReport.setStatus("new");
        plasticReport.setWasteType("plastic");
        trashReports.add(plasticReport);

        TrashReport paperReport = new TrashReport();
        paperReport.setId(2);
        paperReport.setLatitude(55.753215);
        paperReport.setLongitude(37.622504);
        paperReport.setAddress("Москва, Красная площадь");
        paperReport.setDescription("Бумажный мусор");
        paperReport.setStatus("in_progress");
        paperReport.setWasteType("paper");
        trashReports.add(paperReport);

        TrashReport glassReport = new TrashReport();
        glassReport.setId(3);
        glassReport.setLatitude(55.749469);
        glassReport.setLongitude(37.608644);
        glassReport.setAddress("Москва, ул. Арбат");
        glassReport.setDescription("Стеклянный мусор");
        glassReport.setStatus("completed");
        glassReport.setWasteType("glass");
        trashReports.add(glassReport);

        // Добавляем тестовые пункты сбора отходов
        RecyclingPoint plasticPoint = new RecyclingPoint();
        plasticPoint.setId(1);
        plasticPoint.setLatitude(55.746129);
        plasticPoint.setLongitude(37.626591);
        plasticPoint.setName("Пункт приема пластика");
        plasticPoint.setAddress("Москва, ул. Ленина 10");
        plasticPoint.setAcceptedTypes(new String[]{"plastic"});
        recyclingPoints.add(plasticPoint);

        RecyclingPoint paperPoint = new RecyclingPoint();
        paperPoint.setId(2);
        paperPoint.setLatitude(55.758468);
        paperPoint.setLongitude(37.601013);
        paperPoint.setName("Пункт приема бумаги и стекла");
        paperPoint.setAddress("Москва, ул. Тверская 15");
        paperPoint.setAcceptedTypes(new String[]{"paper", "glass"});
        recyclingPoints.add(paperPoint);

        RecyclingPoint mixedPoint = new RecyclingPoint();
        mixedPoint.setId(3);
        mixedPoint.setLatitude(55.739626);
        mixedPoint.setLongitude(37.626788);
        mixedPoint.setName("Пункт приема всех типов отходов");
        mixedPoint.setAddress("Москва, ул. Садовая 20");
        mixedPoint.setAcceptedTypes(new String[]{"plastic", "paper", "glass", "metal", "medical", "construction", "agricultural"});
        recyclingPoints.add(mixedPoint);

        dataLoaded = true;
        Log.d("MapFragment", "Тестовые данные добавлены: " + trashReports.size() + " точек мусора, " + recyclingPoints.size() + " пунктов переработки");
    }

    private void addTestRecyclingPoints() {
        // Очищаем существующие точки
        allRecyclingPoints.clear();
        
        // Пункт приема пластика
        RecyclingPoint plasticPoint = new RecyclingPoint();
        plasticPoint.setId(1);
        plasticPoint.setName("Пункт приема пластика");
        plasticPoint.setAddress("Москва, ул. Ленина 10");
        plasticPoint.setLatitude(55.746129);
        plasticPoint.setLongitude(37.626591);
        plasticPoint.setAcceptedTypes(new String[]{"plastic"});
        allRecyclingPoints.add(plasticPoint);

        // Пункт приема бумаги и стекла
        RecyclingPoint paperGlassPoint = new RecyclingPoint();
        paperGlassPoint.setId(2);
        paperGlassPoint.setName("Пункт приема бумаги и стекла");
        paperGlassPoint.setAddress("Москва, ул. Тверская 15");
        paperGlassPoint.setLatitude(55.758468);
        paperGlassPoint.setLongitude(37.601013);
        paperGlassPoint.setAcceptedTypes(new String[]{"paper", "glass"});
        allRecyclingPoints.add(paperGlassPoint);

        // Пункт приема металла
        RecyclingPoint metalPoint = new RecyclingPoint();
        metalPoint.setId(3);
        metalPoint.setName("Пункт приема металла");
        metalPoint.setAddress("Москва, ул. Марксистская 5");
        metalPoint.setLatitude(55.736729);
        metalPoint.setLongitude(37.664830);
        metalPoint.setAcceptedTypes(new String[]{"metal"});
        allRecyclingPoints.add(metalPoint);

        // Пункт приема медицинских отходов
        RecyclingPoint medicalPoint = new RecyclingPoint();
        medicalPoint.setId(4);
        medicalPoint.setName("Пункт приема медицинских отходов");
        medicalPoint.setAddress("Москва, ул. Дмитровская 32");
        medicalPoint.setLatitude(55.807872);
        medicalPoint.setLongitude(37.575350);
        medicalPoint.setAcceptedTypes(new String[]{"medical"});
        allRecyclingPoints.add(medicalPoint);

        // Пункт приема строительного мусора
        RecyclingPoint constructionPoint = new RecyclingPoint();
        constructionPoint.setId(5);
        constructionPoint.setName("Пункт приема строительных отходов");
        constructionPoint.setAddress("Москва, ул. Октябрьская 10");
        constructionPoint.setLatitude(55.778872);
        constructionPoint.setLongitude(37.595350);
        constructionPoint.setAcceptedTypes(new String[]{"construction"});
        allRecyclingPoints.add(constructionPoint);

        // Пункт приема сельскохозяйственных отходов
        RecyclingPoint agriculturalPoint = new RecyclingPoint();
        agriculturalPoint.setId(6);
        agriculturalPoint.setName("Пункт приема сельхоз отходов");
        agriculturalPoint.setAddress("Москва, ул. Садовая 45");
        agriculturalPoint.setLatitude(55.727872);
        agriculturalPoint.setLongitude(37.615350);
        agriculturalPoint.setAcceptedTypes(new String[]{"agricultural"});
        allRecyclingPoints.add(agriculturalPoint);

        // Универсальный пункт
        RecyclingPoint mixedPoint = new RecyclingPoint();
        mixedPoint.setId(7);
        mixedPoint.setName("Универсальный пункт приема");
        mixedPoint.setAddress("Москва, ул. Садовая 20");
        mixedPoint.setLatitude(55.739626);
        mixedPoint.setLongitude(37.626788);
        mixedPoint.setAcceptedTypes(new String[]{"plastic", "paper", "glass", "metal", 
                                                "medical", "construction", "agricultural"});
        allRecyclingPoints.add(mixedPoint);
        
        Log.d("MapFragment", "Добавлено " + allRecyclingPoints.size() + " тестовых пунктов переработки");
    }

    private void updateMapMarkers() {
        if (mMap == null) return;
        
        Log.d(TAG, "Обновление маркеров на карте");
        
        mMap.clear();
        markerMap.clear();

        // Получаем выбранные типы отходов
        List<String> selectedTypes = getSelectedWasteTypes();
        Log.d(TAG, "Выбранные типы отходов: " + selectedTypes);

        // Если ни один тип не выбран, показываем все точки
        if (selectedTypes.isEmpty()) {
            Log.d(TAG, "Ни один тип не выбран, показываем все точки");
            for (RecyclingPoint point : allRecyclingPoints) {
                addRecyclingPointMarker(point);
            }
            return;
        }

        // Отображаем точки переработки согласно фильтрам
        for (RecyclingPoint point : allRecyclingPoints) {
            if (point.getAcceptedTypes() != null) {
                // Проверяем, принимает ли точка хотя бы один из выбранных типов отходов
                boolean hasMatchingType = Arrays.stream(point.getAcceptedTypes())
                        .anyMatch(selectedTypes::contains);
                
                if (hasMatchingType) {
                    Log.d(TAG, "Добавляем точку: " + point.getName() + " с типами: " + 
                              Arrays.toString(point.getAcceptedTypes()));
                    addRecyclingPointMarker(point);
                }
            }
        }
    }

    private void addTrashMarker(TrashReport report) {
        LatLng position = new LatLng(report.getLatitude(), report.getLongitude());
        BitmapDescriptor icon = getTrashMarkerIcon(report.getStatus(), report.getWasteType());
        
        Marker marker = mMap.addMarker(new MarkerOptions()
                .position(position)
                .title(report.getAddress())
                .snippet("Тип отходов: " + getWasteTypeName(report.getWasteType()))
                .icon(icon));
        
        if (marker != null) {
            markerMap.put(marker, report);
        }
    }

    private void addRecyclingPointMarker(RecyclingPoint point) {
        LatLng position = new LatLng(point.getLatitude(), point.getLongitude());
        BitmapDescriptor icon = getRecyclingPointIcon(point.getAcceptedTypes());
        
        String typesList = Arrays.stream(point.getAcceptedTypes())
                .map(this::getWasteTypeName)
                .collect(Collectors.joining(", "));
        
        Marker marker = mMap.addMarker(new MarkerOptions()
                .position(position)
                .title(point.getName())
                .snippet("Принимает: " + typesList)
                .icon(icon));
        
        if (marker != null) {
            markerMap.put(marker, point);
        }
    }

    private BitmapDescriptor getTrashMarkerIcon(String status, String wasteType) {
        float hue;
        
        // Определяем цвет по типу отходов
        switch (wasteType) {
            case "plastic":
                hue = BitmapDescriptorFactory.HUE_BLUE;
                break;
            case "paper":
                hue = BitmapDescriptorFactory.HUE_YELLOW;
                break;
            case "glass":
                hue = BitmapDescriptorFactory.HUE_CYAN;
                break;
            case "metal":
                hue = BitmapDescriptorFactory.HUE_MAGENTA;
                break;
            case "medical":
                hue = BitmapDescriptorFactory.HUE_RED;
                break;
            case "construction":
                hue = BitmapDescriptorFactory.HUE_ORANGE;
                break;
            case "agricultural":
                hue = BitmapDescriptorFactory.HUE_GREEN;
                break;
            default:
                hue = BitmapDescriptorFactory.HUE_RED;
        }
        
        // Изменяем прозрачность в зависимости от статуса
        float alpha = 1.0f;
        if (status.equals("completed")) {
            alpha = 0.5f;
        }
        
        return BitmapDescriptorFactory.defaultMarker(hue);
    }

    private BitmapDescriptor getRecyclingPointIcon(String[] types) {
        if (types == null || types.length == 0) {
            return BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN);
        }
        
        // Если пункт принимает несколько типов отходов, делаем его зеленым
        if (types.length > 1) {
            return BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN);
        }
        
        // Иначе определяем цвет по типу отходов
        float hue;
        switch (types[0]) {
            case RecyclingPoint.TYPE_PLASTIC:
                hue = BitmapDescriptorFactory.HUE_BLUE;
                break;
            case RecyclingPoint.TYPE_PAPER:
                hue = BitmapDescriptorFactory.HUE_YELLOW;
                break;
            case RecyclingPoint.TYPE_GLASS:
                hue = BitmapDescriptorFactory.HUE_CYAN;
                break;
            case RecyclingPoint.TYPE_METAL:
                hue = BitmapDescriptorFactory.HUE_MAGENTA;
                break;
            case RecyclingPoint.TYPE_MEDICAL:
                hue = BitmapDescriptorFactory.HUE_RED;
                break;
            case RecyclingPoint.TYPE_CONSTRUCTION:
                hue = BitmapDescriptorFactory.HUE_ORANGE;
                break;
            case RecyclingPoint.TYPE_AGRICULTURAL:
                hue = BitmapDescriptorFactory.HUE_GREEN;
                break;
            default:
                hue = BitmapDescriptorFactory.HUE_GREEN;
        }
        
        return BitmapDescriptorFactory.defaultMarker(hue);
    }

    private String getWasteTypeName(String type) {
        switch (type) {
            case "plastic":
                return "Пластик";
            case "paper":
                return "Бумага";
            case "glass":
                return "Стекло";
            case "metal":
                return "Металл";
            case "medical":
                return "Медицинские отходы";
            case "construction":
                return "Строительные отходы";
            case "agricultural":
                return "Сельскохозяйственные отходы";
            default:
                return type;
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        Object item = markerMap.get(marker);
        if (item instanceof TrashReport) {
            showTrashReportDetails((TrashReport) item);
            return true;
        } else if (item instanceof RecyclingPoint) {
            showRecyclingPointDetails((RecyclingPoint) item);
            return true;
        }
        return false;
    }

    private void showTrashReportDetails(TrashReport report) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Информация о мусоре");
        
        String info = "Адрес: " + report.getAddress() + "\n\n" +
                      "Описание: " + report.getDescription() + "\n\n" +
                      "Тип отходов: " + getWasteTypeName(report.getWasteType()) + "\n\n" +
                      "Статус: " + getStatusName(report.getStatus());
        
        builder.setMessage(info);
        builder.setPositiveButton("Закрыть", null);
        builder.show();
    }

    private void showRecyclingPointDetails(RecyclingPoint point) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Пункт переработки");
        
        String typesList = Arrays.stream(point.getAcceptedTypes())
                .map(this::getWasteTypeName)
                .collect(Collectors.joining(", "));
        
        String info = "Название: " + point.getName() + "\n\n" +
                      "Адрес: " + point.getAddress() + "\n\n" +
                      "Принимаемые отходы: " + typesList;
        
        builder.setMessage(info);
        builder.setPositiveButton("Закрыть", null);
        builder.show();
    }

    private String getStatusName(String status) {
        switch (status) {
            case "new":
                return "Новый";
            case "in_progress":
                return "В обработке";
            case "completed":
                return "Убран";
            default:
                return status;
        }
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
        Log.d(TAG, "Открытие экрана создания отчета");
        
        // Проверяем авторизацию пользователя
        if (TazarApplication.getInstance().getAuthToken() == null) {
            Toast.makeText(requireContext(), "Для создания отчета необходимо авторизоваться", 
                Toast.LENGTH_LONG).show();
            return;
        }
        
        // Проверяем разрешение на геолокацию
        if (!checkLocationPermission()) {
            Toast.makeText(requireContext(), "Для создания отчета необходим доступ к геолокации", 
                Toast.LENGTH_LONG).show();
            return;
        }
        
        // Создаем Intent для перехода к активности создания отчета
        Intent intent = new Intent(getActivity(), CreateReportActivity.class);
        
        // Если есть текущая позиция камеры на карте, передаем её
        if (mMap != null) {
            LatLng center = mMap.getCameraPosition().target;
            intent.putExtra("latitude", center.latitude);
            intent.putExtra("longitude", center.longitude);
        }
        
        // Запускаем активность
        try {
            startActivity(intent);
        } catch (Exception e) {
            Log.e(TAG, "Ошибка при открытии экрана создания отчета", e);
            Toast.makeText(requireContext(), 
                "Не удалось открыть экран создания отчета: " + e.getMessage(), 
                Toast.LENGTH_SHORT).show();
        }
    }

    private List<String> getSelectedWasteTypes() {
        List<String> selectedTypes = new ArrayList<>();
        Map<Integer, String> chipToWasteType = new HashMap<>();
        
        // Маппинг ID чипов на типы отходов
        chipToWasteType.put(R.id.chip_plastic, "plastic");
        chipToWasteType.put(R.id.chip_paper, "paper");
        chipToWasteType.put(R.id.chip_glass, "glass");
        chipToWasteType.put(R.id.chip_metal, "metal");
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
                    Log.d(TAG, "Выбран фильтр: " + chip.getText() + " (" + wasteType + ")");
                }
            }
        }
        
        return selectedTypes;
    }

    private void setAllChipsChecked(boolean checked) {
        if (wasteTypeFilterGroup != null) {
            for (int i = 0; i < wasteTypeFilterGroup.getChildCount(); i++) {
                View view = wasteTypeFilterGroup.getChildAt(i);
                if (view instanceof Chip) {
                    ((Chip) view).setChecked(checked);
                }
            }
        }
    }
} 