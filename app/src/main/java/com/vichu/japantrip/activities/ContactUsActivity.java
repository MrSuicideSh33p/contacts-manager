package com.vichu.japantrip.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.vichu.japantrip.R;

import java.util.Objects;

public class ContactUsActivity extends AppCompatActivity {

    private EditText fromNameEditText, titleEditText, descriptionEditText;
    private static final String TO_EMAIL = "mvnath1998@gmail.com";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_us);

        Toolbar toolbar = findViewById(R.id.contactUsToolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> finish());

        fromNameEditText = findViewById(R.id.fromNameEditText);
        titleEditText = findViewById(R.id.titleEditText);
        descriptionEditText = findViewById(R.id.descriptionEditText);
        Button sendButton = findViewById(R.id.sendButton);

        sendButton.setOnClickListener(v -> sendEmail());
    }

    private void sendEmail() {
        String fromName = fromNameEditText.getText().toString().trim();
        String title = titleEditText.getText().toString().trim();
        String description = descriptionEditText.getText().toString().trim();

        if (fromName.isEmpty() || title.isEmpty() || description.isEmpty()) {
            Toast.makeText(this, "Please fill out all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:"));
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{TO_EMAIL});
        intent.putExtra(Intent.EXTRA_SUBJECT, title);
        intent.putExtra(Intent.EXTRA_TEXT, description + "\n\n" + "With Regards, \n" + fromName);

        try {
            startActivity(Intent.createChooser(intent, "Send Email"));
        } catch (Exception e) {
            Toast.makeText(this, "No email client installed!", Toast.LENGTH_SHORT).show();
        }
    }
}
