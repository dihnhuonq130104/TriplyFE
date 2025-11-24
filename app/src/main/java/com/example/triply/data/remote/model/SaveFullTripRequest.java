package com.example.triply.data.remote.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SaveFullTripRequest {
    @SerializedName("title")
    private String title;
    
    @SerializedName("startDate")
    private String startDate;
    
    @SerializedName("endDate")
    private String endDate;
    
    @SerializedName("numberOfPeople")
    private Integer numberOfPeople;
    
    @SerializedName("totalBudget")
    private long totalBudget;
    
    @SerializedName("currency")
    private String currency;
    
    @SerializedName("destinations")
    private List<Destination> destinations;
    
    @SerializedName("flight")
    private Flight flight;
    
    @SerializedName("hotel")
    private Hotel hotel;
    
    @SerializedName("dailyAttractions")
    private List<DailyAttraction> dailyAttractions;
    
    @SerializedName("expenses")
    private List<Expense> expenses;

    public SaveFullTripRequest(String title, String startDate, String endDate, Integer numberOfPeople,
                               long totalBudget, String currency, List<Destination> destinations,
                               Flight flight, Hotel hotel, List<DailyAttraction> dailyAttractions,
                               List<Expense> expenses) {
        this.title = title;
        this.startDate = startDate;
        this.endDate = endDate;
        this.numberOfPeople = numberOfPeople;
        this.totalBudget = totalBudget;
        this.currency = currency;
        this.destinations = destinations;
        this.flight = flight;
        this.hotel = hotel;
        this.dailyAttractions = dailyAttractions;
        this.expenses = expenses;
    }

    // Destination model
    public static class Destination {
        @SerializedName("destinationName")
        private String destinationName;
        
        @SerializedName("arrivalDate")
        private String arrivalDate;
        
        @SerializedName("departureDate")
        private String departureDate;
        
        @SerializedName("notes")
        private String notes;

        public Destination(String destinationName, String arrivalDate, String departureDate, String notes) {
            this.destinationName = destinationName;
            this.arrivalDate = arrivalDate;
            this.departureDate = departureDate;
            this.notes = notes;
        }

        public String getDestinationName() {
            return destinationName;
        }

        public void setDestinationName(String destinationName) {
            this.destinationName = destinationName;
        }

        public String getArrivalDate() {
            return arrivalDate;
        }

        public void setArrivalDate(String arrivalDate) {
            this.arrivalDate = arrivalDate;
        }

        public String getDepartureDate() {
            return departureDate;
        }

        public void setDepartureDate(String departureDate) {
            this.departureDate = departureDate;
        }

        public String getNotes() {
            return notes;
        }

        public void setNotes(String notes) {
            this.notes = notes;
        }
    }

    // Flight model
    public static class Flight {
        @SerializedName("departureId")
        private String departureId;
        
        @SerializedName("arrivalId")
        private String arrivalId;
        
        @SerializedName("airline")
        private String airline;
        
        @SerializedName("airlineId")
        private String airlineId;
        
        @SerializedName("priceVnd")
        private Integer priceVnd;
        
        @SerializedName("flightDuration")
        private String flightDuration;
        
        @SerializedName("departureTime")
        private String departureTime;
        
        @SerializedName("arrivalTime")
        private String arrivalTime;

        public Flight(String departureId, String arrivalId, String airline, String airlineId,
                     Integer priceVnd, String flightDuration, String departureTime, String arrivalTime) {
            this.departureId = departureId;
            this.arrivalId = arrivalId;
            this.airline = airline;
            this.airlineId = airlineId;
            this.priceVnd = priceVnd;
            this.flightDuration = flightDuration;
            this.departureTime = departureTime;
            this.arrivalTime = arrivalTime;
        }

        public String getDepartureId() {
            return departureId;
        }

        public void setDepartureId(String departureId) {
            this.departureId = departureId;
        }

        public String getArrivalId() {
            return arrivalId;
        }

        public void setArrivalId(String arrivalId) {
            this.arrivalId = arrivalId;
        }

        public String getAirline() {
            return airline;
        }

        public void setAirline(String airline) {
            this.airline = airline;
        }

        public String getAirlineId() {
            return airlineId;
        }

        public void setAirlineId(String airlineId) {
            this.airlineId = airlineId;
        }

        public Integer getPriceVnd() {
            return priceVnd;
        }

        public void setPriceVnd(Integer priceVnd) {
            this.priceVnd = priceVnd;
        }

        public String getFlightDuration() {
            return flightDuration;
        }

        public void setFlightDuration(String flightDuration) {
            this.flightDuration = flightDuration;
        }

        public String getDepartureTime() {
            return departureTime;
        }

        public void setDepartureTime(String departureTime) {
            this.departureTime = departureTime;
        }

        public String getArrivalTime() {
            return arrivalTime;
        }

        public void setArrivalTime(String arrivalTime) {
            this.arrivalTime = arrivalTime;
        }
    }

    // Hotel model
    public static class Hotel {
        @SerializedName("name")
        private String name;
        
        @SerializedName("address")
        private String address;
        
        @SerializedName("latitude")
        private Double latitude;
        
        @SerializedName("longitude")
        private Double longitude;
        
        @SerializedName("priceTotalVnd")
        private Integer priceTotalVnd;

        public Hotel(String name, String address, Double latitude, Double longitude, Integer priceTotalVnd) {
            this.name = name;
            this.address = address;
            this.latitude = latitude;
            this.longitude = longitude;
            this.priceTotalVnd = priceTotalVnd;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public Double getLatitude() {
            return latitude;
        }

        public void setLatitude(Double latitude) {
            this.latitude = latitude;
        }

        public Double getLongitude() {
            return longitude;
        }

        public void setLongitude(Double longitude) {
            this.longitude = longitude;
        }

        public Integer getPriceTotalVnd() {
            return priceTotalVnd;
        }

        public void setPriceTotalVnd(Integer priceTotalVnd) {
            this.priceTotalVnd = priceTotalVnd;
        }
    }

    // DailyAttraction model
    public static class DailyAttraction {
        @SerializedName("date")
        private String date;
        
        @SerializedName("time")
        private String time;
        
        @SerializedName("activity")
        private String activity;
        
        @SerializedName("location")
        private String location;
        
        @SerializedName("reason")
        private String reason;

        public DailyAttraction(String date, String time, String activity, String location, String reason) {
            this.date = date;
            this.time = time;
            this.activity = activity;
            this.location = location;
            this.reason = reason;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }

        public String getActivity() {
            return activity;
        }

        public void setActivity(String activity) {
            this.activity = activity;
        }

        public String getLocation() {
            return location;
        }

        public void setLocation(String location) {
            this.location = location;
        }

        public String getReason() {
            return reason;
        }

        public void setReason(String reason) {
            this.reason = reason;
        }
    }

    // Expense model
    public static class Expense {
        @SerializedName("amount")
        private long amount;
        
        @SerializedName("category")
        private String category;
        
        @SerializedName("date")
        private String date;

        public Expense(long amount, String category, String date) {
            this.amount = amount;
            this.category = category;
            this.date = date;
        }

        public long getAmount() {
            return amount;
        }

        public void setAmount(long amount) {
            this.amount = amount;
        }

        public String getCategory() {
            return category;
        }

        public void setCategory(String category) {
            this.category = category;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }
    }

    // Getters and setters for main class
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public Integer getNumberOfPeople() {
        return numberOfPeople;
    }

    public void setNumberOfPeople(Integer numberOfPeople) {
        this.numberOfPeople = numberOfPeople;
    }

    public long getTotalBudget() {
        return totalBudget;
    }

    public void setTotalBudget(long totalBudget) {
        this.totalBudget = totalBudget;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public List<Destination> getDestinations() {
        return destinations;
    }

    public void setDestinations(List<Destination> destinations) {
        this.destinations = destinations;
    }

    public Flight getFlight() {
        return flight;
    }

    public void setFlight(Flight flight) {
        this.flight = flight;
    }

    public Hotel getHotel() {
        return hotel;
    }

    public void setHotel(Hotel hotel) {
        this.hotel = hotel;
    }

    public List<DailyAttraction> getDailyAttractions() {
        return dailyAttractions;
    }

    public void setDailyAttractions(List<DailyAttraction> dailyAttractions) {
        this.dailyAttractions = dailyAttractions;
    }

    public List<Expense> getExpenses() {
        return expenses;
    }

    public void setExpenses(List<Expense> expenses) {
        this.expenses = expenses;
    }
}

