package com.example.salesforecasting;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserProfileChangeRequest;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private final int RC_SIGN_IN = 123;

    private TextInputLayout textInputName;
    private TextInputLayout textInputEmail;
    private TextInputLayout textInputPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        // Initialize TextInputLayouts
        textInputName = findViewById(R.id.textInputName);
        textInputEmail = findViewById(R.id.textInputEmail);
        textInputPassword = findViewById(R.id.textInputPassword);
    }
    public void onRegisterClick(View view) {
        // Get user inputs from EditText fields
        String name = textInputName.getEditText().getText().toString();
        String email = textInputEmail.getEditText().getText().toString();
        String password = textInputPassword.getEditText().getText().toString();

        // Validate user inputs (add your validation logic here)
        if (name.isEmpty()) {
            textInputName.setError("Name is required");
            return;
        }

        if (email.isEmpty()) {
            textInputEmail.setError("Email is required");
            return;
        }


        if (password.isEmpty()) {
            textInputPassword.setError("Password is required");
            return;
        }


        // Now, create a new user with the provided email and password
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Registration successful, update user's display name (optional)
                            FirebaseUser user = mAuth.getCurrentUser();
                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(name)
                                    .build();

                            user.updateProfile(profileUpdates);


                            //  navigate to another activity
                            Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish(); // Close the registration activity

                        } else {
                            // Registration failed, display an error message
                            Toast.makeText(RegisterActivity.this, "Registration failed. Please try again.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void onGoogleSignInClick(View view) {
        // Start the Google Sign-In process
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    // Handle the result of Google Sign-In
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign-In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign-In failed, handle error
                Log.w(TAG, "Google sign-in failed", e);
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Google Sign-in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();

                            // You can access user information here
                            String displayName = user.getDisplayName();
                            String email = user.getEmail();
                            String uid = user.getUid();

                            //  navigate to another activity
                            Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish(); // Close the registration activity
                        } else {
                            // Google Sign-in failed, handle error
                            Log.w(TAG, "signInWithCredential:failure", task.getException());

                            // Update UI to indicate the failure (you can show an error message to the user)
                            Toast.makeText(RegisterActivity.this, "Google Sign-In failed. Please try again.", Toast.LENGTH_SHORT).show();
                        }
                    }

                });
    }

    public void onAlreadyHaveAccountClick(View view) {
        // Implement the logic to navigate to the login UI
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }
}

