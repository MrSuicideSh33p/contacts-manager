package com.vichu.japantrip.repository;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.vichu.japantrip.models.ScheduleDay;
import com.vichu.japantrip.utils.AwsS3Helper;

import java.io.File;
import java.io.FileReader;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

public class ScheduleRepository {
    private static final String TAG = "ScheduleRepository";
    private final Context context;
    private final AwsS3Helper awsS3Helper;
    private static final String LOCAL_JSON_PATH = "schedule.json";

    public ScheduleRepository(Context context) {
        this.context = context;
        awsS3Helper = new AwsS3Helper(context);
    }

    public interface ScheduleCallback {
        void onSuccess(List<ScheduleDay> scheduleDays);
        void onFailure();
    }

    public void fetchScheduleData(ScheduleCallback callback) {
        File localFile = new File(context.getCacheDir(), LOCAL_JSON_PATH);

        awsS3Helper.downloadFile(localFile, new AwsS3Helper.S3DownloadListener() {
            @Override
            public void onDownloadSuccess(File file) {
                try {
                    Gson gson = new Gson();
                    Type listType = new TypeToken<List<ScheduleDay>>(){}.getType();
                    List<ScheduleDay> scheduleList = gson.fromJson(new FileReader(file), listType);

                    // Sort by index
                    Collections.sort(scheduleList, (a, b) -> Integer.compare(a.getIndex(), b.getIndex()));

                    callback.onSuccess(scheduleList);
                } catch (Exception e) {
                    Log.e(TAG, "Error parsing JSON", e);
                    callback.onFailure();
                }
            }

            @Override
            public void onDownloadFailed() {
                callback.onFailure();
            }
        });
    }
}
