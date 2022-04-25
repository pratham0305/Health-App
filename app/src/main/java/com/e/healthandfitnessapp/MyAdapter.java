package com.e.healthandfitnessapp;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

    String array1[], array2[];
    Context context;

    public MyAdapter(Context ct, String exercises[], String links[]){
        context = ct;
        array1 = exercises;
        array2 = links;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.my_row, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        holder.textView1.setText(array1[position]);
        holder.textView2.setText(array2[position]);
        holder.mainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ExercisePageActivity.class);
                intent.putExtra("Exercise", array1[position]);
                intent.putExtra("Link", array2[position]);

                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return array1.length;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        TextView textView1, textView2;
        ConstraintLayout mainLayout;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            textView1 = itemView.findViewById(R.id.exNameTextView);
            textView2 = itemView.findViewById(R.id.exLinkTextView);
            mainLayout = itemView.findViewById(R.id.mainLayout);
        }
    }
}
