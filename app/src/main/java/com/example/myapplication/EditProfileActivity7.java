package com.example.myapplication;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class EditProfileActivity7 extends AppCompatActivity {

    private EditText editName, editPhNo, editLocation, editSalary;
    private Button updateButton;
    private DatabaseReference databaseReference;

    private String profileKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile7);

        editName = findViewById(R.id.editName);
        editPhNo = findViewById(R.id.editPhNo);
        editLocation = findViewById(R.id.editLocation);
        editSalary = findViewById(R.id.editSalary);
        updateButton = findViewById(R.id.updateButton);

        // Get the profile key from the intent
        profileKey = getIntent().getStringExtra("profileKey");
        // Initialize the database reference
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Profiles7").child(profileKey);

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateProfile();
            }
        });

        // Retrieve the profile data from the database
        retrieveProfileData();
    }

    private void retrieveProfileData() {
        databaseReference.get().addOnSuccessListener(snapshot -> {
            if (snapshot.exists()) {
                Profile profile = snapshot.getValue(Profile.class);
                if (profile != null) {
                    editName.setText(profile.getName());
                    editPhNo.setText(profile.getPhoneNo());
                    editLocation.setText(profile.getLocation());
                    editSalary.setText(profile.getSalary());
                }
            } else {
                Toast.makeText(EditProfileActivity7.this, "Profile not found", Toast.LENGTH_SHORT).show();
                finish();
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(EditProfileActivity7.this, "Failed to retrieve profile", Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    private void updateProfile() {
        String name = editName.getText().toString().trim();
        String phoneNo = editPhNo.getText().toString().trim();
        String location = editLocation.getText().toString().trim();
        String salary = editSalary.getText().toString().trim();

        // Validate phone number
        if (!isValidPhoneNumber(phoneNo)) {
            editPhNo.setError("Invalid phone number");
            editPhNo.requestFocus();
            return;
        }

        // Create a new Profile object with the updated data
        Profile profile = new Profile(name, phoneNo, location, salary);

        // Update the profile in the Firebase Database
        databaseReference.setValue(profile).addOnSuccessListener(aVoid -> {
            Toast.makeText(EditProfileActivity7.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
            finish();
        }).addOnFailureListener(e -> {
            Toast.makeText(EditProfileActivity7.this, "Failed to update profile", Toast.LENGTH_SHORT).show();
        });
    }

    private boolean isValidPhoneNumber(String phoneNumber) {
        // Check if the phone number is a 10-digit number and doesn't contain any alphabets
        return phoneNumber.matches("\\d{10}");
    }
}
