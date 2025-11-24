package com.example.triply.data.remote.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SaveTripRequest {
    @SerializedName("title")
    private String title;
    
    @SerializedName("startDate")
    private String startDate;
    
    @SerializedName("endDate")
    private String endDate;
    
    @SerializedName("totalBudget")
    private long totalBudget;
    
    @SerializedName("currency")
    private String currency;
    
    @SerializedName("items")
    private List<ItineraryItem> items;
    
    @SerializedName("estimatedExpenses")
    private List<Expense> estimatedExpenses;

    public SaveTripRequest(String title, String startDate, String endDate, long totalBudget, 
                          String currency, List<ItineraryItem> items, List<Expense> estimatedExpenses) {
        this.title = title;
        this.startDate = startDate;
        this.endDate = endDate;
        this.totalBudget = totalBudget;
        this.currency = currency;
        this.items = items;
        this.estimatedExpenses = estimatedExpenses;
    }

    public static class ItineraryItem {
        @SerializedName("destinationName")
        private String destinationName;
        
        @SerializedName("arrivalDate")
        private String arrivalDate;
        
        @SerializedName("departureDate")
        private String departureDate;
        
        @SerializedName("notes")
        private String notes;

        public ItineraryItem(String destinationName, String arrivalDate, String departureDate, String notes) {
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

    public List<ItineraryItem> getItems() {
        return items;
    }

    public void setItems(List<ItineraryItem> items) {
        this.items = items;
    }

    public List<Expense> getEstimatedExpenses() {
        return estimatedExpenses;
    }

    public void setEstimatedExpenses(List<Expense> estimatedExpenses) {
        this.estimatedExpenses = estimatedExpenses;
    }
}

