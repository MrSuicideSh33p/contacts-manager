package com.vichu.japantrip.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.vichu.japantrip.R;
import com.vichu.japantrip.models.ContactData;
import com.vichu.japantrip.utils.AwsS3Helper;

import java.io.File;
import java.io.FileWriter;

public class ContactEntryActivity extends AppCompatActivity {

    private EditText nameInput, nickNameInput, phoneInput, emailInput, notesInput;
    private Button saveButton;
    private AwsS3Helper awsS3Helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_entry);

        // Initialize UI elements
        nameInput = findViewById(R.id.et_name);
        nickNameInput = findViewById(R.id.et_nickname);
        phoneInput = findViewById(R.id.et_phone);
        emailInput = findViewById(R.id.et_email);
        notesInput = findViewById(R.id.et_notes);
        saveButton = findViewById(R.id.btn_save_contact);
        awsS3Helper = new AwsS3Helper(this);

        // Handle save button click
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveContact();
            }
        });
    }

    private void saveContact() {
        String name = nameInput.getText().toString().trim();
        String nickName = nickNameInput.getText().toString().trim();
        String phone = phoneInput.getText().toString().trim();
        String email = emailInput.getText().toString().trim();
        String notes = notesInput.getText().toString().trim();

        if (name.isEmpty()) {
            Toast.makeText(ContactEntryActivity.this, "Name field is required!", Toast.LENGTH_SHORT).show();
            return;
        }

        ContactData contactData = new ContactData(name, nickName, phone, email, notes);
        String contactContent = contactData.toFileFormat();
        String fileName = "contacts/" + name.replaceAll("\\s+", "_") + ".txt"; // Ensure a valid file name

        awsS3Helper.uploadContact(fileName, contactContent, success -> {
            runOnUiThread(() -> {
                if (success) {
                    Toast.makeText(this, "Contact uploaded to S3!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(this, "Failed to save contact!", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

}
