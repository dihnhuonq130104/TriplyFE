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
import com.example.triply.data.remote.model.SaveTripRequest;
import com.example.triply.data.remote.model.SaveFullTripRequest;
import com.example.triply.data.repository.TripRepository;
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
//        btnBookNow = findViewById(R.id.btn_book_now);
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
            savePlanToDatabase();
        });

//        btnBookNow.setOnClickListener(v -> {
//            Toast.makeText(this, "Chức năng đang phát triển...", Toast.LENGTH_LONG).show();
//        });
    }

    private void savePlanToDatabase() {
        btnSavePlan.setEnabled(false);
        Toast.makeText(this, "Đang lưu kế hoạch...", Toast.LENGTH_SHORT).show();

        Intent intent = getIntent();
        String destination = intent.getStringExtra(PlanActivity.EXTRA_DETAIL_DESTINATION);
        String startDateRaw = intent.getStringExtra(PlanActivity.EXTRA_DETAIL_START_DATE);
        String durationText = intent.getStringExtra(PlanActivity.EXTRA_DETAIL_DURATION_TEXT);
        int peopleCount = intent.getIntExtra(PlanActivity.EXTRA_DETAIL_PEOPLE_COUNT, 1);
        long flightCost = intent.getLongExtra(PlanActivity.EXTRA_DETAIL_FLIGHT_COST, 0);
        long hotelCost = intent.getLongExtra(PlanActivity.EXTRA_DETAIL_HOTEL_COST, 0);
        long totalCost = intent.getLongExtra(PlanActivity.EXTRA_DETAIL_TOTAL_COST, 0);

        if (destination == null || destination.trim().isEmpty()) {
            btnSavePlan.setEnabled(true);
            Toast.makeText(this, "Lỗi: Không tìm thấy thông tin điểm đến", Toast.LENGTH_LONG).show();
            return;
        }

        String title = "Chuyến đi đến " + destination;
        String startDate = convertToIsoDate(startDateRaw);
        String endDate = addDays(startDate, nightCount);
        String notesValue = (durationText != null && !durationText.trim().isEmpty()) ? durationText : "Chuyến đi " + destination;

        // Build destinations
        List<SaveFullTripRequest.Destination> destinations = new ArrayList<>();
        destinations.add(new SaveFullTripRequest.Destination(destination, startDate, endDate, notesValue));

        // Build flight info
        SaveFullTripRequest.Flight flight = buildFlightInfo(intent);

        // Build hotel info
        SaveFullTripRequest.Hotel hotel = buildHotelInfo(intent);

        // Build daily attractions
        List<SaveFullTripRequest.DailyAttraction> dailyAttractions = buildDailyAttractions(intent, startDate);

        // Build expenses
        List<SaveFullTripRequest.Expense> expenses = new ArrayList<>();
        if (flightCost > 0) {
            expenses.add(new SaveFullTripRequest.Expense(flightCost, "Flight", startDate));
        }
        if (hotelCost > 0) {
            expenses.add(new SaveFullTripRequest.Expense(hotelCost, "Accommodation", startDate));
        }

        // Create the full request
        SaveFullTripRequest request = new SaveFullTripRequest(
            title,
            startDate,
            endDate,
            peopleCount,
            totalCost,
            "VND",
            destinations,
            flight,
            hotel,
            dailyAttractions,
            expenses
        );

        new TripRepository().saveFullTrip(request, new TripRepository.SaveTripCallback() {
            @Override
            public void onSuccess(int tripId) {
                runOnUiThread(() -> {
                    btnSavePlan.setEnabled(true);
                    savePlanLocally();
                    Toast.makeText(PlanDetailActivity.this, "Kế hoạch đã được lưu thành công! (ID: " + tripId + ")", Toast.LENGTH_LONG).show();

                    Intent homeIntent = new Intent(PlanDetailActivity.this, HomeActivity.class);
                    homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(homeIntent);
                    finish();
                });
            }

            @Override
            public void onError(String message) {
                runOnUiThread(() -> {
                    btnSavePlan.setEnabled(true);
                    Toast.makeText(PlanDetailActivity.this, message, Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    private SaveFullTripRequest.Flight buildFlightInfo(Intent intent) {
        String depAirport = intent.getStringExtra(PlanActivity.EXTRA_DETAIL_FLIGHT_DEP_AIRPORT);
        String arrAirport = intent.getStringExtra(PlanActivity.EXTRA_DETAIL_FLIGHT_ARR_AIRPORT);
        String airlineInfo = intent.getStringExtra(PlanActivity.EXTRA_DETAIL_FLIGHT_AIRLINE_INFO);
        String depTime = intent.getStringExtra(PlanActivity.EXTRA_DETAIL_FLIGHT_DEP_TIME);
        String arrTime = intent.getStringExtra(PlanActivity.EXTRA_DETAIL_FLIGHT_ARR_TIME);
        String durationStr = intent.getStringExtra(PlanActivity.EXTRA_DETAIL_FLIGHT_DURATION);
        long flightCost = intent.getLongExtra(PlanActivity.EXTRA_DETAIL_FLIGHT_COST, 0);

        if (depAirport == null || arrAirport == null) {
            return null;
        }

        // Extract airport codes (first 3 characters) - IATA codes
        String depId = extractAirportCode(depAirport);
        String arrId = extractAirportCode(arrAirport);

        // Extract airline name and code from airlineInfo (format: "Airline Name - FlightNumber")
        String airline = "";
        String airlineId = "";
        if (airlineInfo != null && airlineInfo.contains(" - ")) {
            airline = airlineInfo.split(" - ")[0].trim();
            // Try to extract airline code (first 2 letters of airline name as default)
            if (airline.length() >= 2) {
                airlineId = airline.substring(0, 2).toUpperCase();
            }
        }

        // Format duration (convert "120 phút" to "2h 0m")
        String flightDuration = formatDuration(durationStr);

        return new SaveFullTripRequest.Flight(
            depId,
            arrId,
            airline,
            airlineId,
            (int) flightCost,
            flightDuration,
            depTime,
            arrTime
        );
    }

    private SaveFullTripRequest.Hotel buildHotelInfo(Intent intent) {
        String hotelName = intent.getStringExtra(PlanActivity.EXTRA_DETAIL_HOTEL_NAME);
        long hotelCost = intent.getLongExtra(PlanActivity.EXTRA_DETAIL_HOTEL_COST, 0);

        if (hotelName == null || hotelName.isEmpty()) {
            return null;
        }

        // For now, we don't have the address and coordinates, so we'll set them to null
        // In a real implementation, you would want to get this data from the plan response
        return new SaveFullTripRequest.Hotel(
            hotelName,
            null,  // address
            null,  // latitude
            null,  // longitude
            (int) hotelCost
        );
    }

    private List<SaveFullTripRequest.DailyAttraction> buildDailyAttractions(Intent intent, String startDate) {
        String[] selectedAttractions = intent.getStringArrayExtra(PlanActivity.EXTRA_DETAIL_SELECTED_ATTRACTIONS);
        
        if (selectedAttractions == null || selectedAttractions.length == 0) {
            return new ArrayList<>();
        }

        List<SaveFullTripRequest.DailyAttraction> dailyAttractions = new ArrayList<>();
        
        for (String attraction : selectedAttractions) {
            // Parse attraction string (format: "09:00 | Location: Activity")
            String time = "09:00";
            String location = "";
            String activity = attraction;
            String reason = "";

            if (attraction.contains(" | ")) {
                String[] parts = attraction.split(" \\| ", 2);
                time = parts[0].trim();
                
                if (parts.length > 1 && parts[1].contains(": ")) {
                    String[] locationActivity = parts[1].split(": ", 2);
                    location = locationActivity[0].trim();
                    if (locationActivity.length > 1) {
                        activity = locationActivity[1].trim();
                    }
                }
            }

            dailyAttractions.add(new SaveFullTripRequest.DailyAttraction(
                startDate,
                time,
                activity,
                location,
                reason
            ));
        }

        return dailyAttractions;
    }

    private String extractAirportCode(String airportName) {
        if (airportName == null || airportName.isEmpty()) {
            return "";
        }
        // Try to extract code from parentheses like "Noi Bai International Airport (HAN)"
        if (airportName.contains("(") && airportName.contains(")")) {
            int start = airportName.indexOf("(") + 1;
            int end = airportName.indexOf(")");
            return airportName.substring(start, end).trim();
        }
        // Otherwise, return first 3 characters as fallback
        return airportName.length() >= 3 ? airportName.substring(0, 3).toUpperCase() : airportName.toUpperCase();
    }

    private String formatDuration(String durationStr) {
        if (durationStr == null || durationStr.isEmpty()) {
            return "";
        }
        // If already in correct format, return as is
        if (durationStr.contains("h") || durationStr.contains("m")) {
            return durationStr.replace(" phút", "m").replace("phút", "m");
        }
        // Convert "120 phút" to "2h 0m"
        try {
            String numStr = durationStr.replaceAll("[^0-9]", "");
            if (!numStr.isEmpty()) {
                int minutes = Integer.parseInt(numStr);
                int hours = minutes / 60;
                int mins = minutes % 60;
                return hours + "h " + mins + "m";
            }
        } catch (Exception e) {
            // If parsing fails, return original
        }
        return durationStr;
    }

    private void savePlanLocally() {
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

    private String convertToIsoDate(String dateStr) {
        if (dateStr == null || dateStr.isEmpty()) {
            return new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(new Date());
        }
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            Date date = inputFormat.parse(dateStr);
            return outputFormat.format(date);
        } catch (Exception e) {
            return new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(new Date());
        }
    }

    private String addDays(String isoDate, int days) {
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            Date date = format.parse(isoDate);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.add(Calendar.DAY_OF_MONTH, days);
            return format.format(calendar.getTime());
        } catch (Exception e) {
            return isoDate;
        }
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