package com.fitme.fitme.workout;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.fitme.fitme.FindBuddyActivity;
import com.fitme.fitme.R;
import com.fitme.fitme.adapter.ListWorkOutAdapter;
import com.fitme.fitme.model.Workout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class WorkOutDesc extends Activity {

    ListView exerciseList;
    private ListWorkOutAdapter Tadapter;
    private List<Workout> myWorkList;
    private String workout_name;
    private TextView tvWname;
    private TextView tvCategory;
    private int checking;
    private Button btnUse;

    public Workout workout;
    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mAuth;
    private DatabaseReference myRef;
    private FirebaseUser user;
    private String savedDate;
    ArrayList<Workout> getwdesc =new ArrayList<>();
    String usermail;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.showworkout);

        Intent intent = getIntent();
        workout_name = intent.getExtras().getString("workname");
        checking = intent.getExtras().getInt("check");
        exerciseList = (ListView)findViewById(R.id.ListView01);


        tvWname = (TextView)findViewById(R.id.tvWname);
        tvCategory = (TextView)findViewById(R.id.tvCategory);
        btnUse = (Button)findViewById(R.id.btnUse);
        tvWname.setText(workout_name);

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
                        String ex_name = get_workout.getE_name();

                        //Won't add workout with workout name Rest
                        if(!"Rest".equals(woname) && workout_name.equals(woname) && !"null".equals(ex_name))
                        {
                            getwdesc.add(get_workout);
                        }
                    }

                }
                createWdesclist();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("DATABASE ERROR", String.valueOf(databaseError));
            }
        });

        //This is for when the workout description is open from find buddy
        if(checking != 1 )
            btnUse.setVisibility(View.VISIBLE);



        //Use to get the workout name that the user wants to show in the buddy list
        btnUse.setOnClickListener( new View.OnClickListener(){

            public void onClick(View V){

                btnUse.setOnTouchListener(new View.OnTouchListener() {

                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        if (event.getAction() == MotionEvent.ACTION_DOWN) {

                            btnUse.setBackgroundColor(Color.RED);
                            return true;
                        } else if (event.getAction() == MotionEvent.ACTION_UP) {

                            btnUse.setBackgroundColor(getResources().getColor(android.R.color.holo_red_dark));
                            return true;
                        }
                        return false;
                    }
                });

                Intent intent = new Intent(WorkOutDesc.this, FindBuddyActivity.class);
                intent.putExtra("workname", tvWname.getText());
                intent.putExtra("workcategory", tvCategory.getText());
                startActivity(intent);

            }
        });
    }
    private void createWdesclist() {
        Tadapter = new ListWorkOutAdapter(this, getwdesc);
        exerciseList.setAdapter(Tadapter);
        tvCategory.setText(getwdesc.get(0).getW_category());
    }

}
