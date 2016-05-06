package com.tssssa.sgaheer.groupify;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.ListFragment;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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
import java.util.Collection;
import java.util.List;
import java.util.Map;


public class HomeActivity extends AppCompatActivity {
    private ViewPager mViewPager;
    private CpAdapter mCpAdapter;
    private Firebase mFirebaseRef;
    private Toolbar homeToolbar;
    private TextView intro;
    private Toast toast;
    private CharSequence toastText;
    private Context context;
    private ArrayList<String> eventList;
    private String usr;
    public final static String LOGOUT_MESSAGE = "com.tssssa.groupify.LOGOUT";
    public static final int NUM_ITEMS = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        mFirebaseRef = new Firebase(getResources().getString(R.string.firebase_url));

        intro = (TextView) findViewById(R.id.home_textView);
        getUsrn();

        homeToolbar = (Toolbar) findViewById(R.id.home_toolbar);
        setSupportActionBar(homeToolbar);

        mCpAdapter = new CpAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mCpAdapter);

        Intent intent = getIntent();
        String user = intent.getStringExtra(LoginActivity.EXTRA_MESSAGE);

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

    private void getUsrn() {
        String usrId = mFirebaseRef.getAuth().getUid().toString();
        String loc = "https://dazzling-heat-7399.firebaseio.com/users/"+usrId;
        Firebase mmFirebaseRef = new Firebase(getResources().getString(R.string.firebase_url));
        Firebase userRef = new Firebase(loc);
        Query ref = userRef.orderByKey();

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                intro.setText(dataSnapshot.child("username").getValue().toString());
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

    private void createList() {
        context = getApplicationContext();
        mFirebaseRef = new Firebase(getResources().getString(R.string.firebase_url));
        eventList = new ArrayList<String>();
        final Firebase eventsRef = mFirebaseRef.child("events");
        Query ref = eventsRef.orderByKey();

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                String num = snapshot.getValue().toString();
                toastText = num;
                for(DataSnapshot postSnapshot: snapshot.getChildren()) {
                    String key = postSnapshot.getKey().toString();
                    eventsRef.child(key).child("name").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            String key = dataSnapshot.getValue().toString();
                            eventList.add(key);
                            toastText = key;
                            toast.makeText(context, toastText, Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onCancelled(FirebaseError firebaseError) {
                          //  toastText = key;
                           // toast.makeText(context, toastText, Toast.LENGTH_SHORT).show();
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


    public static class CpAdapter extends FragmentPagerAdapter {

        public CpAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return NUM_ITEMS;
        }

        @Override
        public Fragment getItem(int position) {
            return ArrayListFragment.newInstance(position);
        }
    }

    public static class ArrayListFragment extends ListFragment {
        int mNum;
        ArrayList<String> eventList;


        static ArrayListFragment newInstance(int num) {
            ArrayListFragment f = new ArrayListFragment();
            Bundle args = new Bundle();
            args.putInt("num", num);
            f.setArguments(args);
            return f;
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            mNum = getArguments() != null ? getArguments().getInt("num") : 1;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.fragment_pager_list, container, false);
            View tv = v.findViewById(R.id.text);
            if (mNum == 0) {
                ((TextView) tv).setText("Events");
            } else if (mNum == 1) {
                ((TextView) tv).setText("My Profile");
            } else if (mNum == 2) {
                ((TextView) tv).setText("Notifications");

            }
            return v;
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            ArrayList<String> test = createList();
            if (mNum == 0) {
                setListAdapter(new ArrayAdapter<String>(getActivity(),
                        android.R.layout.simple_list_item_1, Cheeses.sCheeseStrings));
            }
        }

        @Override
        public void onListItemClick(ListView l, View v, int position, long id) {
            Log.i("FragmentList", "Item clicked: " + id);
        }
        private ArrayList<String> createList() {
            Firebase mFirebaseRef = new Firebase(getResources().getString(R.string.firebase_url));
            eventList = new ArrayList<String>();
            final Firebase eventsRef = mFirebaseRef.child("events");
            Query ref = eventsRef.orderByKey();

            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    String num = snapshot.getValue().toString();
                    for(DataSnapshot postSnapshot: snapshot.getChildren()) {
                        String key = postSnapshot.getKey().toString();
                        eventsRef.child(key).child("name").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                String key = dataSnapshot.getValue().toString();
                                eventList.add(key);
                            }

                            @Override
                            public void onCancelled(FirebaseError firebaseError) {
                                //  toastText = key;
                                // toast.makeText(context, toastText, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {

                }
            });
            return eventList;
        }

        public void test() {

        }
    }
}
