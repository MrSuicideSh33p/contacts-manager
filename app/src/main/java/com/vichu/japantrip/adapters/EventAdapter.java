package com.vichu.japantrip.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.vichu.japantrip.R;
import com.vichu.japantrip.models.Event;
import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.ViewHolder> {
    private final List<Event> events;

    public EventAdapter(List<Event> events) {
        this.events = events;
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

        // Show university if available
        if (event.getUniversity() != null && !event.getUniversity().isEmpty()) {
            holder.textViewUniversity.setText(event.getUniversity());
            holder.textViewUniversity.setVisibility(View.VISIBLE);
        } else {
            holder.textViewUniversity.setVisibility(View.GONE);
        }

        // Show topic if available
        if (event.getTopic() != null && !event.getTopic().isEmpty()) {
            holder.textViewTopic.setText(event.getTopic());
            holder.textViewTopic.setVisibility(View.VISIBLE);
        } else {
            holder.textViewTopic.setVisibility(View.GONE);
        }
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
