package com.fitme.fitme.workout;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.fitme.fitme.R;
import com.fitme.fitme.model.Exercise;

import java.util.ArrayList;
import java.util.List;

public class DisplayExercisesActivity extends AppCompatActivity {

    private ListView exercisesListView;
    private List<Exercise> list;
    private List<String> exercises;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_exercises);

        list = (ArrayList<Exercise>) getIntent().getSerializableExtra("mylist");
        exercises = new ArrayList<>();

        for(int i = 0; i < list.size(); i++) {
            exercises.add(list.get(i).getExercise_name());
        }
        exercisesListView = (ListView) findViewById(R.id.exercisesListView);


        displayExercises();
    }

    private void displayExercises() {
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, exercises);
        exercisesListView.setAdapter(arrayAdapter);
    }


}
