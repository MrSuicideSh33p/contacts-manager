package com.vichu.japantrip.repository;

import android.util.Log;

import com.vichu.japantrip.models.ScheduleDay;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class ScheduleRepository {
    public static List<ScheduleDay> loadSchedule(File file) {
        List<ScheduleDay> scheduleList = new ArrayList<>();
        try {
            String json = new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);
            JSONArray jsonArray = new JSONArray(json);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);
                String date = obj.getString("date");
                String title = obj.getString("title");
                int index = obj.getInt("index");
                List<String> events = new ArrayList<>();
                JSONArray eventsArray = obj.getJSONArray("events");
                for (int j = 0; j < eventsArray.length(); j++) {
                    events.add(eventsArray.getString(j));
                }
                scheduleList.add(new ScheduleDay(date, title, index, events));
            }
        } catch (Exception e) {
            Log.e("ScheduleRepository", "Error reading JSON", e);
        }
        return scheduleList;
    }
}
