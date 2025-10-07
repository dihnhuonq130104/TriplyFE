package com.example.triply;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;

public class DestinationActivity extends AppCompatActivity {

    private ImageView imagePlace;
    private MaterialTextView textTitle, textLocation, textDescription;
    private MaterialButton btnDirections, btnAddToFavorite;
    private boolean isFavorite = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_destination);
        
        initViews();
        loadDestinationData();
        checkFavoriteStatus();
        setupClickListeners();
        setupBottomNavigation();
    }
    
    private void initViews() {
        imagePlace = findViewById(R.id.imagePlace);
        textTitle = findViewById(R.id.textTitle);
        textLocation = findViewById(R.id.textLocation);
        textDescription = findViewById(R.id.textDescription);
        btnDirections = findViewById(R.id.btnDirections);
        btnAddToFavorite = findViewById(R.id.btnAddToFavorite);
    }
    
    private void loadDestinationData() {
        // Sử dụng data mặc định cho Lăng Chủ Tịch
        textTitle.setText("Lăng Chủ Tịch Hồ Chí Minh");
        textLocation.setText("Ba Đình, Hà Nội");
        textDescription.setText("Lăng Chủ tịch Hồ Chí Minh là công trình trang nghiêm, linh thiêng, nơi yên nghỉ của Chủ tịch Hồ Chí Minh - vị lãnh tụ kính yêu của dân tộc Việt Nam. Đây là điểm đến không thể bỏ qua khi du lịch Hà Nội.");
        imagePlace.setImageResource(R.drawable.langbac);
    }
    
    private void checkFavoriteStatus() {
        SharedPreferences prefs = getSharedPreferences("Favorites", MODE_PRIVATE);
        String destinationTitle = textTitle.getText().toString();
        
        // Kiểm tra xem địa điểm này đã được yêu thích chưa
        // Duyệt qua tất cả keys để tìm title matching
        for (String key : prefs.getAll().keySet()) {
            if (key.endsWith("_title")) {
                String savedTitle = prefs.getString(key, "");
                if (destinationTitle.equals(savedTitle)) {
                    isFavorite = true;
                    break;
                }
            }
        }
        
        updateFavoriteButton();
    }
    
    private void updateFavoriteButton() {
        if (isFavorite) {
            btnAddToFavorite.setText("❤️"); // Trái tim full
        } else {
            btnAddToFavorite.setText("🤍"); // Trái tim empty
        }
    }
    
    private void setupClickListeners() {
        // Button Directions
        btnDirections.setOnClickListener(v -> {
            // Mở Google Maps để chỉ đường
            String location = textLocation.getText().toString();
            String query = textTitle.getText().toString() + ", " + location;
            
            // Tạo URI cho Google Maps
            Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + Uri.encode(query));
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");
            
            // Kiểm tra xem có Google Maps không
            if (mapIntent.resolveActivity(getPackageManager()) != null) {
                startActivity(mapIntent);
            } else {
                // Fallback - mở trong browser
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, 
                    Uri.parse("https://www.google.com/maps/search/" + Uri.encode(query)));
                startActivity(browserIntent);
            }
        });
        
        // Button Add to Favorite
        btnAddToFavorite.setOnClickListener(v -> {
            toggleFavorite();
        });
    }
    
    private void toggleFavorite() {
        SharedPreferences prefs = getSharedPreferences("Favorites", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        
        if (isFavorite) {
            // Remove from favorites
            removeFavorite(editor);
            isFavorite = false;
            Toast.makeText(this, "Đã bỏ " + textTitle.getText().toString() + " khỏi danh sách yêu thích!", 
                    Toast.LENGTH_SHORT).show();
        } else {
            // Add to favorites
            addToFavorites(editor);
            isFavorite = true;
            Toast.makeText(this, "Đã thêm " + textTitle.getText().toString() + " vào danh sách yêu thích!", 
                    Toast.LENGTH_SHORT).show();
        }
        
        updateFavoriteButton();
    }
    
    private void addToFavorites(SharedPreferences.Editor editor) {
        String favoriteKey = "favorite_" + System.currentTimeMillis();
        editor.putString(favoriteKey + "_title", textTitle.getText().toString());
        editor.putString(favoriteKey + "_location", textLocation.getText().toString());
        editor.putString(favoriteKey + "_description", textDescription.getText().toString());
        editor.putBoolean("has_favorites", true);
        editor.apply();
    }
    
    private void removeFavorite(SharedPreferences.Editor editor) {
        SharedPreferences prefs = getSharedPreferences("Favorites", MODE_PRIVATE);
        String destinationTitle = textTitle.getText().toString();
        
        // Tìm và xóa favorite entry
        for (String key : prefs.getAll().keySet()) {
            if (key.endsWith("_title")) {
                String savedTitle = prefs.getString(key, "");
                if (destinationTitle.equals(savedTitle)) {
                    String baseKey = key.replace("_title", "");
                    editor.remove(baseKey + "_title");
                    editor.remove(baseKey + "_location");
                    editor.remove(baseKey + "_description");
                    break;
                }
            }
        }
        
        // Check if still has favorites
        boolean stillHasFavorites = false;
        for (String key : prefs.getAll().keySet()) {
            if (key.endsWith("_title") && !key.contains(textTitle.getText().toString())) {
                stillHasFavorites = true;
                break;
            }
        }
        editor.putBoolean("has_favorites", stillHasFavorites);
        editor.apply();
    }
    
    private void addToFavorite() {
        // Lưu vào danh sách yêu thích
        SharedPreferences prefs = getSharedPreferences("Favorites", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        
        // Lưu thông tin địa điểm (có thể mở rộng thành JSON array sau)
        String favoriteKey = "favorite_" + System.currentTimeMillis();
        editor.putString(favoriteKey + "_title", textTitle.getText().toString());
        editor.putString(favoriteKey + "_location", textLocation.getText().toString());
        editor.putString(favoriteKey + "_description", textDescription.getText().toString());
        editor.putBoolean("has_favorites", true);
        editor.apply();
        
        // Hiển thị thông báo
        Toast.makeText(this, "Đã thêm " + textTitle.getText().toString() + " vào danh sách yêu thích!", 
                Toast.LENGTH_SHORT).show();
        
        // Chuyển đến FavoriteActivity để xem danh sách
        Intent intent = new Intent(this, FavoriteActivity.class);
        startActivity(intent);
    }
    
    private void addToSchedule() {
        // Hiển thị thông báo thành công
        Toast.makeText(this, "Đã thêm " + textTitle.getText().toString() + " vào lịch trình!", 
                Toast.LENGTH_SHORT).show();
        
        // Kiểm tra đã có lịch trình chưa
        SharedPreferences prefs = getSharedPreferences("TripSchedule", MODE_PRIVATE);
        boolean hasSchedule = prefs.getBoolean("has_schedule", false);
        
        if (hasSchedule) {
            // Đã có lịch trình → Mở PlanActivity với dữ liệu đã lưu
            Intent intent = new Intent(this, PlanActivity.class);
            intent.putExtra("departure", prefs.getString("departure", ""));
            intent.putExtra("direction", prefs.getString("direction", ""));
            intent.putExtra("numbers_person", prefs.getString("numbers_person", ""));
            intent.putExtra("budget", prefs.getString("budget", ""));
            intent.putExtra("durations", prefs.getString("durations", ""));
            intent.putExtra("start_date", prefs.getString("start_date", ""));
            intent.putExtra("hotel_type", prefs.getString("hotel_type", ""));
            intent.putExtra("added_destination", textTitle.getText().toString());
            startActivity(intent);
        } else {
            // Chưa có lịch trình → Mở ScheduleActivity để tạo mới
            Intent intent = new Intent(this, ScheduleActivity.class);
            intent.putExtra("added_destination", textTitle.getText().toString());
            startActivity(intent);
        }
    }
    
    private void setupBottomNavigation() {
        ImageView navHome = findViewById(R.id.nav_home);
        ImageView navPlan = findViewById(R.id.nav_plan);
        ImageView navFavorite = findViewById(R.id.nav_favorite);
        ImageView navProfile = findViewById(R.id.nav_profile);
        
        // Set all icons as unselected since destination is a detail screen
        navHome.setSelected(false);
        navPlan.setSelected(false);
        navFavorite.setSelected(false);
        navProfile.setSelected(false);
        
        // Setup click listeners for navigation
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
        
        navFavorite.setOnClickListener(v -> {
            Intent intent = new Intent(DestinationActivity.this, FavoriteActivity.class);
            startActivity(intent);
            finish();
        });
        
        navProfile.setOnClickListener(v -> {
            Intent intent = new Intent(DestinationActivity.this, ProfileActivity.class);
            startActivity(intent);
            finish();
        });
    }
    
    // Static method để launch activity với data
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