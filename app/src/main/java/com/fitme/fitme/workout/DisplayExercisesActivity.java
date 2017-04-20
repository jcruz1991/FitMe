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

import java.util.ArrayList;
import java.util.List;

public class DisplayExercisesActivity extends AppCompatActivity {

    private ListView exercisesListView;
    private List<Exercise> list;
    private List<String> exercises;
    private ArrayList<String> gotList;

    private String exerciseDescription;
    private int itemPosition;
    private String exercisename;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_exercises);

        list = (ArrayList<Exercise>) getIntent().getSerializableExtra("mylist");
        gotList =getIntent().getStringArrayListExtra("wlist");

        for (int f =0; f < gotList.size(); f++)
        {
            Log.v("ASASAS", "LIST: " + gotList.get(f));
        }
        exercises = new ArrayList<>();

        for(int i = 0; i < list.size(); i++) {
            exercises.add(list.get(i).getExercise_name());
        }
        exercisesListView = (ListView) findViewById(R.id.exercisesListView);


        displayExercises();

        exercisesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                itemPosition = position;
                displayDialogDescription();
            }
        });
    }

    private void displayDialogDescription() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter Exercise Description");

        final EditText inputWorkoutNameEditText = new EditText(this);

        builder.setView(inputWorkoutNameEditText);
        builder.setPositiveButton("Create", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                exerciseDescription = inputWorkoutNameEditText.getText().toString();

                if(exerciseDescription.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please Enter Workout Description",
                            Toast
                                    .LENGTH_SHORT).show();
                    displayDialogDescription();
                }
                else
                {
                    exercisename= exercises.get(itemPosition);
                    gotList.add(exercisename);
                    gotList.add(exerciseDescription);
                    Intent intent = new Intent(DisplayExercisesActivity.this, WorkoutActivity.class);
                    intent.putStringArrayListExtra("wlist",gotList);
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

    private void displayExercises() {
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, exercises);
        exercisesListView.setAdapter(arrayAdapter);
    }

}
