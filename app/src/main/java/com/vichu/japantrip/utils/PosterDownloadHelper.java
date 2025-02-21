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
import com.vichu.japantrip.models.Poster;

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

public class PosterDownloadHelper {

    private static final String file = "poster/poster.json";

    public interface DownloadListener {
        void onDownloadSuccess(List<Poster> posterList);
        void onDownloadFailed();
    }

    public static void downloadPosterJson(Context context, File localFile, AwsS3Helper awsS3Helper,
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

        awsS3Helper.downloadFile(file, localFile, new AwsS3Helper.S3DownloadListener() {
            @Override
            public void onDownloadSuccess(File file) {
                List<Poster> posterList = parseJsonFile(file);

                if (posterList != null) {
                    listener.onDownloadSuccess(posterList);
                } else {
                    Log.e("PosterDownloadHelper", "Parsed JSON returned null.");
                    Toast.makeText(context, "Failed to parse poster data.", Toast.LENGTH_LONG).show();
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
                Log.e("PosterDownloadHelper", "Failed to download poster.json");
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

    public static List<Poster> parseJsonFile(File file) {
        try {
            // Read the contents of the JSON file
            List<Poster> posterList = getPosters(file);

            if (posterList != null) {
                // Sort by index before displaying
                Collections.sort(posterList, Comparator.comparingInt(Poster::getPosterIndex));
                return posterList;
            } else {
                Log.e("JsonHelper", "Parsed JSON is null.");
                return null;
            }

        } catch (IOException e) {
            Log.e("JsonHelper", "Error reading poster.json: " + e.getMessage(), e);
            return null;
        }
    }

    private static List<Poster> getPosters(File file) throws IOException {
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

        // Convert JSON to List of Poster
        Gson gson = new Gson();
        Type listType = new TypeToken<List<Poster>>() {}.getType();
        List<Poster> posterList = gson.fromJson(jsonString.toString(), listType);
        return posterList;
    }
}
