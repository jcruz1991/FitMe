package com.fitme.fitme;

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

public class FindBuddyActivity extends AppCompatActivity
        implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

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
                userLocation.setEmail(user.getEmail());
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
        Toast.makeText(FindBuddyActivity.this,
                "onConnectionSuspended: " + String.valueOf(i), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(FindBuddyActivity.this,
                "onConnectionFailed: \n" + connectionResult.toString(), Toast.LENGTH_LONG).show();
    }
}
