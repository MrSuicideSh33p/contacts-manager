package com.vichu.japantrip.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.vichu.japantrip.R;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Initialize DrawerLayout and NavigationView
        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        // Setup the Drawer Toggle
        toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Handle Navigation Drawer Clicks
        navigationView.setNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_add_contact) {
                openAddContactScreen();
            } else if (item.getItemId() == R.id.nav_view_contacts) {
                openContactListScreen();
            } else if (item.getItemId() == R.id.nav_schedule) {
                    openScheduleScreen();
            } else if (item.getItemId() == R.id.nav_poster_schedule) {
                openPosterScheduleScreen();
            } else if (item.getItemId() == R.id.nav_contact_us) {
                openContactUsScreen();
            }
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });

        LinearLayout addContactSection = findViewById(R.id.add_contact_section);
        LinearLayout viewContactsSection = findViewById(R.id.view_contacts_section);
        LinearLayout scheduleSection = findViewById(R.id.schedule_section);
        LinearLayout posterSection = findViewById(R.id.poster_schedule_section);
        LinearLayout contactUsSection = findViewById(R.id.contact_us_section);

        addContactSection.setOnClickListener(v -> openAddContactScreen());
        viewContactsSection.setOnClickListener(v -> openContactListScreen());
        scheduleSection.setOnClickListener(v -> openScheduleScreen());
        posterSection.setOnClickListener(v -> openPosterScheduleScreen());
        contactUsSection.setOnClickListener(v -> openContactUsScreen());
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (toggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Open Contact Entry Screen
    private void openAddContactScreen() {
        Intent intent = new Intent(this, AddContactActivity.class);
        startActivity(intent);
    }

    // Open Contact List Screen
    private void openContactListScreen() {
        Intent intent = new Intent(this, ContactListActivity.class);
        startActivity(intent);
    }

    // Open Schedule Screen
    private void openScheduleScreen() {
        Intent intent = new Intent(this, ScheduleActivity.class);
        startActivity(intent);
    }

    // Open Poster Schedule Screen
    private void openPosterScheduleScreen() {
        Intent intent = new Intent(this, PosterActivity.class);
        startActivity(intent);
    }

    // Open Contact Us Screen
    private void openContactUsScreen() {
        Intent intent = new Intent(this, ContactUsActivity.class);
        startActivity(intent);
    }
}
