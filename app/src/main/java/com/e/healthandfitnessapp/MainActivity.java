package com.e.healthandfitnessapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {


    Button mBmiMainBtn;
    Button mHealthTipsMainBtn;
    Button mCalCounterMainBtn;
    Button mExerciseListMainBtn;
    TextView mInfoCentreMainTextView;
    TextView mInfoRightMainTextView;
    TextView mInfoLeftMainTextView;
    TextView mInfoLeftLabelTextView;
    TextView mInfoRightLabelTextView;
    Button mSignOutMainBtn;
    TextView mMainTextView;

    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBmiMainBtn = findViewById(R.id.bmiMainBtn);
        mHealthTipsMainBtn = findViewById(R.id.healthTipsMainBtn);
        mCalCounterMainBtn = findViewById(R.id.calCounterMainBtn);
        mExerciseListMainBtn = findViewById(R.id.exerciseListMainBtn);
        mInfoCentreMainTextView = findViewById(R.id.infoCentreMainTextView);
        mInfoRightMainTextView = findViewById(R.id.infoRightMainTextView);
        mInfoLeftMainTextView = findViewById(R.id.infoLeftMainTextView);
        mInfoLeftLabelTextView = findViewById(R.id.infoLeftLabelTextView);
        mInfoRightLabelTextView = findViewById(R.id.infoRightLabelTextView);
        mSignOutMainBtn = findViewById(R.id.signOutMainBtn);
        mMainTextView = findViewById(R.id.mainTextView);


        mInfoCentreMainTextView.setVisibility(View.INVISIBLE);
        mInfoLeftMainTextView.setVisibility(View.INVISIBLE);
        mInfoRightMainTextView.setVisibility(View.INVISIBLE);
        mInfoLeftLabelTextView.setVisibility(View.INVISIBLE);
        mInfoRightLabelTextView.setVisibility(View.INVISIBLE);

        loadData1();
        loadData2();
        loadData3();

        mBmiMainBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), BmiCalculatorActivity.class));
                finish();
            }
        });

        mHealthTipsMainBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), HealthTipsActivity.class));
                finish();
            }
        });

        mCalCounterMainBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), CalorieCounterActivity.class));
                finish();
            }
        });

        mExerciseListMainBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), ExerciseListActivity.class));
                finish();
            }
        });

        mSignOutMainBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebaseAuth.signOut();
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                finish();
            }
        });
    }

    public void loadData1(){

        String userId = firebaseAuth.getCurrentUser().getUid();
        DocumentReference documentReference = firebaseFirestore.collection(userId).document("BmiInfo");
        documentReference.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {

                        if(documentSnapshot.exists()){
                            String BMI = documentSnapshot.get("BmiAmt").toString();
                            float BMI2 = Float.parseFloat(BMI);
                            BMI = String.format("%.2f", BMI2);
                            mInfoCentreMainTextView.setText(BMI);
                            String CAT = documentSnapshot.get("BmiCat").toString();
                            switch (CAT){
                                case "Severely Underweight":
                                    mInfoCentreMainTextView.setBackgroundColor(getResources().getColor(R.color.orange));
                                    break;
                                case "Underweight":
                                    mInfoCentreMainTextView.setBackgroundColor(getResources().getColor(R.color.yellow));
                                    break;
                                case "Normal":
                                    mInfoCentreMainTextView.setBackgroundColor(getResources().getColor(R.color.green));
                                    break;
                                case "Overweight":
                                    mInfoCentreMainTextView.setBackgroundColor(getResources().getColor(R.color.yellow));
                                    break;
                                case "Obese":
                                    mInfoCentreMainTextView.setBackgroundColor(getResources().getColor(R.color.orange));
                                    break;
                                default:
                                    break;
                            }

                        }
                        else{
                            Toast.makeText(MainActivity.this, "Error fetching data", Toast.LENGTH_SHORT).show();
                            mInfoCentreMainTextView.setText("N/A");
                        }
                        mInfoCentreMainTextView.setVisibility(View.VISIBLE);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity.this, "Error getting BMI" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });



        DocumentReference documentReference2 = firebaseFirestore.collection(userId).document("RegisterInfo");
        documentReference2.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        //if(documentSnapshot.exists()) {
                            String username = documentSnapshot.get("Name").toString();
                            mMainTextView.setText(getString(R.string.mainText1) + username + getString(R.string.mainText2));
                        //}
                        //else{
                         //   mMainTextView.setText(R.string.mainText1 + R.string.mainText2);
                        //}
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity.this, "Error: "+ e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void loadData2(){
        String userId = firebaseAuth.getCurrentUser().getUid().toString();
        final DocumentReference documentReference = firebaseFirestore.collection(userId).document("CalInfo");
        documentReference.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(documentSnapshot.exists()){
                            String calRecom = documentSnapshot.get("CalRecom").toString();
                            float fCalRecom = Float.parseFloat(calRecom);
                            calRecom = String.format("%.2f", fCalRecom);
                            calRecom = calRecom + " kcal";

                            String calAmt = documentSnapshot.get("CalAmt").toString();
                            float fCalAmt = Float.parseFloat(calAmt);
                            calAmt = String.format("%.2f", fCalAmt);
                            calAmt = calAmt + " kcal";

                            if(fCalRecom == 1.0){
                                mInfoLeftLabelTextView.setText(calAmt);
                            }
                            else{
                                mInfoLeftLabelTextView.setText(calAmt + " /\n" + calRecom);
                                int ht = (int) ((fCalAmt*100)/fCalRecom);
                                if (ht>200){
                                    ht = 200;
                                }
                                if(ht==0){
                                    ht = 1;
                                }
                                mInfoLeftMainTextView.getLayoutParams().height = ht;
                                if(ht>=85 && ht<=115){
                                    mInfoLeftMainTextView.setBackgroundColor(getResources().getColor(R.color.blue));
                                }
                                else if(ht>=60 && ht<=140){
                                    mInfoLeftMainTextView.setBackgroundColor(getResources().getColor(R.color.yellow));
                                }
                                else{
                                    mInfoLeftMainTextView.setBackgroundColor(getResources().getColor(R.color.orange));
                                }
                            }
                            mInfoLeftLabelTextView.setVisibility(View.VISIBLE);
                            mInfoLeftMainTextView.setVisibility(View.VISIBLE);
                        }
                        else {
                            Toast.makeText(MainActivity.this, "Error fetching data", Toast.LENGTH_SHORT).show();
                            mInfoLeftLabelTextView.setText("N/A");
                            mInfoLeftLabelTextView.setVisibility(View.VISIBLE);
                        }

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity.this, "Error getting BMI" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.d("Firebase", e.getMessage());
                    }
                });
    }

    public void loadData3(){
        String userId = firebaseAuth.getCurrentUser().getUid();
        firebaseFirestore.collection(userId).document("Score").get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(documentSnapshot.exists()){
                            int points = Integer.parseInt(documentSnapshot.get("Points").toString());
                            String level = documentSnapshot.get("Level").toString();

                            mInfoRightMainTextView.getLayoutParams().height = points*2;
                            if(points == 0){
                                mInfoRightMainTextView.getLayoutParams().height = 1;
                            }
                            mInfoRightMainTextView.setBackgroundColor(getResources().getColor(R.color.blue));
                            String pointString = String.valueOf(points);
                            pointString = "Points: " + pointString;
                            level = "Level: " + level;
                            mInfoRightLabelTextView.setText(pointString + "\n" + level);

                        }
                        else{
                            mInfoRightMainTextView.getLayoutParams().height = 1;
                            mInfoRightMainTextView.setBackgroundColor(getResources().getColor(R.color.blue));
                            String pointString = String.valueOf(0);
                            String points = "Points: 0";
                            String level = "Level: 0";
                            mInfoRightLabelTextView.setText(points + "\n" + level);
                        }
                        mInfoRightLabelTextView.setVisibility(View.VISIBLE);
                        mInfoRightMainTextView.setVisibility(View.VISIBLE);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity.this, "Error getting score", Toast.LENGTH_SHORT).show();
                        Log.d("Firebase", e.getMessage());
                    }
                });
    }

}