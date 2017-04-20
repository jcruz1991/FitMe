package com.fitme.fitme;
/*

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseListAdapter;
import com.fitme.fitme.adapter.ListShowUserActivity;
import com.fitme.fitme.chat.ChatActivity;
import com.fitme.fitme.location.LocationCalculator;
import com.fitme.fitme.model.UserLocation;
import com.fitme.fitme.model.Workout;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.vision.text.Text;
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
*/

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseListAdapter;
import com.fitme.fitme.chat.ChatActivity;
import com.fitme.fitme.location.LocationCalculator;
import com.fitme.fitme.model.UserLocation;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class ShowUserActivity extends AppCompatActivity
        implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener{

/*

    private ListShowUserActivity Tadapter;
    private List<UserLocation> myLocationList;
    ArrayList<UserLocation> getLoc =new ArrayList<>();
    private TextView tvUser;
    private TextView tvLocation;
    private TextView tvWorkout;
    private UserLocation userL;
    private String usermail;
    private String workname;
    private ArrayList<String> passedLList;


    //private Button searchButton;
    private Button removeButton;
    private TextView displayLocalUsers;
    private ListView localUsersListView;///
    private TextView activeUserTextView;

    private String userID;
    private String username;
    private UserLocation userLocation;
    int count = 0;

    // Firebase
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser user;

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
        //setContentView(R.layout.activity_find_buddy);
        setContentView(R.layout.activity_show_user);

        workname = getIntent().getStringExtra("chosenworkname");
        //passedLList =getIntent().getStringArrayListExtra("Llist");
        //for(int i = 0; i <passedLList.size(); i++)
        //{
            //Log.d("MAMAMAM", "ADA: " + passedLList.get(i));

        //}
        // Init Views
        //searchButton = (Button) findViewById(R.id.searchButton);
        removeButton = (Button) findViewById(R.id.cancelButton);
        //displayLocalUsers = (TextView) findViewById(R.id.userTextView);
        localUsersListView = (ListView) findViewById(R.id.showlocalUsers);
        activeUserTextView = (TextView) findViewById(R.id.userTextView);


        // Firebase
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference().child("locations");


            // Gets logged in users unique ID
            user = mFirebaseAuth.getCurrentUser();
            userID = user.getUid();
            username = user.getDisplayName();
            usermail = user.getEmail();
            activeUserTextView.setText(usermail);

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
        /*

        mDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // loops through all children in exercises table
                for (DataSnapshot ds : dataSnapshot.getChildren()) {

                    //UserLocation check_user = new UserLocation();
                    //check_user.setEmail(ds.getValue(UserLocation.class).getEmail());
                    //if(usermail.equals(check_user.getEmail()))
                    //{

                    //if(locationCalculator.calculateDistance(userLocation, userL) <  8.04672)  // 5 miles
                    //{
                        UserLocation get_workout = new UserLocation();
                        get_workout.setEmail(ds.getValue(UserLocation.class).getEmail());
                        get_workout.setCity(ds.getValue(UserLocation.class).getCity());
                        get_workout.setUser_workout(ds.getValue(UserLocation.class).getUser_workout());


                        getLoc.add(get_workout);
                    ///}

                    //}

                }
                createLoclist();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("DATABASE ERROR", String.valueOf(databaseError));
            }
        });


        localUsersListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String key = mAdapter.getRef(position).getKey();
                Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
                intent.putExtra("LOCATIONS_ID", key);
                startActivity(intent);
            }
        });
    }
    private void createLoclist() {
        Tadapter = new ListShowUserActivity(this, getLoc);
        localUsersListView.setAdapter(Tadapter);
    }
    public void removeLocationClicked(View view) {
        //mDatabaseReference.child(userID).removeValue();
        //searchButton.setVisibility(View.VISIBLE);
        //removeButton.setVisibility(View.INVISIBLE);
        findLocalUsers();
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
                userLocation.setEmail(user.getEmail());
                userLocation.setLatitude(mLastLocation.getLatitude());
                userLocation.setLongitude(mLastLocation.getLongitude());
                userLocation.setCity(city);
                userLocation.setUser_workout(workname);

                // Inset into database
                mDatabaseReference.child(userID).setValue(userLocation);

                // Display users near each other
                findLocalUsers();
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
    private void findLocalUsers() {
        mAdapter = new FirebaseListAdapter<UserLocation>(this, UserLocation.class, android.R.layout
                .two_line_list_item, mDatabaseReference) {
            @Override
            protected void populateView(View view, UserLocation user, int position) {

                if(locationCalculator.calculateDistance(userLocation, user) <  8.04672)  // 5 miles
                {
                    ((TextView)view.findViewById(android.R.id.text1)).setText(user.getEmail());
                    ((TextView)view.findViewById(android.R.id.text2)).setText(user.getCity());
                }

            }
        };
        localUsersListView.setAdapter(mAdapter);
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
        Toast.makeText(ShowUserActivity.this,
                "onConnectionSuspended: " + String.valueOf(i), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(ShowUserActivity.this,
                "onConnectionFailed: \n" + connectionResult.toString(), Toast.LENGTH_LONG).show();
    }
    */


    private Button searchButton;
    private Button removeButton;
    private TextView displayLocalUsers;
    private ListView localUsersListView;
    private TextView activeUserTextView;

    private String userID;
    private String username;
    private UserLocation userLocation;
    int count = 0;

    // Firebase
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser user;

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

        // Init Views
        searchButton = (Button) findViewById(R.id.searchButton);
        removeButton = (Button) findViewById(R.id.removeButton);
        displayLocalUsers = (TextView) findViewById(R.id.activeUserTextView);
        localUsersListView = (ListView) findViewById(R.id.localUsers);
        activeUserTextView = (TextView) findViewById(R.id.activeUserTextView);

        //Remove button to Invisible
        removeButton.setVisibility(View.INVISIBLE);

        // Firebase
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference().child("locations");
        mFirebaseAuth = FirebaseAuth.getInstance();

        // Gets logged in users unique ID
        user = mFirebaseAuth.getCurrentUser();
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

        localUsersListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String key = mAdapter.getRef(position).getKey();
                Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
                intent.putExtra("LOCATIONS_ID", key);
                startActivity(intent);
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

        searchButton.setVisibility(View.INVISIBLE);
        removeButton.setVisibility(View.VISIBLE);

    }

    /**
     * Click Event when remove location button is clicked
     *
     * @param view
     */
    public void removeLocationClicked(View view) {
        mDatabaseReference.child(userID).removeValue();
        searchButton.setVisibility(View.VISIBLE);
        removeButton.setVisibility(View.INVISIBLE);
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

                // Inset into database
                mDatabaseReference.child(userID).setValue(userLocation);

                // Display users near each other
                findLocalUsers();
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

    private void findLocalUsers() {
        mAdapter = new FirebaseListAdapter<UserLocation>(this, UserLocation.class, android.R.layout
                .two_line_list_item, mDatabaseReference) {
            @Override
            protected void populateView(View view, UserLocation user, int position) {

                if(locationCalculator.calculateDistance(userLocation, user) <  8.04672)  // 5 miles
                {
                    ((TextView)view.findViewById(android.R.id.text1)).setText(user.getName());
                    ((TextView)view.findViewById(android.R.id.text2)).setText(user.getCity());
                }

            }
        };
        localUsersListView.setAdapter(mAdapter);
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
        Toast.makeText(ShowUserActivity.this,
                "onConnectionSuspended: " + String.valueOf(i), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(ShowUserActivity.this,
                "onConnectionFailed: \n" + connectionResult.toString(), Toast.LENGTH_LONG).show();
    }
}
