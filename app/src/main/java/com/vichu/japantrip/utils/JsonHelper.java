package com.vichu.japantrip.utils;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.vichu.japantrip.models.ScheduleDay;

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

public class JsonHelper {

    public static List<ScheduleDay> parseJsonFile(File file) {
        try {
            // Read the contents of the JSON file
            List<ScheduleDay> scheduleList = getScheduleDays(file);

            if (scheduleList != null) {
                // Sort by index before displaying
                Collections.sort(scheduleList, Comparator.comparingInt(ScheduleDay::getScheduleIndex));
                return scheduleList;
            } else {
                Log.e("JsonHelper", "Parsed JSON is null.");
                return null;
            }

        } catch (IOException e) {
            Log.e("JsonHelper", "Error reading schedule.json: " + e.getMessage(), e);
            return null;
        }
    }

    private static List<ScheduleDay> getScheduleDays(File file) throws IOException {
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
        return scheduleList;
    }
}
