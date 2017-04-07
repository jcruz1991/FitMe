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


    //final Firebase rootRef = new Firebase("https://fitme-a43f9.firebaseio.com/");

    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //Get Firebase auth instance
        mAuth = FirebaseAuth.getInstance();

        mDatabase = FirebaseDatabase.getInstance().getReference();

        inputName = (EditText) findViewById(R.id.registerNameEditText);
        inputEmail = (EditText) findViewById(R.id.registerEmailEditText);
        inputPassword = (EditText) findViewById(R.id.registerPasswordEditText);
        loginTextView = (TextView) findViewById(R.id.loginTextView);
        registerButton = (Button) findViewById(R.id.registerButton);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

    }

    public void registerButtonClicked(View view) {
        final String  name = inputName.getText().toString().trim();
        final String email = inputEmail.getText().toString().trim();
        final String password = inputPassword.getText().toString().trim();

        // Check if input fields are empty
        if (TextUtils.isEmpty(name)) {
            Toast.makeText(getApplicationContext(), "Enter Name", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(getApplicationContext(), "Enter Email", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(getApplicationContext(), "Enter Password", Toast.LENGTH_SHORT).show();
            return;
        }
        // Check Password Length
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

                    createNewUser(task.getResult().getUser().getEmail(), task.getResult().getUser().getUid());
                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }

    private void createNewUser(String email, String uId) {
        String name = inputName.getText().toString();
        Log.d("NAME", name);
        Log.d("EMAIL", email);
        Log.d("UID", uId);

        User user = new User(name, email);

        mDatabase.child("users").child(uId).setValue(user);
    }

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
