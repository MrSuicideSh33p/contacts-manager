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
import com.vichu.japantrip.activities.PosterInfoActivity;
import com.vichu.japantrip.activities.PosterDetailActivity;
import com.vichu.japantrip.models.PosterInfo;

import java.util.List;

public class PosterInfoAdapter extends RecyclerView.Adapter<PosterInfoAdapter.ViewHolder> {
    private static final int POSTER_DETAILS_REQUEST_CODE = 103;
    private final List<PosterInfo> posterInfoList;

    // Callback interface to notify that an poster has been toggled
    public interface OnPosterInfoToggleListener {
        void onPosterInfoToggled(PosterInfo posterInfo);
    }

    private OnPosterInfoToggleListener onPosterInfoToggleListener;

    public void setOnPosterInfoToggleListener(OnPosterInfoToggleListener listener) {
        this.onPosterInfoToggleListener = listener;
    }

    public PosterInfoAdapter(List<PosterInfo> posterInfoList) {
        this.posterInfoList = posterInfoList;
    }

    public void updateData(List<PosterInfo> newPosterInfo) {
        if (newPosterInfo == null) {
            this.posterInfoList.clear();
        } else {
            this.posterInfoList.clear();
            this.posterInfoList.addAll(newPosterInfo);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_poster_info, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PosterInfo posterInfo = posterInfoList.get(position);

        // Bind data to views
        holder.textViewAuthor.setText(posterInfo.getAuthor());
        holder.textViewUniversity.setText(posterInfo.getUniversity());
        holder.textViewTopic.setText(posterInfo.getTopic());

        // Apply strikethrough if the posterInfo is marked as completed
        if (posterInfo.isCompleted()) {
            holder.textViewAuthor.setPaintFlags(holder.textViewAuthor.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            holder.textViewUniversity.setPaintFlags(holder.textViewUniversity.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            holder.textViewTopic.setPaintFlags(holder.textViewTopic.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            holder.textViewAuthor.setPaintFlags(holder.textViewAuthor.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            holder.textViewUniversity.setPaintFlags(holder.textViewUniversity.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            holder.textViewTopic.setPaintFlags(holder.textViewTopic.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
        }

        // Click listener to open PosterDetailActivity
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), PosterDetailActivity.class);
            intent.putExtra("posterInfo", new Gson().toJson(posterInfo));
            ((PosterInfoActivity) v.getContext()).startActivityForResult(intent, POSTER_DETAILS_REQUEST_CODE);
        });

        // Long press listener to toggle completion status and upload immediately
        holder.itemView.setOnLongClickListener(v -> {
            // Toggle the completed status
            posterInfo.setCompleted(!posterInfo.isCompleted());
            notifyItemChanged(position);
            if (onPosterInfoToggleListener != null) {
                onPosterInfoToggleListener.onPosterInfoToggled(posterInfo);
            }
            return true; // Consume long press
        });
    }

    @Override
    public int getItemCount() {
        return posterInfoList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewAuthor, textViewUniversity, textViewTopic;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewAuthor = itemView.findViewById(R.id.textViewAuthor);
            textViewUniversity = itemView.findViewById(R.id.textViewUniversity);
            textViewTopic = itemView.findViewById(R.id.textViewTopic);
        }
    }
}
