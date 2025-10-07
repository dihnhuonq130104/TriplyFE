package com.example.triply;

import android.content.Intent;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

import java.text.DecimalFormat;

public class PlanActivity extends AppCompatActivity {

    private TextView tvDestination, tvDuration, tvPeopleCount;
    private TextView tvFlightStatus, tvHotelStatus, tvAttractionsStatus;
    private TextView tvFlightCost, tvHotelCost, tvAttractionsCost, tvTotalCost;
    
    private RadioGroup rgFlightOptions, rgHotelOptions;
    private MaterialButton btnPreview, btnCreatePlan;
    
    // Cost tracking
    private long flightCost = 0;
    private long hotelCost = 0;
    private long attractionsCost = 0;
    private int selectedAttractionsCount = 0;
    private int peopleCount = 2;
    private int nightCount = 2;
    
    // CheckBoxes for attractions
    private CheckBox cbHoChiMinhMausoleum, cbTempleLiterature, cbHoanKiemLake, 
                     cbOldQuarter, cbOnePillarPagoda, cbHanoiOperaHouse,
                     cbBaDinhSquare, cbWestLake, cbHanoiMuseum, cbLongBienBridge;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan);
        
        initViews();
        loadDataFromIntent();
        setupListeners();
    }
    
    private void initViews() {
        // Header info
        tvDestination = findViewById(R.id.tv_destination);
        tvDuration = findViewById(R.id.tv_duration);
        tvPeopleCount = findViewById(R.id.tv_people_count);
        
        // Status indicators
        tvFlightStatus = findViewById(R.id.tv_flight_status);
        tvHotelStatus = findViewById(R.id.tv_hotel_status);
        tvAttractionsStatus = findViewById(R.id.tv_attractions_status);
        
        // Cost displays
        tvFlightCost = findViewById(R.id.tv_flight_cost);
        tvHotelCost = findViewById(R.id.tv_hotel_cost);
        tvAttractionsCost = findViewById(R.id.tv_attractions_cost);
        tvTotalCost = findViewById(R.id.tv_total_cost);
        
        // Radio groups
        rgFlightOptions = findViewById(R.id.rg_flight_options);
        rgHotelOptions = findViewById(R.id.rg_hotel_options);
        
        // Buttons
        btnPreview = findViewById(R.id.btn_preview);
        btnCreatePlan = findViewById(R.id.btn_create_plan);
        
        // Attraction checkboxes
        cbHoChiMinhMausoleum = findViewById(R.id.cb_ho_chi_minh_mausoleum);
        cbTempleLiterature = findViewById(R.id.cb_temple_literature);
        cbHoanKiemLake = findViewById(R.id.cb_hoan_kiem_lake);
        cbOldQuarter = findViewById(R.id.cb_old_quarter);
        cbOnePillarPagoda = findViewById(R.id.cb_one_pillar_pagoda);
        cbHanoiOperaHouse = findViewById(R.id.cb_hanoi_opera_house);
        cbBaDinhSquare = findViewById(R.id.cb_ba_dinh_square);
        cbWestLake = findViewById(R.id.cb_west_lake);
        cbHanoiMuseum = findViewById(R.id.cb_hanoi_museum);
        cbLongBienBridge = findViewById(R.id.cb_long_bien_bridge);
    }
    
    private void loadDataFromIntent() {
        Intent intent = getIntent();
        if (intent != null) {
            String direction = intent.getStringExtra("direction");
            String durations = intent.getStringExtra("durations");
            String numbersPerson = intent.getStringExtra("numbers_person");
            
            if (direction != null) {
                tvDestination.setText(direction);
            }
            if (durations != null) {
                tvDuration.setText(durations);
                // Extract night count for hotel calculation
                if (durations.contains("2 đêm")) {
                    nightCount = 2;
                } else if (durations.contains("3 đêm")) {
                    nightCount = 3;
                } else if (durations.contains("1 đêm")) {
                    nightCount = 1;
                }
            }
            if (numbersPerson != null) {
                tvPeopleCount.setText(numbersPerson);
                // Extract people count for cost calculation
                if (numbersPerson.contains("2")) {
                    peopleCount = 2;
                } else if (numbersPerson.contains("3")) {
                    peopleCount = 3;
                } else if (numbersPerson.contains("4")) {
                    peopleCount = 4;
                } else {
                    peopleCount = 1;
                }
            }
        }
    }
    
    private void setupListeners() {
        // Back button
        ImageView btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(v -> finish());
        
        // Flight selection
        rgFlightOptions.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rb_vietnam_airlines) {
                flightCost = 2500000 * peopleCount;
                tvFlightStatus.setText("Vietnam Airlines");
            } else if (checkedId == R.id.rb_vietjet) {
                flightCost = 1800000 * peopleCount;
                tvFlightStatus.setText("VietJet Air");
            } else if (checkedId == R.id.rb_bamboo) {
                flightCost = 2200000 * peopleCount;
                tvFlightStatus.setText("Bamboo Airways");
            } else if (checkedId == R.id.rb_jetstar) {
                flightCost = 1600000 * peopleCount;
                tvFlightStatus.setText("Jetstar Pacific");
            }
            updateCostDisplay();
            checkAllSelections();
        });
        
        // Hotel selection
        rgHotelOptions.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rb_lotte_hotel) {
                hotelCost = 3500000 * nightCount;
                tvHotelStatus.setText("Lotte Hotel");
            } else if (checkedId == R.id.rb_intercontinental) {
                hotelCost = 4200000 * nightCount;
                tvHotelStatus.setText("InterContinental");
            } else if (checkedId == R.id.rb_hilton) {
                hotelCost = 3800000 * nightCount;
                tvHotelStatus.setText("Hilton Opera");
            } else if (checkedId == R.id.rb_sheraton) {
                hotelCost = 3200000 * nightCount;
                tvHotelStatus.setText("Sheraton Hotel");
            } else if (checkedId == R.id.rb_pullman) {
                hotelCost = 2800000 * nightCount;
                tvHotelStatus.setText("Pullman Hanoi");
            }
            updateCostDisplay();
            checkAllSelections();
        });
        
        // Attraction checkboxes
        setupAttractionListeners();
        
        // Action buttons
        btnPreview.setOnClickListener(v -> {
            Toast.makeText(this, "Chức năng xem trước đang được phát triển", Toast.LENGTH_SHORT).show();
        });
        
        btnCreatePlan.setOnClickListener(v -> {
            createDetailedPlan();
        });
    }
    
    private void setupAttractionListeners() {
        CheckBox[] checkBoxes = {
            cbHoChiMinhMausoleum, cbTempleLiterature, cbHoanKiemLake, cbOldQuarter,
            cbOnePillarPagoda, cbHanoiOperaHouse, cbBaDinhSquare, cbWestLake,
            cbHanoiMuseum, cbLongBienBridge
        };
        
        long[] costs = {0, 30000, 0, 0, 0, 100000, 0, 0, 40000, 0}; // Cost per person
        
        for (int i = 0; i < checkBoxes.length; i++) {
            final int index = i;
            checkBoxes[i].setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    if (selectedAttractionsCount >= 5) {
                        buttonView.setChecked(false);
                        Toast.makeText(this, "Bạn chỉ có thể chọn tối đa 5 địa điểm", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    selectedAttractionsCount++;
                    attractionsCost += costs[index] * peopleCount;
                } else {
                    selectedAttractionsCount--;
                    attractionsCost -= costs[index] * peopleCount;
                }
                
                tvAttractionsStatus.setText(selectedAttractionsCount + "/5 đã chọn");
                updateCostDisplay();
                checkAllSelections();
            });
        }
    }
    
    private void updateCostDisplay() {
        DecimalFormat formatter = new DecimalFormat("#,###");
        
        tvFlightCost.setText(formatter.format(flightCost) + " VND");
        tvHotelCost.setText(formatter.format(hotelCost) + " VND");
        tvAttractionsCost.setText(formatter.format(attractionsCost) + " VND");
        
        long total = flightCost + hotelCost + attractionsCost;
        tvTotalCost.setText(formatter.format(total) + " VND");
    }
    
    private void checkAllSelections() {
        boolean flightSelected = rgFlightOptions.getCheckedRadioButtonId() != -1;
        boolean hotelSelected = rgHotelOptions.getCheckedRadioButtonId() != -1;
        boolean attractionsSelected = selectedAttractionsCount > 0;
        
        boolean allSelected = flightSelected && hotelSelected && attractionsSelected;
        
        btnPreview.setEnabled(allSelected);
        btnCreatePlan.setEnabled(allSelected);
        
        if (allSelected) {
            btnPreview.setAlpha(1.0f);
            btnCreatePlan.setAlpha(1.0f);
        } else {
            btnPreview.setAlpha(0.5f);
            btnCreatePlan.setAlpha(0.5f);
        }
    }
    
    private void createDetailedPlan() {
        // Show loading message
        Toast.makeText(this, "Đang tạo kế hoạch chi tiết...", Toast.LENGTH_SHORT).show();
        
        // Simulate AI processing time
        btnCreatePlan.postDelayed(() -> {
            Intent intent = new Intent(PlanActivity.this, PlanDetailActivity.class);
            
            // Pass selected options to detail activity
            intent.putExtra("flight_airline", getSelectedFlightAirline());
            intent.putExtra("hotel_name", getSelectedHotelName());
            intent.putExtra("selected_attractions", getSelectedAttractionsArray());
            intent.putExtra("total_cost", flightCost + hotelCost + attractionsCost);
            intent.putExtra("people_count", peopleCount);
            intent.putExtra("night_count", nightCount);
            
            startActivity(intent);
        }, 2000);
    }
    
    private String getSelectedFlightAirline() {
        int checkedId = rgFlightOptions.getCheckedRadioButtonId();
        if (checkedId == R.id.rb_vietnam_airlines) return "Vietnam Airlines";
        if (checkedId == R.id.rb_vietjet) return "VietJet Air";
        if (checkedId == R.id.rb_bamboo) return "Bamboo Airways";
        if (checkedId == R.id.rb_jetstar) return "Jetstar Pacific";
        return "";
    }
    
    private String getSelectedHotelName() {
        int checkedId = rgHotelOptions.getCheckedRadioButtonId();
        if (checkedId == R.id.rb_lotte_hotel) return "Lotte Hotel Hanoi";
        if (checkedId == R.id.rb_intercontinental) return "InterContinental Hanoi";
        if (checkedId == R.id.rb_hilton) return "Hilton Hanoi Opera";
        if (checkedId == R.id.rb_sheraton) return "Sheraton Hanoi Hotel";
        if (checkedId == R.id.rb_pullman) return "Pullman Hanoi";
        return "";
    }
    
    private String[] getSelectedAttractionsArray() {
        CheckBox[] checkBoxes = {
            cbHoChiMinhMausoleum, cbTempleLiterature, cbHoanKiemLake, cbOldQuarter,
            cbOnePillarPagoda, cbHanoiOperaHouse, cbBaDinhSquare, cbWestLake,
            cbHanoiMuseum, cbLongBienBridge
        };
        
        String[] attractionNames = {
            "Lăng Chủ Tịch Hồ Chí Minh", "Văn Miếu - Quốc Tử Giám", "Hồ Hoàn Kiếm", 
            "Phố Cổ Hà Nội", "Chùa Một Cột", "Nhà Hát Lớn Hà Nội",
            "Quảng Trường Ba Đình", "Hồ Tây", "Bảo Tàng Lịch Sử Việt Nam", "Cầu Long Biên"
        };
        
        java.util.List<String> selected = new java.util.ArrayList<>();
        for (int i = 0; i < checkBoxes.length; i++) {
            if (checkBoxes[i].isChecked()) {
                selected.add(attractionNames[i]);
            }
        }
        
        return selected.toArray(new String[0]);
    }
}