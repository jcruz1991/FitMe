package com.fitme.fitme;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.fitme.fitme.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    private EditText inputName, inputEmail, inputPassword;
    private TextView loginTextView;
    private Button registerButton;
    private ProgressBar progressBar;

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //Get Firebase auth instance and database reference
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Init views
        inputName = (EditText) findViewById(R.id.registerNameEditText);
        inputEmail = (EditText) findViewById(R.id.registerEmailEditText);
        inputPassword = (EditText) findViewById(R.id.registerPasswordEditText);
        loginTextView = (TextView) findViewById(R.id.loginTextView);
        registerButton = (Button) findViewById(R.id.registerButton);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

    }

    /**
     * Register Button Click Event
     * @param view
     */
    public void registerButtonClicked(View view) {
        final String  name = inputName.getText().toString().trim();
        final String email = inputEmail.getText().toString().trim();
        final String password = inputPassword.getText().toString().trim();

        // Check if name input field is empty
        if (TextUtils.isEmpty(name)) {
            Toast.makeText(getApplicationContext(), "Enter Name", Toast.LENGTH_SHORT).show();
            return;
        }
        // Check if email input field is empty
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(getApplicationContext(), "Enter Email", Toast.LENGTH_SHORT).show();
            return;
        }
        // Check if password input field is empty
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(getApplicationContext(), "Enter Password", Toast.LENGTH_SHORT).show();
            return;
        }
        // Check Password Length must be greater than or equal to 6
        if(password.length() < 6) {
            Toast.makeText(getApplicationContext(), "Password too short, enter minimum 6 characters", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        // Create User
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                // Successfully created user
                progressBar.setVisibility(View.GONE);

                // Could not create user
                if(!task.isSuccessful()) {
                    Log.d("FAILED", "Could not create user");
                } else {
                    // User can be added to Firebase DB calls createNewUser
                    createNewUser(task.getResult().getUser().getEmail(), task.getResult().getUser().getUid());
                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }

    /**
     * Insert new user into Firebase
     * @param email
     * @param uId
     */
    private void createNewUser(String email, String uId) {
        String name = inputName.getText().toString();
        User user = new User(name, email);

        // Add to Firebase
        mDatabase.child("users").child(uId).setValue(user);
    }

    /**
     * Click Listener if loginTextView clicked return to Login Screen
     * @param view
     */
    public void loginTextViewClicked(View view) {
        // Open Login Activity
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        progressBar.setVisibility(View.GONE);
    }

}
