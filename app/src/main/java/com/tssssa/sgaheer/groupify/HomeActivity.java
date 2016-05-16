package com.tssssa.sgaheer.groupify;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;

/**
 * Created by sgaheer on 30/04/2016.
 * This class is used to handle home screen interface
 * Contain code to load in data from firebase to fill CardView and to obtain username
 */

public class HomeActivity extends AppCompatActivity {
    private Firebase mFirebaseRef;
    private MyRecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private Toolbar homeToolbar;
    private TextView intro;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private FloatingActionButton mFab;

    private Toast toast;
    private CharSequence toastText;
    private Context context = this;
    private ArrayList<GEvents> eventList = new ArrayList<GEvents>();
    private String usr;

    private static String LOG_TAG = "CardView Activity";
    public final static String LOGOUT_MESSAGE = "com.tssssa.groupify.LOGOUT";
    public final static String EXTRA_ID = "com.tssssa.groupify.ID";
    public final static String EXTRA_NAME = "com.tssssa.groupify.NAME";
    public final static String Extra_DESC = "com.tssssa.groupify.DESC";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        mFirebaseRef = new Firebase(getResources().getString(R.string.firebase_url));
        context = getApplicationContext();

        intro = (TextView) findViewById(R.id.home_textView);
        intro.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
        intro.setTextColor(getResources().getColor(R.color.ghostWhite));
        getUsrn();

        homeToolbar = (Toolbar) findViewById(R.id.home_toolbar);
        setSupportActionBar(homeToolbar);

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshHome);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                createList();
            }
        });
        mSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        mFab = (FloatingActionButton) findViewById(R.id.home_fab);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goToCevents = new Intent(context, CEventActivity.class);
                startActivity(goToCevents);
            }
        });

        mRecyclerView = (MyRecyclerView) findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        GEvents redundant = new GEvents("Create an Event", "Enter a Description", "Enter a Location", "N/A", "Enter a Date", "Enter Time");
        eventList.add(redundant);
        mAdapter = new MyRecyclerViewAdapter(eventList, context);
        mRecyclerView.setAdapter(mAdapter);
        createList();

        // Add in notification stuff for background task
        //final Intent serviceIntent = new Intent(this, NotificationService.class);
        //Firebase notiRef = new Firebase("https://dazzling-heat-7399.firebaseio.com/events");


        Firebase evRef = new Firebase("https://dazzling-heat-7399.firebaseio.com/events");
        evRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                eventList.clear();
                createList();
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                toastText = firebaseError.toString();
                toast.makeText(context, toastText, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.action_logout:
                logout();
                return true;
            case R.id.view_account:
                Intent goToViewAcct = new Intent(this, ViewUser.class);
                startActivity(goToViewAcct);
                return true;
            case R.id.get_help:
                Intent goToHelp = new Intent(this, HelpActivity.class);
                startActivity(goToHelp);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        ((MyRecyclerViewAdapter) mAdapter).setOnItemClickListener(new MyRecyclerViewAdapter
                .MyClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                Log.i(LOG_TAG, "Clcked on Item" + position);
            }
        });
    }

    protected void onNewIntent(Intent intent) {
            super.onNewIntent(intent);
            String message = intent.getStringExtra(CEventActivity.CREATE_MESSAGE);
            if(message.equals("create")) {
                createList();
            }
         }

    private void getUsrn() {
        String usrId = mFirebaseRef.getAuth().getUid().toString();
        String loc = "https://dazzling-heat-7399.firebaseio.com/users/"+usrId;
        Firebase userRef = new Firebase(loc);
        Query ref = userRef.orderByKey();

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                intro.setText("Welcome Back " + dataSnapshot.child("username").getValue().toString() + "!");
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    private void logout() {
        mFirebaseRef = new Firebase(getResources().getString(R.string.firebase_url));
        mFirebaseRef.unauth();
        killActivity();
    }

    private void killActivity() {
        Intent goToLogin = new Intent(this, LoginActivity.class);
        goToLogin.putExtra(LOGOUT_MESSAGE, "logout");
        startActivity(goToLogin);
        finish();
    }


    private void updateEventList(ArrayList<GEvents> arList) {
        int fillTest = arList.size();
        if (fillTest != 0) {
            mAdapter = new MyRecyclerViewAdapter(arList, context);
            mRecyclerView.setAdapter(mAdapter);
        }
        mSwipeRefreshLayout.setRefreshing(false);
    }

    private void createList() {
        context = getApplicationContext();
        mFirebaseRef = new Firebase(getResources().getString(R.string.firebase_url));
        final Firebase eventsRef = mFirebaseRef.child("events");
        Query ref = eventsRef.orderByKey();

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Boolean exists = snapshot.hasChildren();
                if(exists == true) {
                    String num = snapshot.getValue().toString();
                    eventList.clear();
                    toastText = num;
                    for(DataSnapshot postSnapshot: snapshot.getChildren()) {
                        String key = postSnapshot.getKey().toString();
                        String test = postSnapshot.getValue().toString();
                        toastText = test;
                        toast.makeText(context, toastText, Toast.LENGTH_SHORT);
                        eventsRef.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                String name = dataSnapshot.child("name").getValue().toString();
                                String desc = dataSnapshot.child("description").getValue().toString();
                                String loc = dataSnapshot.child("location").getValue().toString();
                                String idd = dataSnapshot.child("id").getValue().toString();
                                String dat = dataSnapshot.child("date").getValue().toString();
                                String tim = dataSnapshot.child("time").getValue().toString();
                                GEvents ev = new GEvents(name, loc, desc, idd, dat, tim);
                                eventList.add(ev);
                                updateEventList(eventList);
                            }
                            @Override
                            public void onCancelled(FirebaseError firebaseError) {
                                toastText = firebaseError.toString();
                                toast.makeText(context, toastText, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                toastText = firebaseError.getMessage();
                toast.makeText(context, toastText, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
