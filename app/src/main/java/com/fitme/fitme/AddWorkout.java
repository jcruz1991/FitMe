package com.fitme.fitme;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.fitme.fitme.model.Workout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddWorkout extends AppCompatActivity {

    private EditText inputdate, inputexerciseDesc, inputexercise, inputwname, inputcategory, inputuser;
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
        myRef = mFirebaseDatabase.getReference().child("workouts");

        inputexerciseDesc= (EditText) findViewById(R.id.etDesc);//
        inputwname = (EditText) findViewById(R.id.etwname);
        inputexercise = (EditText) findViewById(R.id.etExercise);
        inputcategory = (EditText) findViewById(R.id.etCate);//
        inputdate = (EditText) findViewById(R.id.etdate);//
        inputuser= (EditText) findViewById(R.id.etuser);
        user = FirebaseAuth.getInstance().getCurrentUser();
        usermail = user.getEmail();

        btnAdd = (Button)findViewById(R.id.btnAdd);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Workout ex = new Workout(inputwname.getText().toString(),inputcategory.getText().toString(),
                       usermail,inputexercise.getText().toString(),
                        inputexerciseDesc.getText().toString(), inputdate.getText().toString());
                        myRef.push().setValue(ex);

            }
        });
    }

}
