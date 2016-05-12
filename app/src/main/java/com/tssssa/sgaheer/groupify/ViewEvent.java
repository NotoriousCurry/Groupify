package com.tssssa.sgaheer.groupify;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;

public class ViewEvent extends AppCompatActivity {
    private Toast toast;
    private Firebase mFirebaseRef;
    private Context context;

    private Toolbar veventToolbar;
    private TextView veName;

    private CharSequence toastText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_event);

        veName = (TextView) findViewById(R.id.view_event_name);
        veventToolbar = (Toolbar) findViewById(R.id.vevent_toolbar);
        setSupportActionBar(veventToolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        String eid = intent.getStringExtra(MyRecyclerViewAdapter.EXTRA_ID);

        veName.setText(eid);
    }
}
