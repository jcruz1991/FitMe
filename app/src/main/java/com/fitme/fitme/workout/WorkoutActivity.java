package com.fitme.fitme.workout;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.fitme.fitme.R;
import com.fitme.fitme.model.Exercise;
import com.fitme.fitme.model.Workout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class WorkoutActivity extends AppCompatActivity {

    public static final String LIST_KEY = "LIST_KEY";

    public Workout workout;

    private ListView bodyTypeListView;
    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mAuth;
    private DatabaseReference myRef;
    private FirebaseUser user;

    private String[] bodyTypes = {"Chest", "Back", "Arms", "Legs"};
    private String workoutName;

    private List<Exercise> chestExercises;
    private List<Exercise> armsExercises;
    private List<Exercise> legsExercises;
    private List<Exercise> backExercises;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout);

        // Get Firebase Instances and Refrences
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference().child("exercises");

        // Get current user
        user = FirebaseAuth.getInstance().getCurrentUser();

        bodyTypeListView = (ListView) findViewById(R.id.bodyTypeListView);

        workout = new Workout();

        chestExercises = new ArrayList<>();
        armsExercises = new ArrayList<>();
        legsExercises = new ArrayList<>();
        backExercises = new ArrayList<>();

        requestWorkoutName();
        retrieveExercises();
        showListView();

        bodyTypeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        Intent chestIntent = new Intent(getApplicationContext(),BodyTypeActivity
                                .class);
                        chestIntent.putExtra("mylist", (Serializable) chestExercises);
                        startActivity(chestIntent);
                        break;
                    case 1:
                        Intent backIntent = new Intent(getApplicationContext(),BodyTypeActivity
                                .class);
                        backIntent.putExtra("mylist", (Serializable) backExercises);
                        startActivity(backIntent);
                        break;
                    case 2:
                        Intent armsIntent = new Intent(getApplicationContext(),BodyTypeActivity
                                .class);
                        armsIntent.putExtra("mylist", (Serializable) armsExercises);
                        startActivity(armsIntent);
                        break;
                    case 3:
                        Intent legsIntent = new Intent(getApplicationContext(),BodyTypeActivity
                                .class);
                        legsIntent.putExtra("mylist", (Serializable) legsExercises);
                        startActivity(legsIntent);
                        break;
                }
                /*
                if(position == 0) {
                    Intent intent = new Intent(getApplicationContext(),BodyTypeActivity.class);
                    intent.putExtra("mylist", (Serializable) chestExercises);
                    startActivity(intent);
                }

                else if(position == 1) {
                    Intent intent = new Intent(getApplicationContext(),BodyTypeActivity.class);
                    intent.putExtra("mylist", (Serializable) backExercises);
                    startActivity(intent);
                }
                else if(position == 2) {
                    Intent intent = new Intent(getApplicationContext(),BodyTypeActivity.class);
                    intent.putExtra("mylist", (Serializable) armsExercises);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(getApplicationContext(),BodyTypeActivity.class);
                    intent.putExtra("mylist", (Serializable) legsExercises);
                    startActivity(intent);
                }
                */

            }
        });
    }

    private void requestWorkoutName() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter Workout Name");

        final EditText inputWorkoutNameEditText = new EditText(this);

        builder.setView(inputWorkoutNameEditText);
        builder.setPositiveButton("Create", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                workoutName = inputWorkoutNameEditText.getText().toString();

                if(workoutName.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please Enter Workout Name", Toast
                            .LENGTH_SHORT).show();
                    requestWorkoutName();
                }
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                requestWorkoutName();
            }
        });
        builder.show();
    }

    /**
     * Retrieve list of exercises from Firebase
     */
    private void retrieveExercises() {
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // loops through all children in exercises table
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Exercise exercise = new Exercise();
                    exercise.setExercise_name(ds.getValue(Exercise.class).getExercise_name());
                    exercise.setBody_type(ds.getValue(Exercise.class).getBody_type());
                    exercise.setExercise_type(ds.getValue(Exercise.class).getExercise_type());
                    Log.d("BODY_TYPE", exercise.getBody_type());

                    if(exercise.getBody_type().equals("Chest")) {
                        chestExercises.add(exercise);
                        Log.d("CHEST COUNT", Integer.toString(chestExercises.size()));
                    }
                    else if(exercise.getBody_type().equals("Arms")) {
                        armsExercises.add(exercise);
                        Log.d("ARMS COUNT", Integer.toString(armsExercises.size()));
                    }
                    else if(exercise.getBody_type().equals("Legs")) {
                        legsExercises.add(exercise);
                        Log.d("LEGS COUNT", Integer.toString(legsExercises.size()));
                    } else {
                        backExercises.add(exercise);
                        Log.d("BACK COUNT", Integer.toString(backExercises.size()));
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("DATABASE ERROR", String.valueOf(databaseError));
            }
        });
    }

    private void showListView() {
        ArrayAdapter<String> itemsAdapter =
                new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, bodyTypes);
        bodyTypeListView.setAdapter(itemsAdapter);
    }
}
