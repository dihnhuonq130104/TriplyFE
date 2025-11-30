package com.example.triply;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.triply.util.TokenManager;

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
            Intent intent = new Intent(ProfileActivity.this, SavedPlansActivity.class);
            startActivity(intent);
        });
        
        menuSettings.setOnClickListener(v -> {
            Toast.makeText(this, "Settings clicked", Toast.LENGTH_SHORT).show();
        });
        
        menuLogout.setOnClickListener(v -> {
            TokenManager tokenManager = new TokenManager(this);
            tokenManager.clear();
            
            Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }
    
    private void setupBottomNavigation() {
        ImageView navHome = findViewById(R.id.nav_home);
        ImageView navPlan = findViewById(R.id.nav_plan);
        ImageView navChatbot = findViewById(R.id.nav_chatbot);
        ImageView navProfile = findViewById(R.id.nav_profile);

        navHome.setSelected(false);
        navPlan.setSelected(false);
        navChatbot.setSelected(false);
        navProfile.setSelected(true);

        navHome.setOnClickListener(v -> {
            finish();
        });

        navPlan.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, ScheduleActivity.class);
            startActivity(intent);
        });

        navChatbot.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, ChatbotActivity.class);
            startActivity(intent);
        });

        navProfile.setOnClickListener(v -> {
        });
    }
}