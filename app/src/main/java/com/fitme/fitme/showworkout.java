package com.fitme.fitme;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;

import com.fitme.fitme.model.Workout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class showworkout extends AppCompatActivity {
    public static final String LIST_KEY = "LIST_KEY";

    public Workout workout;

    private ListView bodyTypeListView;
    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mAuth;
    private DatabaseReference myRef;
    private FirebaseUser user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_exercise);

        // Get Firebase Instances and Refrences
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference().child("workouts");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // loops through all children in exercises table
                for (DataSnapshot ds : dataSnapshot.getChildren()) {

                    Workout get_workout = new Workout();
                    get_workout.setW_name(ds.getValue(Workout.class).getW_name());
                    get_workout.setW_category(ds.getValue(Workout.class).getW_category());
                    get_workout.setUser_name(ds.getValue(Workout.class).getUser_name());
                    get_workout.setE_name(ds.getValue(Workout.class).getE_name());
                    get_workout.setE_desc(ds.getValue(Workout.class).getE_desc());
                    get_workout.setW_date(ds.getValue(Workout.class).getW_date());


                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("DATABASE ERROR", String.valueOf(databaseError));
            }
        });

        // Get current user
        user = FirebaseAuth.getInstance().getCurrentUser();
    }
}
