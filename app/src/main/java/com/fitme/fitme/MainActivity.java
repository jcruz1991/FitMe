package com.fitme.fitme;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    private TextView userTextView;

    DatabaseReference database;
    DatabaseReference ref;
    private FirebaseAuth.AuthStateListener authListener;
    private FirebaseAuth auth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //get firebase auth instance
        auth = FirebaseAuth.getInstance();

        database = FirebaseDatabase.getInstance().getReference();
        ref = database.child("users");

        userTextView = (TextView) findViewById(R.id.userTextView);

        //get current user
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        userTextView.setText(user.getEmail());
    }

    public void findBuddyButtonClicked(View view) {
        Intent intent = new Intent(MainActivity.this, FindBuddyActivity.class);
        startActivity(intent);
    }

    public void signOutButtonClicked(View view) {
        auth.signOut();
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    /*
    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
        auth.addAuthStateListener(authListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (authListener != null) {
            auth.removeAuthStateListener(authListener);
        }
    }
    */
}
