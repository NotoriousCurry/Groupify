package com.tssssa.sgaheer.groupify;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.TextView;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.realtime.util.StringListReader;

import android.view.View;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by sgaheer on 28/04/2016.
 * This class is used to handle the sign up page for creating new user accounts
 * Contain basic error checking code to ensure passwords arent inputted wrong
 */

public class SignUpActivity extends AppCompatActivity {
    private ProgressDialog mAuthProgressDialog;
    private Button sign;
    private TextView emails;
    private TextView passwords;
    private TextView usernames;
    private TextView cpasswords;
    private Toast toast;
    private Context context;
    private Toolbar siupToolbar;

    private String em;
    private String pass;
    private String usr;
    private String cpass;
    private CharSequence toastText;

    private Firebase mFirebaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        Intent intent = getIntent();
        String temail = intent.getStringExtra(LoginActivity.SIGN_MESSAGE);

        siupToolbar = (Toolbar) findViewById(R.id.siup_toolbar);
        setSupportActionBar(siupToolbar);

        mFirebaseRef = new Firebase(getResources().getString(R.string.firebase_url));
        emails = (TextView) findViewById(R.id.signup_email);
        emails.setText(temail);
        passwords = (TextView) findViewById(R.id.signup_password);
        usernames = (TextView) findViewById(R.id.signup_username);
        cpasswords = (TextView) findViewById(R.id.confirm_password);
        cpasswords.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    em = emails.getText().toString();
                    pass = passwords.getText().toString();
                    usr = usernames.getText().toString();
                    cpass = cpasswords.getText().toString();
                    signUp(em, pass, usr, cpass);
                    handled = true;
                }
                return handled;
            }
        });
        sign = (Button) findViewById(R.id.signup);
        sign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                em = emails.getText().toString();
                pass = passwords.getText().toString();
                usr = usernames.getText().toString();
                cpass = cpasswords.getText().toString();
                signUp(em, pass, usr, cpass);
            }
        });
    }

    private void signUp(String em, String pass, String usr, String cpass) {
        boolean check = pass.equals(cpass);
        boolean uCheck = usr.equals("");
        context = getApplicationContext();
        toastText = "Creating Account";
        if (check == false) {
            showErrorDialog("Passwords Do Not Match");
            passwords.setText("");
            cpasswords.setText("");
        } else if (uCheck == true) {
            showErrorDialog("Please Enter a Username");
        } else {
            GUser newUsr = new GUser(usr, em, pass, "", "false");
            createFbUser(newUsr);
            toast.makeText(context, toastText, Toast.LENGTH_SHORT).show();
        }
    }

    private void createFbUser(final GUser newUsr) {
        mAuthProgressDialog = new ProgressDialog(this);
        context = getApplicationContext();
        mAuthProgressDialog.setTitle("Loading");
        mAuthProgressDialog.setMessage("Authenticating with Firebase");
        mAuthProgressDialog.setCancelable(false);
        mAuthProgressDialog.show();

        mFirebaseRef.createUser(newUsr.getEmail(), newUsr.getPassword(), new Firebase.ValueResultHandler<Map<String, Object>>() {
            @Override
            public void onSuccess(Map<String, Object> result) {
                mFirebaseRef.authWithPassword(newUsr.getEmail(), newUsr.getPassword(), new Firebase.AuthResultHandler() {
                    @Override
                    public void onAuthenticated(AuthData authData) {
                        HashMap<String, String> authMap = new HashMap<String, String>();
                        authMap.put("uid", authData.getUid().toString());
                        authMap.put("username", newUsr.getUsername());
                        authMap.put("email", newUsr.getEmail());
                        authMap.put("password", newUsr.getPassword());
                        mFirebaseRef.child("users").child(authData.getUid()).setValue(authMap);
                        toastText = "Successfully Created Account";
                        toast.makeText(context, toastText, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onAuthenticationError(FirebaseError firebaseError) {
                        showErrorDialog(firebaseError.getMessage());
                    }
                });
            }

            public void onError(FirebaseError firebaseError) {
                showErrorDialog(firebaseError.getMessage());
            }
        });
        mAuthProgressDialog.hide();
    }

    private void showErrorDialog(String message) {
        new AlertDialog.Builder(this)
                .setTitle("Error")
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

}
