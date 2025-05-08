package com.tazar.android.ui.fragments;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.TextView;
import android.widget.LinearLayout;
import android.graphics.Typeface;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
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
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.tazar.android.R;
import com.tazar.android.EcoupApplication;
import com.tazar.android.api.ApiConfig;
import com.tazar.android.models.TrashReport;
import com.tazar.android.api.services.TrashReportService;
import com.tazar.android.models.RecyclingPoint;
import com.tazar.android.api.services.RecyclingPointsService;
import com.tazar.android.ui.activities.CreateReportActivity;
import com.tazar.android.helpers.PreferencesManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private static final String TAG = "MapFragment";
    
    private GoogleMap mMap;
    private View loadingView;
    private ChipGroup filterChipGroup;
    private ChipGroup wasteTypesChipGroup;
    private View bottomSheet;
    private TextView pointTitle;
    private TextView pointAddress;
    private MaterialButton routeButton;
    private TextInputEditText searchEditText;
    private FloatingActionButton locationFab;
    private List<RecyclingPoint> recyclingPoints = new ArrayList<>();
    private Map<Marker, Object> markerMap = new HashMap<>();
    private RecyclingPointsService recyclingPointsService;
    private List<RecyclingPoint> allRecyclingPoints = new ArrayList<>();
    private Marker lastClickedMarker;
    private View searchHeader;
    private View searchContent;
    private ImageView searchArrow;
    private boolean isSearchExpanded = true;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        recyclingPointsService = ApiConfig.getService(RecyclingPointsService.class);
    }

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container, @NonNull Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        // Инициализация views
        loadingView = view.findViewById(R.id.loading_view);
        filterChipGroup = view.findViewById(R.id.filter_chip_group);
        bottomSheet = view.findViewById(R.id.bottom_sheet);
        pointTitle = view.findViewById(R.id.point_title);
        pointAddress = view.findViewById(R.id.point_address);
        wasteTypesChipGroup = view.findViewById(R.id.waste_types_chip_group);
        routeButton = view.findViewById(R.id.route_button);
        searchEditText = view.findViewById(R.id.search_edit_text);
        locationFab = view.findViewById(R.id.location_fab);
        searchHeader = view.findViewById(R.id.search_header);
        searchContent = view.findViewById(R.id.search_content);
        searchArrow = view.findViewById(R.id.search_arrow);

        // Настройка поиска
        setupSearch();

        // Настройка фильтров
        setupFilters();

        // Настройка кнопки определения местоположения
        setupLocationButton();

        // Настройка bottom sheet
        setupBottomSheet();

        // Инициализация карты
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        return view;
    }

    private void setupSearch() {
        // Устанавливаем начальное состояние
        searchContent.setVisibility(View.VISIBLE);
        searchArrow.setRotation(180); // Стрелка вверх
        
        searchHeader.setOnClickListener(v -> toggleSearchPanel());
        
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                filterRecyclingPoints(s.toString());
            }
        });
    }

    private void toggleSearchPanel() {
        isSearchExpanded = !isSearchExpanded;
        searchContent.setVisibility(isSearchExpanded ? View.VISIBLE : View.GONE);
        searchArrow.animate()
                .rotation(isSearchExpanded ? 180 : 0)
                .setDuration(200)
                .start();
    }

    private void setupFilters() {
        // Устанавливаем все фильтры невыбранными по умолчанию
        setAllChipsChecked(false);

        // Добавляем слушатель изменений
        filterChipGroup.setOnCheckedStateChangeListener((group, checkedIds) -> {
            Log.d(TAG, "Изменение фильтров. Выбрано: " + checkedIds.size());
            updateMapMarkers();
        });
    }

    private void setupLocationButton() {
        locationFab.setOnClickListener(v -> {
            if (checkLocationPermission()) {
                mMap.setMyLocationEnabled(true);
                // Получаем текущее местоположение и перемещаем камеру
                if (getActivity() != null) {
                    FusedLocationProviderClient locationClient = LocationServices.getFusedLocationProviderClient(getActivity());
                    locationClient.getLastLocation().addOnSuccessListener(location -> {
                        if (location != null) {
                            LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15f));
                        }
                    });
                }
            }
        });
    }

    private void setupBottomSheet() {
        // Настройка поведения bottom sheet
        BottomSheetBehavior<View> behavior = BottomSheetBehavior.from(bottomSheet);
        behavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        
        routeButton.setOnClickListener(v -> {
            Object item = markerMap.get(lastClickedMarker);
            if (item instanceof RecyclingPoint) {
                RecyclingPoint point = (RecyclingPoint) item;
                // Открываем навигацию к точке
                String uri = String.format(Locale.ENGLISH, "google.navigation:q=%f,%f", 
                    point.getLatitude(), point.getLongitude());
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                intent.setPackage("com.google.android.apps.maps");
                startActivity(intent);
            }
        });
    }

    private void filterRecyclingPoints(String query) {
        if (query.isEmpty()) {
            recyclingPoints = new ArrayList<>(allRecyclingPoints);
        } else {
            recyclingPoints = allRecyclingPoints.stream()
                .filter(point -> point.getName().toLowerCase().contains(query.toLowerCase()) ||
                               point.getAddress().toLowerCase().contains(query.toLowerCase()))
                .collect(Collectors.toList());
        }
        updateMapMarkers();
    }

    private void showPointDetails(RecyclingPoint point) {
        // Очищаем предыдущие чипы
        wasteTypesChipGroup.removeAllViews();
        
        // Заполняем информацию
        pointTitle.setText(point.getName());
        pointAddress.setText(point.getAddress());
        
        // Добавляем чипы для типов отходов
        String[] types = point.getAcceptedTypes();
        if (types != null) {
            for (String type : types) {
                Chip chip = new Chip(requireContext());
                chip.setText(getWasteTypeName(type));
                chip.setChipIcon(getWasteTypeIcon(type));
                chip.setClickable(false);
                wasteTypesChipGroup.addView(chip);
            }
        }
        
        // Показываем bottom sheet
        BottomSheetBehavior.from(bottomSheet).setState(BottomSheetBehavior.STATE_EXPANDED);
    }

    private Drawable getWasteTypeIcon(String type) {
        int iconRes;
        switch (type.toLowerCase()) {
            case "plastic":
                iconRes = R.drawable.ic_plastic;
                break;
            case "paper":
                iconRes = R.drawable.ic_paper;
                break;
            case "glass":
                iconRes = R.drawable.ic_glass;
                break;
            case "metal":
                iconRes = R.drawable.ic_metal;
                break;
            case "medical":
                iconRes = R.drawable.ic_plastic; // Временно используем ic_plastic
                break;
            case "construction":
                iconRes = R.drawable.ic_plastic; // Временно используем ic_plastic
                break;
            case "agricultural":
                iconRes = R.drawable.ic_plastic; // Временно используем ic_plastic
                break;
            default:
                iconRes = R.drawable.ic_plastic;
                break;
        }
        return ContextCompat.getDrawable(requireContext(), iconRes);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        
        // Настройка карты
        mMap.getUiSettings().setZoomControlsEnabled(false);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setMapToolbarEnabled(false);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        
        // Настраиваем информационное окно маркера
        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                return null; // Используем стандартное окно
            }

            @Override
            public View getInfoContents(Marker marker) {
                // Создаем кастомное содержимое окна
                Context context = requireContext();
                LinearLayout info = new LinearLayout(context);
                info.setOrientation(LinearLayout.VERTICAL);
                info.setPadding(12, 12, 12, 12);

                TextView title = new TextView(context);
                title.setTextColor(ContextCompat.getColor(context, R.color.textColorPrimary));
                title.setTextSize(14);
                title.setTypeface(null, Typeface.BOLD);
                title.setText(marker.getTitle());

                TextView snippet = new TextView(context);
                snippet.setTextColor(ContextCompat.getColor(context, R.color.textColorSecondary));
                snippet.setTextSize(12);
                snippet.setText(marker.getSnippet());

                info.addView(title);
                info.addView(snippet);

                return info;
            }
        });
        
        // Добавляем слушатель для маркеров
        mMap.setOnMarkerClickListener(this);
        
        // Проверяем разрешение на геолокацию
        if (checkLocationPermission()) {
            mMap.setMyLocationEnabled(true);
            
            // Получаем текущее местоположение и центрируем карту
            if (getActivity() != null) {
                FusedLocationProviderClient locationClient = 
                    LocationServices.getFusedLocationProviderClient(getActivity());
                locationClient.getLastLocation().addOnSuccessListener(location -> {
                    if (location != null) {
                        LatLng currentLocation = new LatLng(
                            location.getLatitude(), 
                            location.getLongitude()
                        );
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 12f));
                    }
                });
            }
        }
        
        // Загружаем точки переработки
        loadRecyclingPoints();
    }

    private void loadRecyclingPoints() {
        showLoading(true);

        String token = "Bearer " + EcoupApplication.getInstance().getAuthToken();
        
        recyclingPointsService.getRecyclingPoints(token).enqueue(new Callback<List<RecyclingPoint>>() {
            @Override
            public void onResponse(@NonNull Call<List<RecyclingPoint>> call, 
                                 @NonNull Response<List<RecyclingPoint>> response) {
                showLoading(false);
                
                if (response.isSuccessful() && response.body() != null) {
                    List<RecyclingPoint> points = response.body();
                    Log.d(TAG, "Получено пунктов переработки: " + points.size());
                    
                    // Очищаем существующие точки
                    allRecyclingPoints.clear();
                    recyclingPoints.clear();
                    
                    // Добавляем тестовые данные, если с сервера ничего не пришло
                    if (points.isEmpty()) {
                        addTestRecyclingPoints();
                    } else {
                        allRecyclingPoints.addAll(points);
                        recyclingPoints.addAll(points);
                    }
                    
                    updateMapMarkers();
                } else {
                    Log.e(TAG, "Ошибка получения пунктов переработки: " + 
                        (response.errorBody() != null ? "Код: " + response.code() : "Нет тела ответа"));
                    
                    // Добавляем тестовые данные, если произошла ошибка
                    addTestRecyclingPoints();
                    
                    Toast.makeText(getContext(), "Используются демо-данные для пунктов переработки", 
                        Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<RecyclingPoint>> call, @NonNull Throwable t) {
                showLoading(false);
                Log.e(TAG, "Сбой сети при получении пунктов переработки", t);
                
                // Добавляем тестовые данные при сбое сети
                addTestRecyclingPoints();
                
                Toast.makeText(getContext(), "Используются демо-данные для пунктов переработки", 
                    Toast.LENGTH_SHORT).show();
            }
        });
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

        // Комбинированный пункт приема пластика и металла
        RecyclingPoint plasticMetalPoint = new RecyclingPoint();
        plasticMetalPoint.setId(7);
        plasticMetalPoint.setName("Пункт приема пластика и металла");
        plasticMetalPoint.setAddress("Москва, ул. Пушкина 22");
        plasticMetalPoint.setLatitude(55.749872);
        plasticMetalPoint.setLongitude(37.608350);
        plasticMetalPoint.setAcceptedTypes(new String[]{"plastic", "metal"});
        allRecyclingPoints.add(plasticMetalPoint);

        // Комбинированный пункт приема бумаги и медицинских отходов
        RecyclingPoint paperMedicalPoint = new RecyclingPoint();
        paperMedicalPoint.setId(8);
        paperMedicalPoint.setName("Пункт приема бумаги и медицинских отходов");
        paperMedicalPoint.setAddress("Москва, ул. Гоголя 17");
        paperMedicalPoint.setLatitude(55.759872);
        paperMedicalPoint.setLongitude(37.638350);
        paperMedicalPoint.setAcceptedTypes(new String[]{"paper", "medical"});
        allRecyclingPoints.add(paperMedicalPoint);

        // Универсальный пункт
        RecyclingPoint mixedPoint = new RecyclingPoint();
        mixedPoint.setId(9);
        mixedPoint.setName("Универсальный пункт приема");
        mixedPoint.setAddress("Москва, ул. Садовая 20");
        mixedPoint.setLatitude(55.739626);
        mixedPoint.setLongitude(37.626788);
        mixedPoint.setAcceptedTypes(new String[]{"plastic", "paper", "glass", "metal", 
                                                "medical", "construction", "agricultural"});
        allRecyclingPoints.add(mixedPoint);
        
        Log.d(TAG, "Добавлено " + allRecyclingPoints.size() + " тестовых пунктов переработки");
        
        // Добавляем в список для отображения
        recyclingPoints.clear();
        recyclingPoints.addAll(allRecyclingPoints);
    }

    private void updateMapMarkers() {
        if (mMap == null) return;

        // Очищаем карту
        mMap.clear();
        markerMap.clear();

        // Получаем выбранные фильтры
        List<String> selectedTypes = getSelectedWasteTypes();
        
        Log.d(TAG, "Обновление маркеров на карте. Всего точек: " + recyclingPoints.size() + 
              ", выбранные типы: " + selectedTypes);

        int addedMarkers = 0;
        // Фильтруем и добавляем точки
        for (RecyclingPoint point : recyclingPoints) {
            // Если нет выбранных фильтров или точка содержит хотя бы один выбранный тип
            if (hasSelectedType(point.getAcceptedTypes(), selectedTypes)) {
                addRecyclingPointMarker(point);
                addedMarkers++;
                Log.d(TAG, "Добавлен маркер для точки: " + point.getName() + 
                          " с типами: " + Arrays.toString(point.getAcceptedTypes()));
            }
        }

        Log.d(TAG, "Добавлено маркеров: " + addedMarkers);

        // Если есть точки на карте, центрируем карту
        if (!markerMap.isEmpty()) {
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            for (Map.Entry<Marker, Object> entry : markerMap.entrySet()) {
                if (entry.getValue() instanceof RecyclingPoint) {
                    RecyclingPoint point = (RecyclingPoint) entry.getValue();
                    builder.include(new LatLng(point.getLatitude(), point.getLongitude()));
                }
            }
            
            try {
                LatLngBounds bounds = builder.build();
                mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
            } catch (Exception e) {
                Log.e(TAG, "Ошибка при центрировании карты", e);
            }
        }
    }

    private void addRecyclingPointMarker(RecyclingPoint point) {
        LatLng position = new LatLng(point.getLatitude(), point.getLongitude());
        
        // Создаем строку с описанием типов отходов
        String snippet = "Принимает: " + Arrays.stream(point.getAcceptedTypes())
                .map(this::getWasteTypeName)
                .collect(Collectors.joining(", "));
        
        MarkerOptions markerOptions = new MarkerOptions()
            .position(position)
            .title(point.getName())
            .snippet(snippet)
            .icon(getRecyclingPointIcon(point.getAcceptedTypes()));
            
        Marker marker = mMap.addMarker(markerOptions);
        if (marker != null) {
            markerMap.put(marker, point);
        }
    }

    private BitmapDescriptor getRecyclingPointIcon(String[] types) {
        if (types == null || types.length == 0) {
            return BitmapDescriptorFactory.defaultMarker();
        }

        // Определяем цвет маркера в зависимости от типа отходов
        float markerColor;
        if (types.length > 1) {
            // Для точек с несколькими типами отходов используем фиолетовый цвет
            markerColor = BitmapDescriptorFactory.HUE_VIOLET;
        } else {
            // Для точек с одним типом отходов используем соответствующий цвет
            switch (types[0].toLowerCase()) {
                case "plastic":
                    markerColor = BitmapDescriptorFactory.HUE_BLUE; // Синий для пластика
                    break;
                case "paper":
                    markerColor = BitmapDescriptorFactory.HUE_YELLOW; // Желтый для бумаги
                    break;
                case "glass":
                    markerColor = BitmapDescriptorFactory.HUE_GREEN; // Зеленый для стекла
                    break;
                case "metal":
                    markerColor = BitmapDescriptorFactory.HUE_RED; // Красный для металла
                    break;
                case "medical":
                    markerColor = BitmapDescriptorFactory.HUE_AZURE; // Голубой для медицинских отходов
                    break;
                case "construction":
                    markerColor = BitmapDescriptorFactory.HUE_ORANGE; // Оранжевый для строительных отходов
                    break;
                case "agricultural":
                    markerColor = BitmapDescriptorFactory.HUE_MAGENTA; // Розовый для сельскохозяйственных отходов
                    break;
                default:
                    markerColor = BitmapDescriptorFactory.HUE_CYAN;
            }
        }

        return BitmapDescriptorFactory.defaultMarker(markerColor);
    }

    private String getWasteTypeName(String type) {
        switch (type.toLowerCase()) {
            case "plastic":
                return "Пластик";
            case "paper":
                return "Бумага";
            case "glass":
                return "Стекло";
            case "metal":
                return "Металл";
            case "medical":
                return "Медицинские";
            case "construction":
                return "Строительные";
            case "agricultural":
                return "Сельские";
            default:
                return type;
        }
    }

    @Override
    public boolean onMarkerClick(@NonNull Marker marker) {
        lastClickedMarker = marker;
        Object item = markerMap.get(marker);
        
        if (item instanceof RecyclingPoint) {
            showPointDetails((RecyclingPoint) item);
            return true;
        }
        
        return false;
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

    public void showAddReportDialog() {
        Log.d(TAG, "Открытие экрана создания отчета");
        
        // Проверяем авторизацию пользователя через оба менеджера
        PreferencesManager preferencesManager = new PreferencesManager(requireContext());
        if (!preferencesManager.isLoggedIn() || EcoupApplication.getInstance().getAuthToken() == null) {
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
        
        for (int i = 0; i < filterChipGroup.getChildCount(); i++) {
            View view = filterChipGroup.getChildAt(i);
            if (view instanceof Chip) {
                Chip chip = (Chip) view;
                if (chip.isChecked()) {
                    String type = chip.getTag() != null ? chip.getTag().toString() : null;
                    if (type != null) {
                        selectedTypes.add(type);
                        Log.d(TAG, "Выбран тип отходов: " + type);
                    }
                }
            }
        }
        
        Log.d(TAG, "Всего выбрано типов: " + selectedTypes.size());
        return selectedTypes;
    }

    private void setAllChipsChecked(boolean checked) {
        if (filterChipGroup != null) {
            for (int i = 0; i < filterChipGroup.getChildCount(); i++) {
                View view = filterChipGroup.getChildAt(i);
                if (view instanceof Chip) {
                    ((Chip) view).setChecked(checked);
                }
            }
        }
    }

    public void showLoading(boolean show) {
        if (loadingView != null) {
            loadingView.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }

    private boolean hasSelectedType(String[] pointTypes, List<String> selectedTypes) {
        if (pointTypes == null || selectedTypes == null || selectedTypes.isEmpty()) {
            return selectedTypes == null || selectedTypes.isEmpty(); // Показываем все точки, если нет фильтров
        }
        
        for (String pointType : pointTypes) {
            if (selectedTypes.contains(pointType)) {
                Log.d(TAG, "Найдено совпадение типа: " + pointType);
                return true;
            }
        }
        
        return false;
    }
} 