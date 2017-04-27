package com.fitme.fitme;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.fitme.fitme.model.User;
import com.fitme.fitme.workout.Calendar;
import com.fitme.fitme.workout.DisplayWorkout;
import com.fitme.fitme.workout.WorkoutActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    public static final int RC_SIGN_IN = 1;

    private DatabaseReference databaseReference;
    private DatabaseReference mRef;
    private FirebaseUser user;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    User activeUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //get Firebase auth instance
        mFirebaseAuth = FirebaseAuth.getInstance();
        activeUser = new User();

        databaseReference = FirebaseDatabase.getInstance().getReference();
        mRef = databaseReference.child("users");

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    // Display users name to userTextView
                    user = FirebaseAuth.getInstance().getCurrentUser();
                    user.getDisplayName();
                } else {
                    // User is signed out
                    startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setIsSmartLockEnabled(false)
                                    .setProviders(
                                            AuthUI.EMAIL_PROVIDER,
                                            AuthUI.GOOGLE_PROVIDER)
                                    .build(),
                            RC_SIGN_IN);
                }
            }
        };
    }

    /**
     * Button click event when user clicks find a buddy button
     * will take them to FindBuddy screen
     * @param view
     */
    public void findBuddyButtonClicked(View view) {
        final Button button = (Button) findViewById(R.id.findBuddyButton);

        button.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {

                    button.setBackgroundColor(Color.RED);
                    return true;
                } else if (event.getAction() == MotionEvent.ACTION_UP) {

                    button.setBackgroundColor(getResources().getColor(android.R.color.holo_purple));
                    return true;
                }
                return false;
            }
        });

        Intent intent = new Intent(MainActivity.this, FindBuddyActivity.class);
        startActivity(intent);
    }

    /**
     * Button click event when users signs out, will take
     * them back to login screen
     * @param view
     */
    public void signOutButtonClicked(View view) {
        mFirebaseAuth.signOut();
        finish();
    }

    /**
     * Button click event when user clicks create button
     * will take them to Create screen
     * @param view
     */
    public void CreateButtonClicked(View view) {
        Intent intent = new Intent(MainActivity.this, Calendar.class);
        //Intent intent = new Intent(MainActivity.this, showworkout.class);
        startActivity(intent);
    }
    public void workoutButtonClicked(View view) {
        Intent intent = new Intent(MainActivity.this, WorkoutActivity.class);
        startActivity(intent);
    }

    public void addButtonClicked(View view) {
        Intent intent = new Intent(MainActivity.this, AddWorkout.class);
        startActivity(intent);
    }
    public void ShowWorkoutButtonClicked(View view) {
        Intent intent = new Intent(MainActivity.this, DisplayWorkout.class);
        startActivity(intent);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                // Sign-in succeeded, set up the UI
                Toast.makeText(this, "Signed in!", Toast.LENGTH_SHORT).show();
            } else if (resultCode == RESULT_CANCELED) {
                // Sign in was canceled by the user, finish the activity
                Toast.makeText(this, "Sign in canceled", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mAuthStateListener != null) {
            mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
        }
    }
}