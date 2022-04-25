package com.e.healthandfitnessapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
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

import java.util.HashMap;
import java.util.Map;

public class ExercisePageActivity extends AppCompatActivity {

    TextView mExPageTitleTextView;
    TextView mExPageLinkTextView;
    TextView mExPageDescTextView;
    Button mLvl1Btn;
    Button mLvl2Btn;
    Button mLvl3Btn;
    Button mBackBtn;
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

    String exerciseName, exerciseLink;

    int level, points, increment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise_page);

        mExPageTitleTextView = findViewById(R.id.exPageTitleTextView);
        mExPageLinkTextView = findViewById(R.id.exPageLinkTextView);
        mExPageDescTextView = findViewById(R.id.exPageDescTextView);
        mLvl1Btn = findViewById(R.id.lvl1Btn);
        mLvl2Btn = findViewById(R.id.lvl2Btn);
        mLvl3Btn = findViewById(R.id.lvl3Btn);
        mBackBtn = findViewById(R.id.backBtn);

        mExPageDescTextView.setText(getResources().getString(R.string.instructions));

        getData();
        setData();

        mExPageLinkTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    Uri uri = Uri.parse(exerciseLink);
                    startActivity(new Intent(Intent.ACTION_VIEW, uri));
            }
        });

        mLvl1Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                increment = 10;
                score();
            }
        });

        mLvl2Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                increment = 15;
                score();
            }
        });

        mLvl3Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                increment = 20;
                score();
            }
        });

        mBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), ExerciseListActivity.class));
                finish();
            }
        });

    }

    private void getData(){
        if (getIntent().hasExtra("Exercise") && getIntent().hasExtra("Link")){
            exerciseName = getIntent().getStringExtra("Exercise");
            exerciseLink = getIntent().getStringExtra("Link");
        }
        else{
            Toast.makeText(this, "No data found", Toast.LENGTH_SHORT).show();
        }
    }

    private void  setData(){
        mExPageTitleTextView.setText(exerciseName);
        mExPageLinkTextView.setText("Video Tutorial");
    }

    public void score(){
        final String userId = firebaseAuth.getCurrentUser().getUid().toString();
        firebaseFirestore.collection(userId).document("Score").get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        int points, level;
                        if(!documentSnapshot.exists()){
                            Map<String, Object> score = new HashMap<>();
                            score.put("Points", increment);
                            score.put("Level", 0);
                            firebaseFirestore.collection(userId).document("Score").set(score);
                        }
                        else {
                            points = Integer.parseInt(documentSnapshot.get("Points").toString());
                            level = Integer.parseInt(documentSnapshot.get("Level").toString());

                            points = points + increment;
                            if(points>=100){
                                level++;
                                points = 100-points;
                            }
                            Map<String, Object> score = new HashMap<>();
                            score.put("Level", level);
                            score.put("Points", points);

                            firebaseFirestore.collection(userId).document("Score").update(score)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(ExercisePageActivity.this, "Score updated", Toast.LENGTH_SHORT).show();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(ExercisePageActivity.this, "Error contacting database", Toast.LENGTH_SHORT).show();
                                            Log.d("Firebase", e.getMessage());
                                        }
                                    });
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ExercisePageActivity.this, "Error contacting database 2", Toast.LENGTH_SHORT).show();
                        Log.d("Firebase", e.getMessage());
                    }
                });
    }
}