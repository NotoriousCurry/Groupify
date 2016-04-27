package com.tssssa.sgaheer.groupify;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.realtime.util.StringListReader;

import android.view.View;

import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {
    private ProgressDialog mAuthProgressDialog;
    private Button sign;
    private TextView emails;
    private TextView passwords;
    private TextView usernames;
    private TextView cpasswords;

    private String em;
    private String pass;
    private String usr;
    private String cpass;

    private Firebase mFirebaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        Intent intent = getIntent();
        String temail = intent.getStringExtra(LoginActivity.SIGN_MESSAGE);

        mFirebaseRef = new Firebase(getResources().getString(R.string.firebase_url));
        emails = (TextView) findViewById(R.id.signup_email);
        emails.setText(temail);
        passwords = (TextView) findViewById(R.id.signup_password);
        usernames = (TextView) findViewById(R.id.signup_username);
        cpasswords = (TextView) findViewById(R.id.confirm_password);
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
        if (check == false) {
            showErrorDialog("Passwords Do Not Match");
            passwords.setText("");
            cpasswords.setText("");
        } else if (uCheck == true) {
            showErrorDialog("Please Enter a Username");
        }
        else {
            GUser newUsr = new GUser(usr, em, pass);
            createFbUser(newUsr);
        }
    }

    private void createFbUser(final GUser newUsr) {
        mAuthProgressDialog = new ProgressDialog(this);
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
                      showSuccessDialog("Successfully created account with uid: " + authData.getUid());
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

    private void showSuccessDialog(String message) {
        new AlertDialog.Builder(this)
                .setTitle("Success")
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

}
