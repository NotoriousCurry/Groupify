package com.tssssa.sgaheer.groupify;

import android.content.Context;
import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;
import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity {
    private Firebase mFirebaseRef;
    private Toolbar homeToolbar;
    private TextView intro;
    private ListView homeEvents;
    private Toast toast;
    private CharSequence toastText;
    private Context context;
    private ArrayList<String> eventList = new ArrayList<String>();
    private ArrayAdapter<String> eventAdapter;
    private String usr;
    public final static String LOGOUT_MESSAGE = "com.tssssa.groupify.LOGOUT";
    public static final int NUM_ITEMS = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        mFirebaseRef = new Firebase(getResources().getString(R.string.firebase_url));

        intro = (TextView) findViewById(R.id.home_textView);
        intro.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
        intro.setTextColor(getResources().getColor(R.color.ghostWhite));
        getUsrn();

        homeToolbar = (Toolbar) findViewById(R.id.home_toolbar);
        setSupportActionBar(homeToolbar);

        homeEvents = (ListView) findViewById(R.id.home_listview_events);
        homeEvents.setBackgroundColor(getResources().getColor(R.color.ghostWhite));
        homeEvents.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                toast.makeText(getApplicationContext(), "List Number " + position, Toast.LENGTH_SHORT).show();
            }
        });

        Firebase evRef = new Firebase("https://dazzling-heat-7399.firebaseio.com/events");
        evRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
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
            case R.id.refresh_page:
                toastText = "Refreshing";
                toast.makeText(context, toastText, Toast.LENGTH_SHORT).show();
                createList();
                return true;
            case R.id.create_group:
                Intent goToCevents = new Intent(this, CEventActivity.class);
                startActivity(goToCevents);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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

    private void updateEventList(ArrayList<String> arList) {
        eventAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, arList);
        homeEvents.setAdapter(eventAdapter);
    }

    private void createList() {
        context = getApplicationContext();
        mFirebaseRef = new Firebase(getResources().getString(R.string.firebase_url));
        final Firebase eventsRef = mFirebaseRef.child("events");
        Query ref = eventsRef.orderByKey();

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
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
                            String key = dataSnapshot.child("name").getValue().toString();
                            eventList.add(key);
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

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                toastText = firebaseError.getMessage();
                toast.makeText(context, toastText, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
