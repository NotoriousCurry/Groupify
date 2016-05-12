package com.tssssa.sgaheer.groupify;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ViewEvent extends AppCompatActivity {
    private Toast toast;
    private Firebase mFirebaseRef;
    private Context context;

    private Toolbar veventToolbar;
    private TextView veName;
    private TextView veDesc;
    private TextView veLoc;
    private ListView eventMembers;
    private Button jEvent;


    private CharSequence toastText;
    private ArrayList<String> eMembers = new ArrayList<String>();
    private ArrayAdapter<String> eventAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_event);

        jEvent = (Button) findViewById(R.id.action_join_event);

        veName = (TextView) findViewById(R.id.view_event_name);
        veDesc = (TextView) findViewById(R.id.view_event_desc);
        veLoc = (TextView) findViewById(R.id.view_event_loc);
        eventMembers = (ListView) findViewById(R.id.vevent_members);
        eventMembers.setBackgroundColor(getResources().getColor(R.color.ghostWhite));
        veventToolbar = (Toolbar) findViewById(R.id.vevent_toolbar);
        setSupportActionBar(veventToolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        final String eid = intent.getStringExtra(MyRecyclerViewAdapter.EXTRA_ID);
        Firebase evRef = new Firebase(getResources().getString(R.string.firebaseevent_url)).child(eid);


        getEventDetails(eid);
        createIdList(eid);
        checkIfMember(eid);

        jEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                joinGroup(eid);
            }
        });

        evRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                createIdList(eid);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                toastText = firebaseError.toString();
                toast.makeText(context, toastText, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getEventDetails(String eid) {
        context = getApplicationContext();
        mFirebaseRef = new Firebase(getResources().getString(R.string.firebaseevent_url));
        final Firebase eventsRef = mFirebaseRef.child(eid);
        Query ref = eventsRef.orderByKey();

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                String name = snapshot.child("name").getValue().toString();
                String desc = snapshot.child("description").getValue().toString();
                String loc = snapshot.child("location").getValue().toString();
                veName.setText("Name: " + name);
                veDesc.setText(desc);
                veLoc.setText("Location: " + loc);
                }
            @Override
            public void onCancelled(FirebaseError firebaseError) {
                toastText = firebaseError.getMessage();
                toast.makeText(context, toastText, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateMemberList(ArrayList<String> mList) {
        eventAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, mList);
        eventMembers.setAdapter(eventAdapter);
    }

    private void createIdList(final String eid) {
        context = getApplicationContext();
        eMembers.clear();
        mFirebaseRef = new Firebase(getResources().getString(R.string.firebaseevent_url));
        final Firebase memberRef = mFirebaseRef.child(eid).child("members");
        Query ref = memberRef.orderByValue();

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    String uId = postSnapshot.getValue().toString();
                    String[] splitId = uId.split("=");
                    uId = splitId[0].replace("{", "").trim();
                    eMembers.add(uId);
                    getMemberUsernames(eMembers);
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                toastText = firebaseError.toString();
                toast.makeText(context,toastText, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkIfMember(String eId) {
        context = getApplicationContext();
        mFirebaseRef = new Firebase(getResources().getString(R.string.firebaseevent_url));
        final String userId = mFirebaseRef.getAuth().getUid().toString();
        final Firebase memberRef = mFirebaseRef.child(eId).child("members");
        Query ref = memberRef.orderByValue();

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Boolean isMember = dataSnapshot.hasChild(userId);
                if (isMember == false) {
                    toastText = "NOT a Member";
                    Toast.makeText(context, toastText, Toast.LENGTH_SHORT).show();
                    jEvent.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                toastText = firebaseError.toString();
                toast.makeText(context,toastText, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getMemberUsernames(ArrayList<String> iList) {
        context = getApplicationContext();
        final ArrayList<String> usernameList = new ArrayList<String>();
        usernameList.clear();
        mFirebaseRef = new Firebase(getResources().getString(R.string.firebaseuser_url));
        int lsize = iList.size();

        for(int i=0; i < lsize; i++) {
            String uId = iList.get(i);
            Firebase userFire = mFirebaseRef.child(uId);
            Query userRef = userFire.orderByKey();
            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String usr = dataSnapshot.child("username").getValue().toString();
                    usernameList.add(usr);
                    updateMemberList(usernameList);
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {
                    toastText = firebaseError.toString();
                    toast.makeText(context,toastText, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void joinGroup(final String eId) {
        context = getApplicationContext();
        toastText = "Joining...";
        Toast.makeText(context, toastText, Toast.LENGTH_SHORT).show();

        mFirebaseRef = new Firebase(getResources().getString(R.string.firebaseevent_url));
        final String userId = mFirebaseRef.getAuth().getUid().toString();
        final Firebase memberRef = mFirebaseRef.child(eId).child("members");
        Map<String, Object> member = new HashMap<String, Object>();
        member.put(userId,userId);
        memberRef.updateChildren(member, new Firebase.CompletionListener() {
            @Override
            public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                if (firebaseError !=null) {
                    toastText = "Unexpected Error. Try Again";
                    toast.makeText(context, toastText, Toast.LENGTH_SHORT).show();
                } else {
                    toastText = "Joined Event";
                    toast.makeText(context, toastText, Toast.LENGTH_SHORT).show();
                    jEvent.setVisibility(View.GONE);
                    eMembers.clear();
                    createIdList(eId);
                }
            }
        });

    }
}
