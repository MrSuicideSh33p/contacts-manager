package com.vichu.japantrip.activities;

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
import com.vichu.japantrip.adapters.EventAdapter;
import com.vichu.japantrip.adapters.ScheduleAdapter;
import com.vichu.japantrip.models.Event;
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

public class DailyScheduleActivity extends AppCompatActivity {

    private static final int EVENT_DETAILS_REQUEST_CODE = 100;
    private int scheduleIndex;
    private List<Event> events;
    private EventAdapter eventAdapter;
    private List<ScheduleDay> scheduleList;
    private AwsS3Helper awsS3Helper;
    private ProgressBar progressBar;
    private TextView progressText;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_schedule);

        progressText = findViewById(R.id.progressText);
        progressBar = findViewById(R.id.progressBar);
        awsS3Helper = new AwsS3Helper(this);

        // Set up toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Retrieve data from intent
        String dayTitle = getIntent().getStringExtra("dayTitle");
        String eventsJson = getIntent().getStringExtra("events");
        scheduleIndex = Integer.parseInt(getIntent().getStringExtra("scheduleIndex"));
        scheduleList = getIntent().getParcelableArrayListExtra("scheduleList");

        // Set the toolbar title to the selected day's title
        getSupportActionBar().setTitle(dayTitle);

        // Parse events JSON string to List<Event>
        Gson gson = new Gson();
        Type eventListType = new TypeToken<List<Event>>() {}.getType();
        events = gson.fromJson(eventsJson, eventListType);

        recyclerView = findViewById(R.id.recyclerViewEvents);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        eventAdapter = new EventAdapter(events);
        eventAdapter.setOnEventToggleListener(updatedEvent -> {
            if (updateEventInList(updatedEvent)) {
                uploadUpdatedSchedule();
            }
        });
        recyclerView.setAdapter(eventAdapter);
    }

    // Handle result from EventDetailActivity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == EVENT_DETAILS_REQUEST_CODE && resultCode == RESULT_OK) {
            if (data != null && data.hasExtra("updatedEvent")) {
                String updatedEventJson = data.getStringExtra("updatedEvent");
                Event updatedEvent = new Gson().fromJson(updatedEventJson, Event.class);
                if (updateEventInList(updatedEvent)) {
                    uploadUpdatedSchedule();
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.daily_schedule_menu, menu);
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
                    for (int i = 0; i < scheduleList.size(); i++) {
                        if (scheduleList.get(i).getScheduleIndex() == scheduleIndex) {
                            updateRecyclerView(scheduleList.get(i).getEvents());
                            break;
                        }
                    }
                } else {
                    Log.e("DailyScheduleActivity", "Parsed JSON returned null.");
                    showError("Failed to parse schedule data.");
                }

                runOnUiThread(() -> progressBar.setVisibility(View.GONE));
                runOnUiThread(() -> progressText.setVisibility(View.GONE));
                recyclerView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onDownloadFailed() {
                Log.e("DailyScheduleActivity", "Failed to download schedule.json");
                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    progressText.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    showError("Failed to download schedule. Please check your internet connection.");
                });
            }
        });
    }

    //TODO: Check possibility to extract this method from ScheduleActivity and DailyScheduleActivity
    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    //TODO: Check possibility to extract this method from ScheduleActivity and DailyScheduleActivity
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
                Log.e("DailyScheduleActivity", "Parsed JSON is null.");
                return null;
            }

        } catch (IOException e) {
            Log.e("DailyScheduleActivity", "Error reading schedule.json: " + e.getMessage(), e);
            return null;
        }
    }

    private void updateRecyclerView(List<Event> events) {
        runOnUiThread(() -> {
            if (eventAdapter != null) {
                eventAdapter.updateData(events);
            } else {
                eventAdapter = new EventAdapter(events);
                recyclerView.setAdapter(eventAdapter);
            }
        });
    }

    private boolean updateEventInList(Event updatedEvent) {
        boolean isUpdated = false;
        for (int i = 0; i < events.size(); i++) {
            if (events.get(i).getIndex() == updatedEvent.getIndex()) {
                events.set(i, updatedEvent);
                Log.w("DailyScheduleActivity", "Updated event notes: " + updatedEvent.getNotes());

                for (int j = 0; j < scheduleList.size(); j++) {
                    if (scheduleList.get(j).getScheduleIndex() == scheduleIndex) {
                        scheduleList.get(j).setEvents(events);
                        Log.w("DailyScheduleActivity", "Updated schedule title: " + scheduleList.get(j).getTitle());
                        isUpdated = true;
                    }
                }
                eventAdapter.notifyItemChanged(i);
                break;
            }
        }
        return isUpdated;
    }

    private void uploadUpdatedSchedule() {
        Gson gson = new Gson();
        String updatedJson = gson.toJson(scheduleList);

        AwsS3Helper awsS3Helper = new AwsS3Helper(this);
        awsS3Helper.uploadScheduleJson(updatedJson, success -> {
            if (success) {
                Log.d("DailyScheduleActivity", "Changes uploaded to S3 successfully.");
                runOnUiThread(() -> Toast.makeText(this, "Changes uploaded successfully", Toast.LENGTH_SHORT).show());

                // Return the updated schedule list to ScheduleActivity
                Intent resultIntent = new Intent();
                resultIntent.putExtra("updatedScheduleList", updatedJson);
                setResult(RESULT_OK, resultIntent);
            } else {
                Log.e("DailyScheduleActivity", "Failed to upload updated schedule.");
                runOnUiThread(() -> Toast.makeText(this, "Failed to upload changes. Please try again.", Toast.LENGTH_SHORT).show());
            }
        });
    }

    //TODO: check if onBackPressed is needed if its being handled above
    @Override
    public void onBackPressed() {
        Intent resultIntent = new Intent();
        resultIntent.putExtra("updatedScheduleList", new Gson().toJson(scheduleList));
        setResult(RESULT_OK, resultIntent);
        finish();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
