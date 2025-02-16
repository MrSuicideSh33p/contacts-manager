package com.vichu.japantrip.models;

import java.util.List;

public class ScheduleDay {
    private String date;
    private int index;
    private List<String> events;

    public ScheduleDay() {
        // Empty constructor for JSON parsing
    }

    public ScheduleDay(String date, int index, List<String> events) {
        this.date = date;
        this.index = index;
        this.events = events;
    }

    public String getDate() {
        return date;
    }

    public int getIndex() {
        return index;
    }

    public List<String> getEvents() {
        return events;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public void setEvents(List<String> events) {
        this.events = events;
    }
}
