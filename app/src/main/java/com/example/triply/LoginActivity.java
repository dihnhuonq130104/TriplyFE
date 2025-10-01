package com.example.triply;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.triply.data.repository.AuthRepository;
import androidx.core.content.ContextCompat;
import android.os.CancellationSignal;
import android.util.Log;
import androidx.credentials.Credential;
import androidx.credentials.CredentialManager;
import androidx.credentials.CredentialManagerCallback;
import androidx.credentials.CustomCredential;
import androidx.credentials.GetCredentialRequest;
import androidx.credentials.GetCredentialResponse;
import androidx.credentials.exceptions.GetCredentialException;
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption;
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential;
// removed GoogleIdTokenParsingException catch; using generic Exception

public class LoginActivity extends AppCompatActivity {

    private EditText emailEditText;
    private EditText passwordEditText;
    private AuthRepository authRepository;
    private CredentialManager credentialManager;
    private static final String TAG = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        emailEditText = findViewById(R.id.et_email);
        passwordEditText = findViewById(R.id.et_password);
        authRepository = new AuthRepository(this);
        credentialManager = CredentialManager.create(this);

        Button loginButton = findViewById(R.id.btn_login);
        Button signupTab = findViewById(R.id.btn_toggle_signup);
        Button loginTab = findViewById(R.id.btn_toggle_login);
        ImageView googleBtn = findViewById(R.id.iv_google);

        loginButton.setOnClickListener(v -> attemptLogin());
        signupTab.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
            startActivity(intent);
        });
        loginTab.setOnClickListener(v -> {
            // current screen
        });

        googleBtn.setOnClickListener(v -> {
            GetSignInWithGoogleOption signInWithGoogleOption = new GetSignInWithGoogleOption.Builder(getString(R.string.default_web_client_id))
                    .build();

            GetCredentialRequest request = new GetCredentialRequest.Builder()
                    .addCredentialOption(signInWithGoogleOption)
                    .build();

            credentialManager.getCredentialAsync(
                    this,
                    request,
                    new CancellationSignal(),
                    ContextCompat.getMainExecutor(this),
                    new CredentialManagerCallback<GetCredentialResponse, GetCredentialException>() {
                        @Override
                        public void onResult(GetCredentialResponse result) {
                            handleSignInWithGoogleOption(result);
                        }

                        @Override
                        public void onError(GetCredentialException e) {
                            Toast.makeText(LoginActivity.this, getString(R.string.toast_login_failed) + ": " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
            );
        });
    }

    private void attemptLogin() {
        String identifier = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString();

        if (TextUtils.isEmpty(identifier)) {
            emailEditText.setError(getString(R.string.error_identifier_required));
            emailEditText.requestFocus();
            return;
        }
        if (identifier.contains("@") && !Patterns.EMAIL_ADDRESS.matcher(identifier).matches()) {
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

        authRepository.login(identifier, password, new AuthRepository.AuthCallback() {
            @Override
            public void onSuccess(com.example.triply.data.remote.model.AuthResponse response) {
                Toast toast = Toast.makeText(LoginActivity.this, getString(R.string.toast_login_success), Toast.LENGTH_LONG);
                toast.show();
                emailEditText.postDelayed(() -> {
                    Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                    startActivity(intent);
                    finish();
                }, 5000);
            }

            @Override
            public void onError(String message) {
                Toast toast = Toast.makeText(LoginActivity.this, getString(R.string.toast_login_failed) + ": " + message, Toast.LENGTH_LONG);
                toast.show();
            }
        });
    }

    private void handleSignInWithGoogleOption(GetCredentialResponse result) {
        Credential credential = result.getCredential();
        if (credential instanceof CustomCredential) {
            CustomCredential customCredential = (CustomCredential) credential;
            if (GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL.equals(customCredential.getType())) {
                try {
                    GoogleIdTokenCredential googleIdTokenCredential = GoogleIdTokenCredential.createFrom(customCredential.getData());
                    String idToken = googleIdTokenCredential.getIdToken();
                    authRepository.socialLogin("GOOGLE", idToken, new AuthRepository.AuthCallback() {
                        @Override
                        public void onSuccess(com.example.triply.data.remote.model.AuthResponse response) {
                            Toast.makeText(LoginActivity.this, getString(R.string.toast_login_success), Toast.LENGTH_LONG).show();
                            emailEditText.postDelayed(() -> {
                                startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                                finish();
                            }, 5000);
                        }

                        @Override
                        public void onError(String message) {
                            Toast.makeText(LoginActivity.this, getString(R.string.toast_login_failed) + ": " + message, Toast.LENGTH_LONG).show();
                        }
                    });
                } catch (Exception e) {
                    Log.e(TAG, "Received an invalid google id token response", e);
                    Toast.makeText(LoginActivity.this, getString(R.string.toast_login_failed), Toast.LENGTH_LONG).show();
                }
            } else {
                Log.e(TAG, "Unexpected type of credential");
                Toast.makeText(LoginActivity.this, getString(R.string.toast_login_failed), Toast.LENGTH_LONG).show();
            }
        } else {
            Log.e(TAG, "Unexpected type of credential");
            Toast.makeText(LoginActivity.this, getString(R.string.toast_login_failed), Toast.LENGTH_LONG).show();
        }
    }
}