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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.Comparator;
import java.util.List;

public class DownloadHelper {

    public interface DownloadListener<T> {
        void onDownloadSuccess(List<T> list);
        void onDownloadFailed();
    }

    public static <T> void downloadJson(Context context, File localFile, AwsS3Helper awsS3Helper,ProgressBar progressBar, TextView progressText,
                                        RecyclerView recyclerView, DownloadListener<T> listener, String file, Type type, Comparator<T> comparator) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(View.VISIBLE);
                progressText.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            }
        });

        awsS3Helper.downloadFile(file, localFile, new AwsS3Helper.S3DownloadListener() {
            @Override
            public void onDownloadSuccess(File file) {
                List<T> list = parseJsonFile(file, type, comparator);

                if (list != null) {
                    listener.onDownloadSuccess(list);
                } else {
                    Log.e("DownloadHelper", "Parsed JSON returned null.");
                    Toast.makeText(context, "Failed to parse JSON data.", Toast.LENGTH_LONG).show();
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
                Log.e("DownloadHelper", "Failed to download json file.");
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

    public static <T> List<T> parseJsonFile(File file, Type type, Comparator<T> comparator) {
        try {
            // Read the contents of the JSON file
            List<T> list = getList(file, type);

            if (list != null && !list.isEmpty()) {
                list.sort(comparator);
                return list;
            } else {
                Log.e("DownloadHelper", "Parsed JSON is null.");
                return null;
            }

        } catch (IOException e) {
            Log.e("DownloadHelper", "Error reading json file: " + e.getMessage(), e);
            return null;
        }
    }

    private static <T> List<T> getList(File file, Type type) throws IOException {
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

        // Convert JSON to List of Type T
        Gson gson = new Gson();
        return gson.fromJson(jsonString.toString(), type);
    }
}
