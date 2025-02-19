package com.vichu.japantrip.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.vichu.japantrip.R;
import com.vichu.japantrip.adapters.EventAdapter;
import com.vichu.japantrip.models.Event;
import com.vichu.japantrip.models.ScheduleDay;
import com.vichu.japantrip.utils.AwsS3Helper;

import java.lang.reflect.Type;
import java.util.List;

public class DailyScheduleActivity extends AppCompatActivity {

    private static final int EVENT_DETAILS_REQUEST_CODE = 100;
    private int scheduleIndex;
    private List<Event> events;
    private EventAdapter eventAdapter;
    private List<ScheduleDay> scheduleList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_schedule);

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

        // Set up RecyclerView
        RecyclerView recyclerView = findViewById(R.id.recyclerViewEvents);
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
