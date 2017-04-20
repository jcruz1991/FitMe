package com.fitme.fitme.chat;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseListAdapter;
import com.fitme.fitme.R;
import com.fitme.fitme.adapter.MessageAdapter;
import com.fitme.fitme.model.Message;
import com.fitme.fitme.model.UserLocation;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {
    private String LOCATION_KEY;
    private String userID;
    private String textMessage;

    private Button sendButton;
    private EditText messageEditText;
    private ListView messageListView;

    private List<Message> friendlyMessages;
    private MessageAdapter messageAdapter;

    private FirebaseUser user;
    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseListAdapter<UserLocation> firebaseListAdapter;
    private DatabaseReference myRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        sendButton = (Button) findViewById(R.id.searchButton);
        messageEditText = (EditText) findViewById(R.id.messageEditText);
        messageListView = (ListView) findViewById(R.id.messageListView);

        // Initialize message ListView and its adapter
        friendlyMessages = new ArrayList<>();
        messageAdapter = new MessageAdapter(this, R.layout.item_message, friendlyMessages);
        messageListView.setAdapter(messageAdapter);


        // Grab Locations Key that is being passed from FindBuddyActivity
        Intent intent = getIntent();
        LOCATION_KEY = intent.getExtras().getString("LOCATIONS_ID");

        // Get Firebase Instances and Refrences
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference().child("locations").child(LOCATION_KEY).child
                ("messages");
        // Gets logged in users unique ID
        user = mAuth.getCurrentUser();
        userID = user.getUid();

        myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Message message = dataSnapshot.getValue(Message.class);
                messageAdapter.add(message);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void sendButtonClicked(View view) {
        textMessage = messageEditText.getText().toString();

        if(checkMessageField()) {
            Message message = new Message(user.getDisplayName(), textMessage);
            myRef.push().setValue(message);
            messageEditText.setText("");

        } else {
            Toast.makeText(getApplicationContext(), "Please enter message", Toast
                    .LENGTH_SHORT).show();
        }
    }

    private boolean checkMessageField() {
        if(textMessage.isEmpty()) {
            return false;
        }
        return true;
    }
}
