package com.example.triply;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        
        setupMenuClicks();
        setupBottomNavigation();
    }
    
    private void setupMenuClicks() {
        LinearLayout menuPersonalInfo = findViewById(R.id.menu_personal_info);
        LinearLayout menuSavedPlans = findViewById(R.id.menu_saved_plans);
        LinearLayout menuSettings = findViewById(R.id.menu_settings);
        LinearLayout menuLogout = findViewById(R.id.menu_logout);
        
        menuPersonalInfo.setOnClickListener(v -> {
            Toast.makeText(this, "Personal Information clicked", Toast.LENGTH_SHORT).show();
        });
        
        menuSavedPlans.setOnClickListener(v -> {
            // Mở SavedPlansActivity để hiển thị danh sách kế hoạch đã lưu
            Intent intent = new Intent(ProfileActivity.this, SavedPlansActivity.class);
            startActivity(intent);
        });
        
        menuSettings.setOnClickListener(v -> {
            Toast.makeText(this, "Settings clicked", Toast.LENGTH_SHORT).show();
        });
        
        menuLogout.setOnClickListener(v -> {
            // Navigate to LoginActivity
            Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }
    
    private void setupBottomNavigation() {
        ImageView navHome = findViewById(R.id.nav_home);
        ImageView navPlan = findViewById(R.id.nav_plan);
        ImageView navFavorite = findViewById(R.id.nav_favorite);
        ImageView navProfile = findViewById(R.id.nav_profile);
        
        // Set profile icon as selected (black) since we're on profile activity
        navHome.setSelected(false);
        navPlan.setSelected(false);
        navFavorite.setSelected(false);
        navProfile.setSelected(true);
        
        // Setup click listeners for navigation
        navHome.setOnClickListener(v -> {
            // Quay về HomeActivity mà không tạo mới
            finish(); // Chỉ finish ProfileActivity để quay về HomeActivity
        });
        
        navPlan.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, ScheduleActivity.class);
            startActivity(intent);
            // Không gọi finish() để có thể quay lại ProfileActivity
        });
        
        navFavorite.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, FavoriteActivity.class);
            startActivity(intent);
            // Không gọi finish() để có thể quay lại ProfileActivity
        });
        
        navProfile.setOnClickListener(v -> {
            // Current screen - do nothing
        });
    }
}