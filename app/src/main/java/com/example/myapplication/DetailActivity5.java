package com.example.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.github.clans.fab.FloatingActionButton;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DetailActivity5 extends AppCompatActivity {

    Button callbtn;
    ImageButton messagebtn;
    TextView detailLocation, detailName, detailPhNo, detailWageExpectation;
    String key = "";
    FloatingActionButton deleteBtn, editButton;

    private static final int REQUEST_CODE_EDIT_PROFILE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail5);
        detailName = findViewById(R.id.detailName);
        detailPhNo = findViewById(R.id.detailPhNo);
        detailWageExpectation = findViewById(R.id.detailWageExpectation);
        detailLocation = findViewById(R.id.detailLocation);
        callbtn = findViewById(R.id.callbtn);
        messagebtn=findViewById(R.id.messagebtn);
        deleteBtn = findViewById(R.id.deleteButton);
        editButton = findViewById(R.id.editButton);

        callbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phoneNumber = detailPhNo.getText().toString();
                if (phoneNumber != null && !phoneNumber.isEmpty()) {
                    Intent callIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phoneNumber));
                    startActivity(callIntent);
                }
            }
        });
        messagebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open the messaging app with a pre-filled message
                openMessagingApp();
            }
        });

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            detailName.setText(bundle.getString("Name"));
            detailLocation.setText(bundle.getString("Location"));
            detailPhNo.setText(bundle.getString("PhNo"));
            detailWageExpectation.setText(bundle.getString("WageExpectation"));
            key = bundle.getString("Key");
        }

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get a reference to the Firebase Realtime Database
                DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("Profiles5");
                // Remove the profile from the database
                databaseRef.child(key).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Delete successful
                        Toast.makeText(DetailActivity5.this, "Profile deleted successfully", Toast.LENGTH_SHORT).show();
                        finish(); // Close the activity
                    }
                });
            }
        });

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DetailActivity5.this, EditProfileActivity5.class);
                intent.putExtra("profileKey", key);
                intent.putExtra("Name", detailName.getText().toString());
                intent.putExtra("PhNo", detailPhNo.getText().toString());
                intent.putExtra("Location", detailLocation.getText().toString());
                intent.putExtra("WageExpectation", detailWageExpectation.getText().toString());

                startActivityForResult(intent, REQUEST_CODE_EDIT_PROFILE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_EDIT_PROFILE && resultCode == Activity.RESULT_OK && data != null) {
            String updatedName = data.getStringExtra("UpdatedName");
            String updatedPhNo = data.getStringExtra("UpdatedPhNo");
            String updatedLocation = data.getStringExtra("UpdatedLocation");
            String updatedWageExpectation = data.getStringExtra("UpdatedWageExpectation");

            // Update the views with the updated data
            detailName.setText(updatedName);
            detailPhNo.setText(updatedPhNo);
            detailLocation.setText(updatedLocation);
            detailWageExpectation.setText(updatedWageExpectation);

            Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
        }
    }

    private void openMessagingApp() {
        String phoneNumber = getIntent().getStringExtra("PhNo");
        // Check if the phone number is not null or empty
        if (phoneNumber != null && !phoneNumber.isEmpty()) {
            // Create an intent to send a message
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("smsto:" + phoneNumber));
            intent.putExtra("sms_body", "Hello, I recently came across your profile on WorkEase.Please let me know if there's a convenient time for us to connect.");
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            }
        }
    }
}
