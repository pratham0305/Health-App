package com.e.healthandfitnessapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Xml;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.xmlpull.v1.XmlPullParser;

import java.util.HashMap;
import java.util.Map;

public class CalorieCounterActivity extends AppCompatActivity {

    EditText mRiceEditText;
    EditText mWheatEditText;
    EditText mPulsesEditText;
    EditText mMeatEditText;
    EditText mFruitsVegEditText;
    EditText mDairyEditText;
    Button mCalCountBtn;
    Button mCalResetBtn;
    TextView mCalAmtTextView;
    TextView mCalRecomTextView;
    Button mHomeBtn;
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    float fCalRecom = (float) 1.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calorie_counter);

        mRiceEditText = findViewById(R.id.riceEditText);
        mWheatEditText = findViewById(R.id.wheatEditText);
        mPulsesEditText = findViewById(R.id.pulsesEditText);
        mMeatEditText = findViewById(R.id.meatEditText);
        mFruitsVegEditText = findViewById(R.id.fruitsVegEditText);
        mDairyEditText = findViewById(R.id.dairyEditText);
        mCalCountBtn = findViewById(R.id.calCountBtn);
        mCalResetBtn = findViewById(R.id.calResetBtn);
        mCalAmtTextView = findViewById(R.id.calAmtTextView);
        mCalRecomTextView = findViewById(R.id.calRecomTextView);
        mHomeBtn = findViewById(R.id.homeBtn3);

        mCalCountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int fcalOK = calculateCalCount();

                if(fcalOK==-1){
                    return;
                }
                calculateCalRecom1();

            }
        });

        mCalResetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRiceEditText.setText("");
                mWheatEditText.setText("");
                mPulsesEditText.setText("");
                mMeatEditText.setText("");
                mFruitsVegEditText.setText("");
                mDairyEditText.setText("");
                mCalAmtTextView.setText("");
            }
        });

        mHomeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
            }
        });


    }

    public int calculateCalCount(){

        //Takes data from edit text and calculates calories
        //displays calories in text view
        //uploads calories to firebase

        String riceString = mRiceEditText.getText().toString().trim();
        String wheatString = mWheatEditText.getText().toString().trim();
        String pulsesString = mPulsesEditText.getText().toString().trim();
        String meatString = mMeatEditText.getText().toString().trim();
        String fruitsVegString = mFruitsVegEditText.getText().toString().trim();
        String dairyString = mDairyEditText.getText().toString().trim();

        if(TextUtils.isEmpty(riceString)){
            mRiceEditText.setError("Amount required");
            return -1;
        }
        if(TextUtils.isEmpty(wheatString)){
            mWheatEditText.setError("Amount required");
            return -1;
        }
        if(TextUtils.isEmpty(pulsesString)){
            mPulsesEditText.setError("Amount required");
            return -1;
        }
        if(TextUtils.isEmpty(meatString)){
            mMeatEditText.setError("Amount required");
            return -1;
        }
        if(TextUtils.isEmpty(fruitsVegString)){
            mFruitsVegEditText.setError("Amount required");
            return -1;
        }
        if(TextUtils.isEmpty(dairyString)){
            mDairyEditText.setError("Amount required");
            return -1;
        }

        Float fRice = Float.parseFloat(riceString);
        Float fWheat = Float.parseFloat(wheatString);
        Float fPulses = Float.parseFloat(pulsesString);
        Float fMeat = Float.parseFloat(meatString);
        Float fFruitsVeg = Float.parseFloat(fruitsVegString);
        Float fDairy = Float.parseFloat(dairyString);

        fRice = fRice*130*10;
        fWheat = fWheat*340*10;
        fPulses = fPulses*132*10;
        fMeat = fMeat*247*10;
        fFruitsVeg = fFruitsVeg*32*10;
        fDairy = fDairy*50*10;

        Float fCalAmt = fRice + fWheat + fPulses + fMeat + fFruitsVeg + fDairy;
        String calAmtString = String.format("%.2f", fCalAmt);
        calAmtString = calAmtString + " kcal/day";

        mCalAmtTextView.setText(calAmtString);

        String userId = firebaseAuth.getCurrentUser().getUid().toString();
        Map<String, Object> calCount = new HashMap<>();
        calCount.put("CalAmt", fCalAmt);
        firebaseFirestore.collection(userId).document("CalInfo").set(calCount)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(CalorieCounterActivity.this, "Calorie intake updated", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(CalorieCounterActivity.this, "Error contacting database", Toast.LENGTH_SHORT).show();
                    }
                });
        return 1;

    }

    public void calculateCalRecom1(){

        //Takes data from firebase
        //Sets variable acc to data
        //Sends data for calculating recommended calories
        //to calculateCalRecom2()

        String userId = firebaseAuth.getCurrentUser().getUid().toString();
        firebaseFirestore.collection(userId).document("BmiInfo").get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(documentSnapshot.exists()){
                            float fWeight = Float.parseFloat(documentSnapshot.get("Weight").toString());
                            float fHeight = Float.parseFloat(documentSnapshot.get("Height").toString());
                            int age = Integer.parseInt(documentSnapshot.get("Age").toString());
                            String gender = documentSnapshot.get("Gender").toString();

                            float a, b, c, d;

                            if(gender == "M"){
                                a = (float) 88.362;
                                b = (float) 13.397;
                                c = (float) 4.799;
                                d = (float) 5.677;
                            }
                            else {
                                a = (float) 447.593;
                                b = (float) 9.247;
                                c = (float) 3.098;
                                d = (float) 4.330;
                            }
                            calculateCalRecom2(a, b, c, d, fHeight, fWeight, age);
                        }
                        else {
                            Toast.makeText(CalorieCounterActivity.this, "Error calculating recommended calories", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(CalorieCounterActivity.this, "Database could not be reached", Toast.LENGTH_SHORT).show();
                        Log.d("Firestore", e.toString());
                    }
                });
    }

    public void calculateCalRecom2(float a, float b, float c, float d, float height, float weight, int age){

        //Takes variables as args
        //Calculates and sets calRecom
        //sets text to calRecom

        fCalRecom = a + (b*weight) + (c*height) -(d*age);

        String calRecomString = String.format("%.2f", fCalRecom);
        calRecomString = calRecomString + " kcal recommended";

        mCalRecomTextView.setText(calRecomString);

        String userId = firebaseAuth.getCurrentUser().getUid().toString();
        Map<String, Object> calRecom = new HashMap<>();
        calRecom.put("CalRecom", fCalRecom);
        firebaseFirestore.collection(userId).document("CalInfo").update(calRecom)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(CalorieCounterActivity.this, "Calorie recommendation updated", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(CalorieCounterActivity.this, "Error contacting database", Toast.LENGTH_SHORT).show();
                        Log.d("Firebase", e.getMessage());
                    }
                });
        return;
    }

}