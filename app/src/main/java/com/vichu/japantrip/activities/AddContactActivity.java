package com.vichu.japantrip.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.vichu.japantrip.R;
import com.vichu.japantrip.models.ContactData;
import com.vichu.japantrip.utils.AwsS3Helper;

public class AddContactActivity extends AppCompatActivity {

    private EditText nameInput, nickNameInput, phoneInput, emailInput, fieldInput, universityInput, notesInput;
    private AwsS3Helper awsS3Helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);

        Toolbar toolbar = findViewById(R.id.contactEntryToolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }

        // Initialize UI elements
        nameInput = findViewById(R.id.et_name);
        nickNameInput = findViewById(R.id.et_nickname);
        phoneInput = findViewById(R.id.et_phone);
        emailInput = findViewById(R.id.et_email);
        fieldInput = findViewById(R.id.et_field);
        universityInput = findViewById(R.id.et_university);
        notesInput = findViewById(R.id.et_notes);
        awsS3Helper = new AwsS3Helper(this);
    }

    @Override
    public void onBackPressed() {
        if (isAnyFieldFilled()) {
            new AlertDialog.Builder(this)
                    .setTitle("Confirm Exit")
                    .setMessage("The text you entered will be lost. Are you sure you want to proceed?")
                    .setPositiveButton("Yes", (dialog, which) -> super.onBackPressed()) // Go back
                    .setNegativeButton("No", (dialog, which) -> dialog.dismiss()) // Stay on page
                    .show();
        } else {
            super.onBackPressed(); // No changes, exit normally
        }
    }

    private boolean isAnyFieldFilled() {
        return !nameInput.getText().toString().trim().isEmpty() ||
                !nickNameInput.getText().toString().trim().isEmpty() ||
                !phoneInput.getText().toString().trim().isEmpty() ||
                !emailInput.getText().toString().trim().isEmpty() ||
                !fieldInput.getText().toString().trim().isEmpty() ||
                !universityInput.getText().toString().trim().isEmpty() ||
                !notesInput.getText().toString().trim().isEmpty();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.contact_entry_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            confirmExit();
            return true;
        } else if (item.getItemId() == R.id.action_save) {
            saveContact(); // Handle save button
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void confirmExit() {
        if (isAnyFieldFilled()) {
            new AlertDialog.Builder(this)
                    .setTitle("Confirm Exit")
                    .setMessage("The text you entered will be lost. Are you sure you want to proceed?")
                    .setPositiveButton("Yes", (dialog, which) -> finish()) // Close activity
                    .setNegativeButton("No", (dialog, which) -> dialog.dismiss()) // Stay on page
                    .show();
        } else {
            finish(); // Exit if no fields are filled
        }
    }

    private void saveContact() {
        String name = nameInput.getText().toString().trim();
        String nickName = nickNameInput.getText().toString().trim();
        String phone = phoneInput.getText().toString().trim();
        String email = emailInput.getText().toString().trim();
        String field = fieldInput.getText().toString().trim();
        String university = universityInput.getText().toString().trim();
        String notes = notesInput.getText().toString().trim();

        if (name.isEmpty()) {
            Toast.makeText(AddContactActivity.this, "Name field is required!", Toast.LENGTH_SHORT).show();
            return;
        }

        ContactData contactData = new ContactData(name, nickName, phone, email, field, university, notes);
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
