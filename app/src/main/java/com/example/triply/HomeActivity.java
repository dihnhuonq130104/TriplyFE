package com.example.triply;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.example.triply.data.remote.model.City;
import com.example.triply.data.remote.model.Destination;
import com.example.triply.data.repository.DestinationRepository;
import com.example.triply.util.TokenManager;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class HomeActivity extends AppCompatActivity {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
    
    private static Map<Integer, List<Destination>> cachedCitiesData = null;
    private ProgressBar progressBar;
    private LinearLayout citiesContainer;
    private int loadedCitiesCount = 0;
    private int totalCitiesCount = 0;
    
    private TextView tvUserName;
    private TextView tvLocation;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    private TokenManager tokenManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        
        tokenManager = new TokenManager(this);
        initViews();
        loadUserInfo();
        initLocationServices();
        
        if (cachedCitiesData == null) {
            loadCitiesAndDestinations();
        } else {
            displayCachedData();
        }
        
        setupBottomNavigation();
    }
    
    private void initViews() {
        progressBar = findViewById(R.id.progress_bar);
        citiesContainer = findViewById(R.id.cities_container);
        tvUserName = findViewById(R.id.tvUserName);
        tvLocation = findViewById(R.id.tvLocation);
    }
    
    private void loadUserInfo() {
        String fullName = tokenManager.getFullName();
        String userName = tokenManager.getUserName();
        String email = tokenManager.getUserEmail();
        if (fullName != null && !fullName.isEmpty()) {
            tvUserName.setText(fullName);
        } else if (userName != null && !userName.isEmpty()) {
            tvUserName.setText(userName);
        } else if (email != null && !email.isEmpty()) {
            tvUserName.setText(email);
        } else {
            tvUserName.setText("Người dùng");
        }
    }
    
    private void initLocationServices() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        checkAndRequestLocationPermission();
    }
    
    private void checkAndRequestLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Yêu cầu quyền
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, 
                               Manifest.permission.ACCESS_COARSE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            // Đã có quyền, lấy vị trí
            getCurrentLocation();
        }
    }
    
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                          @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Quyền được cấp, lấy vị trí
                getCurrentLocation();
            } else {
                // Quyền bị từ chối, hiển thị thông báo và dùng vị trí mặc định
                tvLocation.setText("Việt Nam");
                Toast.makeText(this, "Không có quyền truy cập vị trí. Vui lòng cấp quyền trong Cài đặt.", 
                             Toast.LENGTH_SHORT).show();
            }
        }
    }
    
    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) 
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        
        // Thử lấy vị trí cuối cùng trước
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            // Có vị trí, thực hiện reverse geocoding
                            reverseGeocode(location.getLatitude(), location.getLongitude());
                        } else {
                            // Không có lastLocation, yêu cầu location updates
                            requestLocationUpdates();
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    // Lỗi khi lấy lastLocation, fallback sang location updates
                    requestLocationUpdates();
                });
    }
    
    private void requestLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) 
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        
        LocationRequest locationRequest = new LocationRequest.Builder(
                Priority.PRIORITY_HIGH_ACCURACY, 10000)
                .setWaitForAccurateLocation(false)
                .setMinUpdateIntervalMillis(5000)
                .setMaxUpdateDelayMillis(15000)
                .build();
        
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                Location location = locationResult.getLastLocation();
                if (location != null) {
                    // Lấy được vị trí, thực hiện reverse geocoding
                    reverseGeocode(location.getLatitude(), location.getLongitude());
                    // Dừng location updates sau khi có vị trí
                    stopLocationUpdates();
                }
            }
        };
        
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
    }
    
    private void stopLocationUpdates() {
        if (locationCallback != null) {
            fusedLocationClient.removeLocationUpdates(locationCallback);
        }
    }
    
    private void reverseGeocode(double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            
            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                
                // Ưu tiên lấy tên thành phố
                String cityName = address.getLocality(); // Tên thành phố
                if (cityName == null || cityName.isEmpty()) {
                    cityName = address.getAdminArea(); // Tên tỉnh/bang
                }
                if (cityName == null || cityName.isEmpty()) {
                    cityName = address.getSubAdminArea(); // Tên quận/huyện
                }
                if (cityName == null || cityName.isEmpty()) {
                    cityName = address.getCountryName(); // Tên quốc gia
                }
                
                if (cityName != null && !cityName.isEmpty()) {
                    tvLocation.setText(cityName);
                } else {
                    tvLocation.setText("Việt Nam");
                }
            } else {
                tvLocation.setText("Việt Nam");
            }
        } catch (IOException e) {
            e.printStackTrace();
            tvLocation.setText("Việt Nam");
            Toast.makeText(this, "Không thể xác định vị trí", Toast.LENGTH_SHORT).show();
        }
    }
    
    private void loadCitiesAndDestinations() {
        progressBar.setVisibility(View.VISIBLE);
        cachedCitiesData = new HashMap<>();
        
        new DestinationRepository().getCities(new DestinationRepository.CitiesCallback() {
            @Override
            public void onSuccess(List<City> cities) {
                if (cities != null && !cities.isEmpty()) {
                    totalCitiesCount = cities.size();
                    for (City city : cities) {
                        loadDestinationsForCity(city);
                    }
                } else {
                    runOnUiThread(() -> {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(HomeActivity.this, "Không tìm thấy thành phố nào", Toast.LENGTH_SHORT).show();
                    });
                }
            }

            @Override
            public void onError(String message) {
                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(HomeActivity.this, message, Toast.LENGTH_SHORT).show();
                });
            }
        });
    }
    
    private void loadDestinationsForCity(City city) {
        new DestinationRepository().getDestinationsByCity(city.getCityId(), new DestinationRepository.DestinationsCallback() {
            @Override
            public void onSuccess(List<Destination> destinations) {
                runOnUiThread(() -> {
                    cachedCitiesData.put(city.getCityId(), destinations);
                    displayCitySection(city, destinations);
                    loadedCitiesCount++;
                    
                    if (loadedCitiesCount >= totalCitiesCount) {
                        progressBar.setVisibility(View.GONE);
                    }
                });
            }

            @Override
            public void onError(String message) {
                runOnUiThread(() -> {
                    loadedCitiesCount++;
                    if (loadedCitiesCount >= totalCitiesCount) {
                        progressBar.setVisibility(View.GONE);
                    }
                });
            }
        });
    }
    
    private void displayCachedData() {
        if (cachedCitiesData == null || cachedCitiesData.isEmpty()) {
            return;
        }
        
        citiesContainer.removeAllViews();
        
        new DestinationRepository().getCities(new DestinationRepository.CitiesCallback() {
            @Override
            public void onSuccess(List<City> cities) {
                runOnUiThread(() -> {
                    for (City city : cities) {
                        List<Destination> destinations = cachedCitiesData.get(city.getCityId());
                        if (destinations != null) {
                            displayCitySection(city, destinations);
                        }
                    }
                });
            }

            @Override
            public void onError(String message) {
            }
        });
    }
    
    private void displayCitySection(City city, List<Destination> destinations) {
        TextView cityHeader = new TextView(this);
        cityHeader.setText(city.getName());
        cityHeader.setTextSize(20);
        cityHeader.setTypeface(null, Typeface.BOLD);
        cityHeader.setTextColor(getResources().getColor(android.R.color.black));
        LinearLayout.LayoutParams headerParams = new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        );
        headerParams.setMargins(0, dpToPx(16), 0, dpToPx(8));
        cityHeader.setLayoutParams(headerParams);
        citiesContainer.addView(cityHeader);
        
        if (destinations == null || destinations.isEmpty()) {
            TextView emptyText = new TextView(this);
            emptyText.setText("Chưa có địa điểm nào");
            emptyText.setTextSize(14);
            emptyText.setTextColor(getResources().getColor(android.R.color.darker_gray));
            emptyText.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ));
            citiesContainer.addView(emptyText);
            return;
        }
        
        LinearLayout cardsRow = new LinearLayout(this);
        cardsRow.setOrientation(LinearLayout.HORIZONTAL);
        cardsRow.setLayoutParams(new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            dpToPx(200)
        ));
        
        int count = Math.min(2, destinations.size());
        for (int i = 0; i < count; i++) {
            Destination destination = destinations.get(i);
            CardView card = createDestinationCard(destination);
            cardsRow.addView(card);
        }
        
        citiesContainer.addView(cardsRow);
    }
    
    private CardView createDestinationCard(Destination destination) {
        CardView card = new CardView(this);
        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
            0,
            ViewGroup.LayoutParams.MATCH_PARENT,
            1.0f
        );
        cardParams.setMargins(dpToPx(8), 0, dpToPx(8), 0);
        card.setLayoutParams(cardParams);
        card.setRadius(dpToPx(8));
        card.setCardElevation(dpToPx(3));
        card.setClickable(true);
        card.setFocusable(true);
        card.setOnClickListener(v -> openDestinationDetail(destination));
        
        LinearLayout cardContent = new LinearLayout(this);
        cardContent.setOrientation(LinearLayout.VERTICAL);
        cardContent.setLayoutParams(new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        ));
        
        ImageView imageView = new ImageView(this);
        imageView.setLayoutParams(new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            0,
            1.0f
        ));
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        
        if (destination.getImgPath() != null && !destination.getImgPath().isEmpty()) {
            Glide.with(this)
                .load(destination.getImgPath())
                .placeholder(R.drawable.langbac)
                .error(R.drawable.langbac)
                .into(imageView);
        } else {
            imageView.setImageResource(R.drawable.langbac);
        }
        
        TextView nameText = new TextView(this);
        nameText.setText(destination.getName());
        nameText.setTextSize(14);
        nameText.setGravity(Gravity.CENTER);
        nameText.setPadding(dpToPx(8), dpToPx(8), dpToPx(8), dpToPx(8));
        nameText.setMaxLines(2);
        nameText.setLayoutParams(new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        ));
        
        cardContent.addView(imageView);
        cardContent.addView(nameText);
        card.addView(cardContent);
        
        return card;
    }
    
    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }
    
    private void openDestinationDetail(Destination destination) {
        Intent intent = new Intent(this, DestinationActivity.class);
        intent.putExtra("destination_id", destination.getDestinationId());
        intent.putExtra("destination_name", destination.getName());
        intent.putExtra("destination_address", destination.getAddress());
        intent.putExtra("destination_description", destination.getDescription());
        intent.putExtra("destination_img_path", destination.getImgPath());
        intent.putExtra("destination_google_map_url", destination.getGoogleMapUrl());
        intent.putExtra("place_id", destination.getPlaceId());
        if (destination.getLatitude() != null) {
            intent.putExtra("latitude", destination.getLatitude());
        }
        if (destination.getLongitude() != null) {
            intent.putExtra("longitude", destination.getLongitude());
        }
        startActivity(intent);
    }
    
    private void setupBottomNavigation() {
        ImageView navHome = findViewById(R.id.nav_home);
        ImageView navPlan = findViewById(R.id.nav_plan);
        ImageView navChatbot = findViewById(R.id.nav_chatbot);
        ImageView navProfile = findViewById(R.id.nav_profile);

        navHome.setSelected(true);
        navPlan.setSelected(false);
        navChatbot.setSelected(false);
        navProfile.setSelected(false);

        navHome.setOnClickListener(v -> {
        });

        navPlan.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, ScheduleActivity.class);
            startActivity(intent);
        });

        navChatbot.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, ChatbotActivity.class);
            startActivity(intent);
        });

        navProfile.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, ProfileActivity.class);
            startActivity(intent);
        });
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopLocationUpdates();
    }
}