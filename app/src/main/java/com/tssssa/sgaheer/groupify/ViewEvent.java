package com.tssssa.sgaheer.groupify;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.ViewUtils;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
/**
 * Created by sgaheer on 7/05/2016.
 * This class is used to handle the viewing of individal events
 * Contains code to read in data from firebase & to view a zoom in of the campus map
 */

public class ViewEvent extends AppCompatActivity {
    private Toast toast;
    private Firebase mFirebaseRef;
    private Context context;

    private Toolbar veventToolbar;
    private TextView veName;
    private TextView veDesc;
    private TextView veLoc;
    private TextView veDate;
    private ListView eventMembers;
    private ScrollView scroll;
    private ImageButton jEvent;
    private Animator mCurrentAnimator;


    private CharSequence toastText;
    private int mAnimationDuration;
    private ArrayList<String> eMembers = new ArrayList<String>();
    private ArrayAdapter<String> eventAdapter;
    public static final String EXTRA_USR = "com.tssssa.groupify.EXTRA_USR";
    public static final String EXTRA_ID = "com.tssssa.groupify.EXTRA_ID";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_event);

        jEvent = (ImageButton) findViewById(R.id.action_join_event);

        veName = (TextView) findViewById(R.id.view_event_name);
        veDesc = (TextView) findViewById(R.id.view_event_desc);
        veLoc = (TextView) findViewById(R.id.view_event_loc);
        veDate = (TextView) findViewById(R.id.view_event_date);
        eventMembers = (ListView) findViewById(R.id.vevent_members);
        eventMembers.setBackgroundColor(getResources().getColor(R.color.ghostWhite));
        eventMembers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(context, ViewFriend.class);
                String n = (String)parent.getItemAtPosition(position).toString();
                String i = (String)eMembers.get(position);
                intent.putExtra(EXTRA_USR, n);
                intent.putExtra(EXTRA_ID, i);
                startActivity(intent);
            }
        });

        veventToolbar = (Toolbar) findViewById(R.id.vevent_toolbar);
        setSupportActionBar(veventToolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        mAnimationDuration = getResources().getInteger(android.R.integer.config_shortAnimTime);

        Intent intent = getIntent();
        final String eid = intent.getStringExtra(MyRecyclerViewAdapter.EXTRA_ID);
        // Add code to get extra from notificationService
        Firebase evRef = new Firebase(getResources().getString(R.string.firebaseevent_url)).child(eid);


        getEventDetails(eid);
        createIdList(eid);
        checkIfMember(eid);

        jEvent.setFocusable(false);
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

        final View thumbnail = findViewById(R.id.imgThumb);
        thumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                zoomImage(thumbnail, R.drawable.kmap);
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
                String date = snapshot.child("date").getValue().toString();
                date = date + " - " + snapshot.child("time").getValue().toString();
                veName.setText(name);
                veDesc.setText(desc);
                veLoc.setText("Location: " + loc);
                veDate.setText(date);
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

    private void zoomImage(final View thumbnail, int image) {
        if (mCurrentAnimator != null) {
            mCurrentAnimator.cancel();
        }

        final ImageView expandedImg = (ImageView) findViewById(R.id.expanded_image);
        expandedImg.setImageResource(image);

        final Rect startB = new Rect();
        final Rect finalB = new Rect();
        final Point globalOffset = new Point();

        thumbnail.getGlobalVisibleRect(startB);
        findViewById(R.id.event_container).getGlobalVisibleRect(finalB, globalOffset);
        startB.offset(-globalOffset.x, -globalOffset.y);
        finalB.offset(-globalOffset.x, -globalOffset.y);

        float sScale;
        if ((float)finalB.width() / finalB.height()
                > (float) startB.width() / startB.height()) {
            sScale = (float) startB.height() / finalB.height();
            float sWidth = sScale * finalB.width();
            float dWidth = (sWidth - startB.width()) / 2;
            startB.left -= dWidth;
            startB.right += dWidth;
        } else {
            sScale = (float) startB.width() / finalB.width();
            float sHeight = sScale * finalB.height();
            float dHeight = (sHeight - startB.height()) / 2;
            startB.top -= dHeight;
            startB.bottom += dHeight;
        }

        thumbnail.setAlpha(0f);
        expandedImg.setVisibility(View.VISIBLE);
        expandedImg.setPivotX(0f);
        expandedImg.setPivotY(0f);

        AnimatorSet set = new AnimatorSet();
        set
                .play(ObjectAnimator.ofFloat(expandedImg, View.X, startB.left, finalB.left))
                .with(ObjectAnimator.ofFloat(expandedImg, View.Y, startB.top, finalB.top))
                .with(ObjectAnimator.ofFloat(expandedImg, View.SCALE_X, sScale, 1f))
                .with(ObjectAnimator.ofFloat(expandedImg, View.SCALE_Y, sScale, 1f));
        set.setDuration(mAnimationDuration);
        set.setInterpolator(new DecelerateInterpolator());
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mCurrentAnimator=null;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                mCurrentAnimator=null;
            }
        });
        set.start();
        mCurrentAnimator = set;

        final float sScaleFinal = sScale;
        expandedImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AnimatorSet sett = new AnimatorSet();
                if(mCurrentAnimator != null) {
                    mCurrentAnimator.cancel();
                }
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                sett.play(ObjectAnimator.ofFloat(expandedImg, View.X, startB.left))
                        .with(ObjectAnimator.ofFloat(expandedImg, View.Y, startB.top))
                        .with(ObjectAnimator.ofFloat(expandedImg, View.SCALE_X, sScaleFinal))
                        .with(ObjectAnimator.ofFloat(expandedImg, View.SCALE_Y, sScaleFinal));
                sett.setDuration(mAnimationDuration);
                sett.setInterpolator(new DecelerateInterpolator());
                sett.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationCancel(Animator animation) {
                        thumbnail.setAlpha(1f);
                        expandedImg.setVisibility(View.GONE);
                        mCurrentAnimator=null;
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        thumbnail.setAlpha(1f);
                        expandedImg.setVisibility(View.GONE);
                        mCurrentAnimator=null;
                    }
                });
                sett.start();
                mCurrentAnimator=sett;
            }
        });
    }
}
