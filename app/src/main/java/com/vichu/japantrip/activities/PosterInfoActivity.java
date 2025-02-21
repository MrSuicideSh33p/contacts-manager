package com.vichu.japantrip.activities;

import static com.vichu.japantrip.utils.NumToAplhaHelper.numToLetterByAsciiCode;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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
import com.vichu.japantrip.adapters.PosterInfoAdapter;
import com.vichu.japantrip.models.Poster;
import com.vichu.japantrip.models.PosterInfo;
import com.vichu.japantrip.utils.AwsS3Helper;
import com.vichu.japantrip.utils.DownloadHelper;
import com.vichu.japantrip.utils.RecyclerViewHelper;

import java.io.File;
import java.lang.reflect.Type;
import java.util.Comparator;
import java.util.List;

public class PosterInfoActivity extends AppCompatActivity {

    private static final int POSTER_DETAILS_REQUEST_CODE = 103;
    private final String file = "poster/poster.json";
    private int posterIndex;
    private List<PosterInfo> posterInfoList;
    private PosterInfoAdapter posterInfoAdapter;
    private List<Poster> posterList;
    private AwsS3Helper awsS3Helper;
    private ProgressBar progressBar;
    private TextView progressText;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poster_info);

        progressText = findViewById(R.id.progressText);
        progressBar = findViewById(R.id.progressBar);
        awsS3Helper = new AwsS3Helper(this);

        // Set up toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Retrieve data from intent
        String posterInfoJson = getIntent().getStringExtra("posterInfo");
        posterIndex = Integer.parseInt(getIntent().getStringExtra("posterIndex"));
        posterList = getIntent().getParcelableArrayListExtra("posterList");

        // Set the toolbar title to the selected day's title
        getSupportActionBar().setTitle("Poster Set " + numToLetterByAsciiCode(posterIndex));

        // Parse posterInfo JSON string to List<PosterInfo>
        Gson gson = new Gson();
        Type posterInfoListType = new TypeToken<List<PosterInfo>>() {}.getType();
        posterInfoList = gson.fromJson(posterInfoJson, posterInfoListType);

        recyclerView = findViewById(R.id.recyclerViewPosterInfo);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        posterInfoAdapter = new PosterInfoAdapter(posterInfoList);
        posterInfoAdapter.setOnPosterInfoToggleListener(updatedPosterInfo -> {
            if (updatePosterInList(updatedPosterInfo)) {
                uploadUpdatedPoster();
            }
        });
        recyclerView.setAdapter(posterInfoAdapter);
    }

    // Handle result from PosterDetailActivity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == POSTER_DETAILS_REQUEST_CODE && resultCode == RESULT_OK) {
            if (data != null && data.hasExtra("updatedPosterInfo")) {
                String updatedPosterInfoJson = data.getStringExtra("updatedPosterInfo");
                PosterInfo updatedPosterInfo = new Gson().fromJson(updatedPosterInfoJson, PosterInfo.class);
                if (updatePosterInList(updatedPosterInfo)) {
                    uploadUpdatedPoster();
                }
            }
        }
    }

    private void downloadPosterJsonAndRefreshUI() {
        File localFile = new File(getFilesDir(), "poster.json");
        DownloadHelper.downloadJson(this, localFile, awsS3Helper, progressBar, progressText, recyclerView, new DownloadHelper.DownloadListener<Poster>() {
            @Override
            public void onDownloadSuccess(List<Poster> posterList) {
                for (int i = 0; i < posterList.size(); i++) {
                    if (posterList.get(i).getPosterIndex() == posterIndex) {
                        RecyclerViewHelper.updateRecyclerView(posterInfoAdapter, recyclerView, posterList.get(i).getPosterInfo());
                        break;
                    }
                }
            }

            @Override
            public void onDownloadFailed() {
                Toast.makeText(PosterInfoActivity.this, "Failed to download poster list. Please check your internet connection.", Toast.LENGTH_LONG).show();
            }
        }, file, new TypeToken<List<Poster>>() {}.getType(), Comparator.comparingInt(Poster::getPosterIndex));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.poster_info_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_refresh) {
            downloadPosterJsonAndRefreshUI();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean updatePosterInList(PosterInfo updatedPosterInfo) {
        boolean isUpdated = false;
        for (int i = 0; i < posterInfoList.size(); i++) {
            if (posterInfoList.get(i).getIndex() == updatedPosterInfo.getIndex()) {
                posterInfoList.set(i, updatedPosterInfo);
                Log.w("PosterInfoActivity", "Updated poster notes: " + updatedPosterInfo.getNotes());

                for (int j = 0; j < posterList.size(); j++) {
                    if (posterList.get(j).getPosterIndex() == posterIndex) {
                        posterList.get(j).setPosterInfo(posterInfoList);
                        Log.w("PosterInfoActivity", "Updated poster titled: " + posterList.get(j).getTitle());
                        isUpdated = true;
                    }
                }
                posterInfoAdapter.notifyItemChanged(i);
                break;
            }
        }
        return isUpdated;
    }

    private void uploadUpdatedPoster() {
        String updatedJson = new Gson().toJson(posterList);
        awsS3Helper.uploadUpdatedJson(file, updatedJson, this::handleUploadResult);
    }

    private void handleUploadResult(boolean success) {
        if (success) {
            Log.d("PosterInfoActivity", "Changes uploaded to S3 successfully.");
            runOnUiThread(() -> Toast.makeText(this, "Changes uploaded successfully", Toast.LENGTH_SHORT).show());

            // Return the updated poster list to PosterActivity
            Intent resultIntent = new Intent();
            resultIntent.putExtra("updatedPosterList", new Gson().toJson(posterList));
            setResult(RESULT_OK, resultIntent);
        } else {
            Log.e("PosterInfoActivity", "Failed to upload updated poster list.");
            runOnUiThread(() -> Toast.makeText(this, "Failed to upload changes. Please try again.", Toast.LENGTH_SHORT).show());
        }
    }

    @Override
    public void onBackPressed() {
        Intent resultIntent = new Intent();
        resultIntent.putExtra("updatedPosterList", new Gson().toJson(posterList));
        setResult(RESULT_OK, resultIntent);
        finish();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
