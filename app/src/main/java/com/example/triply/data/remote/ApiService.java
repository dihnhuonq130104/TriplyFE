package com.example.triply.data.remote;

import com.example.triply.data.remote.model.AuthResponse;
import com.example.triply.data.remote.model.LoginRequest;
import com.example.triply.data.remote.model.RegisterRequest;
import com.example.triply.data.remote.model.SocialLoginRequest;
import com.example.triply.data.remote.model.CreatePlanRequest;
import com.example.triply.data.remote.model.PlanResponse;
import com.example.triply.data.remote.model.SaveTripRequest;
import com.example.triply.data.remote.model.SaveFullTripRequest;
import com.example.triply.data.remote.model.City;
import com.example.triply.data.remote.model.Destination;
import com.example.triply.data.remote.model.ChatThread;
import com.example.triply.data.remote.model.ChatMessage;
import com.example.triply.data.remote.model.CreateThreadRequest;
import com.example.triply.data.remote.model.SendMessageRequest;
import com.example.triply.data.remote.model.SendMessageResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ApiService {
    @POST("/api/v1/auth/login")
    Call<AuthResponse> login(@Body LoginRequest request);

    @POST("/api/v1/auth/register")
    Call<AuthResponse> register(@Body RegisterRequest request);

    @POST("/api/v1/auth/social-login")
    Call<AuthResponse> socialLogin(@Body SocialLoginRequest request);

    @GET("/api/v1/auth/me")
    Call<AuthResponse> me(@Header("Authorization") String bearerToken);

    // Trip planning
    @POST("/api/v1/trip/plan")
    Call<PlanResponse> planTrip(@Body CreatePlanRequest request);

    @POST("/api/v1/trip/save")
    Call<Integer> saveTrip(@Body SaveTripRequest request);

    @POST("/api/v1/trip/save-full")
    Call<Integer> saveFullTrip(@Body SaveFullTripRequest request);

    // Destinations
    @GET("/api/v1/destinations/cities")
    Call<List<City>> getCities();

    @GET("/api/v1/destinations/cities/{cityId}/destinations")
    Call<List<Destination>> getDestinationsByCity(@Path("cityId") int cityId);

    @GET("/api/v1/destinations/{destinationId}")
    Call<Destination> getDestinationById(@Path("destinationId") int destinationId);

    // Chat APIs
    @POST("/api/v1/chat/threads")
    Call<ChatThread> createThread(@Body CreateThreadRequest request);

    @GET("/api/v1/chat/threads")
    Call<List<ChatThread>> getThreads();

    @GET("/api/v1/chat/threads/{threadId}/messages")
    Call<List<ChatMessage>> getMessages(@Path("threadId") int threadId);

    @POST("/api/v1/chat/send")
    Call<SendMessageResponse> sendMessage(@Body SendMessageRequest request);
}


