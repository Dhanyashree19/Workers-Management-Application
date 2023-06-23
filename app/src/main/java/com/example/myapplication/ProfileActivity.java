package com.example.myapplication;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.concurrent.TimeUnit;

public class ProfileActivity extends AppCompatActivity {

    DrawerLayout drawerLayout;
    ImageView menu;
    LinearLayout home, profile, about_us, logout, category;

    private EditText uploadName, uploadPhNo, uploadLocation, uploadSalary;
    private Button saveButton;
    private DatabaseReference databaseReference;
    private Spinner dropdownMenu;

    private FirebaseAuth firebaseAuth;
    private String verificationId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        drawerLayout = findViewById(R.id.drawerLayout);
        menu = findViewById(R.id.menu);
        home = findViewById(R.id.home);
        about_us = findViewById(R.id.about_us);
        logout = findViewById(R.id.logout);
        category = findViewById(R.id.category);
        profile = findViewById(R.id.profile);
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Profiles");

        // Initialize Firebase Authentication
        firebaseAuth = FirebaseAuth.getInstance();

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
                redirectActivity(ProfileActivity.this, HomeActivity.class);
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
                redirectActivity(ProfileActivity.this, CategoryActivity.class);
            }
        });

        about_us.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redirectActivity(ProfileActivity.this, AboutActivity.class);
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendOTPAndSaveProfile();
            }
        });
    }

    private void sendOTPAndSaveProfile() {
        String phoneNo = uploadPhNo.getText().toString().trim();

        // Validate phone number
        if (!isValidPhoneNumber(phoneNo)) {
            uploadPhNo.setError("Invalid phone number");
            uploadPhNo.requestFocus();
            return;
        }

        // Send OTP
        sendOTP(phoneNo);
    }

    private boolean isValidPhoneNumber(String phoneNumber) {
        // Implement your phone number validation logic here
        // You can use regex or any other method to validate the phone number format
        // Return true if the phone number is valid, otherwise false
        return !phoneNumber.isEmpty();
    }

    private void sendOTP(String phoneNumber) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,
                60,
                TimeUnit.SECONDS,
                this,
                new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                        // This method is called when the verification is automatically completed
                        // You can directly sign in the user with the credential
                        signInWithPhoneAuthCredential(phoneAuthCredential);
                    }

                    @Override
                    public void onVerificationFailed(FirebaseException e) {
                        // This method is called when the verification process fails
                        Toast.makeText(ProfileActivity.this, "Verification failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCodeSent(String verificationId, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        // This method is called when the verification code is successfully sent
                        ProfileActivity.this.verificationId = verificationId;
                        showOTPDialog();
                    }
                }
        );
    }

    private void showOTPDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("OTP Verification");

        // Set up the input field
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("Verify", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String enteredOTP = input.getText().toString().trim();
                verifyOTP(enteredOTP);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        // Show the dialog
        builder.show();
    }

    private void verifyOTP(String enteredOTP) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, enteredOTP);
        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Verification successful, save the profile
                            saveProfileToDatabase();
                        } else {
                            // Verification failed
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                Toast.makeText(ProfileActivity.this, "Invalid OTP. Profile cannot be saved.", Toast.LENGTH_LONG).show();
                            } else if (task.getException() instanceof FirebaseTooManyRequestsException) {
                                Toast.makeText(ProfileActivity.this, "SMS quota exceeded. Please try again later.", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(ProfileActivity.this, "Verification failed. Please try again.", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                });
    }

    private void saveProfileToDatabase() {
        String name = uploadName.getText().toString().trim();
        String phoneNo = uploadPhNo.getText().toString().trim();
        String location = uploadLocation.getText().toString().trim();
        String salary = uploadSalary.getText().toString().trim();
        String payExpectation = dropdownMenu.getSelectedItem().toString();

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

        Toast.makeText(ProfileActivity.this, "Profile saved", Toast.LENGTH_LONG).show();
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
