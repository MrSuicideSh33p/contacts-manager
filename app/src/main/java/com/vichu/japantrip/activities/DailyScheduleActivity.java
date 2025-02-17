package com.vichu.japantrip.activities;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.vichu.japantrip.R;
import com.vichu.japantrip.adapters.EventAdapter;
import com.vichu.japantrip.models.Event;
import java.lang.reflect.Type;
import java.util.List;

public class DailyScheduleActivity extends AppCompatActivity {

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

        // Set the toolbar title to the selected day's title
        getSupportActionBar().setTitle(dayTitle);

        // Parse events JSON string to List<Event>
        Gson gson = new Gson();
        Type eventListType = new TypeToken<List<Event>>() {}.getType();
        List<Event> events = gson.fromJson(eventsJson, eventListType);

        // Set up RecyclerView
        RecyclerView recyclerView = findViewById(R.id.recyclerViewEvents);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        EventAdapter eventAdapter = new EventAdapter(events);
        recyclerView.setAdapter(eventAdapter);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
