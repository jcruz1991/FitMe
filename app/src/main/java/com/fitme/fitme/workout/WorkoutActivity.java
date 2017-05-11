package com.fitme.fitme.workout;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.fitme.fitme.MainActivity;
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
    private ListView list;
    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mAuth;
    private DatabaseReference myRef;
    private DatabaseReference myWorkoutRef;
    private FirebaseUser user;

    private String[] bodyTypes = {"Chest", "Back", "Arms", "Abs", "Legs"};
    private String workoutName;

    private List<Exercise> chestExercises;
    private List<Exercise> armsExercises;
    private List<Exercise> absExercises;
    private List<Exercise> legsExercises;
    private List<Exercise> backExercises;


    private ArrayList<String> mywList;
    Button bDone;
    Button bCancel;
    private String wCategory;
    private String wDate;
    private String wName;
    private String edesc;
    private String ename;
    private String usermail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_workout);

        mywList = getIntent().getStringArrayListExtra("wlist");
        // Get Firebase Instances and Refrences
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference().child("exercises");
        myWorkoutRef = mFirebaseDatabase.getReference().child("workouts");

        // Get current user
        user = FirebaseAuth.getInstance().getCurrentUser();
        usermail = user.getEmail();

        list = (ListView) findViewById(R.id.list);
        bDone = (Button)findViewById(R.id.bDone);
        bCancel = (Button)findViewById(R.id.bCancel);

        workout = new Workout();

        chestExercises = new ArrayList<>();
        armsExercises = new ArrayList<>();
        absExercises = new ArrayList<>();
        legsExercises = new ArrayList<>();
        backExercises = new ArrayList<>();

        //requestWorkoutName();
        retrieveExercises();
        showListView();

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        Intent chestIntent = new Intent(getApplicationContext(),BodyTypeActivity
                                .class);
                        chestIntent.putExtra("mylist", (Serializable) chestExercises);
                        chestIntent.putStringArrayListExtra("wlist",mywList);
                        startActivity(chestIntent);
                        break;
                    case 1:
                        Intent backIntent = new Intent(getApplicationContext(),BodyTypeActivity
                                .class);
                        backIntent.putExtra("mylist", (Serializable) backExercises);
                        backIntent.putStringArrayListExtra("wlist",mywList);
                        startActivity(backIntent);
                        break;
                    case 2:
                        Intent armsIntent = new Intent(getApplicationContext(),BodyTypeActivity
                                .class);
                        armsIntent.putExtra("mylist", (Serializable) armsExercises);
                        armsIntent.putStringArrayListExtra("wlist",mywList);
                        startActivity(armsIntent);
                        break;
                    case 3:
                        Intent absIntent = new Intent(getApplicationContext(),BodyTypeActivity
                                .class);
                        absIntent.putExtra("mylist", (Serializable) absExercises);
                        absIntent.putStringArrayListExtra("wlist",mywList);
                        startActivity(absIntent);
                        break;
                    case 4:
                        Intent legsIntent = new Intent(getApplicationContext(),BodyTypeActivity
                                .class);
                        legsIntent.putExtra("mylist", (Serializable) legsExercises);
                        legsIntent.putStringArrayListExtra("wlist",mywList);
                        startActivity(legsIntent);
                        break;
                }
            }
        });

        bDone.setOnClickListener( new View.OnClickListener(){
            public void onClick(View V){
                //save the list into database
                final CharSequence[] items = {"Muscle Gain", "Swimming", "Running", "Diet", "Other"};
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(WorkoutActivity.this);
                builder.setTitle("Choose a category for the workout");
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        // Do something with the selection
                        if(item == 0)
                        {
                            wCategory = "Muscle Gain";
                            mywList.add(wCategory);
                            insertWorkout();
                        }
                        else if (item == 1)
                        {
                            wCategory = "Swimming";
                            mywList.add(wCategory);
                            insertWorkout();
                        }
                        else if (item == 2)
                        {
                            wCategory = "Running";
                            mywList.add(wCategory);
                            insertWorkout();
                        }
                        else if (item == 3)
                        {
                            wCategory = "Diet";
                            mywList.add(wCategory);
                            insertWorkout();
                        }
                        else
                        {
                            wCategory = "Other";
                            mywList.add(wCategory);
                            insertWorkout();
                        }


                    }
                });
                android.app.AlertDialog alert = builder.create();
                alert.show();

            }
        });
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
                    else if(exercise.getBody_type().equals("Abs")) {
                        absExercises.add(exercise);
                        Log.d("ABS COUNT", Integer.toString(absExercises.size()));
                    }
                    else if(exercise.getBody_type().equals("Legs")) {
                        legsExercises.add(exercise);
                        Log.d("LEGS COUNT", Integer.toString(legsExercises.size()));
                    }
                    else if(exercise.getBody_type().equals("Back")) {
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
        list.setAdapter(itemsAdapter);
    }

    public void btnCancelButtonClicked(View view) {
        Intent intent = new Intent(WorkoutActivity.this, MainActivity.class);
        startActivity(intent);
    }


    //Insert the workout into the firebase
    private void insertWorkout(){
        //Get the workout name and workout date in the list first
        wName = mywList.get(0);
        wDate = mywList.get(1);
        //Get the exercise name and description in the for loop and save
        //the data into firebase
        for (int i =2; i<mywList.size()-1;++i)
        {
            ename = mywList.get(i);
            edesc = mywList.get(i+1);
            wCategory = mywList.get(mywList.size()-1);

            Workout ex = new Workout(wName, wCategory, usermail, ename, edesc, wDate);
            myWorkoutRef.push().setValue(ex);
            i = i+1;

        }
        Intent intent = new Intent(WorkoutActivity.this, MainActivity.class);
        startActivity(intent);
    }
}
