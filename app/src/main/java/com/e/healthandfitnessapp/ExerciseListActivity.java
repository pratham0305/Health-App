package com.e.healthandfitnessapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class ExerciseListActivity extends AppCompatActivity {

    String exercises[], links[];
    RecyclerView mExListRecyclerView;
    Button mHomeBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise_list);

        mHomeBtn = findViewById(R.id.homeBtn4);
        mExListRecyclerView = findViewById(R.id.exListRecyclerView);

        exercises = getResources().getStringArray(R.array.exercises);
        links = getResources().getStringArray(R.array.links);

        MyAdapter myAdapter = new MyAdapter(this, exercises, links);

        mExListRecyclerView.setAdapter(myAdapter);
        mExListRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mHomeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
            }
        });

    }
}