package com.e.healthandfitnessapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    EditText mNameEditText;
    EditText mEmailEditText;
    EditText mPasswordEditText;
    EditText mPhNoEditText;
    Button mRegisterBtn;
    TextView mLogTextView;
    ProgressBar mRegProgressBar;

    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mNameEditText = findViewById(R.id.nameEditText);
        mEmailEditText = findViewById(R.id.emailRegEditText);
        mPasswordEditText = findViewById(R.id.passwordRegEditText);
        mPhNoEditText = findViewById(R.id.phNoEditText);
        mRegisterBtn = findViewById(R.id.registerBtn);
        mLogTextView = findViewById(R.id.logTextView);
        mRegProgressBar = findViewById(R.id.regProgressBar);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();



        mRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String name = mNameEditText.getText().toString().trim();
                final String email = mEmailEditText.getText().toString().trim();
                final String password = mPasswordEditText.getText().toString().trim();
                final String phno = mPhNoEditText.getText().toString().trim();
                //String userID;

                if(TextUtils.isEmpty(name)){
                    mNameEditText.setError("Name is required.");
                    return;
                }
                if(TextUtils.isEmpty(email)){
                    mEmailEditText.setError("Email is required.");
                    return;
                }
                if(TextUtils.isEmpty(password)){
                    mPasswordEditText.setError("Password is required.");
                    return;
                }
                if(TextUtils.isEmpty(phno)){
                    mPhNoEditText.setError("Phone number is required.");
                    return;
                }

                mRegProgressBar.setVisibility(View.VISIBLE);
                firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(RegisterActivity.this, "Registration Successful", Toast.LENGTH_SHORT).show();
                            final String userID = firebaseAuth.getCurrentUser().getUid();
                            Map<String, Object> userInfo = new HashMap<>();
                            userInfo.put("Name", name);
                            userInfo.put("Email", email);
                            userInfo.put("PhNo", phno);
                            firebaseFirestore.collection(userID).document("RegisterInfo").set(userInfo)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(RegisterActivity.this, "User info uploaded to database", Toast.LENGTH_SHORT).show();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(RegisterActivity.this, "Database could not be reached", Toast.LENGTH_SHORT).show();
                                            Log.d("Firestore", e.toString());
                                        }
                                    });

                            startActivity(new Intent(getApplicationContext(), BmiCalculatorActivity.class));
                        }
                        else{
                            Toast.makeText(RegisterActivity.this, "ERROR! " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            mRegProgressBar.setVisibility(View.INVISIBLE);
                        }
                    }
                });
            }
        });

        mLogTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            }
        });

    }
}