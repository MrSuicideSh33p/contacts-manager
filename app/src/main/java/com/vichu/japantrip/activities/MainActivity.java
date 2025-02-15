package com.vichu.japantrip.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;

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
                openContactEntryScreen();
            } else if (item.getItemId() == R.id.nav_view_contacts) {
                openContactListScreen();
            } else if (item.getItemId() == R.id.nav_contact_us) {
                startActivity(new Intent(MainActivity.this, ContactUsActivity.class));
            }
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });

        // Set Button Click Listeners
        Button addContactBtn = findViewById(R.id.btn_add_contact);
        Button viewContactsBtn = findViewById(R.id.btn_view_contacts);

        addContactBtn.setOnClickListener(v -> openContactEntryScreen());
        viewContactsBtn.setOnClickListener(v -> openContactListScreen());
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (toggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Open Contact Entry Screen
    private void openContactEntryScreen() {
        Intent intent = new Intent(this, AddContactActivity.class);
        startActivity(intent);
    }

    // Open Contact List Screen
    private void openContactListScreen() {
        Intent intent = new Intent(this, ContactListActivity.class);
        startActivity(intent);
    }
}
