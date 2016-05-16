package com.tssssa.sgaheer.groupify;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

/**
 * Created by sgaheer on 13/05/2016.
 * This class is used to handle the view personal account/user page
 * Contains code to read & write user account details to firebase
 */


public class ViewUser extends AppCompatActivity {
    private Toast toast;
    private Firebase mFirebaseRef;
    private Context context;

    private Toolbar vuserToolbar;
    private TextView username;
    private TextView email;
    private EditText name;
    private EditText program;
    private EditText courseOne;
    private EditText courseTwo;
    private EditText courseThree;
    private EditText courseFour;

    private CharSequence toastText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_user);

        username = (TextView) findViewById(R.id.view_username);
        email = (TextView) findViewById(R.id.view_user_email);
        name = (EditText) findViewById(R.id.view_user_name);
        program = (EditText) findViewById(R.id.view_user_program);
        courseOne = (EditText) findViewById(R.id.view_user_course1);
        courseTwo = (EditText) findViewById(R.id.view_user_course2);
        courseThree = (EditText) findViewById(R.id.view_user_course3);
        courseFour = (EditText) findViewById(R.id.view_user_course4);
        UserLevelCircle circle = (UserLevelCircle) findViewById(R.id.userlevel);
        TextView usrLvl = (TextView) findViewById(R.id.level_number);

        vuserToolbar = (Toolbar) findViewById(R.id.vuser_toolbar);
        setSupportActionBar(vuserToolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        Random r = new Random();
        int ri = r.nextInt(9 - 1) + 1;
        usrLvl.setText(Integer.toString(ri));
        AnimateLevel animate = new AnimateLevel(circle, 210);
        animate.setDuration(1700);
        circle.startAnimation(animate);
        getDetails();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.vuser, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.action_update:
                toastText = "Updating...";
                toast.makeText(this, toastText, Toast.LENGTH_SHORT).show();
                updateDetails(name.getText().toString(), program.getText().toString(),
                        courseOne.getText().toString(), courseTwo.getText().toString(),
                        courseThree.getText().toString(), courseFour.getText().toString());
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void getDetails() {
        context = getApplicationContext();
        mFirebaseRef = new Firebase(getResources().getString(R.string.firebase_url));
        String usrId = mFirebaseRef.getAuth().getUid().toString();
        String loc = "https://dazzling-heat-7399.firebaseio.com/users/"+usrId;
        Firebase userRef = new Firebase(loc);
        Query ref = userRef.orderByKey();

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                username.setText(dataSnapshot.child("username").getValue().toString());
                email.setText(dataSnapshot.child("email").getValue().toString());
                if (dataSnapshot.hasChild("name")==true) {
                    String names = dataSnapshot.child("name").getValue().toString();
                    String programs = dataSnapshot.child("program").getValue().toString();
                    String c1 = dataSnapshot.child("course1").getValue().toString();
                    String c2 = dataSnapshot.child("course2").getValue().toString();
                    String c3 = dataSnapshot.child("course3").getValue().toString();
                    String c4 = dataSnapshot.child("course4").getValue().toString();

                    if(names.equals("x") == false) {
                        name.setText(names);
                    }
                    if(programs.equals("x")== false) {
                        program.setText(programs);
                    }
                    if(c1.equals("x") == false) {
                        courseOne.setText(c1);
                    }
                    if(c2.equals("x")==false) {
                        courseTwo.setText(c2);
                    }
                    if(c3.equals("x")==false) {
                        courseThree.setText(c3);
                    }
                    if(c4.equals("x")==false) {
                        courseFour.setText(c4);
                    }
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                toastText = firebaseError.toString();
                toast.makeText(context, toastText, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateDetails(String name, String program, String course1, String course2, String course3, String course4) {
        context = getApplicationContext();
        mFirebaseRef = new Firebase(getResources().getString(R.string.firebase_url));
        Map<String, Object> updates = new HashMap<String, Object>();
        String usrId = mFirebaseRef.getAuth().getUid().toString();
        String loc = "https://dazzling-heat-7399.firebaseio.com/users/"+usrId;
        Firebase userRef = new Firebase(loc);
        if (name.equals(null)) {
            name="x";
        }
        if (program.equals(null)) {
            program = "x";
        }
        if (course1.equals(null)) {
            course1 = "x";
        }
        if (course2.equals(null)) {
            course2 = "x";
        }
        if (course3.equals(null)) {
            course3 = "x";
        }
        if (course4.equals(null)) {
            course4 = "x";
        }
        updates.put("name", name);
        updates.put("program", program);
        updates.put("course1", course1);
        updates.put("course2", course2);
        updates.put("course3", course3);
        updates.put("course4", course4);

        userRef.updateChildren(updates);
        getDetails();

    }
}
