package com.example.triply;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        
        setupDestinationClicks();
        setupBottomNavigation();
    }
    
    private void setupDestinationClicks() {
        findViewById(R.id.card_lang_chu_tich).setOnClickListener(v -> {
            Intent intent = new Intent(this, DestinationActivity.class);
            startActivity(intent);
        });
    }
    
    private void setupBottomNavigation() {
        ImageView navHome = findViewById(R.id.nav_home);
        ImageView navPlan = findViewById(R.id.nav_plan);
        ImageView navFavorite = findViewById(R.id.nav_favorite);
        ImageView navProfile = findViewById(R.id.nav_profile);
        
        navHome.setSelected(true);
        navPlan.setSelected(false);
        navFavorite.setSelected(false);
        navProfile.setSelected(false);
        
        navHome.setOnClickListener(v -> {
        });
        
        navPlan.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, ScheduleActivity.class);
            startActivity(intent);
        });
        
        navFavorite.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, FavoriteActivity.class);
            startActivity(intent);
        });
        
        navProfile.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, ProfileActivity.class);
            startActivity(intent);
        });
    }
}