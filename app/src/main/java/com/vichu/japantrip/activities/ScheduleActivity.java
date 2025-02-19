package com.vichu.japantrip.activities;

import static com.vichu.japantrip.utils.JsonHelper.parseJsonFile;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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
import com.vichu.japantrip.utils.RecyclerViewHelper;

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.schedule_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_refresh) {
            downloadScheduleJson();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //TODO: Check possibility to extract this method from ScheduleActivity and DailyScheduleActivity
    private void downloadScheduleJson() {
        File localFile = new File(getFilesDir(), "schedule.json");

        runOnUiThread(() -> {
            progressBar.setVisibility(View.VISIBLE);
            progressText.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        });

        awsS3Helper.downloadFile(localFile, new AwsS3Helper.S3DownloadListener() {
            @Override
            public void onDownloadSuccess(File file) {
                scheduleList = parseJsonFile(file);

                if (scheduleList != null) {
                    RecyclerViewHelper.updateRecyclerView(scheduleAdapter, recyclerView, scheduleList);
                } else {
                    Log.e("ScheduleActivity", "Parsed JSON returned null.");
                    Toast.makeText(ScheduleActivity.this, "Failed to parse schedule data.", Toast.LENGTH_LONG).show();
                }

                runOnUiThread(() -> progressBar.setVisibility(View.GONE));
                runOnUiThread(() -> progressText.setVisibility(View.GONE));
                recyclerView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onDownloadFailed() {
                Log.e("ScheduleActivity", "Failed to download schedule.json");
                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    progressText.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    Toast.makeText(ScheduleActivity.this, "Failed to download schedule. Please check your internet connection.", Toast.LENGTH_LONG).show();
                });
            }
        });
    }
}
