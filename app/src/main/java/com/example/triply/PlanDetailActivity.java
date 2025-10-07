package com.example.triply;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

import java.text.DecimalFormat;

public class PlanDetailActivity extends AppCompatActivity {

    private TextView tvDestination, tvDuration, tvPeopleCount;
    private MaterialButton btnSavePlan, btnBookNow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan_detail);
        
        initViews();
        loadDataFromIntent();
        setupListeners();
    }
    
    private void initViews() {
        tvDestination = findViewById(R.id.tv_destination);
        tvDuration = findViewById(R.id.tv_duration);
        tvPeopleCount = findViewById(R.id.tv_people_count);
        
        btnSavePlan = findViewById(R.id.btn_save_plan);
        btnBookNow = findViewById(R.id.btn_book_now);
    }
    
    private void loadDataFromIntent() {
        Intent intent = getIntent();
        if (intent != null) {
            // Set basic trip info
            tvDestination.setText("Hà Nội");
            tvDuration.setText("3 ngày 2 đêm");
            
            int peopleCount = intent.getIntExtra("people_count", 2);
            tvPeopleCount.setText(peopleCount + " người");
            
            // Display other data passed from PlanActivity
            String flightAirline = intent.getStringExtra("flight_airline");
            String hotelName = intent.getStringExtra("hotel_name");
            String[] selectedAttractions = intent.getStringArrayExtra("selected_attractions");
            long totalCost = intent.getLongExtra("total_cost", 0);
            
            // You can use this data to populate the detailed plan
            // For now, the layout has static content that matches typical selections
        }
    }
    
    private void setupListeners() {
        // Back button
        ImageView btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(v -> finish());
        
        // Save plan button
        btnSavePlan.setOnClickListener(v -> {
            Toast.makeText(this, "Kế hoạch đã được lưu thành công!", Toast.LENGTH_LONG).show();
            
            // Navigate back to home
            Intent intent = new Intent(PlanDetailActivity.this, HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        });
        
        // Book now button
        btnBookNow.setOnClickListener(v -> {
            Toast.makeText(this, "Chức năng đặt vé đang được phát triển...", Toast.LENGTH_LONG).show();
            // Here you would integrate with booking APIs
        });
    }
}