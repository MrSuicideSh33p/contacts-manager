package com.vichu.japantrip.adapters;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.vichu.japantrip.R;
import com.vichu.japantrip.activities.DailyScheduleActivity;
import com.vichu.japantrip.models.ScheduleDay;

import java.util.ArrayList;
import java.util.List;

public class ScheduleAdapter extends RecyclerView.Adapter<ScheduleAdapter.ViewHolder> {

    private final List<ScheduleDay> scheduleList;

    public ScheduleAdapter(List<ScheduleDay> scheduleList) {
        this.scheduleList = new ArrayList<>(scheduleList);
    }

    public void updateData(List<ScheduleDay> newScheduleList) {
        if (newScheduleList == null) {
            this.scheduleList.clear();
        } else {
            this.scheduleList.clear();
            this.scheduleList.addAll(newScheduleList);
        }
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_schedule, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ScheduleDay scheduleDay = scheduleList.get(position);
        if (position == 0) {
            holder.divider.setVisibility(View.GONE);
        } else {
            holder.divider.setVisibility(View.VISIBLE);
        }
        holder.textViewDay.setText(scheduleDay.getDate());
        holder.textViewTitle.setText(scheduleDay.getTitle());

        // Set click listener for the item
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), DailyScheduleActivity.class);
            intent.putExtra("dayTitle", scheduleDay.getDate() + " " + scheduleDay.getTitle());
            intent.putExtra("events", new Gson().toJson(scheduleDay.getEvents())); // Correct key
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return scheduleList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewDay, textViewTitle;
        View divider;

        public ViewHolder(View itemView) {
            super(itemView);
            textViewDay = itemView.findViewById(R.id.textViewDay);
            textViewTitle = itemView.findViewById(R.id.textViewTitle);
            divider = itemView.findViewById(R.id.divider);
        }
    }
}
