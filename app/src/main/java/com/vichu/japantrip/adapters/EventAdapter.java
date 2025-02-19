package com.vichu.japantrip.adapters;

import android.content.Intent;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.vichu.japantrip.R;
import com.vichu.japantrip.activities.DailyScheduleActivity;
import com.vichu.japantrip.activities.EventDetailActivity;
import com.vichu.japantrip.models.Event;
import com.vichu.japantrip.models.ScheduleDay;

import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.ViewHolder> {
    private static final int EVENT_DETAILS_REQUEST_CODE = 100;
    private final List<Event> events;

    // Callback interface to notify that an event has been toggled
    public interface OnEventToggleListener {
        void onEventToggled(Event event);
    }

    private OnEventToggleListener onEventToggleListener;

    public void setOnEventToggleListener(OnEventToggleListener listener) {
        this.onEventToggleListener = listener;
    }

    public EventAdapter(List<Event> events) {
        this.events = events;
    }

    public void updateData(List<Event> newEvents) {
        if (newEvents == null) {
            this.events.clear();
        } else {
            this.events.clear();
            this.events.addAll(newEvents);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_event, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Event event = events.get(position);

        // Bind data to views
        holder.textViewTime.setText(event.getTime());
        holder.textViewSpeaker.setText(event.getSpeaker());

        if (event.getUniversity() != null && !event.getUniversity().isEmpty()) {
            holder.textViewUniversity.setText(event.getUniversity());
            holder.textViewUniversity.setVisibility(View.VISIBLE);
        } else {
            holder.textViewUniversity.setVisibility(View.GONE);
        }

        if (event.getTopic() != null && !event.getTopic().isEmpty()) {
            holder.textViewTopic.setText(event.getTopic());
            holder.textViewTopic.setVisibility(View.VISIBLE);
        } else {
            holder.textViewTopic.setVisibility(View.GONE);
        }

        // Apply strikethrough if the event is marked as completed
        if (event.isCompleted()) {
            holder.textViewTime.setPaintFlags(holder.textViewTime.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            holder.textViewSpeaker.setPaintFlags(holder.textViewSpeaker.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            holder.textViewUniversity.setPaintFlags(holder.textViewUniversity.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            holder.textViewTopic.setPaintFlags(holder.textViewTopic.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            holder.textViewTime.setPaintFlags(holder.textViewTime.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            holder.textViewSpeaker.setPaintFlags(holder.textViewSpeaker.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            holder.textViewUniversity.setPaintFlags(holder.textViewUniversity.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            holder.textViewTopic.setPaintFlags(holder.textViewTopic.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
        }

        // Click listener to open EventDetailActivity
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), EventDetailActivity.class);
            intent.putExtra("event", new Gson().toJson(event));
            ((DailyScheduleActivity) v.getContext()).startActivityForResult(intent, EVENT_DETAILS_REQUEST_CODE);
        });

        // Long press listener to toggle completion status and upload immediately
        holder.itemView.setOnLongClickListener(v -> {
            // Toggle the completed status
            event.setCompleted(!event.isCompleted());
            notifyItemChanged(position);
            if (onEventToggleListener != null) {
                onEventToggleListener.onEventToggled(event);
            }
            return true; // Consume long press
        });
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewTime, textViewSpeaker, textViewUniversity, textViewTopic;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewTime = itemView.findViewById(R.id.textViewTime);
            textViewSpeaker = itemView.findViewById(R.id.textViewSpeaker);
            textViewUniversity = itemView.findViewById(R.id.textViewUniversity);
            textViewTopic = itemView.findViewById(R.id.textViewTopic);
        }
    }
}
