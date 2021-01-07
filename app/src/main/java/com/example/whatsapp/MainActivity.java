package com.example.whatsapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.HashSet;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    TextView loginTv;
    Button signUpButton;
    EditText emailEt;
    EditText passwordEt;
    static EditText usernameEt;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().hide();
        loginTv = findViewById(R.id.loginTv);
        signUpButton = findViewById(R.id.signUpButton);
        emailEt = findViewById(R.id.emailET);
        passwordEt = findViewById(R.id.passwordET);
        usernameEt = findViewById(R.id.usernameET);

        mAuth = FirebaseAuth.getInstance();

        checkFieldEmptyValue();

        TextWatcher watcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
               checkFieldEmptyValue();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };

        emailEt.addTextChangedListener(watcher);
        passwordEt.addTextChangedListener(watcher);

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.createUserWithEmailAndPassword(emailEt.getText().toString(), passwordEt.getText().toString())
                        .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Log.d(TAG, "createUserWithEmail:success");

                                    HashMap<String,String> userInfo = new HashMap<>();
                                    userInfo.put("email", emailEt.getText().toString());
                                    userInfo.put("username", usernameEt.getText().toString());
                                    FirebaseDatabase.getInstance().getReference().child("users").child(mAuth.getCurrentUser().getUid()).setValue(userInfo);

                                    redirectActivity();

                                } else {
                                    // If sign in fails, display a message to the user.
                                    Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                    Toast.makeText(MainActivity.this, "Authentication failed: " + (task.getException().toString().split(":"))[1],
                                            Toast.LENGTH_LONG).show();

                                }

                                // ...
                            }
                        });
            }
        });

        loginTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signInWithEmailAndPassword(emailEt.getText().toString(), passwordEt.getText().toString())
                        .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Log.d(TAG, "signInWithEmail:success");

                                    redirectActivity();

                                } else {
                                    // If sign in fails, display a message to the user.
                                    Log.w(TAG, "signInWithEmail:failure", task.getException());
                                    Toast.makeText(MainActivity.this, "Authentication failed: " + (task.getException().toString().split(":"))[1],
                                            Toast.LENGTH_LONG).show();

                                }

                                // ...
                            }
                        });
            }
        });

        if (mAuth.getCurrentUser() != null) {
            redirectActivity();

        }


            }

    private void checkFieldEmptyValue() {
        if (emailEt.getText().toString().equals("") || passwordEt.getText().toString().equals("") || usernameEt.getText().toString().equals("")) {
            signUpButton.setEnabled(false);
            loginTv.setEnabled(false);
        } else {
            signUpButton.setEnabled(true);
            loginTv.setEnabled(true);

        }
    }

    private void redirectActivity() {
        Intent intent = new Intent(MainActivity.this, UserListActivity.class);
        startActivity(intent);
    }
}
