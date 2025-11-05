package com.example.triply;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.triply.data.remote.model.PlanResponse;
import com.google.gson.Gson;
import com.google.android.material.button.MaterialButton;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PlanActivity extends AppCompatActivity {

    public static final String EXTRA_DETAIL_DESTINATION = "DETAIL_DESTINATION";
    public static final String EXTRA_DETAIL_DURATION_TEXT = "DETAIL_DURATION_TEXT";
    public static final String EXTRA_DETAIL_PEOPLE_COUNT = "DETAIL_PEOPLE_COUNT";
    public static final String EXTRA_DETAIL_NIGHT_COUNT = "DETAIL_NIGHT_COUNT";
    public static final String EXTRA_DETAIL_START_DATE = "DETAIL_START_DATE";
    public static final String EXTRA_DETAIL_SELECTED_ATTRACTIONS = "DETAIL_SELECTED_ATTRACTIONS";

    public static final String EXTRA_DETAIL_FLIGHT_DEP_AIRPORT = "DETAIL_FLIGHT_DEP_AIRPORT";
    public static final String EXTRA_DETAIL_FLIGHT_DEP_TIME = "DETAIL_FLIGHT_DEP_TIME";
    public static final String EXTRA_DETAIL_FLIGHT_ARR_AIRPORT = "DETAIL_FLIGHT_ARR_AIRPORT";
    public static final String EXTRA_DETAIL_FLIGHT_ARR_TIME = "DETAIL_FLIGHT_ARR_TIME";
    public static final String EXTRA_DETAIL_FLIGHT_AIRLINE_INFO = "DETAIL_FLIGHT_AIRLINE_INFO";
    public static final String EXTRA_DETAIL_FLIGHT_DURATION = "DETAIL_FLIGHT_DURATION";

    public static final String EXTRA_DETAIL_FLIGHT_RETURN_DEP_AIRPORT = "DETAIL_FLIGHT_RETURN_DEP_AIRPORT";
    public static final String EXTRA_DETAIL_FLIGHT_RETURN_DEP_TIME = "DETAIL_FLIGHT_RETURN_DEP_TIME";
    public static final String EXTRA_DETAIL_FLIGHT_RETURN_ARR_AIRPORT = "DETAIL_FLIGHT_RETURN_ARR_AIRPORT";
    public static final String EXTRA_DETAIL_FLIGHT_RETURN_ARR_TIME = "DETAIL_FLIGHT_RETURN_ARR_TIME";
    public static final String EXTRA_DETAIL_FLIGHT_RETURN_AIRLINE_INFO = "DETAIL_FLIGHT_RETURN_AIRLINE_INFO";
    public static final String EXTRA_DETAIL_FLIGHT_RETURN_DURATION = "DETAIL_FLIGHT_RETURN_DURATION";


    public static final String EXTRA_DETAIL_HOTEL_NAME = "DETAIL_HOTEL_NAME";
    public static final String EXTRA_DETAIL_HOTEL_STARS = "DETAIL_HOTEL_STARS";
    public static final String EXTRA_DETAIL_HOTEL_CHECKIN = "DETAIL_HOTEL_CHECKIN";
    public static final String EXTRA_DETAIL_HOTEL_CHECKOUT = "DETAIL_HOTEL_CHECKOUT";

    public static final String EXTRA_DETAIL_FLIGHT_COST = "DETAIL_FLIGHT_COST";
    public static final String EXTRA_DETAIL_HOTEL_COST = "DETAIL_HOTEL_COST";
    public static final String EXTRA_DETAIL_TOTAL_COST = "DETAIL_TOTAL_COST";
    public static final String EXTRA_DETAIL_FLIGHT_AIRLINE_NAME = "DETAIL_FLIGHT_AIRLINE_NAME";


    private TextView tvDestination, tvDuration, tvPeopleCount;
    private TextView tvFlightStatus, tvHotelStatus, tvAttractionsStatus;
    private TextView tvFlightCost, tvHotelCost, tvAttractionsCost, tvTotalCost;

    private RadioGroup rgFlightOptions, rgHotelOptions;
    private MaterialButton btnPreview, btnCreatePlan;
    private long flightCost = 0;
    private long hotelCost = 0;
    private long attractionsCost = 0;
    private int selectedAttractionsCount = 0;
    private int peopleCount = 1;
    private int nightCount = 1;

    private CheckBox[] attractionCheckBoxes;

    private PlanResponse planResponse;
    private String startDateRaw;
    private final int[] flightRadioIds = { R.id.rb_vietnam_airlines, R.id.rb_vietjet, R.id.rb_bamboo, R.id.rb_jetstar };
    private final int[] hotelRadioIds = { R.id.rb_lotte_hotel, R.id.rb_intercontinental, R.id.rb_hilton, R.id.rb_sheraton, R.id.rb_pullman };

    private long[] flightOptionPrices = new long[flightRadioIds.length];
    private String[] flightOptionAirlines = new String[flightRadioIds.length];
    private long[] hotelOptionNightlyPrices = new long[hotelRadioIds.length];
    private String[] hotelOptionNames = new String[hotelRadioIds.length];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan);

        initViews();
        loadDataFromIntent();
        setupListeners();
        updateCostDisplay();
        checkAllSelections();
    }

    private void initViews() {
        tvDestination = findViewById(R.id.tv_destination);
        tvDuration = findViewById(R.id.tv_duration);
        tvPeopleCount = findViewById(R.id.tv_people_count);
        tvFlightStatus = findViewById(R.id.tv_flight_status);
        tvHotelStatus = findViewById(R.id.tv_hotel_status);
        tvAttractionsStatus = findViewById(R.id.tv_attractions_status);
        tvFlightCost = findViewById(R.id.tv_flight_cost);
        tvHotelCost = findViewById(R.id.tv_hotel_cost);
        tvAttractionsCost = findViewById(R.id.tv_attractions_cost);
        tvTotalCost = findViewById(R.id.tv_total_cost);
        rgFlightOptions = findViewById(R.id.rg_flight_options);
        rgHotelOptions = findViewById(R.id.rg_hotel_options);
        btnPreview = findViewById(R.id.btn_preview);
        btnCreatePlan = findViewById(R.id.btn_create_plan);

        attractionCheckBoxes = new CheckBox[]{
                findViewById(R.id.cb_ho_chi_minh_mausoleum), findViewById(R.id.cb_temple_literature),
                findViewById(R.id.cb_hoan_kiem_lake), findViewById(R.id.cb_old_quarter),
                findViewById(R.id.cb_one_pillar_pagoda), findViewById(R.id.cb_hanoi_opera_house),
                findViewById(R.id.cb_ba_dinh_square), findViewById(R.id.cb_west_lake),
                findViewById(R.id.cb_hanoi_museum), findViewById(R.id.cb_long_bien_bridge)
        };
    }

    private void loadDataFromIntent() {
        Intent intent = getIntent();
        if (intent == null) return;

        String direction = intent.getStringExtra("direction");
        String durations = intent.getStringExtra("durations");
        String numbersPerson = intent.getStringExtra("numbers_person");
        this.startDateRaw = intent.getStringExtra("start_date");
        this.nightCount = intent.getIntExtra("night_count", 1);
        String planJson = intent.getStringExtra("plan_json");

        tvDestination.setText(direction);
        tvDuration.setText(durations);
        tvPeopleCount.setText(numbersPerson);

        try {
            if (numbersPerson != null) {
                peopleCount = Integer.parseInt(numbersPerson.replaceAll("[^0-9]", ""));
            }
        } catch (Exception e) {
            peopleCount = 1;
        }

        if (planJson != null && !planJson.isEmpty()) {
            planResponse = new Gson().fromJson(planJson, PlanResponse.class);
            populateDynamicOptions();
        }
    }

    private void populateDynamicOptions() {
        if (planResponse == null) return;

        if (planResponse.flightOther != null) {
            int flightCount = Math.min(planResponse.flightOther.size(), flightRadioIds.length);
            for (int i = 0; i < flightRadioIds.length; i++) {
                RadioButton rb = findViewById(flightRadioIds[i]);
                if (i < flightCount && planResponse.flightOther.get(i).flights != null && !planResponse.flightOther.get(i).flights.isEmpty()) {
                    PlanResponse.FlightItinerary itinerary = planResponse.flightOther.get(i);
                    PlanResponse.FlightLeg leg = itinerary.flights.get(0);

                    flightOptionAirlines[i] = leg.airline;
                    flightOptionPrices[i] = itinerary.price;

                    String departureTimeInfo = "";
                    if (leg.departure_airport != null && leg.departure_airport.time != null) {
                        departureTimeInfo = formatDateTime(leg.departure_airport.time);
                    }

                    String label = leg.airline + " | " + departureTimeInfo + " | " + formatCurrency(itinerary.price) + " VND";
                    rb.setText(label);
                    rb.setVisibility(View.VISIBLE);
                } else {
                    rb.setVisibility(View.GONE);
                }
            }
        }

        if (planResponse.hotels != null) {
            int hotelCount = Math.min(planResponse.hotels.size(), hotelRadioIds.length);
            for (int i = 0; i < hotelRadioIds.length; i++) {
                RadioButton rb = findViewById(hotelRadioIds[i]);
                if (i < hotelCount) {
                    PlanResponse.Hotel hotel = planResponse.hotels.get(i);
                    hotelOptionNames[i] = hotel.name;
                    hotelOptionNightlyPrices[i] = (hotel.rate_per_night != null) ? hotel.rate_per_night.extracted_lowest : 0;
                    String label = hotel.name + " | " + formatCurrency(hotelOptionNightlyPrices[i]) + " VND/đêm";
                    rb.setText(label);
                    rb.setVisibility(View.VISIBLE);
                } else {
                    rb.setVisibility(View.GONE);
                }
            }
        }

        if (planResponse.attractions != null) {
            List<String> suggestions = new ArrayList<>();
            for (PlanResponse.AttractionDay day : planResponse.attractions) {
                if (day != null && day.schedule != null) {
                    for (PlanResponse.AttractionSchedule schedule : day.schedule) {
                        String suggestionText = schedule.time + " | " + schedule.location + ": " + schedule.activity;
                        suggestions.add(suggestionText);
                    }
                }
            }
            int suggestionCount = Math.min(suggestions.size(), attractionCheckBoxes.length);
            for (int i = 0; i < attractionCheckBoxes.length; i++) {
                if (i < suggestionCount) {
                    attractionCheckBoxes[i].setText(suggestions.get(i));
                    attractionCheckBoxes[i].setVisibility(View.VISIBLE);
                } else {
                    attractionCheckBoxes[i].setVisibility(View.GONE);
                }
            }
        }
    }

    private void setupListeners() {
        findViewById(R.id.btn_back).setOnClickListener(v -> finish());

        rgFlightOptions.setOnCheckedChangeListener((group, checkedId) -> {
            int idx = getIndexById(flightRadioIds, checkedId);
            if (idx != -1) {
                flightCost = (flightOptionPrices[idx]) * peopleCount;
                tvFlightStatus.setText(flightOptionAirlines[idx]);
            }
            updateCostDisplay();
            checkAllSelections();
        });

        rgHotelOptions.setOnCheckedChangeListener((group, checkedId) -> {
            int idx = getIndexById(hotelRadioIds, checkedId);
            if (idx != -1) {
                hotelCost = hotelOptionNightlyPrices[idx] * nightCount;
                tvHotelStatus.setText(hotelOptionNames[idx]);
            }
            updateCostDisplay();
            checkAllSelections();
        });

        for (CheckBox cb : attractionCheckBoxes) {
            cb.setOnCheckedChangeListener((buttonView, isChecked) -> {
                updateSelectedAttractionsCount();
                tvAttractionsStatus.setText(selectedAttractionsCount + " đã chọn");
                checkAllSelections();
            });
        }

        btnPreview.setOnClickListener(v -> Toast.makeText(this, "Chức năng đang phát triển", Toast.LENGTH_SHORT).show());
        btnCreatePlan.setOnClickListener(v -> createDetailedPlan());
    }

    private void createDetailedPlan() {
        Toast.makeText(this, "Đang tạo kế hoạch chi tiết...", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(PlanActivity.this, PlanDetailActivity.class);

        intent.putExtra(EXTRA_DETAIL_DESTINATION, tvDestination.getText().toString());
        intent.putExtra(EXTRA_DETAIL_DURATION_TEXT, tvDuration.getText().toString());
        intent.putExtra(EXTRA_DETAIL_PEOPLE_COUNT, peopleCount);
        intent.putExtra(EXTRA_DETAIL_NIGHT_COUNT, nightCount);
        intent.putExtra(EXTRA_DETAIL_START_DATE, startDateRaw);
        intent.putExtra(EXTRA_DETAIL_SELECTED_ATTRACTIONS, getSelectedAttractionsArray());

        PlanResponse.FlightLeg departureFlight = findSelectedFlight(planResponse, getSelectedFlightAirline());
        if (departureFlight != null) {
            intent.putExtra(EXTRA_DETAIL_FLIGHT_DEP_AIRPORT, departureFlight.departure_airport.name);
            intent.putExtra(EXTRA_DETAIL_FLIGHT_DEP_TIME, departureFlight.departure_airport.time);
            intent.putExtra(EXTRA_DETAIL_FLIGHT_ARR_AIRPORT, departureFlight.arrival_airport.name);
            intent.putExtra(EXTRA_DETAIL_FLIGHT_ARR_TIME, departureFlight.arrival_airport.time);
            intent.putExtra(EXTRA_DETAIL_FLIGHT_AIRLINE_INFO, departureFlight.airline + " - " + departureFlight.flight_number);
            intent.putExtra(EXTRA_DETAIL_FLIGHT_DURATION, departureFlight.duration + " phút");

            PlanResponse.FlightLeg returnFlight = createReturnFlight(departureFlight);
            intent.putExtra(EXTRA_DETAIL_FLIGHT_RETURN_DEP_AIRPORT, returnFlight.departure_airport.name);
            intent.putExtra(EXTRA_DETAIL_FLIGHT_RETURN_DEP_TIME, returnFlight.departure_airport.time);
            intent.putExtra(EXTRA_DETAIL_FLIGHT_RETURN_ARR_AIRPORT, returnFlight.arrival_airport.name);
            intent.putExtra(EXTRA_DETAIL_FLIGHT_RETURN_ARR_TIME, returnFlight.arrival_airport.time);
            intent.putExtra(EXTRA_DETAIL_FLIGHT_RETURN_AIRLINE_INFO, returnFlight.airline + " - " + returnFlight.flight_number);
            intent.putExtra(EXTRA_DETAIL_FLIGHT_RETURN_DURATION, returnFlight.duration + " phút");
        }

        PlanResponse.Hotel selectedHotel = findSelectedHotel(planResponse, getSelectedHotelName());
        if (selectedHotel != null) {
            intent.putExtra(EXTRA_DETAIL_HOTEL_NAME, selectedHotel.name);
            intent.putExtra(EXTRA_DETAIL_HOTEL_STARS, "⭐ " + (selectedHotel.extracted_hotel_class != null ? selectedHotel.extracted_hotel_class : "N/A") + " sao");
            intent.putExtra(EXTRA_DETAIL_HOTEL_CHECKIN, "📅 Check-in: " + (selectedHotel.check_in_time != null ? selectedHotel.check_in_time : "N/A"));
            intent.putExtra(EXTRA_DETAIL_HOTEL_CHECKOUT, "📅 Check-out: " + (selectedHotel.check_out_time != null ? selectedHotel.check_out_time : "N/A"));
        }

        intent.putExtra(EXTRA_DETAIL_FLIGHT_COST, flightCost);
        intent.putExtra(EXTRA_DETAIL_HOTEL_COST, hotelCost);
        intent.putExtra(EXTRA_DETAIL_TOTAL_COST, flightCost + hotelCost + attractionsCost);
        intent.putExtra(EXTRA_DETAIL_FLIGHT_AIRLINE_NAME, getSelectedFlightAirline());

        startActivity(intent);
    }

    private PlanResponse.FlightLeg findSelectedFlight(PlanResponse response, String selectedAirline) {
        if (response == null || response.flightOther == null || selectedAirline == null) return null;
        for (PlanResponse.FlightItinerary itinerary : response.flightOther) {
            if (itinerary.flights != null && !itinerary.flights.isEmpty()) {
                PlanResponse.FlightLeg leg = itinerary.flights.get(0);
                if (leg.airline != null && leg.airline.equals(selectedAirline)) {
                    leg.duration = itinerary.total_duration;
                    return leg;
                }
            }
        }
        return null;
    }

    private PlanResponse.FlightLeg createReturnFlight(PlanResponse.FlightLeg departureFlight) {
        PlanResponse.FlightLeg returnFlight = new PlanResponse.FlightLeg();
        returnFlight.airline = departureFlight.airline;
        returnFlight.flight_number = departureFlight.flight_number + "R"; // Thêm R để phân biệt
        returnFlight.duration = departureFlight.duration;

        returnFlight.departure_airport = departureFlight.arrival_airport;
        returnFlight.arrival_airport = departureFlight.departure_airport;

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US);
            Date departureDate = sdf.parse(departureFlight.departure_airport.time);

            Calendar cal = Calendar.getInstance();
            cal.setTime(departureDate);
            cal.add(Calendar.DAY_OF_MONTH, nightCount + 1); // Ngày về là ngày cuối cùng
            cal.set(Calendar.HOUR_OF_DAY, 17); // Giả định bay về lúc 17:30
            cal.set(Calendar.MINUTE, 30);
            returnFlight.departure_airport.time = sdf.format(cal.getTime());

            cal.add(Calendar.MINUTE, returnFlight.duration); // Thêm thời gian bay để có giờ đến
            returnFlight.arrival_airport.time = sdf.format(cal.getTime());

        } catch (Exception e) {
            returnFlight.departure_airport.time = "N/A";
            returnFlight.arrival_airport.time = "N/A";
        }
        return returnFlight;
    }


    private PlanResponse.Hotel findSelectedHotel(PlanResponse response, String selectedHotelName) {
        if (response == null || response.hotels == null || selectedHotelName == null) return null;
        for (PlanResponse.Hotel hotel : response.hotels) {
            if (hotel.name != null && hotel.name.equals(selectedHotelName)) {
                return hotel;
            }
        }
        return null;
    }

    private void updateSelectedAttractionsCount() {
        selectedAttractionsCount = 0;
        for (CheckBox cb : attractionCheckBoxes) {
            if (cb.isChecked() && cb.getVisibility() == View.VISIBLE) {
                selectedAttractionsCount++;
            }
        }
    }

    private void updateCostDisplay() {
        tvFlightCost.setText(formatCurrency(flightCost) + " VND");
        tvHotelCost.setText(formatCurrency(hotelCost) + " VND");
        tvAttractionsCost.setText(formatCurrency(attractionsCost) + " VND");
        long total = flightCost + hotelCost + attractionsCost;
        tvTotalCost.setText(formatCurrency(total) + " VND");
    }

    private void checkAllSelections() {
        boolean allSelected = rgFlightOptions.getCheckedRadioButtonId() != -1 &&
                rgHotelOptions.getCheckedRadioButtonId() != -1 &&
                selectedAttractionsCount > 0;
        btnPreview.setEnabled(allSelected);
        btnCreatePlan.setEnabled(allSelected);
        btnPreview.setAlpha(allSelected ? 1.0f : 0.5f);
        btnCreatePlan.setAlpha(allSelected ? 1.0f : 0.5f);
    }

    private int getIndexById(int[] idArray, int id) {
        for (int i = 0; i < idArray.length; i++) {
            if (idArray[i] == id) return i;
        }
        return -1;
    }

    private String getSelectedFlightAirline() {
        int idx = getIndexById(flightRadioIds, rgFlightOptions.getCheckedRadioButtonId());
        return (idx != -1) ? flightOptionAirlines[idx] : null;
    }

    private String getSelectedHotelName() {
        int idx = getIndexById(hotelRadioIds, rgHotelOptions.getCheckedRadioButtonId());
        return (idx != -1) ? hotelOptionNames[idx] : null;
    }

    private String[] getSelectedAttractionsArray() {
        List<String> selected = new ArrayList<>();
        for (CheckBox cb : attractionCheckBoxes) {
            if (cb.isChecked() && cb.getVisibility() == View.VISIBLE) {
                selected.add(cb.getText().toString());
            }
        }
        return selected.toArray(new String[0]);
    }

    private String formatCurrency(long v) {
        if (v == 0) return "0";
        try {
            return new DecimalFormat("#,###").format(v);
        } catch (Exception e) {
            return String.valueOf(v);
        }
    }

    private String formatDateTime(String rawDateTime) {
        if (rawDateTime == null || !rawDateTime.contains(" ") || !rawDateTime.contains("-")) {
            return "";
        }
        try {
            String[] parts = rawDateTime.split(" ");
            String datePart = parts[0];
            String timePart = parts[1];
            String[] dateParts = datePart.split("-");
            if (dateParts.length == 3) {
                String year = dateParts[0];
                String month = dateParts[1];
                String day = dateParts[2];
                String formattedDate = day + "/" + month + "/" + year;
                return timePart + " - " + formattedDate;
            }
        } catch (Exception e) {
            return rawDateTime;
        }
        return rawDateTime;
    }
}