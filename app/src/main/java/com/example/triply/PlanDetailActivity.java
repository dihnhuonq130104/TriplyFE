package com.example.triply;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.android.material.button.MaterialButton;

import java.lang.reflect.Type;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PlanDetailActivity extends AppCompatActivity {

    private TextView tvDestination, tvDuration, tvPeopleCount;
    private TextView tvFlightDepAirport, tvFlightDepTime, tvFlightArrAirport, tvFlightArrTime, tvFlightAirlineInfo, tvFlightDuration;
    private TextView tvFlightReturnDepAirport, tvFlightReturnDepTime, tvFlightReturnArrAirport, tvFlightReturnArrTime, tvFlightReturnAirlineInfo, tvFlightReturnDuration;
    private TextView tvHotelName, tvHotelStars, tvHotelCheckin, tvHotelCheckout;
    private TextView tvCostFlightLabel, tvCostFlightValue, tvCostHotelLabel, tvCostHotelValue, tvCostTotalValue, tvCostSummaryText;
    private MaterialButton btnSavePlan, btnBookNow;
    private LinearLayout[] dayBlocks;
    private TextView[] dayHeaders;
    private LinearLayout[] dayActivityContainers;
    private final int MAX_DAYS_IN_LAYOUT = 3;
    private int nightCount = 1;
    private Calendar startDateCal;

    public static class SavedPlanData {
        String destination, durationText, startDate, flightAirlineName;
        int peopleCount, nightCount;
        String[] selectedAttractions;
        String flightDepAirport, flightDepTime, flightArrAirport, flightArrTime, flightAirlineInfo, flightDuration;
        String flightReturnDepAirport, flightReturnDepTime, flightReturnArrAirport, flightReturnArrTime, flightReturnAirlineInfo, flightReturnDuration;
        String hotelName, hotelStars, hotelCheckin, hotelCheckout;
        long flightCost, hotelCost, totalCost;
    }

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
        tvFlightDepAirport = findViewById(R.id.tv_flight_dep_airport);
        tvFlightDepTime = findViewById(R.id.tv_flight_dep_time);
        tvFlightArrAirport = findViewById(R.id.tv_flight_arr_airport);
        tvFlightArrTime = findViewById(R.id.tv_flight_arr_time);
        tvFlightAirlineInfo = findViewById(R.id.tv_flight_airline_info);
        tvFlightDuration = findViewById(R.id.tv_flight_duration);
        tvFlightReturnDepAirport = findViewById(R.id.tv_flight_return_dep_airport);
        tvFlightReturnDepTime = findViewById(R.id.tv_flight_return_dep_time);
        tvFlightReturnArrAirport = findViewById(R.id.tv_flight_return_arr_airport);
        tvFlightReturnArrTime = findViewById(R.id.tv_flight_return_arr_time);
        tvFlightReturnAirlineInfo = findViewById(R.id.tv_flight_return_airline_info);
        tvFlightReturnDuration = findViewById(R.id.tv_flight_return_duration);
        tvHotelName = findViewById(R.id.tv_hotel_name);
        tvHotelStars = findViewById(R.id.tv_hotel_stars);
        tvHotelCheckin = findViewById(R.id.tv_hotel_checkin);
        tvHotelCheckout = findViewById(R.id.tv_hotel_checkout);
        dayBlocks = new LinearLayout[]{ findViewById(R.id.ll_day_1_block), findViewById(R.id.ll_day_2_block), findViewById(R.id.ll_day_3_block) };
        dayHeaders = new TextView[]{ findViewById(R.id.tv_day_1_header), findViewById(R.id.tv_day_2_header), findViewById(R.id.tv_day_3_header) };
        dayActivityContainers = new LinearLayout[]{ findViewById(R.id.ll_day_1_activities), findViewById(R.id.ll_day_2_activities), findViewById(R.id.ll_day_3_activities) };
        tvCostFlightLabel = findViewById(R.id.tv_cost_flight_label);
        tvCostFlightValue = findViewById(R.id.tv_cost_flight_value);
        tvCostHotelLabel = findViewById(R.id.tv_cost_hotel_label);
        tvCostHotelValue = findViewById(R.id.tv_cost_hotel_value);
        tvCostTotalValue = findViewById(R.id.tv_cost_total_value);
        tvCostSummaryText = findViewById(R.id.tv_cost_summary_text);
        btnSavePlan = findViewById(R.id.btn_save_plan);
        btnBookNow = findViewById(R.id.btn_book_now);
    }

    private void loadDataFromIntent() {
        Intent intent = getIntent();
        if (intent == null) return;

        String destination = intent.getStringExtra(PlanActivity.EXTRA_DETAIL_DESTINATION);
        String duration = intent.getStringExtra(PlanActivity.EXTRA_DETAIL_DURATION_TEXT);
        int peopleCount = intent.getIntExtra(PlanActivity.EXTRA_DETAIL_PEOPLE_COUNT, 1);
        this.nightCount = intent.getIntExtra(PlanActivity.EXTRA_DETAIL_NIGHT_COUNT, 1);
        String startDateRaw = intent.getStringExtra(PlanActivity.EXTRA_DETAIL_START_DATE);
        this.startDateCal = parseStartDate(startDateRaw);
        String[] selectedAttractions = intent.getStringArrayExtra(PlanActivity.EXTRA_DETAIL_SELECTED_ATTRACTIONS);

        tvFlightDepAirport.setText(intent.getStringExtra(PlanActivity.EXTRA_DETAIL_FLIGHT_DEP_AIRPORT));
        tvFlightDepTime.setText(intent.getStringExtra(PlanActivity.EXTRA_DETAIL_FLIGHT_DEP_TIME));
        tvFlightArrAirport.setText(intent.getStringExtra(PlanActivity.EXTRA_DETAIL_FLIGHT_ARR_AIRPORT));
        tvFlightArrTime.setText(intent.getStringExtra(PlanActivity.EXTRA_DETAIL_FLIGHT_ARR_TIME));
        tvFlightAirlineInfo.setText(intent.getStringExtra(PlanActivity.EXTRA_DETAIL_FLIGHT_AIRLINE_INFO));
        tvFlightDuration.setText(intent.getStringExtra(PlanActivity.EXTRA_DETAIL_FLIGHT_DURATION));
        tvFlightReturnDepAirport.setText(intent.getStringExtra(PlanActivity.EXTRA_DETAIL_FLIGHT_RETURN_DEP_AIRPORT));
        tvFlightReturnDepTime.setText(intent.getStringExtra(PlanActivity.EXTRA_DETAIL_FLIGHT_RETURN_DEP_TIME));
        tvFlightReturnArrAirport.setText(intent.getStringExtra(PlanActivity.EXTRA_DETAIL_FLIGHT_RETURN_ARR_AIRPORT));
        tvFlightReturnArrTime.setText(intent.getStringExtra(PlanActivity.EXTRA_DETAIL_FLIGHT_RETURN_ARR_TIME));
        tvFlightReturnAirlineInfo.setText(intent.getStringExtra(PlanActivity.EXTRA_DETAIL_FLIGHT_RETURN_AIRLINE_INFO));
        tvFlightReturnDuration.setText(intent.getStringExtra(PlanActivity.EXTRA_DETAIL_FLIGHT_RETURN_DURATION));

        tvHotelName.setText(intent.getStringExtra(PlanActivity.EXTRA_DETAIL_HOTEL_NAME));
        tvHotelStars.setText(intent.getStringExtra(PlanActivity.EXTRA_DETAIL_HOTEL_STARS));
        tvHotelCheckin.setText(intent.getStringExtra(PlanActivity.EXTRA_DETAIL_HOTEL_CHECKIN));
        tvHotelCheckout.setText(intent.getStringExtra(PlanActivity.EXTRA_DETAIL_HOTEL_CHECKOUT));

        long flightCost = intent.getLongExtra(PlanActivity.EXTRA_DETAIL_FLIGHT_COST, 0);
        long hotelCost = intent.getLongExtra(PlanActivity.EXTRA_DETAIL_HOTEL_COST, 0);
        long totalCost = intent.getLongExtra(PlanActivity.EXTRA_DETAIL_TOTAL_COST, 0);
        String flightAirlineName = intent.getStringExtra(PlanActivity.EXTRA_DETAIL_FLIGHT_AIRLINE_NAME);

        tvDestination.setText(destination);
        tvDuration.setText(duration);
        tvPeopleCount.setText(peopleCount + " người");

        tvCostFlightLabel.setText("Vé máy bay (" + flightAirlineName + "):");
        tvCostFlightValue.setText(formatCurrency(flightCost) + " VND");
        tvCostHotelLabel.setText("Khách sạn (" + nightCount + " đêm):");
        tvCostHotelValue.setText(formatCurrency(hotelCost) + " VND");
        tvCostTotalValue.setText(formatCurrency(totalCost) + " VND");
        tvCostSummaryText.setText("(Cho " + peopleCount + " người, " + duration + ")");

        populateSchedule(selectedAttractions,
                intent.getStringExtra(PlanActivity.EXTRA_DETAIL_FLIGHT_DEP_TIME),
                intent.getStringExtra(PlanActivity.EXTRA_DETAIL_FLIGHT_RETURN_DEP_TIME),
                intent.getStringExtra(PlanActivity.EXTRA_DETAIL_HOTEL_NAME),
                intent.getStringExtra(PlanActivity.EXTRA_DETAIL_HOTEL_CHECKIN),
                intent.getStringExtra(PlanActivity.EXTRA_DETAIL_HOTEL_CHECKOUT)
        );
    }

    private void populateSchedule(String[] attractions, String departureFlightTime, String returnFlightTime, String hotelName, String checkinTime, String checkoutTime) {
        for (LinearLayout block : dayBlocks) block.setVisibility(View.GONE);
        for (LinearLayout container : dayActivityContainers) container.removeAllViews();

        int totalDays = nightCount + 1;
        List<String> attractionList = (attractions != null) ? new ArrayList<>(Arrays.asList(attractions)) : new ArrayList<>();
        int attractionIndex = 0;

        for (int dayIndex = 0; dayIndex < totalDays && dayIndex < MAX_DAYS_IN_LAYOUT; dayIndex++) {
            dayBlocks[dayIndex].setVisibility(View.VISIBLE);

            String headerDateText = "";
            if (this.startDateCal != null) {
                Calendar currentDayCal = (Calendar) this.startDateCal.clone();
                currentDayCal.add(Calendar.DAY_OF_MONTH, dayIndex);
                SimpleDateFormat outFmt = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                headerDateText = " - " + outFmt.format(currentDayCal.getTime());
            }
            dayHeaders[dayIndex].setText("Ngày " + (dayIndex + 1) + headerDateText);

            LinearLayout currentDayContainer = dayActivityContainers[dayIndex];
            boolean isLastDay = (dayIndex == totalDays - 1);

            if (dayIndex == 0) {
                if (departureFlightTime != null) {
                    addScheduleItem(currentDayContainer, "✈️ Chuyến bay đi:", "Cất cánh lúc " + departureFlightTime, true);
                }
                if (hotelName != null) {
                    String checkinHour = (checkinTime != null && checkinTime.contains(":")) ? checkinTime.split(":")[1].trim() : "14:00";
                    addScheduleItem(currentDayContainer, "🏨 Check-in:", "Nhận phòng tại " + hotelName + " (sau " + checkinHour + ")", true);
                }
            }

            int remainingDays = totalDays - dayIndex;
            int attractionsForThisDay = (remainingDays > 0) ? (int) Math.ceil((double) (attractionList.size() - attractionIndex) / remainingDays) : 0;
            int end = Math.min(attractionIndex + attractionsForThisDay, attractionList.size());

            if (attractionIndex < end) {
                addScheduleItem(currentDayContainer, "📋 Hoạt động trong ngày:", "", false);
                for (int i = attractionIndex; i < end; i++) {
                    addScheduleItem(currentDayContainer, "", "• " + attractionList.get(i), false);
                }
                attractionIndex = end;
            }

            if (isLastDay) {
                if (hotelName != null) {
                    String checkoutHour = (checkoutTime != null && checkoutTime.contains(":")) ? checkoutTime.split(":")[1].trim() : "12:00";
                    addScheduleItem(currentDayContainer, "🏨 Check-out:", "Trả phòng tại " + hotelName + " (trước " + checkoutHour + ")", true);
                }
                if (returnFlightTime != null) {
                    addScheduleItem(currentDayContainer, "✈️ Chuyến bay về:", "Cất cánh lúc " + returnFlightTime, true);
                }
            }
        }
    }

    private void addScheduleItem(LinearLayout container, String title, String detail, boolean isHighlight) {
        LinearLayout itemLayout = new LinearLayout(this);
        itemLayout.setOrientation(LinearLayout.VERTICAL);
        int padding = (int) (4 * getResources().getDisplayMetrics().density);
        itemLayout.setPadding(padding * 2, padding, 0, padding);

        if (!title.isEmpty()) {
            TextView titleTv = new TextView(this);
            titleTv.setText(title);
            titleTv.setTextSize(14f);
            if (isHighlight) {
                titleTv.setTypeface(null, Typeface.BOLD);
            }
            itemLayout.addView(titleTv);
        }

        if (!detail.isEmpty()) {
            TextView detailTv = new TextView(this);
            detailTv.setText(detail);
            detailTv.setTextSize(14f);
            if (!title.isEmpty()) {
                detailTv.setPadding((int) (8 * getResources().getDisplayMetrics().density), 0, 0, 0);
            }
            itemLayout.addView(detailTv);
        }

        container.addView(itemLayout);
    }

    private Calendar parseStartDate(String raw) {
        if (raw == null) return null;
        raw = raw.trim();
        String[] patterns = {"dd/MM/yyyy", "yyyy-MM-dd"};
        for (String p : patterns) {
            try {
                SimpleDateFormat fmt = new SimpleDateFormat(p, Locale.getDefault());
                Date d = fmt.parse(raw);
                if (d != null) {
                    Calendar c = Calendar.getInstance();
                    c.setTime(d);
                    return c;
                }
            } catch (ParseException ignored) {
            }
        }
        return null;
    }

    private String formatCurrency(long value) {
        if (value == 0) return "0";
        try {
            return new DecimalFormat("#,###").format(value);
        } catch (Exception e) {
            return String.valueOf(value);
        }
    }

    private void setupListeners() {
        findViewById(R.id.btn_back).setOnClickListener(v -> finish());

        btnSavePlan.setOnClickListener(v -> {
            savePlan();
            Toast.makeText(this, "Kế hoạch đã được lưu!", Toast.LENGTH_LONG).show();

            Intent intent = new Intent(PlanDetailActivity.this, HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });

        btnBookNow.setOnClickListener(v -> {
            Toast.makeText(this, "Chức năng đang phát triển...", Toast.LENGTH_LONG).show();
        });
    }

    private void savePlan() {
        SharedPreferences prefs = getSharedPreferences("SavedPlansList", MODE_PRIVATE);
        Gson gson = new Gson();

        String json = prefs.getString("plans_json", null);
        Type type = new TypeToken<ArrayList<SavedPlanData>>() {}.getType();
        List<SavedPlanData> plansList = gson.fromJson(json, type);
        if (plansList == null) {
            plansList = new ArrayList<>();
        }

        SavedPlanData newPlan = createPlanDataFromIntent();
        plansList.add(newPlan);

        SharedPreferences.Editor editor = prefs.edit();
        String updatedJson = gson.toJson(plansList);
        editor.putString("plans_json", updatedJson);
        editor.apply();
    }

    private SavedPlanData createPlanDataFromIntent() {
        Intent intent = getIntent();
        SavedPlanData data = new SavedPlanData();

        data.destination = intent.getStringExtra(PlanActivity.EXTRA_DETAIL_DESTINATION);
        data.durationText = intent.getStringExtra(PlanActivity.EXTRA_DETAIL_DURATION_TEXT);
        data.peopleCount = intent.getIntExtra(PlanActivity.EXTRA_DETAIL_PEOPLE_COUNT, 1);
        data.nightCount = intent.getIntExtra(PlanActivity.EXTRA_DETAIL_NIGHT_COUNT, 1);
        data.startDate = intent.getStringExtra(PlanActivity.EXTRA_DETAIL_START_DATE);
        data.flightDepAirport = intent.getStringExtra(PlanActivity.EXTRA_DETAIL_FLIGHT_DEP_AIRPORT);
        data.flightDepTime = intent.getStringExtra(PlanActivity.EXTRA_DETAIL_FLIGHT_DEP_TIME);
        data.flightArrAirport = intent.getStringExtra(PlanActivity.EXTRA_DETAIL_FLIGHT_ARR_AIRPORT);
        data.flightArrTime = intent.getStringExtra(PlanActivity.EXTRA_DETAIL_FLIGHT_ARR_TIME);
        data.flightAirlineInfo = intent.getStringExtra(PlanActivity.EXTRA_DETAIL_FLIGHT_AIRLINE_INFO);
        data.flightDuration = intent.getStringExtra(PlanActivity.EXTRA_DETAIL_FLIGHT_DURATION);
        data.flightReturnDepAirport = intent.getStringExtra(PlanActivity.EXTRA_DETAIL_FLIGHT_RETURN_DEP_AIRPORT);
        data.flightReturnDepTime = intent.getStringExtra(PlanActivity.EXTRA_DETAIL_FLIGHT_RETURN_DEP_TIME);
        data.flightReturnArrAirport = intent.getStringExtra(PlanActivity.EXTRA_DETAIL_FLIGHT_RETURN_ARR_AIRPORT);
        data.flightReturnArrTime = intent.getStringExtra(PlanActivity.EXTRA_DETAIL_FLIGHT_RETURN_ARR_TIME);
        data.flightReturnAirlineInfo = intent.getStringExtra(PlanActivity.EXTRA_DETAIL_FLIGHT_RETURN_AIRLINE_INFO);
        data.flightReturnDuration = intent.getStringExtra(PlanActivity.EXTRA_DETAIL_FLIGHT_RETURN_DURATION);
        data.hotelName = intent.getStringExtra(PlanActivity.EXTRA_DETAIL_HOTEL_NAME);
        data.hotelStars = intent.getStringExtra(PlanActivity.EXTRA_DETAIL_HOTEL_STARS);
        data.hotelCheckin = intent.getStringExtra(PlanActivity.EXTRA_DETAIL_HOTEL_CHECKIN);
        data.hotelCheckout = intent.getStringExtra(PlanActivity.EXTRA_DETAIL_HOTEL_CHECKOUT);
        data.flightCost = intent.getLongExtra(PlanActivity.EXTRA_DETAIL_FLIGHT_COST, 0);
        data.hotelCost = intent.getLongExtra(PlanActivity.EXTRA_DETAIL_HOTEL_COST, 0);
        data.totalCost = intent.getLongExtra(PlanActivity.EXTRA_DETAIL_TOTAL_COST, 0);
        data.flightAirlineName = intent.getStringExtra(PlanActivity.EXTRA_DETAIL_FLIGHT_AIRLINE_NAME);
        data.selectedAttractions = intent.getStringArrayExtra(PlanActivity.EXTRA_DETAIL_SELECTED_ATTRACTIONS);

        return data;
    }
}