package com.example.triply;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Calendar;

public class ScheduleActivity extends AppCompatActivity {

    private TextInputEditText etDeparture, etDirection, etNumbersPerson, etBudget, etDurations, etStartDate, etHotelType;
    private MaterialButton btnPlan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);
        
        initViews();
        loadExistingData();
        setupDatePicker();
        setupPlanButton();
        setupBottomNavigation();
    }
    
    private void initViews() {
        etDeparture = findViewById(R.id.et_departure);
        etDirection = findViewById(R.id.et_direction);
        etNumbersPerson = findViewById(R.id.et_numbers_person);
        etBudget = findViewById(R.id.et_budget);
        etDurations = findViewById(R.id.et_durations);
        etStartDate = findViewById(R.id.et_start_date);
        etHotelType = findViewById(R.id.et_hotel_type);
        btnPlan = findViewById(R.id.btn_plan);
    }
    
    private void loadExistingData() {
        // Kiểm tra dữ liệu từ Intent (khi back từ PlanActivity)
        Intent intent = getIntent();
        if (intent != null) {
            String departure = intent.getStringExtra("departure");
            String direction = intent.getStringExtra("direction");
            String numbersPerson = intent.getStringExtra("numbers_person");
            String budget = intent.getStringExtra("budget");
            String durations = intent.getStringExtra("durations");
            String startDate = intent.getStringExtra("start_date");
            String hotelType = intent.getStringExtra("hotel_type");
            
            // Pre-fill form nếu có dữ liệu
            if (departure != null) etDeparture.setText(departure);
            if (direction != null) etDirection.setText(direction);
            if (numbersPerson != null) etNumbersPerson.setText(numbersPerson);
            if (budget != null) etBudget.setText(budget);
            if (durations != null) etDurations.setText(durations);
            if (startDate != null) etStartDate.setText(startDate);
            if (hotelType != null) etHotelType.setText(hotelType);
        }
    }
    
    private void setupDatePicker() {
        etStartDate.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    String date = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                    etStartDate.setText(date);
                },
                year, month, day
            );
            datePickerDialog.show();
        });
    }
    
    private void setupPlanButton() {
        btnPlan.setOnClickListener(v -> {
            if (validateForm()) {
                // Lưu dữ liệu lịch trình
                saveScheduleData();
                
                // Create bundle to pass data to PlanActivity
                Intent intent = new Intent(ScheduleActivity.this, PlanActivity.class);
                intent.putExtra("departure", etDeparture.getText().toString());
                intent.putExtra("direction", etDirection.getText().toString());
                intent.putExtra("numbers_person", etNumbersPerson.getText().toString());
                intent.putExtra("budget", etBudget.getText().toString());
                intent.putExtra("durations", etDurations.getText().toString());
                intent.putExtra("start_date", etStartDate.getText().toString());
                intent.putExtra("hotel_type", etHotelType.getText().toString());
                
                startActivity(intent);
                finish();
            }
        });
    }
    
    private void saveScheduleData() {
        SharedPreferences prefs = getSharedPreferences("TripSchedule", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        
        editor.putString("departure", etDeparture.getText().toString());
        editor.putString("direction", etDirection.getText().toString());
        editor.putString("numbers_person", etNumbersPerson.getText().toString());
        editor.putString("budget", etBudget.getText().toString());
        editor.putString("durations", etDurations.getText().toString());
        editor.putString("start_date", etStartDate.getText().toString());
        editor.putString("hotel_type", etHotelType.getText().toString());
        editor.putBoolean("has_schedule", true); // Đánh dấu đã có lịch trình
        
        editor.apply();
    }
    
    private boolean validateForm() {
        if (TextUtils.isEmpty(etDeparture.getText())) {
            etDeparture.setError("Vui lòng nhập điểm khởi hành");
            etDeparture.requestFocus();
            return false;
        }
        
        if (TextUtils.isEmpty(etDirection.getText())) {
            etDirection.setError("Vui lòng nhập điểm đến");
            etDirection.requestFocus();
            return false;
        }
        
        if (TextUtils.isEmpty(etNumbersPerson.getText())) {
            etNumbersPerson.setError("Vui lòng nhập số người");
            etNumbersPerson.requestFocus();
            return false;
        }
        
        if (TextUtils.isEmpty(etBudget.getText())) {
            etBudget.setError("Vui lòng nhập ngân sách");
            etBudget.requestFocus();
            return false;
        }
        
        if (TextUtils.isEmpty(etDurations.getText())) {
            etDurations.setError("Vui lòng nhập thời gian");
            etDurations.requestFocus();
            return false;
        }
        
        if (TextUtils.isEmpty(etStartDate.getText())) {
            etStartDate.setError("Vui lòng chọn ngày bắt đầu");
            etStartDate.requestFocus();
            return false;
        }
        
        Toast.makeText(this, "Tạo lịch trình thành công!", Toast.LENGTH_SHORT).show();
        return true;
    }
    
    private void setupBottomNavigation() {
        ImageView navHome = findViewById(R.id.nav_home);
        ImageView navPlan = findViewById(R.id.nav_plan);
        ImageView navFavorite = findViewById(R.id.nav_favorite);
        ImageView navProfile = findViewById(R.id.nav_profile);
        
        // Set plan icon as selected (black) since schedule is part of plan functionality
        navHome.setSelected(false);
        navPlan.setSelected(true);
        navFavorite.setSelected(false);
        navProfile.setSelected(false);
        
        // Setup click listeners for navigation
        navHome.setOnClickListener(v -> {
            Intent intent = new Intent(ScheduleActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
        });
        
        navPlan.setOnClickListener(v -> {
            Intent intent = new Intent(ScheduleActivity.this, PlanActivity.class);
            startActivity(intent);
            finish();
        });
        
        navFavorite.setOnClickListener(v -> {
            Intent intent = new Intent(ScheduleActivity.this, FavoriteActivity.class);
            startActivity(intent);
            finish();
        });
        
        navProfile.setOnClickListener(v -> {
            Intent intent = new Intent(ScheduleActivity.this, ProfileActivity.class);
            startActivity(intent);
            finish();
        });
    }
}