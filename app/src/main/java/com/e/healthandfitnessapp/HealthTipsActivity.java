package com.e.healthandfitnessapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.util.List;

public class HealthTipsActivity extends AppCompatActivity {

    ListView mHealthTipsListView;
    Button mHomeBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_health_tips);

        mHealthTipsListView = findViewById(R.id.healthTipsListView);
        mHomeBtn = findViewById(R.id.homeBtn2);

        String[] healthTips = getResources().getStringArray(R.array.healthTips);
        ArrayAdapter<CharSequence> aa = ArrayAdapter.createFromResource(this, R.array.healthTips, android.R.layout.simple_expandable_list_item_1);
        mHealthTipsListView.setAdapter(aa);


        mHomeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
            }
        });

    }
}