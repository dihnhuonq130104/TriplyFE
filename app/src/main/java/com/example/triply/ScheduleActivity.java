package com.example.triply;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.triply.data.remote.model.CreatePlanRequest;
import com.example.triply.data.remote.model.PlanResponse;
import com.example.triply.data.repository.PlanRepository;
import com.google.gson.Gson;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Calendar;
import java.util.Locale;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

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

                // Gọi API lên kế hoạch
                btnPlan.setEnabled(false);
                Toast.makeText(this, "Đang tạo kế hoạch...", Toast.LENGTH_SHORT).show();

                String origin = etDeparture.getText().toString().trim();
                String destination = etDirection.getText().toString().trim();
                int people = safeParseInt(etNumbersPerson.getText().toString().trim(), 1);
                long budget = safeParseLong(etBudget.getText().toString().trim(), 0);

                String startDateUi = etStartDate.getText().toString().trim();
                String startDate = toIsoDate(startDateUi);
                int nights = extractNights(etDurations.getText().toString().trim());
                String endDate = addDaysIso(startDate, nights);

                CreatePlanRequest request = new CreatePlanRequest(budget, startDate, endDate, people, origin, destination);

                new PlanRepository().planTrip(request, new PlanRepository.PlanCallback() {
                    @Override
                    public void onSuccess(PlanResponse response) {
                        btnPlan.post(() -> {
                            btnPlan.setEnabled(true);
                            // Forward to PlanActivity with response JSON
                            Intent intent = new Intent(ScheduleActivity.this, PlanActivity.class);
                            intent.putExtra("departure", origin);
                            intent.putExtra("direction", destination);
                            intent.putExtra("numbers_person", etNumbersPerson.getText().toString());
                            intent.putExtra("budget", etBudget.getText().toString());
                            intent.putExtra("durations", etDurations.getText().toString());
                            intent.putExtra("start_date", startDateUi);
                            intent.putExtra("hotel_type", etHotelType.getText().toString());
                            intent.putExtra("plan_json", new Gson().toJson(response));
                            startActivity(intent);
                            finish();
                        });
                    }

                    @Override
                    public void onError(Throwable t, retrofit2.Response<?> raw) {
                        btnPlan.post(() -> {
                            btnPlan.setEnabled(true);
                            Toast.makeText(ScheduleActivity.this, "Tạo kế hoạch thất bại: " + t.getMessage(), Toast.LENGTH_LONG).show();
                        });
                    }
                });
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

    private int extractNights(String durations) {
        if (durations == null) return 2;
        if (durations.contains("3 đêm")) return 3;
        if (durations.contains("2 đêm")) return 2;
        if (durations.contains("1 đêm")) return 1;
        // fallback: try extract any digit
        for (char c : durations.toCharArray()) {
            if (Character.isDigit(c)) return Character.getNumericValue(c);
        }
        return 2;
    }

    private String toIsoDate(String ddMMyyyy) {
        try {
            SimpleDateFormat inFmt = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            SimpleDateFormat outFmt = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            Date d = inFmt.parse(ddMMyyyy);
            return outFmt.format(d);
        } catch (Exception e) {
            return ddMMyyyy; // fallback if user already typed ISO
        }
    }

    private String addDaysIso(String isoDate, int days) {
        try {
            SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            Date d = fmt.parse(isoDate);
            long ms = d.getTime() + TimeUnit.DAYS.toMillis(days);
            return fmt.format(new Date(ms));
        } catch (Exception e) {
            return isoDate;
        }
    }

    private int safeParseInt(String s, int def) {
        try { return Integer.parseInt(s.replaceAll("[^0-9]", "")); } catch (Exception e) { return def; }
    }

    private long safeParseLong(String s, long def) {
        try { return Long.parseLong(s.replaceAll("[^0-9]", "")); } catch (Exception e) { return def; }
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