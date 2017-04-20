package com.fitme.fitme.workout;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.fitme.fitme.R;
import com.fitme.fitme.model.Exercise;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class BodyTypeActivity extends AppCompatActivity {

    private ListView bodyTypeListView;

    private String[] bodyTypes = {"Body", "Freeweight", "Cable", "Machine"};
    private List<Exercise> bodyExcercises;
    private List<Exercise> freeweightExcercises;
    private List<Exercise> cableExcercises;
    private List<Exercise> machineExcercises;
    private List<Exercise> list;

    private ArrayList<String> passedList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_body_type);

        bodyTypeListView = (ListView) findViewById(R.id.bodyTypeListView);

        bodyExcercises = new ArrayList<>();
        freeweightExcercises = new ArrayList<>();
        cableExcercises = new ArrayList<>();
        machineExcercises = new ArrayList<>();


        list = (ArrayList<Exercise>) getIntent().getSerializableExtra("mylist");
        passedList =getIntent().getStringArrayListExtra("wlist");
        for (int f =0; f < passedList.size(); f++)
        {
            Log.v("ASASAS", "LIST: " + passedList.get(f));
        }

        for(int i = 0; i < list.size(); i++) {
            if(list.get(i).getExercise_type().equals("Body")) {
                bodyExcercises.add(list.get(i));
            }
            else if(list.get(i).getExercise_type().equals("Freeweight")) {
                freeweightExcercises.add(list.get(i));
            }
            else if(list.get(i).getExercise_type().equals("Cable")) {
                cableExcercises.add(list.get(i));
            }
            else {
                machineExcercises.add(list.get(i));
            }
        }

        showBodyTypesListView();

        bodyTypeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        Intent bodyIntent = new Intent(getApplicationContext(),
                                DisplayExercisesActivity.class);
                        bodyIntent.putExtra("mylist", (Serializable) bodyExcercises);
                        bodyIntent.putStringArrayListExtra("wlist",passedList);
                        startActivity(bodyIntent);
                        break;
                    case 1:
                        Intent freeweightIntent = new Intent(getApplicationContext(),
                                DisplayExercisesActivity.class);
                        freeweightIntent.putExtra("mylist", (Serializable) freeweightExcercises);
                        freeweightIntent.putStringArrayListExtra("wlist",passedList);
                        startActivity(freeweightIntent);
                        break;
                    case 2:
                        Intent cableIntent = new Intent(getApplicationContext(),
                                DisplayExercisesActivity.class);
                        cableIntent.putExtra("mylist", (Serializable) cableExcercises);
                        cableIntent.putStringArrayListExtra("wlist",passedList);
                        startActivity(cableIntent);
                        break;
                    case 3:
                        Intent machineIntent = new Intent(getApplicationContext(),
                                DisplayExercisesActivity.class);
                        machineIntent.putExtra("mylist", (Serializable) machineExcercises);
                        machineIntent.putStringArrayListExtra("wlist",passedList);
                        startActivity(machineIntent);
                        break;
                }
            }
        });
    }

    private void showBodyTypesListView() {
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout
                .simple_list_item_1, bodyTypes);
        bodyTypeListView.setAdapter(arrayAdapter);
    }
}
