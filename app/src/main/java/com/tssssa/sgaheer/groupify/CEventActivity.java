package com.tssssa.sgaheer.groupify;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;


public class CEventActivity extends AppCompatActivity {
    public final static String CREATE_MESSAGE = "com.tssssa.sgaheer.groupify.CREATE";
    private Toolbar ceventToolbar;
    private Toast toast;
    private Firebase mFirebaseRef;
    private Context context;

    private TextView ceName;
    private TextView ceDescription;
    private TextView ceLocation;
    private Button ceCreate;
    private ImageButton ceDate;
    private ImageButton ceTime;

    private CharSequence toastText;
    private String eName;
    private String eDescription;
    private String eLocation;
    private String dates = "";
    private String times = "";
    private ArrayList<String> eAttendees;
    private String[] sArray;
    private int year, month, day, hour, minute;

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
        ceDate = (ImageButton) findViewById(R.id.cevent_dateButton);
        ceTime = (ImageButton) findViewById(R.id.cevent_timeButton);

        ceDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDate(v);
            }
        });

        ceTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setTime(v);
            }
        });

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

    private void setDate(View v) {
        final Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dPG = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                dates = Integer.toString(year) + "/" + Integer.toString(monthOfYear)
                        + "/" + Integer.toString(dayOfMonth);
            }
        }, year, month, day);
        dPG.show();
    }

    private void setTime(View v) {
        final Calendar c = Calendar.getInstance();
        hour = c.get(Calendar.HOUR_OF_DAY);
        minute = c.get(Calendar.MINUTE);

        TimePickerDialog tpG = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        times = Integer.toString(hourOfDay) + ":" + Integer.toString(minute);
                    }
                }, hour, minute, false);
        tpG.show();
    }

    private void checkGEvent(String eName, String eDescription, String eLocation) {
        System.out.println(dates + " " + times);
        boolean tCheck = times.equals("");
        boolean daCheck = dates.equals("");
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
        } else if (daCheck == true){
            toastText = "Choose a Date";
            toast.makeText(context, toastText, Toast.LENGTH_SHORT).show();
        } else if (tCheck == true) {
            toastText = "Choose a Time";
            toast.makeText(context, toastText, Toast.LENGTH_SHORT).show();
        } else {
            toastText = "Creating Group";
            eAttendees = new ArrayList<String>();
            toast.makeText(context, toastText, Toast.LENGTH_SHORT).show();
            GEvents ev = new GEvents(eName, eLocation, eDescription, "N/A", dates, times);
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

        eventDetails.put("name", ev.getName());
        eventDetails.put("description", ev.getDescription());
        eventDetails.put("location", ev.getLocation());
        eventDetails.put("date", ev.getDate());
        eventDetails.put("time", ev.getTime());
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
        attendees.put(userID, userID);
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
        intent.putExtra(CREATE_MESSAGE, "create");
        startActivity(intent);
        finish();
    }
}
