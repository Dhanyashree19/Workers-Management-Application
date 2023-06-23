package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MyAdapter1 extends RecyclerView.Adapter<MyViewHolder1> {
    private Context context;
    private List<Profile> dataList;

    public MyAdapter1(Context context, List<Profile> dataList) {
        this.context = context;
        this.dataList = dataList;
    }

    @NonNull
    @Override
    public MyViewHolder1 onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item, parent, false);
        return new MyViewHolder1(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder1 holder, int position) {
        Profile profile = dataList.get(position);
        holder.recName.setText(profile.getName());
        holder.recLocation.setText(profile.getLocation());

        holder.recCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, DetailActivity1.class);
                intent.putExtra("Name", profile.getName());
                intent.putExtra("Location", profile.getLocation());
                intent.putExtra("PhNo", profile.getPhoneNo());
                intent.putExtra("WageExpectation", profile.getSalary());
                intent.putExtra("Key", profile.getKey());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public void searchDataList(ArrayList<Profile> searchList) {
        dataList = searchList;
        notifyDataSetChanged();
    }
}

class MyViewHolder1 extends RecyclerView.ViewHolder {
    ImageView recImage;
    TextView recName, recDomain, recLocation;
    CardView recCard;

    public MyViewHolder1(@NonNull View itemView) {
        super(itemView);
        recImage = itemView.findViewById(R.id.recImage);
        recCard = itemView.findViewById(R.id.recCard);
        recLocation = itemView.findViewById(R.id.recLocation);
        recName = itemView.findViewById(R.id.recName);
    }
}
