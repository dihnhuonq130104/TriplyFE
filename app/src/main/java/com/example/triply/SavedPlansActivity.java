package com.example.triply;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.android.material.textview.MaterialTextView;

public class SavedPlansActivity extends AppCompatActivity {

    private LinearLayout plansContainer;
    private MaterialTextView tvNoPlans;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_plans);
        
        initViews();
        setupBackButton();
        loadSavedPlans();
        setupBottomNavigation();
    }
    
    private void initViews() {
        plansContainer = findViewById(R.id.plans_container);
        tvNoPlans = findViewById(R.id.tv_no_plans);
    }
    
    private void setupBackButton() {
        ImageView btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(v -> finish());
    }
    
    private void loadSavedPlans() {
        SharedPreferences prefs = getSharedPreferences("TripSchedule", MODE_PRIVATE);
        boolean hasSchedule = prefs.getBoolean("has_schedule", false);
        
        if (hasSchedule) {
            // Hiển thị kế hoạch đã lưu
            tvNoPlans.setVisibility(android.view.View.GONE);
            plansContainer.setVisibility(android.view.View.VISIBLE);
            
            // Tạo card cho kế hoạch đã lưu
            createPlanCard(
                prefs.getString("departure", "Không xác định"),
                prefs.getString("direction", "Không xác định"),
                prefs.getString("start_date", "Không xác định"),
                prefs.getString("durations", "Không xác định"),
                prefs.getString("numbers_person", "1")
            );
        } else {
            // Không có kế hoạch nào được lưu
            tvNoPlans.setVisibility(android.view.View.VISIBLE);
            plansContainer.setVisibility(android.view.View.GONE);
        }
    }
    
    private void createPlanCard(String departure, String destination, String startDate, String duration, String people) {
        // Tạo CardView cho mỗi kế hoạch
        CardView cardView = new CardView(this);
        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        );
        cardParams.setMargins(0, 0, 0, 32);
        cardView.setLayoutParams(cardParams);
        cardView.setCardElevation(8);
        cardView.setRadius(12);
        cardView.setUseCompatPadding(true);
        
        // Tạo LinearLayout bên trong CardView
        LinearLayout innerLayout = new LinearLayout(this);
        innerLayout.setOrientation(LinearLayout.VERTICAL);
        innerLayout.setPadding(24, 24, 24, 24);
        
        // Tiêu đề chuyến đi
        MaterialTextView tvTitle = new MaterialTextView(this);
        tvTitle.setText("Chuyến đi " + departure + " - " + destination);
        tvTitle.setTextSize(18);
        tvTitle.setTextColor(getResources().getColor(android.R.color.black));
        tvTitle.setTypeface(null, android.graphics.Typeface.BOLD);
        
        // Thông tin chi tiết
        MaterialTextView tvInfo = new MaterialTextView(this);
        tvInfo.setText("📅 " + startDate + "\n⏰ " + duration + "\n👥 " + people + " người");
        tvInfo.setTextSize(14);
        tvInfo.setTextColor(getResources().getColor(android.R.color.darker_gray));
        tvInfo.setPadding(0, 16, 0, 0);
        
        innerLayout.addView(tvTitle);
        innerLayout.addView(tvInfo);
        cardView.addView(innerLayout);
        
        // Set click listener để mở kế hoạch chi tiết đã hoàn thành
        cardView.setOnClickListener(v -> {
            SharedPreferences prefs = getSharedPreferences("TripSchedule", MODE_PRIVATE);
            Intent intent = new Intent(SavedPlansActivity.this, PlanDetailActivity.class);
            intent.putExtra("departure", prefs.getString("departure", ""));
            intent.putExtra("direction", prefs.getString("direction", ""));
            intent.putExtra("numbers_person", prefs.getString("numbers_person", ""));
            intent.putExtra("budget", prefs.getString("budget", ""));
            intent.putExtra("durations", prefs.getString("durations", ""));
            intent.putExtra("start_date", prefs.getString("start_date", ""));
            intent.putExtra("hotel_type", prefs.getString("hotel_type", ""));
            // Thêm các thông tin cần thiết cho PlanDetailActivity
            intent.putExtra("people_count", Integer.parseInt(prefs.getString("numbers_person", "1")));
            intent.putExtra("flight_airline", "Vietnam Airlines");
            intent.putExtra("hotel_name", "Pullman Hanoi Hotel");
            intent.putExtra("total_cost", 15000000L);
            startActivity(intent);
        });
        
        plansContainer.addView(cardView);
    }
    
    private void setupBottomNavigation() {
        ImageView navHome = findViewById(R.id.nav_home);
        ImageView navPlan = findViewById(R.id.nav_plan);
        ImageView navFavorite = findViewById(R.id.nav_favorite);
        ImageView navProfile = findViewById(R.id.nav_profile);
        
        // Set no icon as selected since this is a separate screen
        navHome.setSelected(false);
        navPlan.setSelected(false);
        navFavorite.setSelected(false);
        navProfile.setSelected(false);
        
        // Setup click listeners for navigation
        navHome.setOnClickListener(v -> {
            // Sử dụng FLAG_ACTIVITY_CLEAR_TOP để quay về HomeActivity mà không tạo mới
            Intent intent = new Intent(SavedPlansActivity.this, HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });
        
        navPlan.setOnClickListener(v -> {
            Intent intent = new Intent(SavedPlansActivity.this, ScheduleActivity.class);
            startActivity(intent);
            finish(); // Finish SavedPlansActivity vì không cần quay lại
        });
        
        navFavorite.setOnClickListener(v -> {
            Intent intent = new Intent(SavedPlansActivity.this, FavoriteActivity.class);
            startActivity(intent);
            finish(); // Finish SavedPlansActivity vì không cần quay lại
        });
        
        navProfile.setOnClickListener(v -> {
            // Quay về ProfileActivity
            finish(); // Chỉ finish SavedPlansActivity để quay về ProfileActivity
        });
    }
}