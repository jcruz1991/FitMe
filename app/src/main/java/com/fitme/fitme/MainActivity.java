package com.fitme.fitme;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.fitme.fitme.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    private TextView userTextView;

    private DatabaseReference databaseReference;
    private DatabaseReference mRef;
    private FirebaseUser user;
    private FirebaseAuth.AuthStateListener authListener;
    private FirebaseAuth auth;
    User activeUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //get Firebase auth instance
        auth = FirebaseAuth.getInstance();
        activeUser = new User();

        databaseReference = FirebaseDatabase.getInstance().getReference();
        mRef = databaseReference.child("users");

        userTextView = (TextView) findViewById(R.id.userTextView);

        //get current user
        user = FirebaseAuth.getInstance().getCurrentUser();
        // Display users email to userTextView
        userTextView.setText(user.getEmail());
    }

    /**
     * Button click event when user clicks find a buddy button
     * will take them to FindBuddy screen
     * @param view
     */
    public void findBuddyButtonClicked(View view) {
        Intent intent = new Intent(MainActivity.this, FindBuddyActivity.class);
        startActivity(intent);
    }

    /**
     * Button click event when users signs out, will take
     * them back to login screen
     * @param view
     */
    public void signOutButtonClicked(View view) {
        auth.signOut();
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
