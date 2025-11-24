package com.example.triply.data.repository;

import android.content.Context;

import com.example.triply.data.remote.ApiService;
import com.example.triply.data.remote.RetrofitClient;
import com.example.triply.data.remote.model.AuthResponse;
import com.example.triply.data.remote.model.LoginRequest;
import com.example.triply.data.remote.model.RegisterRequest;
import com.example.triply.data.remote.model.SocialLoginRequest;
import com.example.triply.util.TokenManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthRepository {
    private final ApiService apiService;
    private final TokenManager tokenManager;

    public interface AuthCallback {
        void onSuccess(AuthResponse response);
        void onError(String message);
    }

    public AuthRepository(Context context) {
        this.apiService = RetrofitClient.api();
        this.tokenManager = new TokenManager(context.getApplicationContext());
    }

    public void login(String userName, String password, AuthCallback callback) {
        apiService.login(new LoginRequest(userName, password)).enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    AuthResponse authResponse = response.body();
                    tokenManager.saveAccessToken(authResponse.getAccessToken());
                    tokenManager.saveUserInfo(authResponse.getFullName(), authResponse.getUserName(), authResponse.getEmail());
                    callback.onSuccess(authResponse);
                } else {
                    callback.onError("Login failed: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    public void register(RegisterRequest request, AuthCallback callback) {
        apiService.register(request).enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    AuthResponse authResponse = response.body();
                    tokenManager.saveAccessToken(authResponse.getAccessToken());
                    tokenManager.saveUserInfo(authResponse.getFullName(), authResponse.getUserName(), authResponse.getEmail());
                    callback.onSuccess(authResponse);
                } else {
                    callback.onError("Register failed: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    public void socialLogin(String provider, String idToken, AuthCallback callback) {
        apiService.socialLogin(new SocialLoginRequest(provider, idToken)).enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    AuthResponse authResponse = response.body();
                    tokenManager.saveAccessToken(authResponse.getAccessToken());
                    tokenManager.saveUserInfo(authResponse.getFullName(), authResponse.getUserName(), authResponse.getEmail());
                    callback.onSuccess(authResponse);
                } else {
                    callback.onError("Social login failed: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    public String getBearerToken() {
        String token = tokenManager.getAccessToken();
        return token == null ? null : "Bearer " + token;
    }
}


