package com.fitme.fitme;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.fitme.fitme.model.Exercise;
import com.fitme.fitme.model.Workout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddWorkout extends AppCompatActivity {

    private EditText exerciseName, exerciseType, bodyType;
    private Button btnAdd;
    private DatabaseReference databaseReference;

    private FirebaseAuth.AuthStateListener authListener;

    private FirebaseAuth mAuth;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;
    private DatabaseReference mdatabase;
    private FirebaseUser user;
    String usermail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_exercise);
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference().child("exercises");

        exerciseName = (EditText) findViewById(R.id.exerciseNameEditText);
        exerciseType = (EditText) findViewById(R.id.exerciseTypeEditText);
        bodyType = (EditText) findViewById(R.id.bodyTypeEditText);

        user = FirebaseAuth.getInstance().getCurrentUser();
        usermail = user.getEmail();

        btnAdd = (Button)findViewById(R.id.btnAdd);

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String exName = exerciseName.getText().toString().trim();
                String exType = exerciseType.getText().toString().trim();
                String bdType = bodyType.getText().toString().trim();

                if(exName.isEmpty() || exType.isEmpty() || bdType.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "All fields must have text", Toast.LENGTH_SHORT);
                } else {
                    Exercise exercise = new Exercise(exName, bdType, exType);
                    myRef.push().setValue(exercise);

                    exerciseName.setText(null);
                    exerciseType.setText(null);
                    bodyType.setText(null);
                }
            }
        });
    }

}
