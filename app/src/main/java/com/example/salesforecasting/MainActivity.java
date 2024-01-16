package com.example.salesforecasting;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;


public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().setTitle("Dashboard");

        mAuth = FirebaseAuth.getInstance();

        fetchDataFromNetwork();
    }

    private void fetchDataFromNetwork() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final String response = NetworkUtils.fetchData("http://example.com");

                // Switch back to the main thread to update UI
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        // Update your UI with the response here
                    }
                });
            }
        }).start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_sign_out) {
            // Handle the "Sign Out" menu item click here

            // Sign out the user from Firebase
            mAuth.signOut();


             Intent intent = new Intent(MainActivity.this, LoginActivity.class);
             startActivity(intent);

            // Finish the current activity (MainActivity)
            finish();

            // Show a toast message to confirm sign out
            Toast.makeText(this, "Signed Out", Toast.LENGTH_SHORT).show();

            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

