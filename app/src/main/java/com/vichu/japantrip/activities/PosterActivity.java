package com.vichu.japantrip.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.vichu.japantrip.R;
import com.vichu.japantrip.adapters.PosterAdapter;
import com.vichu.japantrip.models.Poster;
import com.vichu.japantrip.utils.AwsS3Helper;
import com.vichu.japantrip.utils.DownloadHelper;
import com.vichu.japantrip.utils.RecyclerViewHelper;

import java.io.File;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class PosterActivity extends AppCompatActivity {

    private static final String file = "poster/poster.json";
    private static final int POSTER_INFO_REQUEST_CODE = 102;
    private RecyclerView recyclerView;
    private PosterAdapter posterAdapter;
    private ProgressBar progressBar;
    private TextView progressText;
    private AwsS3Helper awsS3Helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poster);

        // Toolbar setup
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        recyclerView = findViewById(R.id.recyclerViewPoster);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize the adapter with an empty list
        posterAdapter = new PosterAdapter(Collections.emptyList());
        recyclerView.setAdapter(posterAdapter); // Set adapter immediately

        progressText = findViewById(R.id.progressText);
        progressBar = findViewById(R.id.progressBar);
        awsS3Helper = new AwsS3Helper(this);

        // Call download function when activity starts
        downloadPosterJsonAndRefreshUI();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == POSTER_INFO_REQUEST_CODE && resultCode == RESULT_OK) {
            if (data != null && data.hasExtra("updatedPosterList")) {
                String updatedPosterListJson = data.getStringExtra("updatedPosterList");
                List<Poster> updatedPosterList = new Gson().fromJson(updatedPosterListJson, new TypeToken<List<Poster>>() {}.getType());
                posterAdapter.updateData(updatedPosterList); // Update the adapter with the new data
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.poster_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_refresh) {
            downloadPosterJsonAndRefreshUI();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void downloadPosterJsonAndRefreshUI() {
        File localFile = new File(getFilesDir(), "poster.json");
        DownloadHelper.downloadJson(this, localFile, awsS3Helper, progressBar, progressText, recyclerView, new DownloadHelper.DownloadListener<Poster>() {
            @Override
            public void onDownloadSuccess(List<Poster> posterList) {
                RecyclerViewHelper.updateRecyclerView(posterAdapter, recyclerView, posterList);
            }

            @Override
            public void onDownloadFailed() {
                Toast.makeText(PosterActivity.this, "Failed to download poster info. Please check your internet connection.", Toast.LENGTH_LONG).show();
            }
        }, file, new TypeToken<List<Poster>>() {}.getType(), Comparator.comparingInt(Poster::getPosterIndex));
    }
}
