package com.tssssa.sgaheer.groupify;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.firebase.client.Firebase;

public class HomeActivity extends AppCompatActivity {
    private TextView mLoggedInStatusTextview;
    private Firebase mFirebaseRef;
    public final static String LOGOUT_MESSAGE = "com.tssssa.groupify.LOGOUT";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        mLoggedInStatusTextview = (TextView) findViewById(R.id.login_status);
        mFirebaseRef = new Firebase(getResources().getString(R.string.firebase_url));

        Intent intent = getIntent();
        String user = intent.getStringExtra(LoginActivity.EXTRA_MESSAGE);
        mLoggedInStatusTextview.setText("Logged in as: " + user);
        mLoggedInStatusTextview.setVisibility(View.VISIBLE);
        supportInvalidateOptionsMenu();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_logout) {
            logout();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void logout() {
        mFirebaseRef.unauth();
        Intent goToLogin = new Intent(this, LoginActivity.class);
        goToLogin.putExtra(LOGOUT_MESSAGE, "logout");
        startActivity(goToLogin);
    }
}
