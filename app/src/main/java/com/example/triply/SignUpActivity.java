package com.example.triply;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.triply.data.remote.model.RegisterRequest;
import com.example.triply.data.repository.AuthRepository;

public class SignUpActivity extends AppCompatActivity {

    private EditText emailEditText;
    private EditText passwordEditText;
    private EditText confirmPasswordEditText;
    private EditText fullNameEditText;
    private EditText phoneEditText;
    private EditText addressEditText;
    private EditText userNameEditText;
    private AuthRepository authRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signup);

        emailEditText = findViewById(R.id.et_email);
        passwordEditText = findViewById(R.id.et_password);
        confirmPasswordEditText = findViewById(R.id.et_confirm_password);
        fullNameEditText = findViewById(R.id.et_full_name);
        phoneEditText = findViewById(R.id.et_phone);
        addressEditText = findViewById(R.id.et_address);
        userNameEditText = findViewById(R.id.et_username);
        authRepository = new AuthRepository(this);

        Button createButton = findViewById(R.id.btn_signup);
        Button loginTab = findViewById(R.id.btn_toggle_login);
        Button signupTab = findViewById(R.id.btn_toggle_signup);

        createButton.setOnClickListener(v -> attemptSignup());
        loginTab.setOnClickListener(v -> {
            Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
        signupTab.setOnClickListener(v -> {
            // current screen
        });
    }

    private void attemptSignup() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString();
        String confirmPassword = confirmPasswordEditText.getText().toString();

        if (TextUtils.isEmpty(email)) {
            emailEditText.setError(getString(R.string.error_email_required));
            emailEditText.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.setError(getString(R.string.error_email_invalid));
            emailEditText.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            passwordEditText.setError(getString(R.string.error_password_required));
            passwordEditText.requestFocus();
            return;
        }
        if (password.length() < 6) {
            passwordEditText.setError(getString(R.string.error_password_short));
            passwordEditText.requestFocus();
            return;
        }
        if (!password.equals(confirmPassword)) {
            confirmPasswordEditText.setError(getString(R.string.error_password_mismatch));
            confirmPasswordEditText.requestFocus();
            return;
        }

        String fullName = fullNameEditText.getText().toString().trim();
        String phone = phoneEditText.getText().toString().trim();
        String address = addressEditText.getText().toString().trim();
        String userName = userNameEditText.getText().toString().trim();

        RegisterRequest request = new RegisterRequest(fullName, email, phone, address, userName, password);
        authRepository.register(request, new AuthRepository.AuthCallback() {
            @Override
            public void onSuccess(com.example.triply.data.remote.model.AuthResponse response) {
                Toast.makeText(SignUpActivity.this, getString(R.string.toast_signup_success), Toast.LENGTH_LONG).show();
                emailEditText.postDelayed(() -> {
                    startActivity(new Intent(SignUpActivity.this, HomeActivity.class));
                    finish();
                }, 5000);
            }

            @Override
            public void onError(String message) {
                Toast.makeText(SignUpActivity.this, getString(R.string.toast_signup_failed) + ": " + message, Toast.LENGTH_LONG).show();
            }
        });
    }
}