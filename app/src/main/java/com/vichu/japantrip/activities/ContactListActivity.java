package com.vichu.japantrip.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
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

    private MenuItem searchItem;
    private TextView progressText;
    private ListView contactListView;
    private ImageView emptyStateImage;
    private ProgressBar progressBar;
    private ArrayAdapter<String> adapter;
    private final List<String> contactNames = new ArrayList<>();
    private final List<String> reusableContactNames = new ArrayList<>(); // Store original list separately
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

        progressText = findViewById(R.id.progressText);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.contact_list_menu, menu);
        searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setIconified(false);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false; // No need to handle submit separately
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.isEmpty()) {
                    resetList(); // Show all contacts when empty
                } else {
                    filterContacts(newText); // Perform fuzzy search
                }
                return true;
            }
        });

        searchView.setOnCloseListener(() -> {
            searchView.setQuery("", false);  // Clear search bar text
            searchItem.collapseActionView(); // Collapse search view
            return false;
        });

        searchView.setOnQueryTextFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                searchView.setQuery("", false);  // Clear text when losing focus
                searchView.setIconified(false);
            }
        });

        return true;
    }

    public void filterContacts(String query) {
        List<String> filteredList = new ArrayList<>();
        for (String contact : reusableContactNames) {
            if (contact.toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(contact);
            }
        }
        contactNames.clear();
        contactNames.addAll(filteredList);
        adapter.notifyDataSetChanged();
    }

    public void resetList() {
        contactNames.clear();
        contactNames.addAll(reusableContactNames);
        adapter.notifyDataSetChanged();
    }

    private void fetchContacts() {
        progressBar.setVisibility(View.VISIBLE);
        progressText.setVisibility(View.VISIBLE);

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
                    progressText.setVisibility(View.GONE);

                    if (contactNames.isEmpty()) {
                        emptyStateImage.setVisibility(View.VISIBLE);
                        contactListView.setVisibility(View.GONE);
                    } else {
                        reusableContactNames.clear();
                        reusableContactNames.addAll(contactNames);
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
                    progressText.setVisibility(View.GONE);
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
