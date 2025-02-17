package com.vichu.japantrip.models;

import java.util.List;

public class ScheduleDay {
    private String date;
    private String title;
    private int index;
    private List<Event> events;

    public ScheduleDay(String date, String title, int index, List<Event> events) {
        this.date = date;
        this.title = title;
        this.index = index;
        this.events = events;
    }

    public String getDate() {
        return date;
    }

    public String getTitle() {
        return title;
    }

    public int getIndex() {
        return index;
    }

    public List<Event> getEvents() {
        return events;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
