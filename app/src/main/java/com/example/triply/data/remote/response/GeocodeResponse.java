package com.example.triply.data.remote.response;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class GeocodeResponse {
    @SerializedName("results")
    private List<Result> results;
    
    @SerializedName("status")
    private String status;

    public List<Result> getResults() {
        return results;
    }

    public void setResults(List<Result> results) {
        this.results = results;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public static class Result {
        @SerializedName("formatted_address")
        private String formattedAddress;
        
        @SerializedName("geometry")
        private Geometry geometry;
        
        @SerializedName("place_id")
        private String placeId;

        public String getFormattedAddress() {
            return formattedAddress;
        }

        public void setFormattedAddress(String formattedAddress) {
            this.formattedAddress = formattedAddress;
        }

        public Geometry getGeometry() {
            return geometry;
        }

        public void setGeometry(Geometry geometry) {
            this.geometry = geometry;
        }

        public String getPlaceId() {
            return placeId;
        }

        public void setPlaceId(String placeId) {
            this.placeId = placeId;
        }
    }

    public static class Geometry {
        @SerializedName("location")
        private Location location;

        public Location getLocation() {
            return location;
        }

        public void setLocation(Location location) {
            this.location = location;
        }
    }

    public static class Location {
        @SerializedName("lat")
        private double lat;
        
        @SerializedName("lng")
        private double lng;

        public double getLat() {
            return lat;
        }

        public void setLat(double lat) {
            this.lat = lat;
        }

        public double getLng() {
            return lng;
        }

        public void setLng(double lng) {
            this.lng = lng;
        }
    }
}
