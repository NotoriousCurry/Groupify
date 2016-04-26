package com.tssssa.sgaheer.groupify;

import android.app.AlertDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import com.firebase.client.Firebase;
import android.view.View;

public class SignUpActivity extends AppCompatActivity {
    private Button sign;
    private TextView email;
    private TextView password;
    private TextView username;
    private TextView cpassword;

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
        email = (TextView) findViewById(R.id.signup_email);
        email.setText(temail);
        password = (TextView) findViewById(R.id.signup_password);
        username = (TextView) findViewById(R.id.signup_username);
        cpassword = (TextView) findViewById(R.id.confirm_password);
        sign = (Button) findViewById(R.id.signup);
        sign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                em = email.getText().toString();
                pass = password.getText().toString();
                usr = username.getText().toString();
                cpass = cpassword.getText().toString();
                signUp(em, pass, usr, cpass);
            }
        });
    }

    private void signUp(String em, String pass, String usr, String cpass) {
        if (pass != cpass) {
            showErrorDialog("Passwords Do Not Match");
            password.setText("");
            cpassword.setText("");
        } else {
            createFbUser(em, pass, usr, cpass);
        }
    }

    private void createFbUser(String em, String pass, String usr, String cpass) {

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
