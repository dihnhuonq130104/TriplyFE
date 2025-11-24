package com.example.triply.data.repository;

import com.example.triply.data.remote.ApiService;
import com.example.triply.data.remote.RetrofitClient;
import com.example.triply.data.remote.model.SaveTripRequest;
import com.example.triply.data.remote.model.SaveFullTripRequest;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TripRepository {
    private final ApiService apiService;

    public interface SaveTripCallback {
        void onSuccess(int tripId);
        void onError(String message);
    }

    public TripRepository() {
        this.apiService = RetrofitClient.api();
    }

    public void saveTrip(SaveTripRequest request, SaveTripCallback callback) {
        apiService.saveTrip(request).enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Lưu kế hoạch thất bại: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {
                callback.onError("Lưu kế hoạch thất bại: " + t.getMessage());
            }
        });
    }

    public void saveFullTrip(SaveFullTripRequest request, SaveTripCallback callback) {
        apiService.saveFullTrip(request).enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Lưu kế hoạch đầy đủ thất bại: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {
                callback.onError("Lưu kế hoạch đầy đủ thất bại: " + t.getMessage());
            }
        });
    }
}

