package com.tssssa.sgaheer.groupify;

import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;

import java.util.Random;

public class ViewFriend extends AppCompatActivity {
    private Toast toast;
    private Firebase mFirebaseRef;
    private Context context;

    private Toolbar vfriendToolbar;
    private TextView username;
    private TextView email;
    private TextView name;
    private TextView program;
    private TextView courseOne;
    private TextView courseTwo;
    private TextView courseThree;
    private TextView courseFour;

    private CharSequence toastText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_friend);

        username = (TextView) findViewById(R.id.view_friend_username);
        email = (TextView) findViewById(R.id.view_friend_email);
        name = (TextView) findViewById(R.id.view_friend_name);
        program = (TextView) findViewById(R.id.view_friend_program);
        courseOne = (TextView) findViewById(R.id.view_friend_course1);
        courseTwo = (TextView) findViewById(R.id.view_friend_course2);
        courseThree = (TextView) findViewById(R.id.view_friend_course3);
        courseFour = (TextView) findViewById(R.id.view_friend_course4);
        UserLevelCircle circle = (UserLevelCircle) findViewById(R.id.friendlevel);
        TextView frLvl = (TextView) findViewById(R.id.friend_level_number);

        vfriendToolbar = (Toolbar) findViewById(R.id.vfriend_toolbar);
        setSupportActionBar(vfriendToolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        final String usr = intent.getStringExtra(ViewEvent.EXTRA_USR);
        final String id = intent.getStringExtra(ViewEvent.EXTRA_ID);
        System.out.println(id);
        username.setText(usr);
        Random r = new Random();
        int ri = r.nextInt(9 - 1) + 1;
        frLvl.setText(Integer.toString(ri));
        Random radius = new Random();
        int ra = radius.nextInt(350 - 50) + 50;
        AnimateLevel animate = new AnimateLevel(circle, ra);
        animate.setDuration(1700);
        circle.startAnimation(animate);
        getDetails(id);
    }

    private void getDetails(String id) {
        context = getApplicationContext();
        String loc = "https://dazzling-heat-7399.firebaseio.com/users/"+id;
        Firebase userRef = new Firebase(loc);
        Query ref = userRef.orderByKey();

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
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
}
