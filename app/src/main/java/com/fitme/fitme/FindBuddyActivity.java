package com.fitme.fitme;

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
import com.fitme.fitme.chat.ChatActivity;
import com.fitme.fitme.location.LocationCalculator;
import com.fitme.fitme.model.UserLocation;
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

    List<Address> addresses;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    Geocoder geocoder;

    int count = 0;

    private FirebaseUser user;
    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseListAdapter<UserLocation> firebaseListAdapter;
    private DatabaseReference myRef;

    private List<UserLocation> locals;
    private List<UserLocation> usersNearYou;
    private Button searchButton;
    private Button removeButton;
    private String userID;
    private TextView displayLocalUsers;
    private TextView email;
    private TextView city;
    private ListView localUsersListView;
    private LocationCalculator locationCalculator;

    UserLocation userLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_buddy);

        // Init Views
        searchButton = (Button) findViewById(R.id.searchButton);
        removeButton = (Button) findViewById(R.id.removeButton);
        displayLocalUsers = (TextView) findViewById(R.id.activeUserTextView);
        localUsersListView = (ListView) findViewById(R.id.localUsers);

        //Remove button to Invisible
        removeButton.setVisibility(View.INVISIBLE);

        // Init userLocation Onject and List
        userLocation = new UserLocation();
        locals = new ArrayList<UserLocation>();

        // Get Firebase Instances and Refrences
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference().child("locations");
        // Gets logged in users unique ID
        user = mAuth.getCurrentUser();
        userID = user.getUid();

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
                String key = firebaseListAdapter.getRef(position).getKey();
                Toast.makeText(getApplicationContext(), key, Toast.LENGTH_LONG).show();
                Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
                intent.putExtra("LOCATIONS_ID", key);
                startActivity(intent);
            }
        });

        firebaseListAdapter = new
                FirebaseListAdapter<UserLocation>(this, UserLocation.class, android.R.layout
                        .simple_list_item_1, myRef) {

                    @Override
                    protected void populateView(View v, UserLocation model, int position) {

                        v = View.inflate(getApplicationContext(), R.layout.location_item_list, null);
                        email = (TextView) v.findViewById(R.id.email);
                        city = (TextView) v.findViewById(R.id.city);

                        //email.setText(usersNearYou.get(position).getEmail());
                        //city.setText(usersNearYou.get(position).getCity());
                        email.setText(model.getEmail());
                        //Log.d("DISPLAY EMAIL", model.getEmail());
                        city.setText(model.getCity());
                        //Log.d("DISPLAY CITY", model.getCity());
                    }
                };
        localUsersListView.setAdapter(firebaseListAdapter);

    }

    /**
     * Click Event when search for partner button is clicked
     * @param view
     */
    public void searchButtonClicked(View view) {

        // Get User Location and add it into the database
        getMyLocation();
        searchButton.setVisibility(View.INVISIBLE);
        removeButton.setVisibility(View.VISIBLE);
        // Grab list of users in locations table
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                showData(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    /**
     * Click Event when remove location button is clicked
     * @param view
     */
    public void removeLocationClicked(View view) {
        myRef.child(userID).removeValue();
        searchButton.setVisibility(View.VISIBLE);
        removeButton.setVisibility(View.INVISIBLE);
    }

    /**
     * Will read from the locations table in database
     * @param dataSnapshot
     */
    private void showData(DataSnapshot dataSnapshot) {

        // loops through all children in locations table
        for (DataSnapshot ds : dataSnapshot.getChildren()) {
            UserLocation uLocation = new UserLocation();
            uLocation.setEmail(ds.getValue(UserLocation.class).getEmail());
            uLocation.setLatitude(ds.getValue(UserLocation.class).getLatitude());
            uLocation.setLongitude(ds.getValue(UserLocation.class).getLongitude());
            uLocation.setCity(ds.getValue(UserLocation.class).getCity());

            // Add user to list
            locals.add(uLocation);
        }

        Log.d("COUNT", Integer.toString(locals.size()));
        // Call comparee Locations to get users near you
        compareLocations(locals);
        // Display local users to listview
        displayLocalUsers(usersNearYou);
    }

    /**
     * Will compare users distances
     * @param locals
     */
    private void compareLocations(List<UserLocation> locals) {
        locationCalculator = new LocationCalculator();
        usersNearYou = new ArrayList<UserLocation>();

        // Check users to see if in 5 mile radius (8.04672 km = 5 mi)
        for(int i = 0; i < locals.size(); i++) {
            if(locationCalculator.calculateDistance(userLocation,locals.get(i)) < 8.04672) {
                usersNearYou.add(locals.get(i)); // Add user to list
            }
        }
    }

    /**
     * Retrieve device location
     **/
    private void getMyLocation() {
        try {
            // Returns last know location from user
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

            // Checks if location can be found
            if (mLastLocation != null) {
                addresses = geocoder.getFromLocation(mLastLocation.getLatitude(), mLastLocation.getLongitude(), 1);
                String city = addresses.get(0).getLocality();

                // Set userLocation object variables
                userLocation.setId(count++);
                userLocation.setEmail(user.getEmail());
                userLocation.setLatitude(mLastLocation.getLatitude());
                userLocation.setLongitude(mLastLocation.getLongitude());
                userLocation.setCity(city);

                // Display Toast(pop-up message displaying email, latitude, longitude and city
                Toast.makeText(getApplicationContext(), "EMAIL: " + userLocation.getEmail() + " LATITUDE: " + userLocation.getLatitude() + " LONGITUDE: " + userLocation.getLongitude() + " CITY: " +
                        userLocation.getCity(), Toast.LENGTH_LONG).show();

                // Inset into database
                insertIntoDatabase(userLocation);
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
     * Adds users to database
     * @param userLocation
     */
    private void insertIntoDatabase(UserLocation userLocation) {
        myRef.child(userID).setValue(userLocation);
    }

    /**
     * Display list of usersNearYou onto ListView
     * @param usersNearYou
     */
    private void displayLocalUsers(final List<UserLocation> usersNearYou) {
        Log.d("DISPLAY COUNT", Integer.toString(usersNearYou.size()));
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
}
