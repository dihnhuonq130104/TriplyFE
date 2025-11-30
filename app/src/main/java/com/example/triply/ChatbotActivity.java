package com.example.triply;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputEditText;

public class ChatbotActivity extends AppCompatActivity {

    private LinearLayout messagesContainer;
    private ScrollView scrollView;
    private TextInputEditText etMessage;
    private ImageButton btnSend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatbot);

        messagesContainer = findViewById(R.id.messages_container);
        scrollView = findViewById(R.id.messages_scroll);
        etMessage = findViewById(R.id.et_message);
        btnSend = findViewById(R.id.btn_send);

        setupBottomNavigation();

        btnSend.setOnClickListener(v -> {
            String text = etMessage.getText() == null ? "" : etMessage.getText().toString().trim();
            if (TextUtils.isEmpty(text)) return;
            addMessage(text, true);
            etMessage.setText("");

            // Simple echo bot reply for UI demonstration
            messagesContainer.postDelayed(() -> addMessage("Bot: Tôi đã nhận: " + text, false), 600);
        });
    }

    private void addMessage(String text, boolean isUser) {
        TextView tv = new TextView(this);
        tv.setText(text);
        tv.setTextSize(16);
        tv.setPadding(28, 18, 28, 18);
        tv.setMaxWidth((int) (getResources().getDisplayMetrics().widthPixels * 0.75));

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        params.topMargin = 12;

        if (isUser) {
            params.gravity = Gravity.END;
            tv.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_light));
            tv.setTextColor(getResources().getColor(android.R.color.white));
        } else {
            params.gravity = Gravity.START;
            tv.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
            tv.setTextColor(getResources().getColor(android.R.color.black));
        }

        tv.setLayoutParams(params);
        messagesContainer.addView(tv);

        scrollView.post(() -> scrollView.fullScroll(View.FOCUS_DOWN));
    }

    private void setupBottomNavigation() {
        ImageView navHome = findViewById(R.id.nav_home);
        ImageView navPlan = findViewById(R.id.nav_plan);
        ImageView navChatbot = findViewById(R.id.nav_chatbot);
        ImageView navProfile = findViewById(R.id.nav_profile);

        navHome.setSelected(false);
        navPlan.setSelected(false);
        navChatbot.setSelected(true);
        navProfile.setSelected(false);

        navHome.setOnClickListener(v -> {
            finish();
        });

        navPlan.setOnClickListener(v -> {
            // open schedule
        });

        navChatbot.setOnClickListener(v -> {
            // already here
        });

        navProfile.setOnClickListener(v -> {
            // open profile
        });
    }
}
