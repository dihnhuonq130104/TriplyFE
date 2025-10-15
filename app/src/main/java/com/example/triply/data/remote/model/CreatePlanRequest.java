package com.example.triply.data.remote.model;

public class CreatePlanRequest {
    public long budget;
    public String startDate;
    public String endDate;
    public int people;
    public String origin;
    public String destination;

    public CreatePlanRequest(long budget, String startDate, String endDate, int people, String origin, String destination) {
        this.budget = budget;
        this.startDate = startDate;
        this.endDate = endDate;
        this.people = people;
        this.origin = origin;
        this.destination = destination;
    }
}
