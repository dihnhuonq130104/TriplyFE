package com.example.triply;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ScheduleActivity extends AppCompatActivity {

    private TextInputEditText etDeparture, etDirection, etNumbersPerson, etBudget, etDurations, etStartDate;
    private MaterialButton btnPlan;
    private FrameLayout loadingOverlay;

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
        btnPlan = findViewById(R.id.btn_plan);
        loadingOverlay = findViewById(R.id.loading_overlay);
    }

    private void loadExistingData() {
        Intent intent = getIntent();
        if (intent != null) {
            String departure = intent.getStringExtra("departure");
            String direction = intent.getStringExtra("direction");
            String numbersPerson = intent.getStringExtra("numbers_person");
            String budget = intent.getStringExtra("budget");
            String durations = intent.getStringExtra("durations");
            String startDate = intent.getStringExtra("start_date");
            if (departure != null) etDeparture.setText(departure);
            if (direction != null) etDirection.setText(direction);
            if (numbersPerson != null) etNumbersPerson.setText(numbersPerson);
            if (budget != null) etBudget.setText(budget);
            if (durations != null) etDurations.setText(durations);
            if (startDate != null) etStartDate.setText(startDate);
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
                saveScheduleData();
                showLoading(true);

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
                        runOnUiThread(() -> {
                            showLoading(false);
                            Toast.makeText(ScheduleActivity.this, "Tạo kế hoạch thành công!", Toast.LENGTH_SHORT).show();
                            
                            Intent intent = new Intent(ScheduleActivity.this, PlanActivity.class);
                            intent.putExtra("departure", origin);
                            intent.putExtra("direction", destination);
                            intent.putExtra("numbers_person", etNumbersPerson.getText().toString());
                            intent.putExtra("budget", etBudget.getText().toString());
                            intent.putExtra("durations", etDurations.getText().toString());
                            intent.putExtra("start_date", startDateUi);
                            intent.putExtra("night_count", nights);
                            intent.putExtra("plan_json", new Gson().toJson(response));
                            startActivity(intent);
                            finish();
                        });
                    }

                    @Override
                    public void onError(Throwable t, retrofit2.Response<?> raw) {
                        runOnUiThread(() -> {
                            showLoading(false);
                            Toast.makeText(ScheduleActivity.this, "Tạo kế hoạch thất bại: " + t.getMessage(), Toast.LENGTH_LONG).show();
                        });
                    }
                });
            }
        });
    }

    private void showLoading(boolean show) {
        loadingOverlay.setVisibility(show ? View.VISIBLE : View.GONE);
        btnPlan.setEnabled(!show);
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
        int nights = extractNights(etDurations.getText().toString().trim());
        editor.putInt("night_count", nights);
        editor.putBoolean("has_schedule", true);
        editor.apply();
    }

    private int extractNights(String durations) {
        if (durations == null || durations.trim().isEmpty()) return 1;
        String lower = durations.toLowerCase().trim();
        try {
            Matcher mNight = Pattern.compile("(\\d+)\\s*đêm").matcher(lower);
            if (mNight.find() && mNight.group(1) != null) {
                return Integer.parseInt(mNight.group(1));
            }

            Matcher mDay = Pattern.compile("(\\d+)\\s*ngày").matcher(lower);
            if (mDay.find() && mDay.group(1) != null) {
                int days = Integer.parseInt(mDay.group(1));
                return Math.max(1, days - 1); // 2 ngày -> 1 đêm
            }

            Matcher mNumberOnly = Pattern.compile("(\\d+)").matcher(lower);
            if (mNumberOnly.find() && mNumberOnly.group(1) != null) {
                int days = Integer.parseInt(mNumberOnly.group(1));
                return Math.max(1, days - 1);
            }

        } catch (Exception ignored) { }
        return 1;
    }

    private String toIsoDate(String ddMMyyyy) {
        try {
            SimpleDateFormat inFmt = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            SimpleDateFormat outFmt = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            Date d = inFmt.parse(ddMMyyyy);
            return outFmt.format(d);
        } catch (Exception e) {
            return ddMMyyyy;
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

        return true;
    }

    private void setupBottomNavigation() {
        ImageView navHome = findViewById(R.id.nav_home);
        ImageView navPlan = findViewById(R.id.nav_plan);
        ImageView navChatbot = findViewById(R.id.nav_chatbot);
        ImageView navProfile = findViewById(R.id.nav_profile);

        navHome.setSelected(false);
        navPlan.setSelected(true);
        navChatbot.setSelected(false);
        navProfile.setSelected(false);

        navHome.setOnClickListener(v -> {
            Intent intent = new Intent(ScheduleActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
        });


        navPlan.setOnClickListener(v -> {
        });

        navChatbot.setOnClickListener(v -> {
            Intent intent = new Intent(ScheduleActivity.this, ChatbotActivity.class);
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