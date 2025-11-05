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
        
        int favoriteCount = allFavorites.size();
        if (allFavorites.containsKey("has_favorites")) {
            favoriteCount--;
        }
        
        if (favoriteCount > 0) {
            statusText.setText("Danh sách địa điểm yêu thích của bạn:");
            
            List<String> favoritesList = new ArrayList<>();
            for (Map.Entry<String, ?> entry : allFavorites.entrySet()) {
                String key = entry.getKey();
                if (!"has_favorites".equals(key)) {
                    favoritesList.add(key);
                }
            }
            
            StringBuilder favoritesText = new StringBuilder();
            for (String favorite : favoritesList) {
                favoritesText.append("❤️ ").append(favorite).append("\n\n");
            }
            
            MaterialTextView favoritesDisplay = new MaterialTextView(this);
            favoritesDisplay.setText(favoritesText.toString());
            favoritesDisplay.setTextSize(16);
            favoritesDisplay.setPadding(20, 20, 20, 20);
            
            recyclerFavorites.setVisibility(View.GONE);
            
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
        
        navHome.setSelected(false);
        navPlan.setSelected(false);
        navFavorite.setSelected(true);
        navProfile.setSelected(false);
        
        navHome.setOnClickListener(v -> {
            Intent intent = new Intent(FavoriteActivity.this, HomeActivity.class);
            startActivity(intent);
        });
        
        navPlan.setOnClickListener(v -> {
            Intent intent = new Intent(FavoriteActivity.this, ScheduleActivity.class);
            startActivity(intent);
        });
        
        navFavorite.setOnClickListener(v -> {
        });
        
        navProfile.setOnClickListener(v -> {
            Intent intent = new Intent(FavoriteActivity.this, ProfileActivity.class);
            startActivity(intent);
        });
    }
}