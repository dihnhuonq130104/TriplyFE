package com.example.triply;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.triply.data.remote.model.ChatMessage;
import com.example.triply.data.remote.model.ChatThread;
import com.example.triply.data.remote.model.SendMessageResponse;
import com.example.triply.data.repository.ChatRepository;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ChatbotActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "ChatbotPrefs";
    private static final String KEY_THREAD_ID = "current_thread_id";

    private LinearLayout messagesContainer;
    private ScrollView scrollView;
    private TextInputEditText etMessage;
    private ImageButton btnSend;
    private ProgressBar progressBar;

    private ChatRepository chatRepository;
    private Integer currentThreadId;
    private boolean isWaitingForResponse = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatbot);

        messagesContainer = findViewById(R.id.messages_container);
        scrollView = findViewById(R.id.messages_scroll);
        etMessage = findViewById(R.id.et_message);
        btnSend = findViewById(R.id.btn_send);
        progressBar = findViewById(R.id.progress_bar_chat);

        chatRepository = new ChatRepository();

        messagesContainer.removeAllViews();

        loadOrCreateThread();
        setupBottomNavigation();

        btnSend.setOnClickListener(v -> {
            String text = etMessage.getText() == null ? "" : etMessage.getText().toString().trim();
            if (TextUtils.isEmpty(text)) return;
            if (isWaitingForResponse) {
                Toast.makeText(this, "Vui lòng đợi phản hồi trước", Toast.LENGTH_SHORT).show();
                return;
            }
            if (currentThreadId == null) {
                Toast.makeText(this, "Đang khởi tạo chat...", Toast.LENGTH_SHORT).show();
                return;
            }

            sendMessage(text);
        });
    }

    private void loadOrCreateThread() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        int savedThreadId = prefs.getInt(KEY_THREAD_ID, -1);

        if (savedThreadId != -1) {
            currentThreadId = savedThreadId;
            loadChatHistory();
        } else {
            createNewThread();
        }
    }

    private void createNewThread() {
        progressBar.setVisibility(View.VISIBLE);
        String threadTitle = "Chat " + new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(new Date());

        chatRepository.createThread(threadTitle, new ChatRepository.CreateThreadCallback() {
            @Override
            public void onSuccess(ChatThread thread) {
                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    currentThreadId = thread.getThreadId();
                    saveThreadId(currentThreadId);
                    addMessage("Xin chào! Tôi là trợ lý du lịch AI. Tôi có thể giúp gì cho bạn?", false);
                });
            }

            @Override
            public void onError(String message) {
                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(ChatbotActivity.this, "Lỗi khởi tạo chat: " + message, Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void loadChatHistory() {
        if (currentThreadId == null) return;

        progressBar.setVisibility(View.VISIBLE);
        chatRepository.getMessages(currentThreadId, new ChatRepository.GetMessagesCallback() {
            @Override
            public void onSuccess(List<ChatMessage> messages) {
                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    for (ChatMessage msg : messages) {
                        boolean isUser = "user".equals(msg.getRole());
                        addMessage(msg.getContent(), isUser);
                    }
                    if (messages.isEmpty()) {
                        addMessage("Xin chào! Tôi là trợ lý du lịch AI. Tôi có thể giúp gì cho bạn?", false);
                    }
                });
            }

            @Override
            public void onError(String message) {
                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    addMessage("Xin chào! Tôi là trợ lý du lịch AI. Tôi có thể giúp gì cho bạn?", false);
                });
            }
        });
    }

    private void sendMessage(String text) {
        addMessage(text, true);
        etMessage.setText("");
        isWaitingForResponse = true;
        btnSend.setEnabled(false);

        addTypingIndicator();

        chatRepository.sendMessage(currentThreadId, text, new ChatRepository.SendMessageCallback() {
            @Override
            public void onSuccess(SendMessageResponse response) {
                runOnUiThread(() -> {
                    removeTypingIndicator();
                    String botReply = response.getAssistantMessage();
                    if (botReply != null && !botReply.isEmpty()) {
                        addMessage(botReply, false);
                    } else {
                        addMessage("Xin lỗi, tôi không thể trả lời lúc này.", false);
                    }
                    isWaitingForResponse = false;
                    btnSend.setEnabled(true);
                });
            }

            @Override
            public void onError(String message) {
                runOnUiThread(() -> {
                    removeTypingIndicator();
                    addMessage("Xin lỗi, có lỗi xảy ra: " + message, false);
                    isWaitingForResponse = false;
                    btnSend.setEnabled(true);
                });
            }
        });
    }

    private void saveThreadId(int threadId) {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        prefs.edit().putInt(KEY_THREAD_ID, threadId).apply();
    }

    private TextView typingIndicator;

    private void addTypingIndicator() {
        typingIndicator = new TextView(this);
        typingIndicator.setText("Đang trả lời...");
        typingIndicator.setTextSize(14);
        typingIndicator.setPadding(28, 18, 28, 18);
        typingIndicator.setTextColor(getResources().getColor(android.R.color.darker_gray));

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        params.topMargin = 12;
        params.gravity = Gravity.START;

        typingIndicator.setLayoutParams(params);
        messagesContainer.addView(typingIndicator);
        scrollView.post(() -> scrollView.fullScroll(View.FOCUS_DOWN));
    }

    private void removeTypingIndicator() {
        if (typingIndicator != null && typingIndicator.getParent() != null) {
            messagesContainer.removeView(typingIndicator);
            typingIndicator = null;
        }
    }

    private void addMessage(String text, boolean isUser) {
        TextView tv = new TextView(this);
        tv.setText(text);
        tv.setTextSize(15);
        tv.setPadding(32, 20, 32, 20);
        tv.setMaxWidth((int) (getResources().getDisplayMetrics().widthPixels * 0.75));

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        params.topMargin = 16;
        params.bottomMargin = 4;

        if (isUser) {
            params.gravity = Gravity.END;
            params.setMargins(48, 16, 16, 4);
            tv.setBackgroundResource(R.drawable.bg_message_user);
            tv.setTextColor(getResources().getColor(android.R.color.white));
        } else {
            params.gravity = Gravity.START;
            params.setMargins(16, 16, 48, 4);
            tv.setBackgroundResource(R.drawable.bg_message_bot);
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
            Intent intent = new Intent(ChatbotActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
        });

        navPlan.setOnClickListener(v -> {
            Intent intent = new Intent(ChatbotActivity.this, ScheduleActivity.class);
            startActivity(intent);
            finish();
        });

        navChatbot.setOnClickListener(v -> {
            // already here
        });

        navProfile.setOnClickListener(v -> {
            Intent intent = new Intent(ChatbotActivity.this, ProfileActivity.class);
            startActivity(intent);
            finish();
        });
    }
}
