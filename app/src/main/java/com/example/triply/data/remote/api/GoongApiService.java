package com.example.triply.data.remote.api;

import com.example.triply.data.remote.response.DirectionResponse;
import com.example.triply.data.remote.response.GeocodeResponse;
import com.example.triply.data.remote.response.PlaceDetailResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GoongApiService {
    
    @GET("Place/Detail")
    Call<PlaceDetailResponse> getPlaceDetail(
            @Query("place_id") String placeId,
            @Query("api_key") String apiKey
    );
    
    @GET("geocode")
    Call<GeocodeResponse> getGeocode(
            @Query("latlng") String latlng,
            @Query("api_key") String apiKey
    );

    @GET("Direction")
    Call<DirectionResponse> getDirection(
            @Query("origin") String origin,
            @Query("destination") String destination,
            @Query("vehicle") String vehicle,
            @Query("api_key") String apiKey
    );
}
