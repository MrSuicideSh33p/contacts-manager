package com.vichu.japantrip.utils;

import androidx.recyclerview.widget.RecyclerView;

import com.vichu.japantrip.adapters.EventAdapter;
import com.vichu.japantrip.adapters.PosterAdapter;
import com.vichu.japantrip.adapters.PosterInfoAdapter;
import com.vichu.japantrip.adapters.ScheduleAdapter;
import com.vichu.japantrip.models.Event;
import com.vichu.japantrip.models.Poster;
import com.vichu.japantrip.models.PosterInfo;
import com.vichu.japantrip.models.ScheduleDay;

import java.util.List;

public class RecyclerViewHelper {

    public static <T> void updateRecyclerView(RecyclerView.Adapter<?> adapter, RecyclerView recyclerView, List<T> data) {
        if (adapter != null) {
            if (adapter instanceof ScheduleAdapter) {
                ((ScheduleAdapter) adapter).updateData((List<ScheduleDay>) data);
            } else if (adapter instanceof EventAdapter) {
                ((EventAdapter) adapter).updateData((List<Event>) data);
            } else if (adapter instanceof PosterAdapter) {
                ((PosterAdapter) adapter).updateData((List<Poster>) data);
            } else if (adapter instanceof PosterInfoAdapter) {
                ((PosterInfoAdapter) adapter).updateData((List<PosterInfo>) data);
            }
        } else {
            if (data.get(0) instanceof ScheduleDay) {
                recyclerView.setAdapter(new ScheduleAdapter((List<ScheduleDay>) data));
            } else if (data.get(0) instanceof Event) {
                recyclerView.setAdapter(new EventAdapter((List<Event>) data));
            } else if (data.get(0) instanceof Poster) {
                recyclerView.setAdapter(new PosterAdapter((List<Poster>) data));
            } else if (data.get(0) instanceof PosterInfo) {
                recyclerView.setAdapter(new PosterInfoAdapter((List<PosterInfo>) data));
            }
        }
    }
}
