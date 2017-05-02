package com.fitme.fitme;

import android.app.Activity;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseListAdapter;
import com.fitme.fitme.adapter.ListSavedExercise;
import com.fitme.fitme.adapter.ListShowUserActivity;
import com.fitme.fitme.chat.ChatActivity;
import com.fitme.fitme.location.LocationCalculator;
import com.fitme.fitme.model.GetUserLocation;
import com.fitme.fitme.model.User;
import com.fitme.fitme.model.UserLocation;
import com.fitme.fitme.model.Workout;
import com.fitme.fitme.workout.WorkOutDesc;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class FindBuddyActivity extends AppCompatActivity
        implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private Button searchButton;
    private Button removeButton;
    private TextView displayLocalUsers;
    private ListView localUsersListView;
    private TextView activeUserTextView;
    private Spinner spinner;
    //private ArrayList<String> getCloseUser = new ArrayList<>();

    private String userID;
    private String username;
    private String usermail;
    private String chosenWorkout = "";
    private String chosenCategory = "";
    private String userkey;
    private String key;
    private UserLocation userLocation;
    int count = 0;
    private Double selectedDistance = 0.0;
    private int checked;
    ArrayList<Workout> getwname =new ArrayList<>();
    ArrayList <GetUserLocation> getLname =new ArrayList<>();
    List<String> distances = new ArrayList<String>();
    private ListSavedExercise Tadapter;
    private ListShowUserActivity Ladapter;
    ListView listView;
    private int checkRequest;
    private String workoutSelected;

    // Firebase
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser user;
    private DatabaseReference myRef;
    private DatabaseReference mLocationRef;


    // Firebase Adapter
    private FirebaseListAdapter<UserLocation> mAdapter;

    LocationCalculator locationCalculator;

    // Location
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    Geocoder geocoder;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_buddy);

        workoutSelected = getIntent().getStringExtra("workoutSelected");
        chosenWorkout = getIntent().getStringExtra("workname");
        chosenCategory = getIntent().getStringExtra("workcategory");
        // Init Views
        searchButton = (Button) findViewById(R.id.searchButton);
        removeButton = (Button) findViewById(R.id.removeButton);
        displayLocalUsers = (TextView) findViewById(R.id.activeUserTextView);
        localUsersListView = (ListView) findViewById(R.id.localUsers);
        activeUserTextView = (TextView) findViewById(R.id.activeUserTextView);
        listView = (ListView) findViewById(R.id.workoutL);
        spinner = (Spinner) findViewById(R.id.mileSpinner);

        //Remove button to Invisible
        removeButton.setVisibility(View.GONE);
        searchButton.setVisibility(View.GONE);

        if(workoutSelected.equals("1"))
            searchButton.setVisibility(View.VISIBLE);


        checked = 0;
        checkRequest = 0;

        // Firebase
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference().child("workouts");
        mDatabaseReference = mFirebaseDatabase.getReference().child("locations");
        mFirebaseAuth = FirebaseAuth.getInstance();

        mLocationRef = mFirebaseDatabase.getReference().child("locations");

        // Gets logged in users unique ID
        user = mFirebaseAuth.getCurrentUser();
        usermail = user.getEmail();
        userID = user.getUid();
        username = user.getDisplayName();
        activeUserTextView.setText(username);

        locationCalculator = new LocationCalculator();
        userLocation = new UserLocation();


        // Location
        // Geocoder converts longitude and latitude to street address
        geocoder = new Geocoder(this, Locale.getDefault());

        // Create an instance of GoogleAPIClient.
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        distances.add("5 miles");
        distances.add("10 miles");
        distances.add("15 miles");
        distances.add("20 miles");

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, distances);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spinner.setAdapter(dataAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if(parent.getItemAtPosition(position).equals("5 miles")) {
                    selectedDistance = 8.04672; // 8.04672 km = 5 miles
                }
                else if(parent.getItemAtPosition(position).equals("10 miles")) {
                    selectedDistance = 16.0934; // 16.0934 km = 5 miles
                }
                else if(parent.getItemAtPosition(position).equals("15 miles")) {
                    selectedDistance = 24.1402; // 24.1402 km = 15 miles
                } else {
                    selectedDistance = 32.1869; // 24.1402 km = 15 miles
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });



        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // loops through all children in exercises table
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Workout check_user = new Workout();
                    check_user.setUser_name(ds.getValue(Workout.class).getUser_name());
                    if (usermail.equals(check_user.getUser_name())) {

                        Workout get_workout = new Workout();
                        get_workout.setW_name(ds.getValue(Workout.class).getW_name());
                        get_workout.setW_category(ds.getValue(Workout.class).getW_category());
                        get_workout.setUser_name(ds.getValue(Workout.class).getUser_name());
                        get_workout.setE_name(ds.getValue(Workout.class).getE_name());
                        get_workout.setE_desc(ds.getValue(Workout.class).getE_desc());
                        get_workout.setW_date(ds.getValue(Workout.class).getW_date());

                        String woname = get_workout.getW_name();

                        //Won't add workout with workout name Rest
                        if (!"Rest".equals(woname)) {
                            int counter = 0;
                            for (int j = 0; j < getwname.size(); j++) {
                                //check to see if there are duplicate same workout name
                                if (getwname.get(j).getW_name().equals(woname))
                                    counter++;
                            }
                            //If there is no duplicate name in the list already add it in getwname
                            if (counter == 0)
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
                String text = textView.getText().toString(); //get the text of the string

                Intent intent = new Intent(FindBuddyActivity.this, WorkOutDesc.class);
                intent.putExtra("workname", text);
                intent.putExtra("check", 2);
                startActivity(intent);
            }
        });


        localUsersListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                TextView uid = (TextView) view.findViewById(R.id.tvWorkout);
                String key = uid.getText().toString(); // Get the text of the string

                Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
                intent.putExtra("LOCATIONS_ID", key);
                startActivity(intent);
            }
        });

        localUsersListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                // Grabs User ID
                TextView uid = (TextView) view.findViewById(R.id.tvWorkout);
                key = uid.getText().toString();    //get the text of the string

                // Searches locations table using the ID
                mLocationRef.orderByKey().addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        for (DataSnapshot childDataSnapshot : dataSnapshot.getChildren()) {

                            String dataKey = childDataSnapshot.getKey();

                            // If ID is found
                            if(dataKey.equals(key)) {

                                // Set longitude and latitude locations
                                UserLocation user = new UserLocation();
                                user.setLatitude(childDataSnapshot.getValue(UserLocation.class).getLatitude());
                                user.setLongitude(childDataSnapshot.getValue(UserLocation.class).getLongitude());

                                // Opens up Google Maps to see user's location
                                String strUri = "http://maps.google.com/maps?q=loc:" + user.getLatitude() + "," + user.getLongitude() + " (" + "Label which you want" + ")";
                                Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(strUri));

                                intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
                                startActivity(intent);

                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });

                return true;
            }
        });
    }


    /**
     * Click Event when search for partner button is clicked
     *
     * @param view
     */
    public void searchButtonClicked(View view) {
        // Get User Location and add it into the database

        retrieveUserLocation();

        listView.setVisibility(View.INVISIBLE);
        localUsersListView.setVisibility(View.VISIBLE);

        searchButton.setVisibility(View.GONE);
        removeButton.setVisibility(View.VISIBLE);

    }

    private void retrieveUserLocation() {
        try {
            // Returns last know location from user
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

            // Checks if location can be found
            if (mLastLocation != null) {
                List<Address> addresses = geocoder.getFromLocation(mLastLocation.getLatitude(), mLastLocation.getLongitude(), 1);
                String city = addresses.get(0).getLocality();

                // Set userLocation object variables
                userLocation.setId(count++);
                userLocation.setName(user.getDisplayName());
                userLocation.setLatitude(mLastLocation.getLatitude());
                userLocation.setLongitude(mLastLocation.getLongitude());
                userLocation.setCity(city);
                userLocation.setUser_workout(chosenWorkout);
                userLocation.setUser_category(chosenCategory);

                // Inset into database
                mDatabaseReference.child(userID).setValue(userLocation);

                // Display users near each other
                putCurrentUserfirst();

            }
            else {
                // Location could not be found
                Toast.makeText(getApplicationContext(), "Location Not Found", Toast.LENGTH_LONG).show();
            }
        } catch (SecurityException e) {
            Toast.makeText(getApplicationContext(), "SecurityException: " + e.toString(), Toast
                    .LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * Click Event when remove location button is clicked
     * @param view
     */
    public void removeLocationClicked(View view) {
        mDatabaseReference.child(userID).removeValue();

        removeButton.setVisibility(View.GONE);
        searchButton.setVisibility(View.GONE);

        localUsersListView.setAdapter(null);

        localUsersListView.setVisibility(View.INVISIBLE);
        listView.setVisibility(View.VISIBLE);
    }


    private void putCurrentUserfirst(){
        mLocationRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // loops through all children in exercises table
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    String requests = ds.getKey();
                    GetUserLocation get_userL = new GetUserLocation();
                    get_userL.setCity(ds.getValue(UserLocation.class).getCity());
                    get_userL.setLatitude(ds.getValue(UserLocation.class).getLatitude());
                    get_userL.setLongitude(ds.getValue(UserLocation.class).getLongitude());
                    get_userL.setName(ds.getValue(UserLocation.class).getName());
                    get_userL.setUser_category(ds.getValue(UserLocation.class).getUser_category());
                    get_userL.setUser_workout(ds.getValue(UserLocation.class).getUser_workout());
                    get_userL.setUser_uid(requests);
                    if(username.equals(get_userL.getName()))    //set the current user as the first of the list
                    {
                        getLname.add(get_userL);
                        userkey = getLname.get(0).getUser_uid();
                    }
                }
                showLocalUsers();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("DATABASE ERROR", String.valueOf(databaseError));
            }
        });

    }
    private void showLocalUsers(){
        mLocationRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // loops through all children in exercises table
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    String requests = ds.getKey();
                    GetUserLocation get_userL = new GetUserLocation();
                    get_userL.setCity(ds.getValue(UserLocation.class).getCity());
                    get_userL.setLatitude(ds.getValue(UserLocation.class).getLatitude());
                    get_userL.setLongitude(ds.getValue(UserLocation.class).getLongitude());
                    get_userL.setName(ds.getValue(UserLocation.class).getName());
                    get_userL.setUser_category(ds.getValue(UserLocation.class).getUser_category());
                    get_userL.setUser_workout(ds.getValue(UserLocation.class).getUser_workout());
                    get_userL.setUser_uid(requests);

                    if(!username.equals(get_userL.getName()))
                    {
                        if (locationCalculator.calculateDistance(userLocation, get_userL) < selectedDistance)  // 5 miles
                        {
                            if(getLname.isEmpty())
                            {
                                getLname.add(get_userL);
                            }
                            else
                            {
                                for(int j = 0; j < getLname.size(); ++ j)
                                {
                                    if(requests.equals(getLname.get(j).getUser_uid()))
                                        checkRequest ++;
                                }
                                if(!requests.equals(userkey) && checkRequest==0)
                                {
                                    getLname.add(get_userL);
                                    Log.d("PLEASE SHOW:", "SHOW: " + requests + " ::  " +getLname.get(0).getUser_uid());
                                }


                            }
                        }
                    }
                }
                createLoclist();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("DATABASE ERROR", String.valueOf(databaseError));
            }
        });

    }

    @Override
    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {}

    @Override
    public void onConnectionSuspended(int i) {
        Toast.makeText(FindBuddyActivity.this,
                "onConnectionSuspended: " + String.valueOf(i), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(FindBuddyActivity.this,
                "onConnectionFailed: \n" + connectionResult.toString(), Toast.LENGTH_LONG).show();
    }

    private void createWnamelist() {
        Tadapter = new ListSavedExercise(this, getwname);
        listView.setAdapter(Tadapter);
    }

    private void createLoclist() {
        Ladapter = new ListShowUserActivity(this, getLname);
        localUsersListView.setAdapter(Ladapter);
    }
}

