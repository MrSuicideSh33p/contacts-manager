package com.vichu.japantrip.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.vichu.japantrip.R;
import com.vichu.japantrip.models.ContactData;
import com.vichu.japantrip.utils.AwsS3Helper;

import java.util.Objects;

public class ContactDetailsActivity extends AppCompatActivity {

    private EditText nameEditText, nickNameEditText, phoneEditText, emailEditText, fieldText, universityText, notesText;
    private AwsS3Helper awsS3Helper;
    private String contactFile;
    private boolean isEditing = false;
    private MenuItem saveMenuItem, editMenuItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_details);

        nameEditText = findViewById(R.id.nameEditText);
        nickNameEditText = findViewById(R.id.nickNameEditText);
        phoneEditText = findViewById(R.id.phoneEditText);
        emailEditText = findViewById(R.id.emailEditText);
        fieldText = findViewById(R.id.fieldText);
        universityText = findViewById(R.id.universityText);
        notesText = findViewById(R.id.notesText);

        awsS3Helper = new AwsS3Helper(this);

        Toolbar toolbar = findViewById(R.id.contactDetailsToolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> finish());

        Intent intent = getIntent();
        contactFile = intent.getStringExtra("contactFile");

        loadContactDetails();

        // Initially disable editing
        setEditingEnabled(false);
    }

    private void loadContactDetails() {
        awsS3Helper.fetchContactDetails(contactFile, (contactData, errorMessage) -> {
            if (errorMessage != null) {
                runOnUiThread(() -> Toast.makeText(this, "Error: " + errorMessage, Toast.LENGTH_SHORT).show());
            } else {
                runOnUiThread(() -> {
                    nameEditText.setText(contactData.getName());
                    nickNameEditText.setText(contactData.getNickName());
                    phoneEditText.setText(contactData.getPhone());
                    emailEditText.setText(contactData.getEmail());
                    fieldText.setText(contactData.getField());
                    universityText.setText(contactData.getUniversity());
                    notesText.setText(contactData.getNotes());
                });
            }
        });
    }

    private void setEditingEnabled(boolean enabled) {
        nameEditText.setEnabled(enabled);
        nickNameEditText.setEnabled(enabled);
        phoneEditText.setEnabled(enabled);
        emailEditText.setEnabled(enabled);
        fieldText.setEnabled(enabled);
        universityText.setEnabled(enabled);
        notesText.setEnabled(enabled);
        isEditing = enabled;
        if (saveMenuItem != null) {
            saveMenuItem.setVisible(enabled);
        }
        if (editMenuItem != null) {
            editMenuItem.setVisible(enabled ? false : true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.contact_details_menu, menu);
        saveMenuItem = menu.findItem(R.id.save_contact);
        editMenuItem = menu.findItem(R.id.edit_contact);
        saveMenuItem.setVisible(false); // Hide save initially
        editMenuItem.setVisible(true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.edit_contact) {
            setEditingEnabled(true);
            return true;
        } else if (item.getItemId() == R.id.save_contact) {
            saveContact();
            return true;
        } else if (item.getItemId() == R.id.delete_contact) {
            confirmDelete();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void confirmDelete() {
        new AlertDialog.Builder(this)
                .setTitle("Delete Connection")
                .setMessage("Are you sure you want to delete this connection?")
                .setPositiveButton("Delete", (dialog, which) -> deleteContact())
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deleteContact() {
        awsS3Helper.deleteContact(contactFile, success -> {
            if (success) {
                runOnUiThread(() -> {
                    Toast.makeText(this, "Connection deleted", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                });
            }
        });
    }

    private void saveContact() {
        String newName = nameEditText.getText().toString().trim();
        String newNickName = nickNameEditText.getText().toString().trim();
        String newPhone = phoneEditText.getText().toString().trim();
        String newEmail = emailEditText.getText().toString().trim();
        String newField = fieldText.getText().toString().trim();
        String newUniversity = universityText.getText().toString().trim();
        String newNotes = notesText.getText().toString().trim();

        if (newName.isEmpty()) {
            Toast.makeText(this, "Name field is required!", Toast.LENGTH_SHORT).show();
            return;
        }

        ContactData updatedContact = new ContactData(newName, newNickName, newPhone, newEmail, newField, newUniversity, newNotes);
        String updatedFileContent = updatedContact.toFileFormat();

        awsS3Helper.uploadContact(contactFile, updatedFileContent, (success) -> {
            runOnUiThread(() -> {
                if (success) {
                    Toast.makeText(this, "Connection updated", Toast.LENGTH_SHORT).show();
                    setEditingEnabled(false);

                    // Redirect back to ContactListActivity
                    Intent intent = new Intent(this, ContactListActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(this, "Update failed", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

}
