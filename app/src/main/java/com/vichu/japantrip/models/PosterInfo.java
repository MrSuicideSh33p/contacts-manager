package com.vichu.japantrip.models;

import android.os.Parcel;
import android.os.Parcelable;

public class PosterInfo implements Parcelable {
    private final int index;
    private final String author;
    private final String university;
    private final String topic;
    private String notes;
    private boolean completed;

    public int getIndex() {
        return index;
    }

    public String getAuthor() {
        return author;
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

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    protected PosterInfo(Parcel parcel) {
        index = parcel.readInt();
        author = parcel.readString();
        university = parcel.readString();
        topic = parcel.readString();
        notes = parcel.readString();
        completed = parcel.readBoolean();
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeInt(index);
        parcel.writeString(author);
        parcel.writeString(university);
        parcel.writeString(topic);
        parcel.writeString(notes);
        parcel.writeBoolean(completed);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<PosterInfo> CREATOR = new Creator<PosterInfo>() {
        @Override
        public PosterInfo createFromParcel(Parcel in) {
            return new PosterInfo(in);
        }

        @Override
        public PosterInfo[] newArray(int size) {
            return new PosterInfo[size];
        }
    };
}
