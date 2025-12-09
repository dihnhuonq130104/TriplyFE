package com.example.triply.data.remote.response;

import com.google.gson.annotations.SerializedName;

public class PlaceDetailResponse {
    @SerializedName("result")
    private Result result;
    
    @SerializedName("status")
    private String status;

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public static class Result {
        @SerializedName("geometry")
        private Geometry geometry;
        
        @SerializedName("name")
        private String name;
        
        @SerializedName("formatted_address")
        private String formattedAddress;
        
        @SerializedName("place_id")
        private String placeId;

        public Geometry getGeometry() {
            return geometry;
        }

        public void setGeometry(Geometry geometry) {
            this.geometry = geometry;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getFormattedAddress() {
            return formattedAddress;
        }

        public void setFormattedAddress(String formattedAddress) {
            this.formattedAddress = formattedAddress;
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
        private String lat;
        
        @SerializedName("lng")
        private String lng;

        public String getLat() {
            return lat;
        }

        public void setLat(String lat) {
            this.lat = lat;
        }

        public String getLng() {
            return lng;
        }

        public void setLng(String lng) {
            this.lng = lng;
        }
    }
}
