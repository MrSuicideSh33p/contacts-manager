package com.vichu.japantrip.utils;

import androidx.recyclerview.widget.RecyclerView;

import com.vichu.japantrip.adapters.EventAdapter;
import com.vichu.japantrip.adapters.ScheduleAdapter;
import com.vichu.japantrip.models.Event;
import com.vichu.japantrip.models.ScheduleDay;

import java.util.List;

public class RecyclerViewHelper {

    public static <T> void updateRecyclerView(RecyclerView.Adapter<?> adapter, RecyclerView recyclerView, List<T> data) {
        if (adapter != null) {
            if (adapter instanceof ScheduleAdapter) {
                ((ScheduleAdapter) adapter).updateData((List<ScheduleDay>) data);
            } else if (adapter instanceof EventAdapter) {
                ((EventAdapter) adapter).updateData((List<Event>) data);
            }
        } else {
            if (data.get(0) instanceof ScheduleDay) {
                recyclerView.setAdapter(new ScheduleAdapter((List<ScheduleDay>) data));
            } else if (data.get(0) instanceof Event) {
                recyclerView.setAdapter(new EventAdapter((List<Event>) data));
            }
        }
    }
}