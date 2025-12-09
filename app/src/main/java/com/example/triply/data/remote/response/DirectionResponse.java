package com.example.triply.data.remote.response;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class DirectionResponse {
    @SerializedName("routes")
    private List<Route> routes;
    
    @SerializedName("geocoded_waypoints")
    private List<GeocodedWaypoint> geocodedWaypoints;

    public List<Route> getRoutes() {
        return routes;
    }

    public void setRoutes(List<Route> routes) {
        this.routes = routes;
    }

    public List<GeocodedWaypoint> getGeocodedWaypoints() {
        return geocodedWaypoints;
    }

    public void setGeocodedWaypoints(List<GeocodedWaypoint> geocodedWaypoints) {
        this.geocodedWaypoints = geocodedWaypoints;
    }

    public static class Route {
        @SerializedName("overview_polyline")
        private OverviewPolyline overviewPolyline;
        
        @SerializedName("legs")
        private List<Leg> legs;
        
        @SerializedName("summary")
        private String summary;

        public OverviewPolyline getOverviewPolyline() {
            return overviewPolyline;
        }

        public void setOverviewPolyline(OverviewPolyline overviewPolyline) {
            this.overviewPolyline = overviewPolyline;
        }

        public List<Leg> getLegs() {
            return legs;
        }

        public void setLegs(List<Leg> legs) {
            this.legs = legs;
        }

        public String getSummary() {
            return summary;
        }

        public void setSummary(String summary) {
            this.summary = summary;
        }
    }

    public static class OverviewPolyline {
        @SerializedName("points")
        private String points;

        public String getPoints() {
            return points;
        }

        public void setPoints(String points) {
            this.points = points;
        }
    }

    public static class Leg {
        @SerializedName("distance")
        private Distance distance;
        
        @SerializedName("duration")
        private Duration duration;
        
        @SerializedName("start_address")
        private String startAddress;
        
        @SerializedName("end_address")
        private String endAddress;

        public Distance getDistance() {
            return distance;
        }

        public void setDistance(Distance distance) {
            this.distance = distance;
        }

        public Duration getDuration() {
            return duration;
        }

        public void setDuration(Duration duration) {
            this.duration = duration;
        }

        public String getStartAddress() {
            return startAddress;
        }

        public void setStartAddress(String startAddress) {
            this.startAddress = startAddress;
        }

        public String getEndAddress() {
            return endAddress;
        }

        public void setEndAddress(String endAddress) {
            this.endAddress = endAddress;
        }
    }

    public static class Distance {
        @SerializedName("text")
        private String text;
        
        @SerializedName("value")
        private int value;

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public int getValue() {
            return value;
        }

        public void setValue(int value) {
            this.value = value;
        }
    }

    public static class Duration {
        @SerializedName("text")
        private String text;
        
        @SerializedName("value")
        private int value;

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public int getValue() {
            return value;
        }

        public void setValue(int value) {
            this.value = value;
        }
    }

    public static class GeocodedWaypoint {
        @SerializedName("geocoder_status")
        private String geocoderStatus;
        
        @SerializedName("place_id")
        private String placeId;

        public String getGeocoderStatus() {
            return geocoderStatus;
        }

        public void setGeocoderStatus(String geocoderStatus) {
            this.geocoderStatus = geocoderStatus;
        }

        public String getPlaceId() {
            return placeId;
        }

        public void setPlaceId(String placeId) {
            this.placeId = placeId;
        }
    }
}
