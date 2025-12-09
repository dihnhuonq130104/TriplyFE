package com.example.triply;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;

public class DestinationActivity extends AppCompatActivity {

    private ImageView imagePlace;
    private MaterialTextView textTitle, textLocation, textDescription;
    private MaterialButton btnDirections;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_destination);
        
        initViews();
        loadDestinationData();
        setupBottomNavigation();
    }
    
    private void initViews() {
        imagePlace = findViewById(R.id.imagePlace);
        textTitle = findViewById(R.id.textTitle);
        textLocation = findViewById(R.id.textLocation);
        textDescription = findViewById(R.id.textDescription);
        btnDirections = findViewById(R.id.btnDirections);
    }
    
    private void loadDestinationData() {
        Intent intent = getIntent();
        int destinationId = intent.getIntExtra("destination_id", -1);
        String destinationName = intent.getStringExtra("destination_name");
        String destinationAddress = intent.getStringExtra("destination_address");
        String destinationDescription = intent.getStringExtra("destination_description");
        String imgPath = intent.getStringExtra("destination_img_path");
        final String googleMapUrl = intent.getStringExtra("destination_google_map_url");
        final String placeId = intent.getStringExtra("place_id");
        final double latitude = intent.getDoubleExtra("latitude", 0.0);
        final double longitude = intent.getDoubleExtra("longitude", 0.0);
        
        if (destinationName != null) {
            textTitle.setText(destinationName);
        } else {
            textTitle.setText("Lăng Chủ Tịch Hồ Chí Minh");
        }
        
        if (destinationAddress != null) {
            textLocation.setText(destinationAddress);
        } else {
            textLocation.setText("Ba Đình, Hà Nội");
        }
        
        if (destinationDescription != null) {
            textDescription.setText(destinationDescription);
        } else {
            textDescription.setText("No description for dipslay");
        }
        
        if (imgPath != null && !imgPath.isEmpty()) {
            Glide.with(this)
                .load(imgPath)
                .placeholder(R.drawable.langbac)
                .error(R.drawable.langbac)
                .into(imagePlace);
        } else {
            imagePlace.setImageResource(R.drawable.langbac);
        }
        
        // Open MapActivity with destination details
        btnDirections.setOnClickListener(v -> {
            Intent mapIntent = new Intent(DestinationActivity.this, MapActivity.class);
            mapIntent.putExtra("destination_name", destinationName);
            mapIntent.putExtra("destination_address", destinationAddress);
            if (latitude != 0.0 && longitude != 0.0) {
                mapIntent.putExtra("latitude", latitude);
                mapIntent.putExtra("longitude", longitude);
            } else if (placeId != null && !placeId.isEmpty()) {
                mapIntent.putExtra("place_id", placeId);
            }
            startActivity(mapIntent);
        });
    }
    
    
    
    private void addToSchedule() {
        Toast.makeText(this, "Đã thêm " + textTitle.getText().toString() + " vào lịch trình!",
                Toast.LENGTH_SHORT).show();
        
        SharedPreferences prefs = getSharedPreferences("TripSchedule", MODE_PRIVATE);
        boolean hasSchedule = prefs.getBoolean("has_schedule", false);
        
        if (hasSchedule) {
            Intent intent = new Intent(this, PlanActivity.class);
            intent.putExtra("departure", prefs.getString("departure", ""));
            intent.putExtra("direction", prefs.getString("direction", ""));
            intent.putExtra("numbers_person", prefs.getString("numbers_person", ""));
            intent.putExtra("budget", prefs.getString("budget", ""));
            intent.putExtra("durations", prefs.getString("durations", ""));
            intent.putExtra("start_date", prefs.getString("start_date", ""));
            intent.putExtra("added_destination", textTitle.getText().toString());
            startActivity(intent);
        } else {
            Intent intent = new Intent(this, ScheduleActivity.class);
            intent.putExtra("added_destination", textTitle.getText().toString());
            startActivity(intent);
        }
    }
    
    private void setupBottomNavigation() {
        ImageView navHome = findViewById(R.id.nav_home);
        ImageView navPlan = findViewById(R.id.nav_plan);
        ImageView navChatbot = findViewById(R.id.nav_chatbot);
        ImageView navProfile = findViewById(R.id.nav_profile);

        navHome.setSelected(false);
        navPlan.setSelected(false);
        navChatbot.setSelected(false);
        navProfile.setSelected(false);

        navHome.setOnClickListener(v -> {
            Intent intent = new Intent(DestinationActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
        });

        navPlan.setOnClickListener(v -> {
            Intent intent = new Intent(DestinationActivity.this, ScheduleActivity.class);
            startActivity(intent);
            finish();
        });

        navChatbot.setOnClickListener(v -> {
            Intent intent = new Intent(DestinationActivity.this, ChatbotActivity.class);
            startActivity(intent);
            finish();
        });

        navProfile.setOnClickListener(v -> {
            Intent intent = new Intent(DestinationActivity.this, ProfileActivity.class);
            startActivity(intent);
            finish();
        });
    }
    public static void launch(AppCompatActivity context, String title, String location,
                             String description, int imageResId) {
        Intent intent = new Intent(context, DestinationActivity.class);
        intent.putExtra("destination_title", title);
        intent.putExtra("destination_location", location);
        intent.putExtra("destination_description", description);
        intent.putExtra("destination_image", imageResId);
        context.startActivity(intent);
    }
}