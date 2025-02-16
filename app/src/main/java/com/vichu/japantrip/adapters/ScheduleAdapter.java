package com.vichu.japantrip.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.vichu.japantrip.R;
import com.vichu.japantrip.models.ScheduleDay;

import java.util.List;

public class ScheduleAdapter extends RecyclerView.Adapter<ScheduleAdapter.ViewHolder> {

    private List<ScheduleDay> scheduleList;

    public ScheduleAdapter(List<ScheduleDay> scheduleList) {
        this.scheduleList = scheduleList;
    }

    public void updateData(List<ScheduleDay> newScheduleList) {
        this.scheduleList.clear();
        this.scheduleList.addAll(newScheduleList);
        notifyDataSetChanged(); // Notify RecyclerView of changes
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_schedule, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ScheduleDay scheduleDay = scheduleList.get(position);
        holder.textViewDay.setText(scheduleDay.getDate());
        holder.textViewTitle.setText(scheduleDay.getTitle());
    }

    @Override
    public int getItemCount() {
        return scheduleList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewDay, textViewTitle;

        public ViewHolder(View itemView) {
            super(itemView);
            textViewDay = itemView.findViewById(R.id.textViewDay);
            textViewTitle = itemView.findViewById(R.id.textViewTitle);
        }
    }
}
