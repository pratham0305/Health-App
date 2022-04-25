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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class BmiCalculatorActivity extends AppCompatActivity {

    EditText mWeightEditText;
    EditText mHeightEditText;
    EditText mAgeEditText;
    EditText mGenderEditText;
    Button mBmiCalcBtn;
    TextView mBmiCatTextView;
    TextView mBmiAmtTextView;
    Button mBmiResetBtn;
    Button mHomeBtn;
    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bmi_calculator);

        mWeightEditText = findViewById(R.id.weightEditText);
        mHeightEditText = findViewById(R.id.heightEditText);
        mAgeEditText = findViewById(R.id.ageEditText);
        mGenderEditText = findViewById(R.id.genderEditText);
        mBmiCalcBtn = findViewById(R.id.bmiCalcBtn);
        mBmiCatTextView = findViewById(R.id.bmiCatTextView);
        mBmiAmtTextView = findViewById(R.id.bmiAmtTextView);
        mBmiResetBtn = findViewById(R.id.bmiResetBtn);
        mHomeBtn = findViewById(R.id.homeBtn);



        mBmiCalcBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String weight = mWeightEditText.getText().toString().trim();
                String height = mHeightEditText.getText().toString().trim();
                String agestring = mAgeEditText.getText().toString().trim();

                String gender = mGenderEditText.getText().toString().trim();

                if(TextUtils.isEmpty(weight)) {
                    mWeightEditText.setError("Weight is required");
                    return;
                }
                if(TextUtils.isEmpty(height)){
                    mHeightEditText.setError("Height is required");
                    return;
                }
                if(TextUtils.isEmpty(agestring)){
                    mHeightEditText.setError("Age is required");
                    return;
                }
                if(TextUtils.isEmpty(gender)){
                    mHeightEditText.setError("Gender is required");
                    return;
                }

                int age = Integer.parseInt(agestring);
                float fweight = Float.parseFloat(weight);
                float fheight = Float.parseFloat(height);
                fheight = fheight/100;
                //Converting fheight's value from cm to m

                float bmiVal;
                bmiVal = fweight/(fheight*fheight);

                String bmiCat = "";
                if(bmiVal < 16){
                    bmiCat = "Severely Underweight";
                }
                else if(bmiVal < 18.5){
                    bmiCat = "Underweight";
                }
                else if(bmiVal <25){
                    bmiCat = "Normal";
                }
                else if(bmiVal < 30){
                    bmiCat = "Overweight";
                }
                else{
                    bmiCat = "Obese";
                }


                String BmiVal = String.format("%.2f", bmiVal);
                mBmiAmtTextView.setText(BmiVal);
                mBmiCatTextView.setText(bmiCat);
                fheight = fheight*100;

                final String userID = firebaseAuth.getCurrentUser().getUid();
                Map<String, Object> userInfo = new HashMap<>();
                userInfo.put("BmiAmt", bmiVal);
                userInfo.put("BmiCat", bmiCat);
                userInfo.put("Weight", fweight);
                userInfo.put("Height", fheight);
                userInfo.put("Age", age);
                userInfo.put("Gender", gender);
                firebaseFirestore.collection(userID).document("BmiInfo").set(userInfo)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(BmiCalculatorActivity.this, "BMI updated to database", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(BmiCalculatorActivity.this, "Database could not be reached", Toast.LENGTH_SHORT).show();
                                Log.d("Firestore", e.toString());
                            }
                        });


            }
        });

        mBmiResetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mWeightEditText.setText("");
                mHeightEditText.setText("");
                mBmiCatTextView.setText("");
                mBmiAmtTextView.setText("");
                mAgeEditText.setText("");
                mGenderEditText.setText("");
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
}