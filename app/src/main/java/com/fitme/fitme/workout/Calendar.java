package com.fitme.fitme.workout;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.fitme.fitme.R;
import com.fitme.fitme.model.Workout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;

public class Calendar extends AppCompatActivity
{
    public Workout workout;

    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private DatabaseReference myRef;


    ArrayList<Workout> getwork =new ArrayList<>();
    ArrayList<Workout> workingpract =new ArrayList<>();
    ArrayList<String> NewDates = new ArrayList<String>();
    private String trimmonth;
    private String storemonth;
    private String storeday;
    private String storeyear;
    final Context ncontext = this;
    private String sUserinput;
    private String collectivedate;
    String usermail;

    ArrayList<String> createworkout = new ArrayList<String>();
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calendar_main);
        // Get Firebase Instances and Refrences
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
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

                        getwork.add(get_workout);
                    }

                }
                //copy the workout name and split the date into day,month and year
                for (int i = 0; i < getwork.size(); i++) {
                    String splitdate = getwork.get(i).getW_date();              
                    splitdate = splitdate.replaceAll("-"," ");
                    //split the date into day, month, and year
                    final String[] splited = splitdate.split("\\s+");
                    NewDates.add(getwork.get(i).getW_name());
                    NewDates.add(splited[0]);
                    NewDates.add(splited[1]);
                    NewDates.add(splited[2]);
                }
                final HashSet<Date> events = new HashSet<>();
                events.add(new Date());

                final CalendarView cv = ((CalendarView)findViewById(R.id.calendar_view));               
                cv.updateCalendar(NewDates);

                // assign event handler
                cv.setEventHandler(new CalendarView.EventHandler()
                {
                    @Override
                    public void onDayPress(Date date)
                    {
                        // show returned day
                        DateFormat df = SimpleDateFormat.getDateInstance();

                        Toast.makeText(Calendar.this, df.format(date), Toast.LENGTH_SHORT).show();

                /*Getting the day, month and year to store in database*/

                        String ab = df.format(date).toString();
                        //change the "," into " " in date
                        ab = ab.replaceAll(","," ");
                        //split the date into day, month, and year
                        final String[] splited = ab.split("\\s+");                    
                        storeday = splited[1].trim();
                        //get the year from 2017 to 117 to add into database                    
                        storeyear = splited[2].trim();
                        trimmonth = splited[0].trim();

                        if(trimmonth.equals("Jan")) {
                            storemonth = "1";
                        }
                        else if(trimmonth.equals("Feb")) {
                            storemonth = "2";
                        }
                        else if(trimmonth.equals("Mar")){
                            storemonth = "3";
                        }
                        else if(trimmonth.equals("Apr")){
                            storemonth = "4";
                        }
                        else if(trimmonth.equals("May")) {
                            storemonth = "5";
                        }
                        else if(trimmonth.equals("Jun")) {
                            storemonth = "6";
                        }
                        else if(trimmonth.equals("Jul")) {
                            storemonth = "7";
                        }
                        else if(trimmonth.equals("Aug")) {
                            storemonth = "8";
                        }
                        else if(trimmonth.equals("Sep")) {
                            storemonth = "9";
                        }
                        else if(trimmonth.equals("Oct")) {
                            storemonth = "10";
                        }
                        else if(trimmonth.equals("Nov")) {
                            storemonth = "11";
                        }
                        else if(trimmonth.equals("Dec")) {
                            storemonth = "12";
                        }
                        collectivedate = storemonth+"-"+storeday+"-"+storeyear;
                        cv.updateCalendar(NewDates);

                        //if selected date already have a routine or rest day then dont show the dialog but show the
                        //workout of the routine
                        final CharSequence[] items = {"Create Workout", "Saved Workout", "Rest Day"};
                        AlertDialog.Builder builder = new AlertDialog.Builder(Calendar.this);
                        builder.setTitle("Make your selection");
                        builder.setItems(items, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int item) {
                                // Do something with the selection
                                if(item == 0)
                                {
                                    requestWorkoutName();
                                }
                                else if (item == 1)
                                {
                                    Intent intent = new Intent(Calendar.this, Choose_Workout.class);
                                    intent.putExtra("thedate",collectivedate);
                                    startActivity(intent);
                                }
                                else if (item == 2)
                                {
                                    //update the database to store rest day and reload the screen again
                                    Workout ex = new Workout("Rest","Other", usermail,"Rest", "Rest", collectivedate);
                                    myRef.push().setValue(ex);

                                    Intent intent = new Intent(Calendar.this, Calendar.class);
                                    startActivity(intent);
                                }

                            }
                        });
                        AlertDialog alert = builder.create();
                        alert.show();
                    }


                    @Override
                    public void setEvents() {
                        cv.updateCalendar(NewDates);
                    }
                });

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("DATABASE ERROR", String.valueOf(databaseError));
            }

        });
    }

    private void requestWorkoutName() {
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
        builder.setTitle("Enter Workout Name");

        final EditText inputWorkoutNameEditText = new EditText(this);

        builder.setView(inputWorkoutNameEditText);
        builder.setPositiveButton("Create", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String workoutName = inputWorkoutNameEditText.getText().toString();

                if(workoutName.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please Enter Workout Name", Toast
                            .LENGTH_SHORT).show();
                    requestWorkoutName();
                }
                else{

                    createworkout.add(workoutName);
                    createworkout.add(collectivedate);
                    Intent intent = new Intent(Calendar.this, WorkoutActivity.class);
                    intent.putStringArrayListExtra("wlist",createworkout);
                    startActivity(intent);

                }

            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }
}
