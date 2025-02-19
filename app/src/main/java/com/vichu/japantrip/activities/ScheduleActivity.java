package com.vichu.japantrip.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ScheduleActivity extends AppCompatActivity {

    private static final int DAILY_SCHEDULE_REQUEST_CODE = 101;
    private RecyclerView recyclerView;
    private ScheduleAdapter scheduleAdapter;
    private ProgressBar progressBar;
    private TextView progressText;
    private AwsS3Helper awsS3Helper;

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
        downloadScheduleJson();
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

    private void downloadScheduleJson() {
        File localFile = new File(getFilesDir(), "schedule.json");

        progressBar.setVisibility(View.VISIBLE);
        progressText.setVisibility(View.VISIBLE);

        awsS3Helper.downloadFile(localFile, new AwsS3Helper.S3DownloadListener() {
            @Override
            public void onDownloadSuccess(File file) {
                List<ScheduleDay> scheduleList = parseJsonFile(file);

                if (scheduleList != null) {
                    updateRecyclerView(scheduleList);
                } else {
                    Log.e("ScheduleActivity", "Parsed JSON returned null.");
                    showError("Failed to parse schedule data.");
                }

                runOnUiThread(() -> progressBar.setVisibility(View.GONE));
                runOnUiThread(() -> progressText.setVisibility(View.GONE));
            }

            @Override
            public void onDownloadFailed() {
                Log.e("ScheduleActivity", "Failed to download schedule.json");
                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    progressText.setVisibility(View.GONE);
                    showError("Failed to download schedule. Please check your internet connection.");
                });
            }
        });
    }

    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    private List<ScheduleDay> parseJsonFile(File file) {
        try {
            // Read the contents of the JSON file
            FileInputStream fis = new FileInputStream(file);
            InputStreamReader isr = new InputStreamReader(fis, StandardCharsets.UTF_8);
            BufferedReader reader = new BufferedReader(isr);
            StringBuilder jsonString = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                jsonString.append(line);
            }

            reader.close();
            isr.close();
            fis.close();

            // Convert JSON to List of ScheduleDay
            Gson gson = new Gson();
            Type listType = new TypeToken<List<ScheduleDay>>() {}.getType();
            List<ScheduleDay> scheduleList = gson.fromJson(jsonString.toString(), listType);

            if (scheduleList != null) {
                // Sort by index before displaying
                Collections.sort(scheduleList, Comparator.comparingInt(ScheduleDay::getScheduleIndex));
                return scheduleList;
            } else {
                Log.e("ScheduleActivity", "Parsed JSON is null.");
                return null;
            }

        } catch (IOException e) {
            Log.e("ScheduleActivity", "Error reading schedule.json: " + e.getMessage(), e);
            return null;
        }
    }

    private void updateRecyclerView(List<ScheduleDay> scheduleList) {
        runOnUiThread(() -> {
            if (scheduleAdapter != null) {
                scheduleAdapter.updateData(scheduleList);
            } else {
                scheduleAdapter = new ScheduleAdapter(scheduleList);
                recyclerView.setAdapter(scheduleAdapter);
            }
        });
    }
}
