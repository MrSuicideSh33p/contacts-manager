package com.vichu.japantrip.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.vichu.japantrip.R;

public class PasscodeActivity extends AppCompatActivity {
    private EditText passcodeInput;
    private SharedPreferences sharedPreferences;
    private static final String PASSCODE_KEY = "1234";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passcode);

        passcodeInput = findViewById(R.id.passcodeInput);
        Button submitButton = findViewById(R.id.submitButton);
        sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);

        // Check if passcode already exists
        String savedPasscode = sharedPreferences.getString(PASSCODE_KEY, null);

        if (savedPasscode == null) {
            // First-time setup
            submitButton.setOnClickListener(view -> setupNewPasscode());
        } else {
            // Passcode verification
            submitButton.setOnClickListener(view -> verifyPasscode(savedPasscode));
        }
    }

    private void setupNewPasscode() {
        String enteredPasscode = passcodeInput.getText().toString();
        if (enteredPasscode.length() == 4) {
            sharedPreferences.edit().putString(PASSCODE_KEY, enteredPasscode).apply();
            Toast.makeText(this, "Passcode Set!", Toast.LENGTH_SHORT).show();
            navigateToMain();
        } else {
            Toast.makeText(this, "Enter a 4-digit passcode", Toast.LENGTH_SHORT).show();
        }
    }

    private void verifyPasscode(String savedPasscode) {
        String enteredPasscode = passcodeInput.getText().toString();
        if (enteredPasscode.equals(savedPasscode)) {
            navigateToMain();
        } else {
            Toast.makeText(this, "Incorrect passcode!", Toast.LENGTH_SHORT).show();
        }
    }

    private void navigateToMain() {
        startActivity(new Intent(PasscodeActivity.this, MainActivity.class));
        finish();
    }
}
