package com.example.triply;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.triply.data.remote.ApiService;
import com.example.triply.data.remote.RetrofitClient;
import com.example.triply.data.remote.model.UserProfile;
import com.example.triply.util.TokenManager;
import com.google.android.material.textview.MaterialTextView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends AppCompatActivity {
    private static final String TAG = "ProfileActivity";
    
    private MaterialTextView profileName;
    private MaterialTextView profileEmail;
    private MaterialTextView profilePhone;
    private MaterialTextView profileUsername;
    private MaterialTextView profileAddress;
    private TokenManager tokenManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        
        tokenManager = new TokenManager(this);
        
        initViews();
        loadUserProfile();
        setupMenuClicks();
        setupBottomNavigation();
    }
    
    private void initViews() {
        profileName = findViewById(R.id.profile_name);
        profileEmail = findViewById(R.id.profile_email);
        profilePhone = findViewById(R.id.profile_phone);
        profileUsername = findViewById(R.id.profile_username);
        profileAddress = findViewById(R.id.profile_address);
    }
    
    private void loadUserProfile() {
        String token = tokenManager.getAccessToken();
        if (token == null) {
            Toast.makeText(this, "Token not found", Toast.LENGTH_SHORT).show();
            return;
        }
        
        ApiService apiService = RetrofitClient.getInstance().create(ApiService.class);
        Call<UserProfile> call = apiService.me("Bearer " + token);
        
        call.enqueue(new Callback<UserProfile>() {
            @Override
            public void onResponse(Call<UserProfile> call, Response<UserProfile> response) {
                if (response.isSuccessful() && response.body() != null) {
                    UserProfile profile = response.body();
                    updateUI(profile);
                } else {
                    Log.e(TAG, "Failed to load profile: " + response.code());
                    Toast.makeText(ProfileActivity.this, "Failed to load profile", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UserProfile> call, Throwable t) {
                Log.e(TAG, "Error loading profile", t);
                Toast.makeText(ProfileActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    private void updateUI(UserProfile profile) {
        if (profile.getFullName() != null && !profile.getFullName().isEmpty()) {
            profileName.setText(profile.getFullName());
        } else {
            profileName.setText("No name");
        }
        
        if (profile.getEmail() != null && !profile.getEmail().isEmpty()) {
            profileEmail.setText("Email: " + profile.getEmail());
            profileEmail.setVisibility(View.VISIBLE);
        } else {
            profileEmail.setVisibility(View.GONE);
        }
        
        if (profile.getPhone() != null && !profile.getPhone().isEmpty()) {
            profilePhone.setText("Phone: " + profile.getPhone());
            profilePhone.setVisibility(View.VISIBLE);
        } else {
            profilePhone.setVisibility(View.GONE);
        }
        
        if (profile.getUserName() != null && !profile.getUserName().isEmpty()) {
            profileUsername.setText("@" + profile.getUserName());
            profileUsername.setVisibility(View.VISIBLE);
        } else {
            profileUsername.setVisibility(View.GONE);
        }
        
        if (profile.getAddress() != null && !profile.getAddress().isEmpty()) {
            profileAddress.setText("Address: " + profile.getAddress());
            profileAddress.setVisibility(View.VISIBLE);
        } else {
            profileAddress.setVisibility(View.GONE);
        }
    }
    
    private void setupMenuClicks() {
        LinearLayout menuSavedPlans = findViewById(R.id.menu_saved_plans);
        LinearLayout menuLogout = findViewById(R.id.menu_logout);
        
        menuSavedPlans.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, SavedPlansActivity.class);
            startActivity(intent);
        });
        
        menuLogout.setOnClickListener(v -> {
            tokenManager.clear();
            
            Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }
    
    private void setupBottomNavigation() {
        ImageView navHome = findViewById(R.id.nav_home);
        ImageView navPlan = findViewById(R.id.nav_plan);
        ImageView navChatbot = findViewById(R.id.nav_chatbot);
        ImageView navProfile = findViewById(R.id.nav_profile);

        navHome.setSelected(false);
        navPlan.setSelected(false);
        navChatbot.setSelected(false);
        navProfile.setSelected(true);

        navHome.setOnClickListener(v -> {
            finish();
        });

        navPlan.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, ScheduleActivity.class);
            startActivity(intent);
        });

        navChatbot.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, ChatbotActivity.class);
            startActivity(intent);
        });

        navProfile.setOnClickListener(v -> {
        });
    }
}