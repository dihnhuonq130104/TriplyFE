package com.example.triply.data.repository;

import androidx.annotation.Nullable;

import com.example.triply.data.remote.ApiService;
import com.example.triply.data.remote.RetrofitClient;
import com.example.triply.data.remote.model.CreatePlanRequest;
import com.example.triply.data.remote.model.PlanResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PlanRepository {

    public interface PlanCallback {
        void onSuccess(PlanResponse response);
        void onError(Throwable t, @Nullable Response<?> response);
    }

    private final ApiService apiService;

    public PlanRepository() {
        this.apiService = RetrofitClient.api();
    }

    public void planTrip(CreatePlanRequest request, PlanCallback callback) {
        apiService.planTrip(request).enqueue(new Callback<PlanResponse>() {
            @Override
            public void onResponse(Call<PlanResponse> call, Response<PlanResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError(new RuntimeException("Plan trip failed"), response);
                }
            }

            @Override
            public void onFailure(Call<PlanResponse> call, Throwable t) {
                callback.onError(t, null);
            }
        });
    }
}
