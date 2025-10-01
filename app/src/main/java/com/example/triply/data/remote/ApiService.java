package com.example.triply.data.remote;

import com.example.triply.data.remote.model.AuthResponse;
import com.example.triply.data.remote.model.LoginRequest;
import com.example.triply.data.remote.model.RegisterRequest;
import com.example.triply.data.remote.model.SocialLoginRequest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface ApiService {
    @POST("/api/v1/auth/login")
    Call<AuthResponse> login(@Body LoginRequest request);

    @POST("/api/v1/auth/register")
    Call<AuthResponse> register(@Body RegisterRequest request);

    @POST("/api/v1/auth/social-login")
    Call<AuthResponse> socialLogin(@Body SocialLoginRequest request);

    @GET("/api/v1/auth/me")
    Call<AuthResponse> me(@Header("Authorization") String bearerToken);
}


