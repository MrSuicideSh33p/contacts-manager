package com.vichu.japantrip.activities;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.vichu.japantrip.R;
import com.vichu.japantrip.utils.AwsS3Helper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class ContactListActivity extends AppCompatActivity {

    private ListView contactListView;
    private AwsS3Helper awsS3Helper;
    private List<String> contactList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_list);

        contactListView = findViewById(R.id.lv_contacts);
        awsS3Helper = new AwsS3Helper(this);
        loadContactsFromS3();
    }

    private void loadContactsFromS3() {
        new Thread(() -> {
            try {
                runOnUiThread(() -> Toast.makeText(this, "Fetching contacts...", Toast.LENGTH_SHORT).show());

                File file = awsS3Helper.downloadFile("contacts/latest_contacts.txt", this);

                if (file != null && file.exists()) {
                    List<String> tempContactList = new ArrayList<>();
                    BufferedReader reader = new BufferedReader(new FileReader(file));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        tempContactList.add(line);
                    }
                    reader.close();

                    // Update UI on the main thread
                    runOnUiThread(() -> {
                        contactList.clear();
                        contactList.addAll(tempContactList);
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, contactList);
                        contactListView.setAdapter(adapter);
                        Toast.makeText(this, "Contacts loaded successfully!", Toast.LENGTH_SHORT).show();
                    });
                } else {
                    runOnUiThread(() -> Toast.makeText(this, "No contacts found!", Toast.LENGTH_SHORT).show());
                }
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(this, "Error reading file: " + e.getMessage(), Toast.LENGTH_LONG).show());
            }
        }).start();
    }

}
