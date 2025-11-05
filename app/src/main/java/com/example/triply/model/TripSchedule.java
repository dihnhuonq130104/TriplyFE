package com.example.triply.model;

public class TripSchedule {
    private String departure;
    private String direction;
    private String numbersPerson;
    private String budget;
    private String durations;
    private String startDate;

    public TripSchedule() {}

    public TripSchedule(String departure, String direction, String numbersPerson,
                       String budget, String durations, String startDate) {
        this.departure = departure;
        this.direction = direction;
        this.numbersPerson = numbersPerson;
        this.budget = budget;
        this.durations = durations;
        this.startDate = startDate;
    }

    // Getters and Setters
    public String getDeparture() { return departure; }
    public void setDeparture(String departure) { this.departure = departure; }

    public String getDirection() { return direction; }
    public void setDirection(String direction) { this.direction = direction; }

    public String getNumbersPerson() { return numbersPerson; }
    public void setNumbersPerson(String numbersPerson) { this.numbersPerson = numbersPerson; }

    public String getBudget() { return budget; }
    public void setBudget(String budget) { this.budget = budget; }

    public String getDurations() { return durations; }
    public void setDurations(String durations) { this.durations = durations; }

    public String getStartDate() { return startDate; }
    public void setStartDate(String startDate) { this.startDate = startDate; }
}