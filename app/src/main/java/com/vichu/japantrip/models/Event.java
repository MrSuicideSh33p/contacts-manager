package com.vichu.japantrip.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Event implements Parcelable {
    private int index;
    private String time;
    private String speaker;
    private String university;
    private String topic;
    private String notes;

    public int getIndex() {
        return index;
    }

    public String getTime() {
        return time;
    }

    public String getSpeaker() {
        return speaker;
    }

    public String getUniversity() {
        return university;
    }

    public String getTopic() {
        return topic;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    protected Event(Parcel parcel) {
        index = parcel.readInt();
        time = parcel.readString();
        speaker = parcel.readString();
        university = parcel.readString();
        topic = parcel.readString();
        notes = parcel.readString();
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeInt(index);
        parcel.writeString(time);
        parcel.writeString(speaker);
        parcel.writeString(university);
        parcel.writeString(topic);
        parcel.writeString(notes);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Event> CREATOR = new Creator<Event>() {
        @Override
        public Event createFromParcel(Parcel in) {
            return new Event(in);
        }

        @Override
        public Event[] newArray(int size) {
            return new Event[size];
        }
    };
}
