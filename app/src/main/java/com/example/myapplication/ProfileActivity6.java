package com.example.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ProfileActivity6 extends AppCompatActivity {

    DrawerLayout drawerLayout;
    ImageView menu;
    LinearLayout home, profile, about_us, logout, category;

    private EditText uploadName, uploadPhNo, uploadLocation, uploadSalary;
    private Button saveButton;
    private DatabaseReference databaseReference;
    private Spinner dropdownMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile6);
        drawerLayout = findViewById(R.id.drawerLayout);
        menu = findViewById(R.id.menu);
        home = findViewById(R.id.home);
        about_us = findViewById(R.id.about_us);
        logout = findViewById(R.id.logout);
        category = findViewById(R.id.category);
        profile = findViewById(R.id.profile);
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Profiles6");

        // Find views
        uploadName = findViewById(R.id.uploadName);
        uploadPhNo = findViewById(R.id.uploadPhNo);
        uploadLocation = findViewById(R.id.uploadLocation);
        uploadSalary = findViewById(R.id.uploadSalary);
        dropdownMenu = findViewById(R.id.dropdownMenu);
        saveButton = findViewById(R.id.saveButton);

        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDrawer(drawerLayout);
            }
        });

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redirectActivity(ProfileActivity6.this, HomeActivity.class);
            }
        });

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recreate();
            }
        });
        category.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redirectActivity(ProfileActivity6.this, CategoryActivity.class);
            }
        });

        about_us.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redirectActivity(ProfileActivity6.this, AboutActivity.class);
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity6.this, MainActivity.class);
                startActivity(intent);
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveProfile();
            }
        });
    }

    private void saveProfile() {
        String name = uploadName.getText().toString().trim();
        String phoneNo = uploadPhNo.getText().toString().trim();
        String location = uploadLocation.getText().toString().trim();
        String salary = uploadSalary.getText().toString().trim();
        String payExpectation = dropdownMenu.getSelectedItem().toString();

        // Validate phone number
        if (!isValidPhoneNumber(phoneNo)) {
            uploadPhNo.setError("Invalid phone number");
            uploadPhNo.requestFocus();
            return;
        }

        // Concatenate the pay expectation with the entered salary
        String finalSalary = salary + " " + payExpectation;

        // Create a new Profile object
        Profile profile = new Profile(name, phoneNo, location, finalSalary);

        // Generate a unique key for the profile
        String profileKey = databaseReference.push().getKey();

        // Set the key for the profile object
        profile.setKey(profileKey);

        // Save the profile to Firebase Database
        databaseReference.child(profileKey).setValue(profile);

        // Clear the input fields
        uploadName.setText("");
        uploadPhNo.setText("");
        uploadLocation.setText("");
        uploadSalary.setText("");
        Toast.makeText(ProfileActivity6.this,"Profile saved",Toast.LENGTH_LONG).show();
    }

    private boolean isValidPhoneNumber(String phoneNumber) {
        // Check if the phone number is a 10-digit number and doesn't contain any alphabets
        return phoneNumber.matches("\\d{10}");
    }

    public static void openDrawer(DrawerLayout drawerLayout) {
        drawerLayout.openDrawer(GravityCompat.START);
    }

    public static void closeDrawer(DrawerLayout drawerLayout) {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
    }

    public static void redirectActivity(Activity activity, Class HomeActivity) {
        Intent intent = new Intent(activity, HomeActivity);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);
        activity.finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        closeDrawer(drawerLayout);
    }
}
