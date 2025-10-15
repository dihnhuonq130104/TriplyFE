package com.example.triply.data.remote.model;

import java.util.List;

public class PlanResponse {
    public List<SimpleFlight> flights; // optional
    public List<Object> flightBest; // backend returns array, keep generic if empty
    public List<FlightItinerary> flightOther;
    public List<Hotel> hotels;
    public List<AttractionDay> attractions;
    public List<Object> weather;
    public String suggestedTitle;
    public long estimatedTotal;

    public static class SimpleFlight {
        public String departure_id;
        public String destination;
        public String arrival_id;
        public String airline;
        public String airline_id;
        public long price_vnd;
        public String flight_duration;
    }

    public static class FlightItinerary {
        public List<FlightLeg> flights;
        public int total_duration;
        public CarbonEmissions carbon_emissions;
        public long price;
        public String type;
        public String airline_logo;
        public String departure_token;
    }

    public static class FlightLeg {
        public Airport departure_airport;
        public Airport arrival_airport;
        public int duration;
        public String airplane;
        public String airline;
        public String airline_logo;
        public String travel_class;
        public String flight_number;
        public String legroom;
        public List<String> extensions;
        public Boolean often_delayed_by_over_30_min;
    }

    public static class Airport {
        public String name;
        public String id;
        public String time;
    }

    public static class CarbonEmissions {
        public long this_flight;
        public long typical_for_this_route;
        public int difference_percent;
    }

    public static class Hotel {
        public String type;
        public String name;
        public String description;
        public String link;
        public String property_token;
        public String serpapi_property_details_link;
        public Gps gps_coordinates;
        public String check_in_time;
        public String check_out_time;
        public Rate rate_per_night;
        public Rate total_rate;
        public Integer extracted_hotel_class;
        public Double overall_rating;
        public Integer reviews;
        public List<String> amenities;
        public boolean eco_certified;
    }

    public static class Gps {
        public double latitude;
        public double longitude;
    }

    public static class Rate {
        public String lowest;
        public long extracted_lowest;
        public String before_taxes_fees;
        public Long extracted_before_taxes_fees;
    }

    public static class AttractionDay {
        public String date;
        public List<AttractionSchedule> schedule;
    }

    public static class AttractionSchedule {
        public String time;
        public String activity;
        public String location;
        public String reason;
    }
}
