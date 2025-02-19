package com.vichu.japantrip.utils;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

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

public class ScheduleDownloadHelper {

    public interface DownloadListener {
        void onDownloadSuccess(List<ScheduleDay> scheduleList);
        void onDownloadFailed();
    }

    public static void downloadScheduleJson(Context context, File localFile, AwsS3Helper awsS3Helper,
                                            ProgressBar progressBar, TextView progressText, RecyclerView recyclerView,
                                            DownloadListener listener) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(View.VISIBLE);
                progressText.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            }
        });

        awsS3Helper.downloadFile(localFile, new AwsS3Helper.S3DownloadListener() {
            @Override
            public void onDownloadSuccess(File file) {
                List<ScheduleDay> scheduleList = parseJsonFile(file);

                if (scheduleList != null) {
                    listener.onDownloadSuccess(scheduleList);
                } else {
                    Log.e("ScheduleDownloadHelper", "Parsed JSON returned null.");
                    Toast.makeText(context, "Failed to parse schedule data.", Toast.LENGTH_LONG).show();
                }

                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setVisibility(View.GONE);
                        progressText.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                    }
                });
            }

            @Override
            public void onDownloadFailed() {
                Log.e("ScheduleDownloadHelper", "Failed to download schedule.json");
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setVisibility(View.GONE);
                        progressText.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                    }
                });
                listener.onDownloadFailed();
            }
        });
    }

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
