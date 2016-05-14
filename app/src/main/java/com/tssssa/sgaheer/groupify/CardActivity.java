package com.tssssa.sgaheer.groupify;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;

/*********************************************************************************************
 * This class is now redundant, use it as reference if more cardViews need to be implemented *
 ********************************************************************************************/

public class CardActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private static String LOG_TAG = "CardViewActivity";
    private Context context;
    private Firebase mFirebaseRef;
    private Toast toast;
    private CharSequence toastText;
    private ArrayList<GEvents> eventList = new ArrayList<GEvents>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card);

        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        GEvents redundant = new GEvents("Test Name", "Test Loc", "Test Desc", "N/A", "Test Date", "Test Time");
        eventList.add(redundant);
        mAdapter = new MyRecyclerViewAdapter(eventList, context);
        mRecyclerView.setAdapter(mAdapter);
        getDataSet();
    }

    protected void updateList(ArrayList<GEvents> mDataSet) {
        mAdapter = new MyRecyclerViewAdapter(mDataSet, context);
        mRecyclerView.setAdapter(mAdapter);
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

    private void getDataSet() {
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
                            String name = dataSnapshot.child("name").getValue().toString();
                            String desc = dataSnapshot.child("description").getValue().toString();

                            GEvents ev = new GEvents(name, "Loc", desc, "N/A", "Date", "Time");
                            eventList.add(ev);
                            System.out.println(ev.getName());
                            System.out.println(ev.getDescription());
                            updateList(eventList);
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

    private ArrayList<GEvents> testSet() {
        ArrayList results = new ArrayList<GEvents>();
        for (int index = 0; index < 20; index++) {
            GEvents obj = new GEvents("a", "b",
                    "c", "d", "e", "f");
            results.add(index, obj);
        }
        return results;
    }
}
