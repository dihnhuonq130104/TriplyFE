package com.example.triply;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.android.material.textview.MaterialTextView;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

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
        SharedPreferences prefs = getSharedPreferences("SavedPlansList", MODE_PRIVATE);
        Gson gson = new Gson();

        String json = prefs.getString("plans_json", null);
        Type type = new TypeToken<ArrayList<PlanDetailActivity.SavedPlanData>>() {}.getType();
        List<PlanDetailActivity.SavedPlanData> plansList = gson.fromJson(json, type);

        if (plansList != null && !plansList.isEmpty()) {
            tvNoPlans.setVisibility(View.GONE);
            plansContainer.setVisibility(View.VISIBLE);
            plansContainer.removeAllViews();

            for (PlanDetailActivity.SavedPlanData planData : plansList) {
                createPlanCard(planData);
            }
        } else {
            tvNoPlans.setVisibility(View.VISIBLE);
            plansContainer.setVisibility(View.GONE);
        }
    }

    private void createPlanCard(final PlanDetailActivity.SavedPlanData planData) {
        CardView cardView = new CardView(this);
        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        cardParams.setMargins(0, 0, 0, 32);
        cardView.setLayoutParams(cardParams);
        cardView.setCardElevation(8);
        cardView.setRadius(16);
        cardView.setUseCompatPadding(true);

        LinearLayout innerLayout = new LinearLayout(this);
        innerLayout.setOrientation(LinearLayout.VERTICAL);
        innerLayout.setPadding(32, 32, 32, 32);

        MaterialTextView tvTitle = new MaterialTextView(this);
        tvTitle.setText("Chuyến đi đến " + planData.destination);
        tvTitle.setTextSize(18);
        tvTitle.setTextColor(getResources().getColor(android.R.color.black));
        tvTitle.setTypeface(null, android.graphics.Typeface.BOLD);

        MaterialTextView tvInfo = new MaterialTextView(this);
        tvInfo.setText("📅 " + planData.startDate + "\n⏰ " + planData.durationText + "\n👥 " + planData.peopleCount + " người");
        tvInfo.setTextSize(14);
        tvInfo.setTextColor(getResources().getColor(android.R.color.darker_gray));
        tvInfo.setPadding(0, 16, 0, 0);

        innerLayout.addView(tvTitle);
        innerLayout.addView(tvInfo);
        cardView.addView(innerLayout);

        cardView.setOnClickListener(v -> {
            Intent intent = new Intent(SavedPlansActivity.this, PlanDetailActivity.class);

            intent.putExtra("IS_VIEWING_SAVED_PLAN", true);

            intent.putExtra(PlanActivity.EXTRA_DETAIL_DESTINATION, planData.destination);
            intent.putExtra(PlanActivity.EXTRA_DETAIL_DURATION_TEXT, planData.durationText);
            intent.putExtra(PlanActivity.EXTRA_DETAIL_PEOPLE_COUNT, planData.peopleCount);
            intent.putExtra(PlanActivity.EXTRA_DETAIL_NIGHT_COUNT, planData.nightCount);
            intent.putExtra(PlanActivity.EXTRA_DETAIL_START_DATE, planData.startDate);
            intent.putExtra(PlanActivity.EXTRA_DETAIL_SELECTED_ATTRACTIONS, planData.selectedAttractions);
            intent.putExtra(PlanActivity.EXTRA_DETAIL_FLIGHT_DEP_AIRPORT, planData.flightDepAirport);
            intent.putExtra(PlanActivity.EXTRA_DETAIL_FLIGHT_DEP_TIME, planData.flightDepTime);
            intent.putExtra(PlanActivity.EXTRA_DETAIL_FLIGHT_ARR_AIRPORT, planData.flightArrAirport);
            intent.putExtra(PlanActivity.EXTRA_DETAIL_FLIGHT_ARR_TIME, planData.flightArrTime);
            intent.putExtra(PlanActivity.EXTRA_DETAIL_FLIGHT_AIRLINE_INFO, planData.flightAirlineInfo);
            intent.putExtra(PlanActivity.EXTRA_DETAIL_FLIGHT_DURATION, planData.flightDuration);
            intent.putExtra(PlanActivity.EXTRA_DETAIL_FLIGHT_RETURN_DEP_AIRPORT, planData.flightReturnDepAirport);
            intent.putExtra(PlanActivity.EXTRA_DETAIL_FLIGHT_RETURN_DEP_TIME, planData.flightReturnDepTime);
            intent.putExtra(PlanActivity.EXTRA_DETAIL_FLIGHT_RETURN_ARR_AIRPORT, planData.flightReturnArrAirport);
            intent.putExtra(PlanActivity.EXTRA_DETAIL_FLIGHT_RETURN_ARR_TIME, planData.flightReturnArrTime);
            intent.putExtra(PlanActivity.EXTRA_DETAIL_FLIGHT_RETURN_AIRLINE_INFO, planData.flightReturnAirlineInfo);
            intent.putExtra(PlanActivity.EXTRA_DETAIL_FLIGHT_RETURN_DURATION, planData.flightReturnDuration);
            intent.putExtra(PlanActivity.EXTRA_DETAIL_HOTEL_NAME, planData.hotelName);
            intent.putExtra(PlanActivity.EXTRA_DETAIL_HOTEL_STARS, planData.hotelStars);
            intent.putExtra(PlanActivity.EXTRA_DETAIL_HOTEL_CHECKIN, planData.hotelCheckin);
            intent.putExtra(PlanActivity.EXTRA_DETAIL_HOTEL_CHECKOUT, planData.hotelCheckout);
            intent.putExtra(PlanActivity.EXTRA_DETAIL_FLIGHT_COST, planData.flightCost);
            intent.putExtra(PlanActivity.EXTRA_DETAIL_HOTEL_COST, planData.hotelCost);
            intent.putExtra(PlanActivity.EXTRA_DETAIL_TOTAL_COST, planData.totalCost);
            intent.putExtra(PlanActivity.EXTRA_DETAIL_FLIGHT_AIRLINE_NAME, planData.flightAirlineName);

            startActivity(intent);
        });

        plansContainer.addView(cardView);
    }
}