package com.example.triply;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;

public class DestinationActivity extends AppCompatActivity {

    private ImageView imagePlace;
    private MaterialTextView textTitle, textLocation, textDescription;
    private MaterialButton btnDirections, btnAddToFavorite;
    private boolean isFavorite = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_destination);
        
        initViews();
        loadDestinationData();
        checkFavoriteStatus();
        setupClickListeners();
        setupBottomNavigation();
    }
    
    private void initViews() {
        imagePlace = findViewById(R.id.imagePlace);
        textTitle = findViewById(R.id.textTitle);
        textLocation = findViewById(R.id.textLocation);
        textDescription = findViewById(R.id.textDescription);
        btnDirections = findViewById(R.id.btnDirections);
        btnAddToFavorite = findViewById(R.id.btnAddToFavorite);
    }
    
    private void loadDestinationData() {
        Intent intent = getIntent();
        String destinationName = intent.getStringExtra("destination_name");
        String destinationAddress = intent.getStringExtra("destination_address");
        String destinationDescription = intent.getStringExtra("destination_description");
        String imgPath = intent.getStringExtra("destination_img_path");
        final String googleMapUrl = intent.getStringExtra("destination_google_map_url");
        
        if (destinationName != null) {
            textTitle.setText(destinationName);
        } else {
            textTitle.setText("Lăng Chủ Tịch Hồ Chí Minh");
        }
        
        if (destinationAddress != null) {
            textLocation.setText(destinationAddress);
        } else {
            textLocation.setText("Ba Đình, Hà Nội");
        }
        
        if (destinationDescription != null) {
            textDescription.setText(destinationDescription);
        } else {
            textDescription.setText("Lăng Chủ tịch Hồ Chí Minh là công trình trang nghiêm, linh thiêng, nơi yên nghỉ của Chủ tịch Hồ Chí Minh - vị lãnh tụ kính yêu của dân tộc Việt Nam. Đây là điểm đến không thể bỏ qua khi du lịch Hà Nội.");
        }
        
        if (imgPath != null && !imgPath.isEmpty()) {
            Glide.with(this)
                .load(imgPath)
                .placeholder(R.drawable.langbac)
                .error(R.drawable.langbac)
                .into(imagePlace);
        } else {
            imagePlace.setImageResource(R.drawable.langbac);
        }
        
        if (googleMapUrl != null && !googleMapUrl.isEmpty()) {
            btnDirections.setOnClickListener(v -> {
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(googleMapUrl));
                if (mapIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(mapIntent);
                } else {
                    Toast.makeText(this, "Không thể mở Google Maps", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
    
    private void checkFavoriteStatus() {
        SharedPreferences prefs = getSharedPreferences("Favorites", MODE_PRIVATE);
        String destinationTitle = textTitle.getText().toString();
        
        for (String key : prefs.getAll().keySet()) {
            if (key.endsWith("_title")) {
                String savedTitle = prefs.getString(key, "");
                if (destinationTitle.equals(savedTitle)) {
                    isFavorite = true;
                    break;
                }
            }
        }
        
        updateFavoriteButton();
    }
    
    private void updateFavoriteButton() {
        if (isFavorite) {
            btnAddToFavorite.setText("❤️");
        } else {
            btnAddToFavorite.setText("🤍");
        }
    }
    
    private void setupClickListeners() {
        btnAddToFavorite.setOnClickListener(v -> {
            toggleFavorite();
        });
    }
    
    private void toggleFavorite() {
        SharedPreferences prefs = getSharedPreferences("Favorites", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        
        if (isFavorite) {
            removeFavorite(editor);
            isFavorite = false;
            Toast.makeText(this, "Đã bỏ " + textTitle.getText().toString() + " khỏi danh sách yêu thích!", 
                    Toast.LENGTH_SHORT).show();
        } else {
            addToFavorites(editor);
            isFavorite = true;
            Toast.makeText(this, "Đã thêm " + textTitle.getText().toString() + " vào danh sách yêu thích!", 
                    Toast.LENGTH_SHORT).show();
        }
        
        updateFavoriteButton();
    }
    
    private void addToFavorites(SharedPreferences.Editor editor) {
        String favoriteKey = "favorite_" + System.currentTimeMillis();
        editor.putString(favoriteKey + "_title", textTitle.getText().toString());
        editor.putString(favoriteKey + "_location", textLocation.getText().toString());
        editor.putString(favoriteKey + "_description", textDescription.getText().toString());
        editor.putBoolean("has_favorites", true);
        editor.apply();
    }
    
    private void removeFavorite(SharedPreferences.Editor editor) {
        SharedPreferences prefs = getSharedPreferences("Favorites", MODE_PRIVATE);
        String destinationTitle = textTitle.getText().toString();
        
        for (String key : prefs.getAll().keySet()) {
            if (key.endsWith("_title")) {
                String savedTitle = prefs.getString(key, "");
                if (destinationTitle.equals(savedTitle)) {
                    String baseKey = key.replace("_title", "");
                    editor.remove(baseKey + "_title");
                    editor.remove(baseKey + "_location");
                    editor.remove(baseKey + "_description");
                    break;
                }
            }
        }
        
        boolean stillHasFavorites = false;
        for (String key : prefs.getAll().keySet()) {
            if (key.endsWith("_title") && !key.contains(textTitle.getText().toString())) {
                stillHasFavorites = true;
                break;
            }
        }
        editor.putBoolean("has_favorites", stillHasFavorites);
        editor.apply();
    }
    
    private void addToFavorite() {
        SharedPreferences prefs = getSharedPreferences("Favorites", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        String favoriteKey = "favorite_" + System.currentTimeMillis();
        editor.putString(favoriteKey + "_title", textTitle.getText().toString());
        editor.putString(favoriteKey + "_location", textLocation.getText().toString());
        editor.putString(favoriteKey + "_description", textDescription.getText().toString());
        editor.putBoolean("has_favorites", true);
        editor.apply();
        Toast.makeText(this, "Đã thêm " + textTitle.getText().toString() + " vào danh sách yêu thích!",
                Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, FavoriteActivity.class);
        startActivity(intent);
    }
    
    private void addToSchedule() {
        Toast.makeText(this, "Đã thêm " + textTitle.getText().toString() + " vào lịch trình!",
                Toast.LENGTH_SHORT).show();
        
        SharedPreferences prefs = getSharedPreferences("TripSchedule", MODE_PRIVATE);
        boolean hasSchedule = prefs.getBoolean("has_schedule", false);
        
        if (hasSchedule) {
            Intent intent = new Intent(this, PlanActivity.class);
            intent.putExtra("departure", prefs.getString("departure", ""));
            intent.putExtra("direction", prefs.getString("direction", ""));
            intent.putExtra("numbers_person", prefs.getString("numbers_person", ""));
            intent.putExtra("budget", prefs.getString("budget", ""));
            intent.putExtra("durations", prefs.getString("durations", ""));
            intent.putExtra("start_date", prefs.getString("start_date", ""));
            intent.putExtra("added_destination", textTitle.getText().toString());
            startActivity(intent);
        } else {
            Intent intent = new Intent(this, ScheduleActivity.class);
            intent.putExtra("added_destination", textTitle.getText().toString());
            startActivity(intent);
        }
    }
    
    private void setupBottomNavigation() {
        ImageView navHome = findViewById(R.id.nav_home);
        ImageView navPlan = findViewById(R.id.nav_plan);
        ImageView navFavorite = findViewById(R.id.nav_favorite);
        ImageView navProfile = findViewById(R.id.nav_profile);
        
        navHome.setSelected(false);
        navPlan.setSelected(false);
        navFavorite.setSelected(false);
        navProfile.setSelected(false);
        
        navHome.setOnClickListener(v -> {
            Intent intent = new Intent(DestinationActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
        });
        
        navPlan.setOnClickListener(v -> {
            Intent intent = new Intent(DestinationActivity.this, ScheduleActivity.class);
            startActivity(intent);
            finish();
        });
        
        navFavorite.setOnClickListener(v -> {
            Intent intent = new Intent(DestinationActivity.this, FavoriteActivity.class);
            startActivity(intent);
            finish();
        });
        
        navProfile.setOnClickListener(v -> {
            Intent intent = new Intent(DestinationActivity.this, ProfileActivity.class);
            startActivity(intent);
            finish();
        });
    }
    public static void launch(AppCompatActivity context, String title, String location,
                             String description, int imageResId) {
        Intent intent = new Intent(context, DestinationActivity.class);
        intent.putExtra("destination_title", title);
        intent.putExtra("destination_location", location);
        intent.putExtra("destination_description", description);
        intent.putExtra("destination_image", imageResId);
        context.startActivity(intent);
    }
}