package com.vichu.japantrip.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class Poster implements Parcelable {
    private final String title;
    private final int posterIndex;
    private List<PosterInfo> posterInfo;

    public Poster(String title, int posterIndex, List<PosterInfo> posterInfo) {
        this.title = title;
        this.posterIndex = posterIndex;
        this.posterInfo = posterInfo;
    }

    public String getTitle() {
        return title;
    }

    public int getPosterIndex() {
        return posterIndex;
    }

    public List<PosterInfo> getPosterInfo() {
        return posterInfo;
    }

    public void setPosterInfo(List<PosterInfo> posterInfo) {
        this.posterInfo = posterInfo;
    }

    // Parcelable implementation
    protected Poster(Parcel in) {
        title = in.readString();
        posterIndex = in.readInt();
        posterInfo = in.createTypedArrayList(PosterInfo.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeInt(posterIndex);
        dest.writeTypedList(posterInfo);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Poster> CREATOR = new Creator<Poster>() {
        @Override
        public Poster createFromParcel(Parcel in) {
            return new Poster(in);
        }

        @Override
        public Poster[] newArray(int size) {
            return new Poster[size];
        }
    };
}
