package com.vichu.japantrip.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.vichu.japantrip.R;

public class PasscodeActivity extends AppCompatActivity {
    private TextView passcodeDisplay;
    private SharedPreferences sharedPreferences;
    private static final String PASSCODE_KEY = "1234";
    private StringBuilder enteredPasscode = new StringBuilder();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passcode);

        passcodeDisplay = findViewById(R.id.passcodeDisplay);
        sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        String savedPasscode = sharedPreferences.getString(PASSCODE_KEY, null);

        if (savedPasscode == null) {
            // First-time setup
        }
    }

    public void onKeypadClick(View view) {
        if (enteredPasscode.length() < 4) {
            enteredPasscode.append(((Button) view).getText().toString());
            updatePasscodeDisplay();

            if (enteredPasscode.length() == 4) {
                validatePasscode();
            }
        }
    }

    public void onDeleteClick(View view) {
        if (enteredPasscode.length() > 0) {
            enteredPasscode.deleteCharAt(enteredPasscode.length() - 1);
            updatePasscodeDisplay();
        }
    }

    private void updatePasscodeDisplay() {
        StringBuilder maskedPasscode = new StringBuilder();
        for (int i = 0; i < enteredPasscode.length(); i++) {
            maskedPasscode.append("•");
        }
        passcodeDisplay.setText(maskedPasscode);
    }

    private void validatePasscode() {
        String savedPasscode = sharedPreferences.getString(PASSCODE_KEY, null);
        if (savedPasscode != null && enteredPasscode.toString().equals(savedPasscode)) {
            navigateToMain();
        } else {
            Toast.makeText(this, "Incorrect passcode!", Toast.LENGTH_SHORT).show();
            enteredPasscode.setLength(0);
            updatePasscodeDisplay();
        }
    }

    private void navigateToMain() {
        startActivity(new Intent(PasscodeActivity.this, MainActivity.class));
        finish();
    }
}
