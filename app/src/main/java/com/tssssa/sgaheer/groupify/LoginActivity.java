package com.tssssa.sgaheer.groupify;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.view.menu.MenuItemImpl;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by sgaheer on 26/04/2016.
 */
public class LoginActivity extends AppCompatActivity {
    private static final String TAG = LoginActivity.class.getSimpleName();
    public final static String EXTRA_MESSAGE = "com.tssssa.sgaheer.groupify.MESSAGE";
    public final static String SIGN_MESSAGE = "com.tssssa.sgaheer.groupigy.SIGN";

    private ProgressDialog mAuthProgressDialog;
    private Firebase mFirebaseRef;
    private AuthData mAuthData;
    private Firebase.AuthStateListener mAuthStateListener;

    private Button mPasswordLoginButton;
    private Button mSignupButton;
    private TextView mEmail;
    private TextView mPass;

    private String lEmail;
    private String lPass;

    /***************************
     * Setting up the UI stuff *
     **************************/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        final Intent goToSign = new Intent(this, SignUpActivity.class);


        /********************************
         * FOR THE PASSWORD BASED LOGIN *
         *******************************/
        mEmail = (TextView) findViewById(R.id.email);
        mPass = (TextView) findViewById(R.id.password);

        mPasswordLoginButton = (Button) findViewById(R.id.login_with_password);
        mPasswordLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                lEmail = mEmail.getText().toString();
                lPass = mPass.getText().toString();
                loginWithPassword(lEmail, lPass);

            }
        });

        mSignupButton = (Button) findViewById(R.id.signup_transition);
        mSignupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                lEmail = mEmail.getText().toString();
                if (lEmail != "") {
                    goToSign.putExtra(SIGN_MESSAGE, lEmail);
                    startActivity(goToSign);
                } else {
                    startActivity(goToSign);
                }
            }
        });

        /*****************
         * General Stuff (progressDialog & prev login checks)*
         ****************/

        mFirebaseRef = new Firebase(getResources().getString(R.string.firebase_url));

        mAuthProgressDialog = new ProgressDialog(this);
        mAuthProgressDialog.setTitle("Loading");
        mAuthProgressDialog.setMessage("Authenticating with Firebase");
        mAuthProgressDialog.setCancelable(false);
        mAuthProgressDialog.show();

        mAuthStateListener = new Firebase.AuthStateListener() {
            @Override
            public void onAuthStateChanged(AuthData authData) {
                mAuthProgressDialog.hide();
                setAuthenticatedUser(authData);
            }
        };

        mFirebaseRef.addAuthStateListener(mAuthStateListener);
    }

    //protected void onNewIntent(Intent intent) {
    //    super.onNewIntent(intent);
    //    String message = intent.getStringExtra(HomeActivity.LOGOUT_MESSAGE);
    //    if(message =="logout") {
    //        logout();
    //    }
    // }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (this.mAuthData != null) {
            getMenuInflater().inflate(R.menu.main, menu);
            return true;
        } else {
            return false;
        }
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
        if (this.mAuthData != null) {
            mFirebaseRef.unauth();
            setAuthenticatedUser(null);
        }
    }

    private void authWithFirebase(final String provider, Map<String, String> options) {
        if (options.containsKey("error")) {
            showErrorDialog(options.get("error"));
        } else {
            mAuthProgressDialog.show();
            if (provider.equals("twitter")) {
                mFirebaseRef.authWithOAuthToken(provider, options, new AuthResultHandler(provider));
            } else {
                mFirebaseRef.authWithOAuthToken(provider, options.get("oauth_token"), new AuthResultHandler(provider));
            }
        }
    }

    private void setAuthenticatedUser(AuthData authData) {
        if (authData != null) {
            //
            String name = null;
            if (authData.getProvider().equals("password")) {
                name = authData.getUid();
            } else {
                Log.e(TAG, "Invalid Provider: " + authData.getProvider());
            }
            if (name != null) {
                // Go to Home screen if successful login)
                Intent goToHome = new Intent(this, HomeActivity.class);
                String user = mEmail.getText().toString();
                goToHome.putExtra(EXTRA_MESSAGE, user);
                startActivity(goToHome);
            }
        } else {
            // implement something here if needed
        }
        this.mAuthData = authData;
        supportInvalidateOptionsMenu();
    }

    private void showErrorDialog(String message) {
        new AlertDialog.Builder(this)
                .setTitle("Error")
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private class AuthResultHandler implements Firebase.AuthResultHandler {
        private final String provider;

        public AuthResultHandler(String provider) {
            this.provider = provider;
        }

        @Override
        public void onAuthenticated(AuthData authData) {
            mAuthProgressDialog.hide();
            // Saves some details about user in Firebase
            Map<String, String> maaps = new HashMap<String, String>();
            maaps.put("Email", lEmail);
            mFirebaseRef.child("users").child(authData.getUid()).setValue(maaps);
            Log.i(TAG, provider + "auth successful");
            setAuthenticatedUser(authData);
        }

        @Override
        public void onAuthenticationError(FirebaseError firebaseError) {
            mAuthProgressDialog.hide();
            showErrorDialog(firebaseError.toString());
        }
    }

    public void loginWithPassword(String email, String password) {
        mAuthProgressDialog.show();
        mFirebaseRef.authWithPassword(email, password, new AuthResultHandler("password"));
    }
}
