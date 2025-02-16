package com.vichu.japantrip.activities;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.vichu.japantrip.models.ScheduleDay;
import com.vichu.japantrip.repository.ScheduleRepository;

import java.util.List;

public class ScheduleActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ScheduleRepository repository = new ScheduleRepository(this);
        repository.fetchScheduleData(new ScheduleRepository.ScheduleCallback() {
            @Override
            public void onSuccess(List<ScheduleDay> scheduleDays) {
                for (ScheduleDay day : scheduleDays) {
                    Log.d("TEST", "Date: " + day.getDate());
                }
            }

            @Override
            public void onFailure() {
                Log.e("TEST", "Failed to fetch schedule");
            }
        });
    }

}
