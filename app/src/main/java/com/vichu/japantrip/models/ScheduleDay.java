package com.vichu.japantrip.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class ScheduleDay implements Parcelable {
    private String date;
    private String title;
    private int scheduleIndex;
    private List<Event> events;

    public ScheduleDay(String date, String title, int scheduleIndex, List<Event> events) {
        this.date = date;
        this.title = title;
        this.scheduleIndex = scheduleIndex;
        this.events = events;
    }

    // Getter methods
    public String getDate() {
        return date;
    }

    public String getTitle() {
        return title;
    }

    public int getScheduleIndex() {
        return scheduleIndex;
    }

    public List<Event> getEvents() {
        return events;
    }

    public void setEvents(List<Event> events) {
        this.events = events;
    }

    // Parcelable implementation
    protected ScheduleDay(Parcel in) {
        date = in.readString();
        title = in.readString();
        scheduleIndex = in.readInt();
        events = in.createTypedArrayList(Event.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(date);
        dest.writeString(title);
        dest.writeInt(scheduleIndex);
        dest.writeTypedList(events);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ScheduleDay> CREATOR = new Creator<ScheduleDay>() {
        @Override
        public ScheduleDay createFromParcel(Parcel in) {
            return new ScheduleDay(in);
        }

        @Override
        public ScheduleDay[] newArray(int size) {
            return new ScheduleDay[size];
        }
    };
}
