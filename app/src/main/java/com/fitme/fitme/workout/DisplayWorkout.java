package com.fitme.fitme.workout;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.fitme.fitme.R;
import com.fitme.fitme.adapter.ListSavedExercise;
import com.fitme.fitme.model.Workout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class DisplayWorkout extends AppCompatActivity{

    ListView listView;
    private ListSavedExercise Tadapter;
    ArrayList<Workout> getwname =new ArrayList<>();
    public Workout workout;
    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mAuth;
    private DatabaseReference myRef;
    private FirebaseUser user;
    private String savedDate;
    String usermail;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout);

        savedDate = getIntent().getStringExtra("thedate");
        listView = (ListView)findViewById(R.id.bodyTypeListView);

        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        //myRef = mFirebaseDatabase.getReference().child("workout");
        myRef = mFirebaseDatabase.getReference().child("workouts");
        // Get current user
        user = FirebaseAuth.getInstance().getCurrentUser();
        usermail = user.getEmail();

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // loops through all children in exercises table
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Workout check_user = new Workout();
                    check_user.setUser_name(ds.getValue(Workout.class).getUser_name());
                    if(usermail.equals(check_user.getUser_name()))
                    {
                        Workout get_workout = new Workout();
                        get_workout.setW_name(ds.getValue(Workout.class).getW_name());
                        get_workout.setW_category(ds.getValue(Workout.class).getW_category());
                        get_workout.setUser_name(ds.getValue(Workout.class).getUser_name());
                        get_workout.setE_name(ds.getValue(Workout.class).getE_name());
                        get_workout.setE_desc(ds.getValue(Workout.class).getE_desc());
                        get_workout.setW_date(ds.getValue(Workout.class).getW_date());

                        String woname =get_workout.getW_name();

                        //Won't add workout with workout name Rest
                        if(!"Rest".equals(woname))
                        {
                            int counter = 0;
                            for(int j = 0; j < getwname.size(); j++)
                            {
                                //check to see if there are duplicate same workout name
                                if(getwname.get(j).getW_name().equals(woname))
                                    counter++;
                            }
                            //If there is no duplicate name in the list already add it in getwname
                            if(counter == 0)
                                getwname.add(get_workout);
                        }
                    }

                }
                createWnamelist();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("DATABASE ERROR", String.valueOf(databaseError));
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // ListView Clicked item value
                TextView textView = (TextView) view.findViewById(R.id.tBody_type);
                String text = textView.getText().toString();    //get the text of the string

                Intent intent = new Intent(DisplayWorkout.this, WorkOutDesc.class);
                intent.putExtra("workname", text);
                intent.putExtra("check", 1);
                startActivity(intent);
            }
        });
    }

    private void createWnamelist() {
        Tadapter = new ListSavedExercise(this, getwname);
        listView.setAdapter(Tadapter);
    }
}
