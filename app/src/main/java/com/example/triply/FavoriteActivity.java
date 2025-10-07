package com.example.triply;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FavoriteActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);
        
        loadFavoritesList();
        setupBottomNavigation();
    }
    
    private void loadFavoritesList() {
        SharedPreferences prefs = getSharedPreferences("Favorites", MODE_PRIVATE);
        Map<String, ?> allFavorites = prefs.getAll();
        
        MaterialTextView statusText = findViewById(R.id.favorite_status);
        RecyclerView recyclerFavorites = findViewById(R.id.recycler_favorites);
        
        // Remove "has_favorites" key from count since it's just a flag
        int favoriteCount = allFavorites.size();
        if (allFavorites.containsKey("has_favorites")) {
            favoriteCount--;
        }
        
        if (favoriteCount > 0) {
            statusText.setText("Danh sách địa điểm yêu thích của bạn:");
            
            // Show the favorites list
            List<String> favoritesList = new ArrayList<>();
            for (Map.Entry<String, ?> entry : allFavorites.entrySet()) {
                String key = entry.getKey();
                // Skip the flag key
                if (!"has_favorites".equals(key)) {
                    favoritesList.add(key);
                }
            }
            
            // For now, just display text representation of favorites
            StringBuilder favoritesText = new StringBuilder();
            for (String favorite : favoritesList) {
                favoritesText.append("❤️ ").append(favorite).append("\n\n");
            }
            
            // Create a simple text display
            MaterialTextView favoritesDisplay = new MaterialTextView(this);
            favoritesDisplay.setText(favoritesText.toString());
            favoritesDisplay.setTextSize(16);
            favoritesDisplay.setPadding(20, 20, 20, 20);
            
            // Hide the default empty message and show favorites
            recyclerFavorites.setVisibility(View.GONE);
            
            // Add the favorites display to the layout
            LinearLayout contentLayout = (LinearLayout) statusText.getParent();
            contentLayout.addView(favoritesDisplay, contentLayout.indexOfChild(statusText) + 1);
            
        } else {
            statusText.setText("Chưa có địa điểm yêu thích nào.\nHãy thêm địa điểm từ trang chi tiết!");
            recyclerFavorites.setVisibility(View.GONE);
        }
    }
    
    private void setupBottomNavigation() {
        ImageView navHome = findViewById(R.id.nav_home);
        ImageView navPlan = findViewById(R.id.nav_plan);
        ImageView navFavorite = findViewById(R.id.nav_favorite);
        ImageView navProfile = findViewById(R.id.nav_profile);
        
        // Set favorite icon as selected (black) since we're on favorite activity
        navHome.setSelected(false);
        navPlan.setSelected(false);
        navFavorite.setSelected(true);
        navProfile.setSelected(false);
        
        // Setup click listeners for navigation
        navHome.setOnClickListener(v -> {
            Intent intent = new Intent(FavoriteActivity.this, HomeActivity.class);
            startActivity(intent);
            // Không gọi finish() để có thể quay lại FavoriteActivity
        });
        
        navPlan.setOnClickListener(v -> {
            // Luôn mở ScheduleActivity để nhập thông tin chuyến đi mới (nhất quán với HomeActivity)
            Intent intent = new Intent(FavoriteActivity.this, ScheduleActivity.class);
            startActivity(intent);
            // Không gọi finish() để có thể quay lại FavoriteActivity
        });
        
        navFavorite.setOnClickListener(v -> {
            // Current screen - do nothing
        });
        
        navProfile.setOnClickListener(v -> {
            Intent intent = new Intent(FavoriteActivity.this, ProfileActivity.class);
            startActivity(intent);
            // Không gọi finish() để có thể quay lại FavoriteActivity
        });
    }
}