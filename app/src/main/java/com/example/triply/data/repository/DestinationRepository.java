package com.example.triply.data.repository;

import com.example.triply.data.remote.ApiService;
import com.example.triply.data.remote.RetrofitClient;
import com.example.triply.data.remote.model.City;
import com.example.triply.data.remote.model.Destination;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DestinationRepository {
    private final ApiService apiService;

    public interface CitiesCallback {
        void onSuccess(List<City> cities);
        void onError(String message);
    }

    public interface DestinationsCallback {
        void onSuccess(List<Destination> destinations);
        void onError(String message);
    }

    public interface DestinationCallback {
        void onSuccess(Destination destination);
        void onError(String message);
    }

    public DestinationRepository() {
        this.apiService = RetrofitClient.api();
    }

    public void getCities(CitiesCallback callback) {
        apiService.getCities().enqueue(new Callback<List<City>>() {
            @Override
            public void onResponse(Call<List<City>> call, Response<List<City>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Lấy danh sách thành phố thất bại: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<City>> call, Throwable t) {
                callback.onError("Lỗi kết nối: " + t.getMessage());
            }
        });
    }

    public void getDestinationsByCity(int cityId, DestinationsCallback callback) {
        apiService.getDestinationsByCity(cityId).enqueue(new Callback<List<Destination>>() {
            @Override
            public void onResponse(Call<List<Destination>> call, Response<List<Destination>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Lấy danh sách địa điểm thất bại: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<Destination>> call, Throwable t) {
                callback.onError("Lỗi kết nối: " + t.getMessage());
            }
        });
    }

    public void getDestinationById(int destinationId, DestinationCallback callback) {
        apiService.getDestinationById(destinationId).enqueue(new Callback<Destination>() {
            @Override
            public void onResponse(Call<Destination> call, Response<Destination> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Lấy thông tin địa điểm thất bại: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Destination> call, Throwable t) {
                callback.onError("Lỗi kết nối: " + t.getMessage());
            }
        });
    }
}

