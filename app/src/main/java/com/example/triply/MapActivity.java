package com.example.triply;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;

import com.example.triply.data.remote.api.GoongApiService;
import com.example.triply.data.remote.api.RetrofitInstance;
import com.example.triply.data.remote.response.DirectionResponse;
import com.example.triply.data.remote.response.PlaceDetailResponse;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.material.button.MaterialButton;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.LineString;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.geometry.LatLngBounds;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.plugins.annotation.SymbolManager;
import com.mapbox.mapboxsdk.plugins.annotation.SymbolOptions;
import com.mapbox.mapboxsdk.style.layers.LineLayer;
import com.mapbox.mapboxsdk.style.layers.Property;
import com.mapbox.mapboxsdk.style.layers.PropertyFactory;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapActivity extends AppCompatActivity {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;

    private MapView mapView;
    private MapboxMap mapboxMap;
    private SymbolManager symbolManager;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;

    private CardView infoCard;
    private TextView tvDestinationName, tvDestinationAddress, tvDistance, tvDuration;
    private ProgressBar progressBar;

    private String destinationName;
    private String destinationAddress;
    private String placeId;
    private Double destinationLat;
    private Double destinationLng;
    private LatLng destinationLatLng;
    private LatLng currentLatLng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize Mapbox
        Mapbox.getInstance(this, getString(R.string.mapbox_access_token));

        setContentView(R.layout.activity_map);

        initViews();
        getIntentData();
        initLocationServices();

        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this::onMapReady);
    }

    private void initViews() {
        mapView = findViewById(R.id.mapView);
        ImageView btnBack = findViewById(R.id.btnBack);
        infoCard = findViewById(R.id.infoCard);
        tvDestinationName = findViewById(R.id.tvDestinationName);
        tvDestinationAddress = findViewById(R.id.tvDestinationAddress);
        tvDistance = findViewById(R.id.tvDistance);
        tvDuration = findViewById(R.id.tvDuration);
//        MaterialButton btnStartNavigation = findViewById(R.id.btnStartNavigation);
        progressBar = findViewById(R.id.progressBar);

        btnBack.setOnClickListener(v -> finish());

//        btnStartNavigation.setOnClickListener(v -> {
//            if (currentLatLng != null && destinationLatLng != null) {
//                // Bắt đầu dẫn đường bằng cách gọi Directions API và vẽ route
//                fetchDirections();
//                Toast.makeText(this, "Đang tính toán đường đi...", Toast.LENGTH_SHORT).show();
//            } else if (destinationLatLng != null) {
//                Toast.makeText(this, "Không thể xác định vị trí hiện tại", Toast.LENGTH_SHORT).show();
//            } else {
//                Toast.makeText(this, "Không có thông tin điểm đến", Toast.LENGTH_SHORT).show();
//            }
//        });
    }

    private void getIntentData() {
        Intent intent = getIntent();
        destinationName = intent.getStringExtra("destination_name");
        destinationAddress = intent.getStringExtra("destination_address");
        placeId = intent.getStringExtra("place_id");
        
        // Ưu tiên sử dụng latitude/longitude nếu có
        if (intent.hasExtra("latitude") && intent.hasExtra("longitude")) {
            destinationLat = intent.getDoubleExtra("latitude", 0.0);
            destinationLng = intent.getDoubleExtra("longitude", 0.0);
            if (destinationLat == 0.0 && destinationLng == 0.0) {
                destinationLat = null;
                destinationLng = null;
            }
        }

        tvDestinationName.setText(destinationName != null ? destinationName : "Địa điểm");
        tvDestinationAddress.setText(destinationAddress != null ? destinationAddress : "");
    }

    private void initLocationServices() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
    }

    private void onMapReady(@NonNull MapboxMap mapboxMap) {
        this.mapboxMap = mapboxMap;

        String styleUrl = getString(R.string.goong_map_url) +
                "/assets/goong_map_web.json?api_key=" +
                getString(R.string.goong_map_key);

        mapboxMap.setStyle(styleUrl, style -> {
            symbolManager = new SymbolManager(mapView, mapboxMap, style);
            symbolManager.setIconAllowOverlap(true);
            symbolManager.setIconIgnorePlacement(true);

            checkLocationPermissionAndGetLocation();
        });
    }

    private void checkLocationPermissionAndGetLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            getCurrentLocation();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation();
            } else {
                Toast.makeText(this, "Cần quyền truy cập vị trí để hiển thị đường đi",
                        Toast.LENGTH_LONG).show();
                // Still try to get destination location
                if (destinationLat != null && destinationLng != null) {
                    handleDestinationWithCoordinates();
                } else if (placeId != null && !placeId.isEmpty()) {
                    fetchDestinationLocation();
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    private void getCurrentLocation() {
        progressBar.setVisibility(View.VISIBLE);

        // Thử lấy vị trí cuối cùng trước
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    if (location != null) {
                        handleLocationReceived(location);
                    } else {
                        // Không có lastLocation, yêu cầu location updates
                        requestLocationUpdates();
                    }
                })
                .addOnFailureListener(e -> {
                    // Lỗi khi lấy lastLocation, fallback sang location updates
                    requestLocationUpdates();
                });
    }
    
    @SuppressLint("MissingPermission")
    private void requestLocationUpdates() {
        LocationRequest locationRequest = new LocationRequest.Builder(
                Priority.PRIORITY_HIGH_ACCURACY, 10000)
                .setWaitForAccurateLocation(false)
                .setMinUpdateIntervalMillis(5000)
                .setMaxUpdateDelayMillis(15000)
                .build();
        
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                android.location.Location location = locationResult.getLastLocation();
                if (location != null) {
                    handleLocationReceived(location);
                    // Dừng location updates sau khi có vị trí
                    stopLocationUpdates();
                }
            }
        };
        
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
    }
    
    private void handleLocationReceived(android.location.Location location) {
        currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
        
        // Add current location marker
        addMarker(currentLatLng, "Vị trí của bạn");
        
        // Get destination location - Ưu tiên latitude/longitude
        if (destinationLat != null && destinationLng != null) {
            handleDestinationWithCoordinates();
        } else if (placeId != null && !placeId.isEmpty()) {
            fetchDestinationLocation();
        } else {
            progressBar.setVisibility(View.GONE);
            moveCameraToLocation(currentLatLng);
        }
    }
    
    private void stopLocationUpdates() {
        if (locationCallback != null) {
            fusedLocationClient.removeLocationUpdates(locationCallback);
        }
    }
    
    private void handleDestinationWithCoordinates() {
        destinationLatLng = new LatLng(destinationLat, destinationLng);
        
        // Add destination marker
        addMarker(destinationLatLng, destinationName);
        
        // Show info card
        infoCard.setVisibility(View.VISIBLE);
        
        // Fetch directions if we have current location
        if (currentLatLng != null) {
            fetchDirections();
        } else {
            progressBar.setVisibility(View.GONE);
            moveCameraToLocation(destinationLatLng);
        }
    }

    private void fetchDestinationLocation() {
        GoongApiService service = RetrofitInstance.getRetrofitInstance(
                getString(R.string.goong_api_url)
        ).create(GoongApiService.class);

        Call<PlaceDetailResponse> call = service.getPlaceDetail(
                placeId,
                getString(R.string.goong_api_key)
        );

        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<PlaceDetailResponse> call,
                                 @NonNull Response<PlaceDetailResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    PlaceDetailResponse data = response.body();
                    if (data.getResult() != null &&
                            data.getResult().getGeometry() != null &&
                            data.getResult().getGeometry().getLocation() != null) {

                        PlaceDetailResponse.Location location =
                                data.getResult().getGeometry().getLocation();

                        try {
                            double lat = Double.parseDouble(location.getLat());
                            double lng = Double.parseDouble(location.getLng());
                            destinationLatLng = new LatLng(lat, lng);

                            // Add destination marker
                            addMarker(destinationLatLng, destinationName);

                            // Show info card
                            infoCard.setVisibility(View.VISIBLE);

                            // Fetch directions if we have current location
                            if (currentLatLng != null) {
                                fetchDirections();
                            } else {
                                progressBar.setVisibility(View.GONE);
                                moveCameraToLocation(destinationLatLng);
                            }
                        } catch (NumberFormatException e) {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(MapActivity.this,
                                    "Lỗi khi phân tích tọa độ",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(MapActivity.this,
                            "Không thể lấy thông tin địa điểm",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<PlaceDetailResponse> call, @NonNull Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(MapActivity.this,
                        "Lỗi kết nối: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchDirections() {
        if (currentLatLng == null || destinationLatLng == null) {
            return;
        }

        String origin = currentLatLng.getLatitude() + "," + currentLatLng.getLongitude();
        String destination = destinationLatLng.getLatitude() + "," + destinationLatLng.getLongitude();

        GoongApiService service = RetrofitInstance.getRetrofitInstance(
                getString(R.string.goong_api_url)
        ).create(GoongApiService.class);

        Call<DirectionResponse> call = service.getDirection(
                origin,
                destination,
                "car",
                getString(R.string.goong_api_key)
        );

        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<DirectionResponse> call,
                                 @NonNull Response<DirectionResponse> response) {
                progressBar.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null) {
                    DirectionResponse data = response.body();
                    if (data.getRoutes() != null && !data.getRoutes().isEmpty()) {
                        DirectionResponse.Route route = data.getRoutes().get(0);

                        // Update distance and duration
                        if (route.getLegs() != null && !route.getLegs().isEmpty()) {
                            DirectionResponse.Leg leg = route.getLegs().get(0);
                            if (leg.getDistance() != null) {
                                tvDistance.setText(leg.getDistance().getText());
                            }
                            if (leg.getDuration() != null) {
                                tvDuration.setText(leg.getDuration().getText());
                            }
                        }

                        // Draw route on map
                        if (route.getOverviewPolyline() != null &&
                                route.getOverviewPolyline().getPoints() != null) {
                            String geometry = route.getOverviewPolyline().getPoints();
                            drawRoute(geometry);
                        }

                        // Fit bounds to show both markers
                        fitBoundsToMarkers();
                    }
                } else {
                    Toast.makeText(MapActivity.this,
                            "Không thể tính toán đường đi",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<DirectionResponse> call, @NonNull Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(MapActivity.this,
                        "Lỗi khi tính đường đi: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addMarker(LatLng latLng, String title) {
        if (mapboxMap == null) return;

        symbolManager.create(new SymbolOptions()
                .withLatLng(latLng)
                .withTextField(title)
                .withTextOffset(new Float[]{0f, -2f}));
    }

    private void drawRoute(String encodedPolyline) {
        if (mapboxMap == null || mapboxMap.getStyle() == null) return;

        try {
            List<Point> points = LineString.fromPolyline(encodedPolyline, 5).coordinates();

            LineString lineString = LineString.fromLngLats(points);
            Feature feature = Feature.fromGeometry(lineString);

            Style style = mapboxMap.getStyle();

            // Remove existing route if any
            if (style.getSource("route-source") != null) {
                style.removeLayer("route-layer");
                style.removeSource("route-source");
            }

            GeoJsonSource geoJsonSource = new GeoJsonSource("route-source", feature);
            style.addSource(geoJsonSource);

            LineLayer lineLayer = new LineLayer("route-layer", "route-source")
                    .withProperties(
                            PropertyFactory.lineCap(Property.LINE_CAP_ROUND),
                            PropertyFactory.lineJoin(Property.LINE_JOIN_ROUND),
                            PropertyFactory.lineOpacity(0.8f),
                            PropertyFactory.lineWidth(6f),
                            PropertyFactory.lineColor(Color.parseColor("#007AFF"))
                    );

            style.addLayer(lineLayer);

        } catch (Exception e) {
            Toast.makeText(this, "Lỗi khi vẽ đường đi: " + e.getMessage(),
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void moveCameraToLocation(LatLng latLng) {
        if (mapboxMap == null) return;

        CameraPosition position = new CameraPosition.Builder()
                .target(latLng)
                .zoom(15)
                .build();

        mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(position), 1000);
    }

    private void fitBoundsToMarkers() {
        if (mapboxMap == null || currentLatLng == null || destinationLatLng == null) return;

        LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
        boundsBuilder.include(currentLatLng);
        boundsBuilder.include(destinationLatLng);

        LatLngBounds bounds = boundsBuilder.build();

        mapboxMap.animateCamera(
                CameraUpdateFactory.newLatLngBounds(bounds, 100),
                1500
        );
    }

    // Mapbox lifecycle methods
    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopLocationUpdates();
        if (symbolManager != null) {
            symbolManager.onDestroy();
        }
        mapView.onDestroy();
    }
}
