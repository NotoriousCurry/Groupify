package com.tssssa.sgaheer.groupify;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class CEventActivity extends AppCompatActivity {
    private Toolbar ceventToolbar;
    private Toast toast;
    private Firebase mFirebaseRef;
    private Context context;

    private TextView ceName;
    private TextView ceDescription;
    private TextView ceLocation;
    private Button ceCreate;

    private CharSequence toastText;
    private String eName;
    private String eDescription;
    private String eLocation;
    private ArrayList<String> eAttendees;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cevent);

        ceventToolbar = (Toolbar) findViewById(R.id.cevent_toolbar);
        setSupportActionBar(ceventToolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        mFirebaseRef = new Firebase(getResources().getString(R.string.firebase_url));
        ceName = (TextView) findViewById(R.id.create_event_name);
        ceDescription = (TextView) findViewById(R.id.create_event_description);
        ceLocation = (TextView) findViewById(R.id.create_event_location);
        ceCreate = (Button) findViewById(R.id.cevent_button);

        ceCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eName = ceName.getText().toString();
                eDescription = ceDescription.getText().toString();
                eLocation = ceLocation.getText().toString();
                checkGEvent(eName, eDescription, eLocation);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.cevent, menu);
        return true;
    }

    private void checkGEvent(String eName, String eDescription, String eLocation) {
        boolean ncheck = eName.equals("");
        boolean dcheck = eDescription.equals("");
        boolean lCheck = eLocation.equals("");
        context = getApplicationContext();
        if (ncheck == true) {
            toastText = "Enter a Name";
            toast.makeText(context, toastText, Toast.LENGTH_SHORT).show();
        } else if (dcheck == true) {
            toastText = "Enter a Description";
            toast.makeText(context, toastText, Toast.LENGTH_SHORT).show();
        } else if (lCheck == true) {
            toastText = "Enter a Location";
            toast.makeText(context, toastText, Toast.LENGTH_SHORT).show();
        } else {
            toastText = "Creating Group";
            eAttendees = new ArrayList<String>();
            toast.makeText(context, toastText, Toast.LENGTH_SHORT).show();
            GEvents ev = new GEvents(eName, eLocation, eDescription, eAttendees);
            createGEvent(ev);
        }
    }

    private void createGEvent(GEvents ev) {
        String userID = mFirebaseRef.getAuth().getUid().toString();
        context = getApplicationContext();
        Firebase postEventRef = mFirebaseRef.child("events");
        Firebase newPostRef = postEventRef.push();
        Map<String, String> eventDetails = new HashMap<String, String>();
        Map<String, Object> attendees = new HashMap<String, Object>();
        Map<String, Object> eid = new HashMap<String, Object>();

        eventDetails.put("name", ev.geteName());
        eventDetails.put("description", ev.geteDescription());
        eventDetails.put("location", ev.geteLocation());
        newPostRef.setValue(eventDetails, new Firebase.CompletionListener() {
            @Override
            public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                if(firebaseError !=null) {
                    showErrorDialog(firebaseError.getMessage());
                } else {
                    toastText = "Event Created";
                    toast.makeText(context, toastText, Toast.LENGTH_SHORT).show();
                }
            }
        });

        String postID = newPostRef.getKey();
        Firebase addIdRef = postEventRef.child(postID);
        eid.put("id", postID);
        addIdRef.updateChildren(eid);

        Firebase memberPath = new Firebase("https://dazzling-heat-7399.firebaseio.com/events/"+postID).child("members");
        attendees.put(userID, "true");
        memberPath.updateChildren(attendees, new Firebase.CompletionListener() {
            @Override
            public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                if(firebaseError !=null) {
                    showErrorDialog(firebaseError.getMessage());
                } else {
                    toastText = "Joined Event";
                    toast.makeText(context, toastText, Toast.LENGTH_SHORT).show();
                    killActivity();
                }
            }
        });
    }

    private void showErrorDialog(String message) {
        new AlertDialog.Builder(this)
                .setTitle("Error")
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void killActivity() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        finish();
    }
}
