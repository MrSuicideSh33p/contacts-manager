package com.vichu.japantrip.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.gson.Gson;
import com.vichu.japantrip.R;
import com.vichu.japantrip.models.PosterInfo;

public class PosterDetailActivity extends AppCompatActivity {
    private EditText editTextAuthor, editTextUniversity, editTextTopic, editTextNotes;
    private boolean isEditing = false;
    private MenuItem editMenuItem, saveMenuItem;
    private PosterInfo posterInfo;
    private String originalNotes;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poster_detail);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        editTextAuthor = findViewById(R.id.editTextAuthor);
        editTextUniversity = findViewById(R.id.editTextUniversity);
        editTextTopic = findViewById(R.id.editTextTopic);
        editTextNotes = findViewById(R.id.editTextNotes);

        // Get posterInfo data from intent
        String posterInfoJson = getIntent().getStringExtra("posterInfo");
        if (posterInfoJson != null) {
            posterInfo = new Gson().fromJson(posterInfoJson, PosterInfo.class);
            populatePosterInfoData();

            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle(posterInfo.getAuthor());
            }
        }

        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    //TODO: Check if the way this is done is optimal
    @Override
    protected void onResume() {
        super.onResume();
        String posterInfoJson = getIntent().getStringExtra("updatedPosterInfo");
        if (posterInfoJson != null) {
            posterInfo = new Gson().fromJson(posterInfoJson, PosterInfo.class);
            populatePosterInfoData();
        }
    }

    private void populatePosterInfoData() {
        if (posterInfo != null) {
            editTextAuthor.setText(posterInfo.getAuthor());
            editTextUniversity.setText(posterInfo.getUniversity());
            editTextTopic.setText(posterInfo.getTopic());
            editTextNotes.setText(posterInfo.getNotes());

            // Store original values to check for changes
            originalNotes = posterInfo.getNotes();

            setEditingEnabled(false);
        }
    }

    private void setEditingEnabled(boolean enabled) {
        editTextNotes.setEnabled(enabled);

        int greyColor = getResources().getColor(R.color.gray, getTheme());
        int blackColor = getResources().getColor(R.color.black, getTheme());

        editTextAuthor.setTextColor(enabled ? greyColor : blackColor);
        editTextUniversity.setTextColor(enabled ? greyColor : blackColor);
        editTextTopic.setTextColor(enabled ? greyColor : blackColor);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_poster_detail, menu);
        editMenuItem = menu.findItem(R.id.action_edit);
        saveMenuItem = menu.findItem(R.id.action_save);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_edit) {
            enableEditing();
            return true;
        } else if (item.getItemId() == R.id.action_save) {
            saveChanges();
            Toast.makeText(this, "Changes saved!", Toast.LENGTH_SHORT).show();

            // Send updated posterInfo back to PosterInfoActivity
            Intent intent = new Intent();
            intent.putExtra("updatedPosterInfo", new Gson().toJson(posterInfo));
            setResult(RESULT_OK, intent);

            finish(); // Close the current activity
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void enableEditing() {
        isEditing = true;
        setEditingEnabled(true);
        editMenuItem.setVisible(false);
        saveMenuItem.setVisible(true);
    }

    private void saveChanges() {
        posterInfo.setNotes(editTextNotes.getText().toString());
        isEditing = false;
        setEditingEnabled(false);
        editMenuItem.setVisible(true);
        saveMenuItem.setVisible(false);
    }

    @Override
    public void onBackPressed() {
        if (isEditing && hasUnsavedChanges()) {
            showUnsavedChangesDialog();
        } else {
            super.onBackPressed();
        }
    }

    private boolean hasUnsavedChanges() {
        return !editTextNotes.getText().toString().equals(originalNotes);
    }

    private void showUnsavedChangesDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Unsaved Changes")
                .setMessage("You have unsaved changes. Do you want to discard them?")
                .setPositiveButton("Discard", (dialog, which) -> finish())
                .setNegativeButton("Cancel", null)
                .show();
    }
}
