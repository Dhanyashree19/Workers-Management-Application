package com.example.myapplication;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.github.clans.fab.FloatingActionButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.concurrent.TimeUnit;

public class DetailActivity extends AppCompatActivity {

    Button callbtn;
    ImageButton messagebtn;
    TextView detailLocation, detailName, detailPhNo, detailWageExpectation;
    String key = "";
    FloatingActionButton deleteBtn, editButton;

    private static final int REQUEST_CODE_EDIT_PROFILE = 1;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;
    private String verificationId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();

        detailName = findViewById(R.id.detailName);
        detailPhNo = findViewById(R.id.detailPhNo);
        detailWageExpectation = findViewById(R.id.detailWageExpectation);
        detailLocation = findViewById(R.id.detailLocation);
        callbtn = findViewById(R.id.callbtn);
        messagebtn = findViewById(R.id.messagebtn);
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
                showOTPDialog(new OTPVerificationCallback() {
                    @Override
                    public void onOTPVerified() {
                        deleteProfile();
                    }

                    @Override
                    public void onOTPVerificationFailed(String errorMessage) {
                        Toast.makeText(DetailActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showOTPDialog(new OTPVerificationCallback() {
                    @Override
                    public void onOTPVerified() {
                        goToEditProfileActivity();
                    }

                    @Override
                    public void onOTPVerificationFailed(String errorMessage) {
                        Toast.makeText(DetailActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void deleteProfile() {
        // Get a reference to the Firebase Realtime Database
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("Profiles");
        // Remove the profile from the database
        databaseRef.child(key).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // Delete successful
                Toast.makeText(DetailActivity.this, "Profile deleted successfully", Toast.LENGTH_SHORT).show();
                finish(); // Close the activity
            }
        });
    }

    private void goToEditProfileActivity() {
        Intent intent = new Intent(DetailActivity.this, EditProfileActivity.class);
        intent.putExtra("profileKey", key);
        intent.putExtra("Name", detailName.getText().toString());
        intent.putExtra("PhNo", detailPhNo.getText().toString());
        intent.putExtra("Location", detailLocation.getText().toString());
        intent.putExtra("WageExpectation", detailWageExpectation.getText().toString());

        startActivityForResult(intent, REQUEST_CODE_EDIT_PROFILE);
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
            intent.putExtra("sms_body", "Hello, I recently came across your profile on WorkEase. Please let me know if there's a convenient time for us to connect.");
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            }
        }
    }

    private void showOTPDialog(final OTPVerificationCallback callback) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("OTP Verification");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        builder.setView(input);

        builder.setPositiveButton("Verify", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String otp = input.getText().toString().trim();
                if (!otp.isEmpty()) {
                    verifyOTP(otp, callback);
                } else {
                    Toast.makeText(DetailActivity.this, "Please enter the OTP", Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    private void verifyOTP(String otp, final OTPVerificationCallback callback) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, otp);
        signInWithPhoneAuthCredential(credential, callback);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential, final OTPVerificationCallback callback) {
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(DetailActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull com.google.android.gms.tasks.Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Verification successful
                            callback.onOTPVerified();
                        } else {
                            // Verification failed
                            String errorMessage = "OTP verification failed";
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                errorMessage = "Invalid OTP";
                            } else if (task.getException() instanceof FirebaseAuthException) {
                                errorMessage = ((FirebaseAuthException) task.getException()).getMessage();
                            }
                            callback.onOTPVerificationFailed(errorMessage);
                        }
                    }
                });
    }

    private void sendOTP(String phoneNumber, final PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,
                60,
                TimeUnit.SECONDS,
                DetailActivity.this,
                callbacks);
    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks verificationCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            // This method will be invoked when verification is completed automatically
            // (e.g., auto-retrieval of OTP) without user interaction.
            String otp = phoneAuthCredential.getSmsCode();
            if (otp != null) {
                verifyOTP(otp, new OTPVerificationCallback() {
                    @Override
                    public void onOTPVerified() {
                        // OTP automatically verified
                        // Perform your desired action here
                    }

                    @Override
                    public void onOTPVerificationFailed(String errorMessage) {
                        Toast.makeText(DetailActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            Toast.makeText(DetailActivity.this, "Verification failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            verificationId = s;
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        String phoneNumber = detailPhNo.getText().toString();
        sendOTP(phoneNumber, verificationCallbacks);
    }

    interface OTPVerificationCallback {
        void onOTPVerified();

        void onOTPVerificationFailed(String errorMessage);
    }
}
