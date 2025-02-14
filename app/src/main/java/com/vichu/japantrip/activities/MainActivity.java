package com.vichu.japantrip.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.vichu.japantrip.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize buttons
        Button addContactBtn = findViewById(R.id.btn_add_contact);
        Button viewContactsBtn = findViewById(R.id.btn_view_contacts);

        // Navigate to Contact Entry Page
        addContactBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ContactEntryActivity.class);
                startActivity(intent);
            }
        });

        // Navigate to Contact Retrieval Page
        viewContactsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ContactListActivity.class);
                startActivity(intent);
            }
        });
    }
}
