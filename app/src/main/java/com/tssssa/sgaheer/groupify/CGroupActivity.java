package com.tssssa.sgaheer.groupify;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.firebase.client.Firebase;
/**
 * Created by sgaheer on 01/05/2016.
 * This class is meant to handle the creation of groups, however has not been implemented yet
 */

public class CGroupActivity extends AppCompatActivity {
    private Toolbar cgroupToolbar;
    private Firebase mFirebaseRef;
    public final static String LOGOUT_MESSAGE = "com.tssssa.groupify.LOGOUT";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cgroup);

        cgroupToolbar = (Toolbar) findViewById(R.id.cgroup_toolbar);
        setSupportActionBar(cgroupToolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.cgroup, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.action_cglogout:
                logout();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void logout() {
        mFirebaseRef.unauth();
        Intent goToLogin = new Intent(this, LoginActivity.class);
        goToLogin.putExtra(LOGOUT_MESSAGE, "logout");
        startActivity(goToLogin);
    }
}
