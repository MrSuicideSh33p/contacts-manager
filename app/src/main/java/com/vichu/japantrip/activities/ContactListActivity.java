package com.vichu.japantrip.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.vichu.japantrip.R;
import com.vichu.japantrip.utils.AwsS3Helper;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ContactListActivity extends AppCompatActivity {

    private ListView contactListView;
    private ImageView emptyStateImage;
    private ProgressBar progressBar;
    private ArrayAdapter<String> adapter;
    private final List<String> contactNames = new ArrayList<>();
    private final List<String> contactFiles = new ArrayList<>();
    private AwsS3Helper awsS3Helper;
    private static final int REQUEST_CODE_CONTACT_DETAILS = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_list);
        emptyStateImage = findViewById(R.id.emptyStateImage);
        contactListView = findViewById(R.id.contactListView);

        Toolbar toolbar = findViewById(R.id.contactListToolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> finish());

        ListView contactListView = findViewById(R.id.contactListView);
        progressBar = findViewById(R.id.progressBar);

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, contactNames);
        contactListView.setAdapter(adapter);

        awsS3Helper = new AwsS3Helper(this);

        fetchContacts();

        contactListView.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent(ContactListActivity.this, ContactDetailsActivity.class);
            intent.putExtra("contactName", contactNames.get(position));
            intent.putExtra("contactFile", contactFiles.get(position));
            startActivityForResult(intent, REQUEST_CODE_CONTACT_DETAILS);
        });
    }

    private void fetchContacts() {
        progressBar.setVisibility(View.VISIBLE);

        awsS3Helper.fetchContactList(new AwsS3Helper.S3ContactFetchListener() {
            @Override
            public void onSuccess(List<String> names, List<String> files) {
                contactNames.clear();
                contactFiles.clear();
                contactNames.addAll(names);
                contactFiles.addAll(files);

                runOnUiThread(() -> {
                    adapter.notifyDataSetChanged();
                    progressBar.setVisibility(View.GONE);

                    if (contactNames.isEmpty()) {
                        emptyStateImage.setVisibility(View.VISIBLE);
                        contactListView.setVisibility(View.GONE);
                    } else {
                        emptyStateImage.setVisibility(View.GONE);
                        contactListView.setVisibility(View.VISIBLE);
                    }
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    Toast.makeText(ContactListActivity.this, "Error fetching contacts: " + error, Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                });
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_CONTACT_DETAILS && resultCode == RESULT_OK) {
            fetchContacts();  // Refresh contacts after an update or delete
        }
    }
}
