package com.vichu.japantrip.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.vichu.japantrip.R;

public class PasscodeActivity extends AppCompatActivity {
    private TextView passcodeDots;
    private SharedPreferences sharedPreferences;
    private static final String PASSCODE_KEY = "passcode";
    private StringBuilder enteredPasscode = new StringBuilder();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passcode);

        passcodeDots = findViewById(R.id.passcodeDisplay);
        sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);

        String savedPasscode = sharedPreferences.getString(PASSCODE_KEY, "");
        boolean isSettingNewPasscode = savedPasscode.isEmpty();

        setupKeypad(isSettingNewPasscode, savedPasscode);
    }

    public void onKeypadClick(View view) {
        if (view instanceof Button) {
            Button button = (Button) view;
            String value = button.getText().toString();
            handleButtonClick(value);
        }
    }

    private void setupKeypad(boolean isSettingNewPasscode, String savedPasscode) {
        for (int i = 0; i <= 9; i++) {
            int buttonId = getResources().getIdentifier("button" + i, "id", getPackageName());
            Button numberButton = findViewById(buttonId);
            if (numberButton != null) {
                int finalI = i;
                numberButton.setOnClickListener(view -> handleButtonClick(String.valueOf(finalI)));
            }
        }

        Button deleteButton = findViewById(R.id.deleteButton);
        deleteButton.setOnClickListener(view -> handleDeleteButton());
    }

    private void handleButtonClick(String digit) {
        if (enteredPasscode.length() < 4) {
            enteredPasscode.append(digit);
            updatePasscodeDots();

            String savedPasscode = sharedPreferences.getString(PASSCODE_KEY, "");
            boolean isSettingNewPasscode = savedPasscode.isEmpty();

            if (enteredPasscode.length() == 4) {
                if (isSettingNewPasscode) {
                    saveNewPasscode();
                } else {
                    verifyPasscode(savedPasscode);
                }
            }
        }
    }


    private void handleDeleteButton() {
        if (enteredPasscode.length() > 0) {
            enteredPasscode.deleteCharAt(enteredPasscode.length() - 1);
            updatePasscodeDots();
        }
    }

    private void saveNewPasscode() {
        sharedPreferences.edit().putString(PASSCODE_KEY, enteredPasscode.toString()).apply();
        Toast.makeText(this, "Passcode Set!", Toast.LENGTH_SHORT).show();
        navigateToMain();
    }

    private void verifyPasscode(String savedPasscode) {
        if (enteredPasscode.toString().equals(savedPasscode)) {
            navigateToMain();
        } else {
            vibrateOnError();
            enteredPasscode.setLength(0);
            updatePasscodeDots();
            Toast.makeText(this, "Incorrect passcode!", Toast.LENGTH_SHORT).show();
        }
    }

    private void updatePasscodeDots() {
        StringBuilder dots = new StringBuilder();
        for (int i = 0; i < enteredPasscode.length(); i++) {
            dots.append("● ");
        }
        for (int i = enteredPasscode.length(); i < 4; i++) {
            dots.append("○ ");
        }
        passcodeDots.setText(dots.toString().trim());
    }

    private void vibrateOnError() {
        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        if (vibrator != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
            }
        }
    }

    private void navigateToMain() {
        startActivity(new Intent(PasscodeActivity.this, MainActivity.class));
        finish();
    }
}
