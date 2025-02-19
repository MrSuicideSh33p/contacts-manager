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
import com.vichu.japantrip.adapters.ScheduleAdapter;
import com.vichu.japantrip.models.ScheduleDay;
import com.vichu.japantrip.utils.AwsS3Helper;
import com.vichu.japantrip.utils.RecyclerViewHelper;
import com.vichu.japantrip.utils.ScheduleDownloadHelper;

import java.io.File;
import java.util.Collections;
import java.util.List;

public class ScheduleActivity extends AppCompatActivity {

    private static final int DAILY_SCHEDULE_REQUEST_CODE = 101;
    private RecyclerView recyclerView;
    private ScheduleAdapter scheduleAdapter;
    private ProgressBar progressBar;
    private TextView progressText;
    private AwsS3Helper awsS3Helper;
    private List<ScheduleDay> scheduleList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);

        // Toolbar setup
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        recyclerView = findViewById(R.id.recyclerViewSchedule);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize the adapter with an empty list
        scheduleAdapter = new ScheduleAdapter(Collections.emptyList());
        recyclerView.setAdapter(scheduleAdapter); // Set adapter immediately

        progressText = findViewById(R.id.progressText);
        progressBar = findViewById(R.id.progressBar);
        awsS3Helper = new AwsS3Helper(this);

        // Call download function when activity starts
        downloadScheduleJsonAndRefreshUI();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == DAILY_SCHEDULE_REQUEST_CODE && resultCode == RESULT_OK) {
            if (data != null && data.hasExtra("updatedScheduleList")) {
                String updatedScheduleListJson = data.getStringExtra("updatedScheduleList");
                List<ScheduleDay> updatedScheduleList = new Gson().fromJson(updatedScheduleListJson, new TypeToken<List<ScheduleDay>>() {}.getType());
                scheduleAdapter.updateData(updatedScheduleList); // Update the adapter with the new data
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.schedule_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_refresh) {
            downloadScheduleJsonAndRefreshUI();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void downloadScheduleJsonAndRefreshUI() {
        File localFile = new File(getFilesDir(), "schedule.json");
        ScheduleDownloadHelper.downloadScheduleJson(this, localFile, awsS3Helper, progressBar, progressText, recyclerView, new ScheduleDownloadHelper.DownloadListener() {
            @Override
            public void onDownloadSuccess(List<ScheduleDay> scheduleList) {
                RecyclerViewHelper.updateRecyclerView(scheduleAdapter, recyclerView, scheduleList);
            }

            @Override
            public void onDownloadFailed() {
                Toast.makeText(ScheduleActivity.this, "Failed to download schedule. Please check your internet connection.", Toast.LENGTH_LONG).show();
            }
        });
    }
}
