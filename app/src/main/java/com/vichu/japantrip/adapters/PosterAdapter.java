package com.vichu.japantrip.adapters;

import static com.vichu.japantrip.utils.NumToAplhaHelper.numToLetterByAsciiCode;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.vichu.japantrip.R;
import com.vichu.japantrip.activities.PosterActivity;
import com.vichu.japantrip.activities.PosterInfoActivity;
import com.vichu.japantrip.models.Poster;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PosterAdapter extends RecyclerView.Adapter<PosterAdapter.ViewHolder> {

    private static final int POSTER_INFO_REQUEST_CODE = 102;
    private final ArrayList<Poster> posterList;

    public PosterAdapter(List<Poster> posterList) {
        this.posterList = new ArrayList<>(posterList);
    }

    public void updateData(List<Poster> newPosterList) {
        if (newPosterList == null) {
            this.posterList.clear();
        } else {
            this.posterList.clear();
            this.posterList.addAll(newPosterList);
        }
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_poster, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Poster poster = posterList.get(position);
        if (position == 0) {
            holder.divider.setVisibility(View.GONE);
        } else {
            holder.divider.setVisibility(View.VISIBLE);
        }
        holder.textViewIndex.setText(numToLetterByAsciiCode(poster.getPosterIndex()));
        holder.textViewTitle.setText(poster.getTitle());

        // Set click listener for the item
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), PosterInfoActivity.class);
            intent.putExtra("posterInfo", new Gson().toJson(poster.getPosterInfo()));
            intent.putExtra("posterIndex", new Gson().toJson(poster.getPosterIndex()));
            intent.putParcelableArrayListExtra("posterList", posterList);
            ((PosterActivity) v.getContext()).startActivityForResult(intent, POSTER_INFO_REQUEST_CODE);
        });
    }

    @Override
    public int getItemCount() {
        return posterList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewIndex, textViewTitle;
        View divider;

        public ViewHolder(View itemView) {
            super(itemView);
            textViewIndex = itemView.findViewById(R.id.textViewIndex);
            textViewTitle = itemView.findViewById(R.id.textViewTitle);
            divider = itemView.findViewById(R.id.divider);
        }
    }
}
